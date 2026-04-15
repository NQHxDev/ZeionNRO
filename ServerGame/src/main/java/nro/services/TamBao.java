package nro.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import nro.jdbc.DBService;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.utils.Log;
import nro.utils.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * Tầm Bảo - đọc vật phẩm từ SQL, cấu trúc giống Phúc Lợi.
 * - Mỗi "pool" gắn với một key_item_id (loại khóa).
 * - Dùng key nào thì chỉ quay ra vật phẩm có key đó.
 * - Nếu pool thiếu slot, sẽ bù bằng fallback IDs.
 * - Ghi lịch sử thắng vào history_tambao.
 */
public class TamBao {

    // ====== cấu hình ======
    private static final int REQUIRED_SLOTS = 14;
    private static final int[] FALLBACK_IDS = {220,221,222,223,224,15,15,17,18,19,20,381,382,383,384,385};
    private static final int DEFAULT_VIP_FLAG = 0;
    private static final int DEFAULT_FALLBACK_KEY = 1778;
    public static final List<TamBao_Item> MOC_TAMBAO = new ArrayList<>();
    private final Map<Integer, List<Integer>> POOL_TILE = new HashMap<>();

    private final Map<Integer, List<Item>> POOLS = new HashMap<>();
    private int DEFAULT_KEY_ITEM_ID = -1;

    private final Map<Integer, List<Integer>> POOL_VIP_FLAGS = new HashMap<>();

    private static final byte START = 0;
    private static TamBao instance;

    public static TamBao gI() {
        if (instance == null) instance = new TamBao();
        return instance;
    }

    private TamBao() {
    }

