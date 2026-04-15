package nro.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.player.Player;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import nro.jdbc.DBService;
import nro.jdbc.daos.PlayerDAO;
import nro.server.Manager;
import nro.server.io.Message;
import nro.utils.Log;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Hoàng Việt - 0857853150
 *
 */
public class PhucLoi {

    private int id;
    private String name;
    private int max_count;
    private int count;
    private byte active;
    private static final byte START = 1;

    private int tab_id;
    public static final List<PhucLoiManager> PHUCLOI_MANAGER = new ArrayList<>();
    public final List<Item> PHUCLOI_LIST_ITEM = new ArrayList<>();
    public static final List<PhucLoi> PHUCLOI_TEMPLATES = new ArrayList<>();

    private static PhucLoi i;

    public static PhucLoi gI() {
        if (i == null) {
            i = new PhucLoi();
        }
        return i;
    }

    public void Send_PhucLoi(Player pl) {
        Check_active(pl);
        Message msg = null;
        try {
            msg = new Message(103);
            msg.writer().writeByte(START);
            msg.writer().writeByte(PHUCLOI_MANAGER.size());

            for (int j = 0; j < PHUCLOI_MANAGER.size(); j++) {
                PhucLoiManager manager = PHUCLOI_MANAGER.get(j);
                msg.writer().writeUTF(manager.tab_name);
                msg.writer().writeInt(manager.max_tab);
                msg.writer().writeInt(manager.id_tab);
                msg.writer().writeUTF(manager.info_phucloi);
                msg.writer().writeInt(manager.action);
                msg.writer().writeUTF(manager.tichLuy);
                msg.writer().writeByte(PHUCLOI_TEMPLATES.size());
                for (int k = 0; k < PHUCLOI_TEMPLATES.size(); k++) {
                    PhucLoi phucloi = PHUCLOI_TEMPLATES.get(k);
                    msg.writer().writeInt(pl.checkNhan[k]);
                    msg.writer().writeInt(phucloi.tab_id);
                    if (phucloi.tab_id != manager.id_tab) {
                        continue;
                    }
                    msg.writer().writeInt(phucloi.tab_id);
                    msg.writer().writeInt(phucloi.id);
                    msg.writer().writeUTF(phucloi.name);
                    msg.writer().writeInt(phucloi.max_count);
                    msg.writer().writeByte(phucloi.active);
                    msg.writer().writeInt(countPlayer(pl, phucloi));
                    msg.writer().writeInt(phucloi.PHUCLOI_LIST_ITEM.size());
                    for (int h = 0; h < phucloi.PHUCLOI_LIST_ITEM.size(); h++) {
                        Item item = phucloi.PHUCLOI_LIST_ITEM.get(h);
                        msg.writer().writeShort(item.template.id);
                        msg.writer().writeInt(item.quantity);
                        msg.writer().writeUTF(item.getInfo());
                        msg.writer().writeUTF(item.getContent());
                        List<ItemOption> itemOptions = item.getDisplayOptions();
                        msg.writer().writeByte(itemOptions.size()); //options
                        for (ItemOption o : itemOptions) {
                            msg.writer().writeByte(o.optionTemplate.id);
                            msg.writer().writeInt(o.param);
                        }
                    }
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Check_active(Player pl) {
        try {
            pl.checkNhan = new int[PHUCLOI_TEMPLATES.size()];
            for (int a = 0; a < PHUCLOI_TEMPLATES.size(); a++) {
                PhucLoi ploi = PHUCLOI_TEMPLATES.get(a);
                for (int t = 0; t < pl.listNhan.size(); t++) {
                    if (pl.listNhan.get(t).equals(ploi.id)) {
                        pl.checkNhan[a] = 1;
                    }
                }
                for (int j = 0; j < pl.listOnline.size(); j++) {
                    if (pl.listOnline.get(j).equals(ploi.id)) {
                        pl.checkNhan[a] = 1;
                    }
                }
                for (int u = 0; u < pl.listDiemDanh.size(); u++) {
                    if (pl.listDiemDanh.get(u).equals(ploi.id)) {
                        pl.checkNhan[a] = 1;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    public int countPlayer(Player pl, PhucLoi phucloi) {
        switch (phucloi.tab_id) {
            case 0: {
                phucloi.count = pl.phutOnline;
                break;
            }
            case 1: {
                Calendar calendar = Calendar.getInstance();
                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                phucloi.count = dayOfWeek;
                break;
            }
            case 2: {
                phucloi.count = pl.getSession().tong_nap;
                break;
            }
            case 3,4,5,6,7: {
                phucloi.count = pl.getSession().vnd;
                break;
            }
            default:
                phucloi.count = 0;
        }
        return phucloi.count;
    }

    public void Active_PhucLoi(Player pl, int tabid) {
        PhucLoi phucloi = PHUCLOI_TEMPLATES.get(tabid);
        if (phucloi.active == 0 || phucloi.active == 2) {
            if (phucloi.tab_id == 0) {
                if (pl.checkNhan[tabid] == 0) {
                    if (countPlayer(pl, phucloi) >= phucloi.max_count) {
                        if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                            pl.listOnline.add(tabid);
                            for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                                Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                                InventoryService.gI().addItemBag(pl, item, -333);
                                Service.getInstance().sendThongBao(pl, "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                            }
                            InventoryService.gI().sendItemBags(pl);
                            Send_PhucLoi(pl);
                        } else {
                            Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                        }
                    } else {
                        Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
                    }
                } else {
                    Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
                }
            } else if (phucloi.tab_id == 1) {
                if (pl.checkNhan[tabid] == 0) {
                    if (countPlayer(pl, phucloi) == phucloi.max_count) {
                        if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                            pl.listDiemDanh.add(tabid);
                            for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                                Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                                InventoryService.gI().addItemBag(pl, item, -333);
                                Service.getInstance().sendThongBao(pl, "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                            }
                            InventoryService.gI().sendItemBags(pl);
                            Send_PhucLoi(pl);
                        } else {
                            Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                        }
                    } else {
                        Service.getInstance().sendThongBao(pl, "|7|Không đủ điều kiện Nhận thưởng");
                    }
                } else {
                    Service.getInstance().sendThongBao(pl, "|7|Bạn đã nhận rồi mà !!!");
                }
            } else {
                if (pl.checkNhan[tabid] == 0) {
                    if (countPlayer(pl, phucloi) >= phucloi.max_count) {
                        if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                            pl.listNhan.add(tabid);
                            for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                                Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                                InventoryService.gI().addItemBag(pl, item, -333);
                                Service.getInstance().sendThongBao(pl, "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                            }
                            InventoryService.gI().sendItemBags(pl);
                            Send_PhucLoi(pl);
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
        } else {
            if (phucloi.tab_id >= 3) {
                if (countPlayer(pl, phucloi) >= phucloi.max_count) {
                    if (phucloi.PHUCLOI_LIST_ITEM.size() < InventoryService.gI().getCountEmptyBag(pl)) {
                        if (!PlayerDAO.checkVnd(pl, phucloi.max_count)) {
                            Service.getInstance().sendThongBao(pl, "|7|Không đủ Coin");
                            return;
                        }
                        PlayerDAO.subVnd(pl, phucloi.max_count);
                        for (int j = 0; j < phucloi.PHUCLOI_LIST_ITEM.size(); j++) {
                            Item item = phucloi.PHUCLOI_LIST_ITEM.get(j);
                            InventoryService.gI().addItemBag(pl, item, -333);
                            Service.getInstance().sendThongBao(pl, "|2|Đã nhận x" + item.quantity + " " + item.template.name + "\n");
                        }
                        InventoryService.gI().sendItemBags(pl);
                        Send_PhucLoi(pl);
                    } else {
                        Service.getInstance().sendThongBao(pl, "|7|Hành trang không đủ chổ trống");
                    }
                } else {
                    Service.getInstance().sendThongBao(pl, "|7|Còn thiếu " + (phucloi.max_count - countPlayer(pl, phucloi)) + " Coin");
                }
            }
        }
    }

    public void load_PhucLoi() {
        try {
            Connection con = DBService.gI().getConnectionForGame();
            PreparedStatement ps = con.prepareStatement("select * from phuc_loi");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                PhucLoiManager phucloimanager = new PhucLoiManager();
                phucloimanager.tab_name = rs.getString("name");
                phucloimanager.max_tab = rs.getInt("max_tab");
                phucloimanager.id_tab = rs.getInt("id_tab");
                phucloimanager.info_phucloi = rs.getString("info_phucloi");
                phucloimanager.action = rs.getInt("action");
                phucloimanager.tichLuy = rs.getString("tich_luy");
                PHUCLOI_MANAGER.add(phucloimanager);
            }
            Log.success("Load phuc_loi thành công (" + PHUCLOI_MANAGER.size() + ")");
        } catch (SQLException e) {
            Log.error(PhucLoi.class, e, "Lỗi load database");
            System.exit(0);
        }
    }

    public void load_PhucLoiTab() {
        try {
            Connection con = DBService.gI().getConnectionForGame();
            PreparedStatement ps = con.prepareStatement("select * from phuc_loi_tab");
            ResultSet rs = ps.executeQuery();
            JSONValue jv = new JSONValue();
            JSONArray dataArray = null;
            JSONObject dataObject = null;
            while (rs.next()) {
                PhucLoi phucloi = new PhucLoi();
                phucloi.id = rs.getInt("id");
                phucloi.tab_id = rs.getInt("tab_id");
                phucloi.name = rs.getString("name");
                phucloi.max_count = rs.getInt("max_count");
                phucloi.active = rs.getByte("active");
                dataArray = (JSONArray) jv.parse(rs.getString("list_item"));
                int size = dataArray.size();
                for (int i = 0; i < size; i++) {
                    dataObject = (JSONObject) jv.parse(String.valueOf(dataArray.get(i)));
                    int itemID = Integer.parseInt(String.valueOf(dataObject.get("id")));
                    int quantity = Integer.parseInt(String.valueOf(dataObject.get("quantity")));
                    JSONArray options = (JSONArray) dataObject.get("options");
                    Item item = ItemService.gI().createNewItem((short) itemID, quantity);
                    for (int j = 0; j < options.size(); j++) {
                        JSONObject obj = (JSONObject) options.get(j);
                        int optionID = ((Long) obj.get("id")).intValue();
                        int param = ((Long) obj.get("param")).intValue();
                        item.itemOptions.add(new ItemOption(optionID, param));
                    }
                    phucloi.PHUCLOI_LIST_ITEM.add(item);
                }
                PhucLoi.PHUCLOI_TEMPLATES.add(phucloi);
            }
            Log.success("Load PHUCLOI_TEMPLATES thành công (" + PHUCLOI_TEMPLATES.size() + ")");
        } catch (SQLException e) {
            Log.error(PhucLoi.class, e, "Lỗi load database");
            System.exit(0);
        }
    }

}
