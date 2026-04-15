package nro.services;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import nro.consts.ConstNpc;
import nro.jdbc.DBService;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.utils.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Hoàng Việt - 0857853150
 *
 */
public class KhamNgoc {

    @Getter
    public int id;

    private static final byte START = 0;
    public final List<KhamNgocTemplate> khamNgocTemplates = new ArrayList<>();
    public static final List<KhamNgoc> KHAM_NGOC = new ArrayList<>();
    private static KhamNgoc i;

    public static KhamNgoc gI() {
        if (i == null) {
            i = new KhamNgoc();
        }
        return i;
    }

    public void loadKhamNgoc() {
        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT * FROM `kham_ngoc`");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    KhamNgoc khamngoc = new KhamNgoc();
                    khamngoc.id = rs.getInt("id");
                    JSONArray jArr = new JSONArray(rs.getString("options"));
                    for (int i = 0; i < jArr.length(); i++) {
                        KhamNgocTemplate khamngocOptions = new KhamNgocTemplate();
                        JSONObject obj = jArr.getJSONObject(i);
                        khamngocOptions.level = obj.getInt("level");
                        khamngocOptions.tempId = obj.getInt("tempid");
                        khamngocOptions.max_value = obj.getInt("max_value");
                        int oID = obj.getInt("id");
                        int oParam = obj.getInt("param");
                        khamngocOptions.options = new ItemOption(oID, oParam);
                        khamngocOptions.options.optionTemplate.id = oID;
                        khamngocOptions.options.param = oParam;
                        khamngoc.khamNgocTemplates.add(khamngocOptions);
                    }
                    KHAM_NGOC.add(khamngoc);
                }
                Log.success("Load Kham Ngoc thành công (" + KHAM_NGOC.size() + ")");
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Send_KhamNgocTemplate(Player pl) {
        Message msg = null;
        try {
            msg = new Message(108);
            msg.writer().writeByte(0);
            msg.writer().writeByte(START);
            msg.writer().writeByte(KHAM_NGOC.size());

            for (int j = 0; j < KHAM_NGOC.size(); j++) {
                KhamNgoc manager = KHAM_NGOC.get(j);
                msg.writer().writeInt(manager.id);
                msg.writer().writeByte(manager.khamNgocTemplates.size());
                for (int k = 0; k < manager.khamNgocTemplates.size(); k++) {
                    KhamNgocTemplate template = manager.khamNgocTemplates.get(k);
                    msg.writer().writeInt(template.level);
                    msg.writer().writeInt(template.tempId);
                    msg.writer().writeInt(template.max_value);
                    msg.writer().writeInt(template.options.optionTemplate.id);
                    msg.writer().writeInt(template.options.param);
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_KhamNgoc_Player(Player pl) {
        Message msg = null;
        try {
            msg = new Message(108);
            msg.writer().writeByte(1);
            msg.writer().writeByte(pl.active_kham_ngoc);
            msg.writer().writeByte(pl.khamNgoc.size());

            for (int j = 0; j < pl.khamNgoc.size(); j++) {
                KhamNgocPlayer manager = pl.khamNgoc.get(j);
                msg.writer().writeInt(manager.idNro);
                msg.writer().writeInt(manager.levelNro);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void activeKhamNgoc(Player pl, byte active) {
        pl.active_kham_ngoc = active;
        pl.nPoint.calPoint();
        Service.getInstance().point(pl);
        Send_KhamNgoc_Player(pl);
    }
    
    
    public void NangCapKhamNgoc(Player pl, byte nro) {
        pl.nroKhamNgoc = nro;
        KhamNgoc khamNgoc = KHAM_NGOC.get(nro);
        KhamNgocPlayer manager = pl.khamNgoc.get(nro);
        int level = manager.levelNro;
        int max_level = khamNgoc.khamNgocTemplates.size();
        if ((level + 1) >= max_level) {
            Service.getInstance().sendThongBao(pl, "Bạn đã đạt cấp tối đa");
            return;
        }
        int idTemp = khamNgoc.khamNgocTemplates.get(level + 1).tempId;
        int max_quatity = khamNgoc.khamNgocTemplates.get(level + 1).max_value;
        pl.idTempNangCap = idTemp;
        pl.slItem = max_quatity;
        if ((level + 1) >= max_level) {
            Service.getInstance().sendThongBao(pl, "Bạn đã đạt cấp tối đa");
            return;
        }
        if (nro > 0) {
            int levelBefore = pl.khamNgoc.get(nro - 1).levelNro;
            int levelBeforeMax = KHAM_NGOC.get(nro - 1).khamNgocTemplates.size();
            if (levelBefore == - 1) {
                Service.getInstance().sendThongBao(pl, "Vui lòng kích hoạt Ngọc rồng " + nro + " sao trước");
                return;
            }
            if (levelBefore < (levelBeforeMax - 1)) {
                Service.getInstance().sendThongBao(pl, "Vui lòng Nâng Ngọc rồng " + nro + " sao đến cấp tối đa trước");
                return;
            }
        }
        Item item = InventoryService.gI().findItemBagByTemp(pl, (short) idTemp);
        Item it = ItemService.gI().createNewItem((short) idTemp);
        if (item == null || item.quantity < max_quatity) {
            Service.getInstance().sendThongBao(pl, "Không đủ nguyên liệu. Còn thiếu " + (item == null ? max_quatity : (max_quatity - item.quantity)) + " " + it.template.name);
            return;
        }
        NpcService.gI().createMenuConMeo(pl, ConstNpc.NANG_CAP_KHAM_NGOC, 0,
                "Bạn có muốn dùng\n"
                        + "|1|" + max_quatity + " " + it.template.name
                        + "\n|6|để nâng cấp Ngọc rồng " + (nro + 1) + " sao không?\n",
                 "Đồng ý", "Đóng");
    }

//    public void NangCapKhamNgoc(Player pl, byte nro) {
//        KhamNgoc khamNgoc = KHAM_NGOC.get(nro);
//        KhamNgocPlayer manager = pl.khamNgoc.get(nro);
//        int level = manager.levelNro;
//        int max_level = khamNgoc.khamNgocTemplates.size();
//        if ((level + 1) >= max_level) {
//            Service.getInstance().sendThongBao(pl, "Bạn đã đạt cấp tối đa");
//            return;
//        }
//        if (nro > 0) {
//            int levelBefore = pl.khamNgoc.get(nro - 1).levelNro;
//            int levelBeforeMax = KHAM_NGOC.get(nro - 1).khamNgocTemplates.size();
//            if (levelBefore == - 1) {
//                Service.getInstance().sendThongBao(pl, "Vui lòng kích hoạt Ngọc rồng " + nro + " sao trước");
//                return;
//            }
//            if (levelBefore < (levelBeforeMax - 1)) {
//                Service.getInstance().sendThongBao(pl, "Vui lòng Nâng Ngọc rồng " + nro + " sao đến cấp tối đa trước");
//                return;
//            }
//        }
//        int idTemp = khamNgoc.khamNgocTemplates.get(level + 1).tempId;
//        int max_quatity = khamNgoc.khamNgocTemplates.get(level + 1).max_value;
//        Item item = InventoryService.gI().findItemBagByTemp(pl, (short) idTemp);
//        Item it = ItemService.gI().createNewItem((short) idTemp);
//        if (item == null || item.quantity < max_quatity) {
//            Service.getInstance().sendThongBao(pl, "Không đủ nguyên liệu. Còn thiếu " + (item == null ? max_quatity : (max_quatity - item.quantity)) + " " + it.template.name);
//            return;
//        }
//        InventoryService.gI().subQuantityItemsBag(pl, item, max_quatity);
//        manager.levelNro++;
//        InventoryService.gI().sendItemBags(pl);
//        pl.nPoint.calPoint();
//        Service.getInstance().point(pl);
//        Send_KhamNgoc_Player(pl);
//        if (manager.levelNro == 0) {
//            Service.getInstance().sendThongBao(pl, "|2|Kích hoạt thành công Ngọc rồng " + (nro + 1) + " sao");
//        } else {
//            Service.getInstance().sendThongBao(pl, "|2|Nâng thành công Ngọc rồng " + (nro + 1) + " sao lên Cấp " + manager.levelNro);
//        }
//    }

}