    public void loadItem_TamBao() {
        POOLS.clear();
        POOL_VIP_FLAGS.clear();
        POOL_TILE.clear();
        DEFAULT_KEY_ITEM_ID = -1;

        final String sql = """
        SELECT id, key_item_id, item_id, quantity, item_options, tile_trung_thuong, des,
               start_at, end_at, enabled
        FROM tambao_items
        WHERE enabled = 1
          AND (start_at IS NULL OR start_at <= NOW())
          AND (end_at IS NULL OR end_at >= NOW())
        ORDER BY id ASC
    """;

        int totalRows = 0;
        try (Connection con = DBService.gI().getConnectionForGame(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            JSONValue jv = new JSONValue();

            while (rs.next()) {
                totalRows++;
                int keyId = rs.getInt("key_item_id");
                POOLS.computeIfAbsent(keyId, k -> new ArrayList<>());
                POOL_VIP_FLAGS.computeIfAbsent(keyId, k -> new ArrayList<>());
                POOL_TILE.computeIfAbsent(keyId, k -> new ArrayList<>());

                Integer singleId = getNullableInt(rs, "item_id");
                Integer singleQty = getNullableInt(rs, "quantity");
                String optCompact = rs.getString("item_options");
                int tilePercent = rs.getInt("tile_trung_thuong"); // 1 == 1%

                if (singleId != null && singleQty != null) {
                    Item it = ItemService.gI().createNewItem(singleId.shortValue(), singleQty);
                    if (optCompact != null && !optCompact.isEmpty()) {
                        addOptionsFromCompact(optCompact, it);
                    }

                    POOLS.get(keyId).add(it);
                    POOL_VIP_FLAGS.get(keyId).add(DEFAULT_VIP_FLAG);
                    POOL_TILE.get(keyId).add(Math.max(0, Math.min(100, tilePercent))); // clamp 0..100
                }
            }

            // Chỉ chọn key mặc định, KHÔNG bù fallback ở đây
            if (!POOLS.isEmpty() && DEFAULT_KEY_ITEM_ID == -1) {
                DEFAULT_KEY_ITEM_ID = POOLS.keySet().iterator().next();
            }
            Log.success("Load tambao_items: rows=" + totalRows + ", pools=" + POOLS.size());
        } catch (Exception e) {
            Log.error(TamBao.class, e, "Lỗi loadItem_TamBao()");
        }
    }

    private void padWithFallback(int keyId, List<Item> list) {
        List<Integer> vipFlags = POOL_VIP_FLAGS.get(keyId);
        List<Integer> tiles = POOL_TILE.get(keyId);     // <<< thêm
        if (vipFlags == null || tiles == null) {
            return;
        }

        int need = REQUIRED_SLOTS - list.size();
        int idx = 0;
        while (need-- > 0) {
            int itemId = FALLBACK_IDS[idx % FALLBACK_IDS.length];
            Item fb = ItemService.gI().createNewItem((short) itemId, 1);
            list.add(fb);
            vipFlags.add(DEFAULT_VIP_FLAG);
            tiles.add(0); // fallback không có tile riêng
            idx++;
        }
    }

    private static class SpinPool {

        final int keyId;
        final List<Item> items;      // đúng 14 item
        final List<Integer> tiles;   // cùng thứ tự với items

        SpinPool(int keyId, List<Item> items, List<Integer> tiles) {
            this.keyId = keyId;
            this.items = items;
            this.tiles = tiles;
        }
    }
    private final Map<Long, SpinPool> LAST_SPIN_VIEW = new java.util.concurrent.ConcurrentHashMap<>();
    private SpinPool ensureSpinPool14(int keyId) {
        List<Item> sqlItems = POOLS.getOrDefault(keyId, Collections.emptyList());
        List<Integer> sqlTiles = POOL_TILE.getOrDefault(keyId, Collections.emptyList());

        // 1) Chọn từ SQL: nếu >14 thì random 14; nếu <=14 lấy hết
        List<Item> chosenItems = new ArrayList<>(REQUIRED_SLOTS);
        List<Integer> chosenTiles = new ArrayList<>(REQUIRED_SLOTS);

        int sqlCount = sqlItems.size();
        if (sqlCount > 0) {
            List<Integer> idx = new ArrayList<>();
            for (int i = 0; i < sqlCount; i++) {
                idx.add(i);
            }
            Collections.shuffle(idx);

            int take = Math.min(REQUIRED_SLOTS, sqlCount);
            for (int k = 0; k < take; k++) {
                int i = idx.get(k);
                chosenItems.add(sqlItems.get(i));
                Integer t = (i < sqlTiles.size() ? sqlTiles.get(i) : 0);
                chosenTiles.add(sanitizePercent(t));
            }
        }

        // 2) Nếu thiếu slot thì bù fallback (tile = 0)
        int missing = REQUIRED_SLOTS - chosenItems.size();
        if (missing > 0) {
            List<Integer> fb = new ArrayList<>();
            for (int id : FALLBACK_IDS) {
                fb.add(id);
            }
            Collections.shuffle(fb);
            for (int k = 0; k < missing; k++) {
                int itemId = fb.get(k % fb.size());
                Item fbItem = ItemService.gI().createNewItem((short) itemId, 1);
                chosenItems.add(fbItem);
                chosenTiles.add(0);
            }
        }

        // 3) Trộn thứ tự cuối cùng để “mỗi lần load vòng quay” là 1 bố cục ngẫu nhiên
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < REQUIRED_SLOTS; i++) {
            order.add(i);
        }
        Collections.shuffle(order);

        List<Item> shuffledItems = new ArrayList<>(REQUIRED_SLOTS);
        List<Integer> shuffledTiles = new ArrayList<>(REQUIRED_SLOTS);
        for (int i : order) {
            shuffledItems.add(chosenItems.get(i));
            shuffledTiles.add(chosenTiles.get(i));
        }

        return new SpinPool(keyId, shuffledItems, shuffledTiles);
    }

    private int sanitizePercent(Integer t) {
        return (t == null) ? 0 : Math.max(0, Math.min(100, t));
    }

    private int pickIndexByPercent(List<Integer> tiles) {
        int n = tiles.size();
        if (n == 0) {
            return 0;
        }

        double[] weights = new double[n];
        double sumPos = 0.0;
        int zeros = 0;

        for (int i = 0; i < n; i++) {
            int t = (tiles.get(i) == null) ? 0 : tiles.get(i);
            if (t > 0) {
                weights[i] = t; // % trực tiếp
                sumPos += t;
            } else {
                weights[i] = 0.0;
                zeros++;
            }
        }

        if (sumPos > 0) {
            if (sumPos > 100.0) {
                // chuẩn hoá về 100%
                for (int i = 0; i < n; i++) {
                    if (weights[i] > 0) {
                        weights[i] = (weights[i] / sumPos) * 100.0;
                    }
                }
            } else if (sumPos < 100.0) {
                // chia phần trăm dư cho các item tile=0 (thường là fallback)
                if (zeros > 0) {
                    double bonus = (100.0 - sumPos) / zeros;
                    for (int i = 0; i < n; i++) {
                        if (weights[i] == 0.0) {
                            weights[i] = bonus;
                        }
                    }
                } else {
                    // không còn item 0% để nhận dư → scale lên 100
                    for (int i = 0; i < n; i++) {
                        weights[i] = (weights[i] / sumPos) * 100.0;
                    }
                }
            }
        } else {
            // tất cả = 0 → chia đều 100%
            double each = 100.0 / n;
            Arrays.fill(weights, each);
        }

        // quay theo phân phối tích luỹ
        double total = 0.0;
        for (double w : weights) {
            total += w;
        }

        double r = Math.random() * total;
        double acc = 0.0;
        for (int i = 0; i < n; i++) {
            acc += weights[i];
            if (r < acc) {
                return i;
            }
        }
        return n - 1;
    }

    // =========================================================
    // API QUAY
    // =========================================================
// Luôn dùng key mặc định nếu key hiện hành chưa thiết lập
    public void QuayTamBao(Player pl, int solan) {
        int keyId = (DEFAULT_KEY_ITEM_ID != -1) ? DEFAULT_KEY_ITEM_ID : DEFAULT_FALLBACK_KEY;
        QuayTamBaoWithKey(pl, solan, keyId);
    }

    public void QuayTamBaoWithKey(Player pl, int solan, int keyItemId) {
        int effectiveKeyId = keyItemId;
        List<Item> pool = POOLS.get(effectiveKeyId);

        if (pool == null || pool.isEmpty()) {
            effectiveKeyId = DEFAULT_FALLBACK_KEY;
            pool = POOLS.get(effectiveKeyId);
            if (pool == null || pool.isEmpty()) {
                pool = buildFallbackPool(); // không dùng tiles ở đây
            }
        }

        // kiểm tra chìa
        Item key = InventoryService.gI().findItemBagByTemp(pl, effectiveKeyId);
        if (key == null || key.quantity < solan) {
            if (effectiveKeyId != DEFAULT_FALLBACK_KEY) {
                Item fbKey = InventoryService.gI().findItemBagByTemp(pl, DEFAULT_FALLBACK_KEY);
                if (fbKey != null && fbKey.quantity >= solan) {
                    effectiveKeyId = DEFAULT_FALLBACK_KEY;
                    key = fbKey;
                }
            }
        }
        if (key == null || key.quantity < solan) {
            String kname = ItemService.gI().getTemplate(effectiveKeyId).name;
            Service.getInstance().sendThongBao(pl, "|7|Không đủ " + kname);
            return;
        }

        // hành trang
        if (InventoryService.gI().getCountEmptyBag(pl) < solan) {
            Service.getInstance().sendThongBao(pl, "Hành trang cần ít nhất " + solan + " chổ trống");
            return;
        }

        // dùng pool đúng với UI đã gửi (nếu có), nếu khác key hoặc chưa có → tạo mới
        SpinPool sp = LAST_SPIN_VIEW.get(pl.id);
        if (sp == null || sp.keyId != effectiveKeyId || sp.items.size() != REQUIRED_SLOTS) {
            sp = ensureSpinPool14(effectiveKeyId);
            LAST_SPIN_VIEW.put(pl.id, sp);
        }

        List<Item> spinItems = sp.items;
        List<Integer> spinTiles = sp.tiles;

        pl.list_id_nhan = new int[REQUIRED_SLOTS];
        String text = "|7|Nhận được\n";

        for (int i = 0; i < solan; i++) {
            int k = pickIndexByPercent(spinTiles);
            Item base = spinItems.get(k);

            int qty = base.quantity;
            if (base.template != null && base.template.type == 9) {
                qty = Util.nextInt(1_000_000, 20_000_000);
            }

            Item prize = cloneWithQuantity(base, qty);

            pl.list_id_nhan[k] = 1;
            InventoryService.gI().addItemBag(pl, prize, -333);

            if (prize.template.type == 9) {
                text += "|5|x" + Util.format(prize.quantity) + " " + prize.template.name + "\n";
            } else if (prize.template.id == 457) {
                text += "|8|x" + Util.format(prize.quantity) + " " + prize.template.name + "\n";
            } else {
                text += "|6|x" + Util.format(prize.quantity) + " " + prize.template.name + "\n";
            }
            insertHistory(pl, prize);
        }

        pl.diem_quay += solan;
        InventoryService.gI().subQuantityItemsBag(pl, key, solan);
        Service.getInstance().sendThongBaoFromAdmin(pl, text);
        InventoryService.gI().sendItemBags(pl);
        Send_MocTamBao(pl);
        Send_QuayThuong(pl);
    }

    // Nếu chưa có, thêm helper tạo pool fallback 14 item thường
    private List<Item> buildFallbackPool() {
        List<Item> list = new ArrayList<>(REQUIRED_SLOTS);
        for (int i = 0; i < REQUIRED_SLOTS; i++) {
            int itemId = FALLBACK_IDS[i % FALLBACK_IDS.length];
            list.add(ItemService.gI().createNewItem((short) itemId, 1));
        }
        return list;
    }


    // =========================================================
    // SEND DATA CHO CLIENT (wire-protocol giữ nguyên)
    // =========================================================
    public void Send_QuayThuong(Player pl) {
        try {
            Message msg = new Message(106);
            msg.writer().writeByte(2);
            msg.writer().writeByte(pl.list_id_nhan.length);
            for (int v : pl.list_id_nhan) msg.writer().writeInt(v);
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) { }
    }

    /**
     * Gửi danh sách vật phẩm trong pool theo key mặc định.
     * Nếu bạn có UI cho
     * nhiều key khác nhau, hãy thêm overload `Send_TamBao(pl, keyItemId)`.
     */
    public void Send_TamBao(Player pl) {
        int keyId = (DEFAULT_KEY_ITEM_ID != -1) ? DEFAULT_KEY_ITEM_ID : DEFAULT_FALLBACK_KEY;
        SpinPool sp = ensureSpinPool14(keyId);
        LAST_SPIN_VIEW.put(pl.id, sp); // cache cho lần quay sắp tới
        try {
            Message msg = new Message(106);
            msg.writer().writeByte(0);
            msg.writer().writeByte(START);
            msg.writer().writeShort((short) keyId);
            msg.writer().writeShort(ItemService.gI().getTemplate(keyId).iconID);
            msg.writer().writeByte(REQUIRED_SLOTS);

            for (int i = 0; i < REQUIRED_SLOTS; i++) {
                Item it = sp.items.get(i);
                msg.writer().writeByte(0);                 // active_vip flag
                msg.writer().writeShort(it.template.id);
                msg.writer().writeInt(it.quantity);
                msg.writer().writeUTF(it.getInfo());
                msg.writer().writeUTF(it.getContent());
                List<ItemOption> opts = it.getDisplayOptions();
                msg.writer().writeByte(opts.size());
                for (ItemOption o : opts) {
                    msg.writer().writeByte(o.optionTemplate.id);
                    msg.writer().writeInt(o.param);
                }
            }
            pl.sendMessage(msg);
        } catch (Exception ignored) {
        }
    }

    public void Send_MocTamBao(Player pl) {
        // giữ nguyên code cũ của bạn (đọc MOC_TAMBAO từ DB),
        // chỉ việc gọi lại như hiện tại. Không đổi wire-protocol.
        TamBao.gI().Check_active(pl);
        try {
            Message msg = new Message(106);
            msg.writer().writeByte(1);
            msg.writer().writeInt(pl.diem_quay);
            msg.writer().writeByte(MOC_TAMBAO.size());
            for (int h = 0; h < MOC_TAMBAO.size(); h++) {
                msg.writer().writeInt(pl.checkNhan_TamBao[h]);
                TamBao_Item item = MOC_TAMBAO.get(h);
                msg.writer().writeShort(item.template.id);
                msg.writer().writeInt(item.quantity);
                msg.writer().writeUTF(item.getInfo());
                msg.writer().writeUTF(item.getContent());
                List<ItemOption> itemOptions = item.getDisplayOptions();
                msg.writer().writeInt(item.id_moc);
                msg.writer().writeInt(item.max_value);
                msg.writer().writeByte(itemOptions.size());
                for (ItemOption o : itemOptions) {
                    msg.writer().writeByte(o.optionTemplate.id);
                    msg.writer().writeInt(o.param);
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) { }
    }

    public void Active_TamBao(Player pl, int id) {
        TamBao_Item item = MOC_TAMBAO.get(id);
        if (pl.checkNhan_TamBao[id] == 0) {
            if (pl.diem_quay >= item.max_value) {
                if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
                    pl.listNhan_TamBao.add(id);
                    InventoryService.gI().addItemBag(pl, item, -333);
                    Service.getInstance().sendThongBao(pl, "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                    InventoryService.gI().sendItemBags(pl);
                    Send_MocTamBao(pl);
                } else {
                    Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                }
            } else {
                Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
            }
        } else {
            Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
        }
    }

    public void Check_active(Player pl) {
        try {
            pl.checkNhan_TamBao = new int[MOC_TAMBAO.size()];
            for (int a = 0; a < MOC_TAMBAO.size(); a++) {
                TamBao_Item moc = MOC_TAMBAO.get(a);
                for (int t = 0; t < pl.listNhan_TamBao.size(); t++) {
                    if (pl.listNhan_TamBao.get(t).equals(moc.id_moc)) {
                        pl.checkNhan_TamBao[a] = 1;
                    }
                }
            }
        } catch (Exception ignored) { }
    }

    // =========================================================
    // LOAD MỐC TẦM BẢO (đọc giống Phúc Lợi) — GIỮ LẠI từ code của bạn, chỉ parse options chuẩn
    // =========================================================
    public void load_mocTamBao() {
        MOC_TAMBAO.clear();
        final String sql = "SELECT * FROM moc_vong_quay"; // hoặc đổi sang bảng mới nếu bạn muốn
        try (Connection con = DBService.gI().getConnectionForGame();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            JSONValue jsonValue = new JSONValue();
            while (rs.next()) {
                TamBao_Item tambao = new TamBao_Item();
                int id_moc = rs.getInt("id");
                int templateId = rs.getInt("item_id");

                tambao.template = ItemService.gI().getTemplate(templateId);
                tambao.quantity = rs.getInt("quantity");
                tambao.createTime = System.currentTimeMillis();
                tambao.max_value = rs.getInt("max_value");
                tambao.id_moc = id_moc;

                tambao.itemOptions.clear();
                String raw = rs.getString("item_options");
                Object parsed = jsonValue.parse(raw);
                if (parsed instanceof JSONArray arr) {
                    for (Object entry : arr) {
                        // hỗ trợ [{id,param}] hoặc [id,param] hoặc chuỗi JSON
                        addOptionFromFlexibleEntry(entry, tambao);
                    }
                }
                MOC_TAMBAO.add(tambao);
            }
            Log.success("Load Mốc Tầm Bảo thành công (" + MOC_TAMBAO.size() + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // HELPERS
    // =========================================================
    private static Integer getNullableInt(ResultSet rs, String col) {
        try {
            int v = rs.getInt(col);
            return rs.wasNull() ? null : v;
        } catch (Exception e) {
            return null;
        }
    }

    private void addOptionsFromCompact(String compact, Item target) {
        // "30-1,77-50" -> (30,1) (77,50)
        String[] parts = compact.split(",");
        for (String p : parts) {
            p = p.trim();
            if (p.isEmpty()) {
                continue;
            }
            String[] kv = p.split("-");
            if (kv.length < 2) {
                continue;
            }
            int id = Integer.parseInt(kv[0].trim());
            int param = Integer.parseInt(kv[1].trim());
            target.itemOptions.add(new ItemOption(id, param));
        }
    }
    // parse 1 vật phẩm từ JSONObject: {"id":457,"quantity":9999,"options":[{"id":30,"param":1},...]}
    private Item parseItemObject(JSONObject obj) {
        if (obj == null) return null;
        Object idv = obj.get("id");
        Object qv = obj.get("quantity");
        if (idv == null || qv == null) return null;

        int id = toInt(idv);
        int qty = toInt(qv);
        Item it = ItemService.gI().createNewItem((short) id, qty);

        Object opts = obj.get("options");
        if (opts instanceof JSONArray arr) {
            for (Object entry : arr) {
                addOptionFromFlexibleEntry(entry, it);
            }
        }
        return it;
    }

    // chấp nhận {id,param} hoặc [id,param] hoặc chuỗi JSON tương ứng
    private void addOptionFromFlexibleEntry(Object entry, Item target) {
        JSONValue jv = new JSONValue();
        if (entry instanceof JSONObject jo) {
            int oid = toInt(jo.get("id"));
            int par = toInt(jo.get("param"));
            target.itemOptions.add(new ItemOption(oid, par));
            return;
        }
        if (entry instanceof JSONArray pair) {
            if (pair.size() >= 2) {
                int oid = toInt(pair.get(0));
                int par = toInt(pair.get(1));
                target.itemOptions.add(new ItemOption(oid, par));
            }
            return;
        }
        if (entry instanceof String s) {
            Object again = jv.parse(s);
            if (again != null && again != entry) addOptionFromFlexibleEntry(again, target);
        }
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number n) return n.intValue();
        return Integer.parseInt(String.valueOf(o));
    }

    // clone base item + set quantity; copy option theo template id/param
    private Item cloneWithQuantity(Item base, int quantity) {
        Item it = ItemService.gI().createNewItem(base.template.id, quantity);
        for (ItemOption io : base.itemOptions) {
            it.itemOptions.add(new ItemOption(io.optionTemplate.id, io.param));
        }
        return it;
    }

    // ghi lịch sử quay (lưu JSON của vật phẩm)
    private void insertHistory(Player pl, Item prize) {
        final String sql = "INSERT INTO history_tambao (id_player, item) VALUES (?, ?)";
        try (Connection con = DBService.gI().getConnectionForGame();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, pl.id);
            ps.setString(2, itemToJson(prize));
            ps.executeUpdate();
        } catch (Exception ignored) { }
    }

    // serialize item -> JSON giống Phúc Lợi để tiện xem log
    private String itemToJson(Item it) {
        JSONObject obj = new JSONObject();
        obj.put("id", (int) it.template.id);
        obj.put("quantity", it.quantity);
        JSONArray opts = new JSONArray();
        for (ItemOption io : it.itemOptions) {
            JSONObject o = new JSONObject();
            o.put("id", io.optionTemplate.id);
            o.put("param", io.param);
            opts.add(o);
        }
        obj.put("options", opts);
        return obj.toJSONString();
    }
}
