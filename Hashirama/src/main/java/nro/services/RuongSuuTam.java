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
public class RuongSuuTam {

    private static final byte START = 0;
    private static RuongSuuTam i;
    public static List<RuongSuuTamTemplate> listRuong = new ArrayList<>();
    public static List<Item> listCaiTrang = new ArrayList<>();
    public static List<Item> listPhuKien = new ArrayList<>();
    public static List<Item> listPet = new ArrayList<>();
    public static List<Item> listLinhThu = new ArrayList<>();
    public static List<Item> listThuCuoi = new ArrayList<>();

    public static byte size_ruong = 20;//mặc định kích thước rương khi tạo acc
    public static final byte MAX_SIZE = 40;//Giới hạn tối đa khi mở rộng rương
    
    public static final int ID_TEMP = 457;//id vật phẩm tốn khi mở rộng 1 ô
    public static final int QUATITY = 100;//số lượng item mở rộng 1 ô

    public static RuongSuuTam gI() {
        if (i == null) {
            i = new RuongSuuTam();
        }
        return i;
    }

    public void loadRuongSuuTam() {
        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT * FROM `ruong_suu_tam`");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    RuongSuuTamTemplate ruong = new RuongSuuTamTemplate();
                    ruong.id = rs.getInt("id");
                    ruong.type = rs.getByte("type");
                    ruong.id_item = rs.getInt("id_item");
                    ruong.option_id = rs.getInt("option_id");
                    ruong.param = rs.getInt("param");
                    Item it;
                    if (ruong.type == 0) {
                        it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
                        it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));
                        listCaiTrang.add(it);
                    } else if (ruong.type == 1) {
                        it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
                        it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));
                        listPhuKien.add(it);
                    } else if (ruong.type == 2) {
                        it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
                        it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));
                        listPet.add(it);
                    } else if (ruong.type == 3) {
                        it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
                        it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));
                        listLinhThu.add(it);
                    } else if (ruong.type == 4) {
                        it = ItemService.gI().createNewItem((short) ruong.id_item, 1);
                        it.itemOptions.add(new ItemOption(ruong.option_id, ruong.param));
                        listThuCuoi.add(it);
                    }
                    listRuong.add(ruong);
                }
                Log.success("Load Ruong Suu Tam thành công (" + listRuong.size() + ")");
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Send_RuongSuuTamTemplate(Player pl) {
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(0);
            msg.writer().writeByte(START);
            msg.writer().writeByte(pl.active_ruong_suu_tam);
            msg.writer().writeInt(listRuong.size());

            msg.writer().writeInt(listCaiTrang.size());
            msg.writer().writeInt(listPhuKien.size());
            msg.writer().writeInt(listPet.size());
            msg.writer().writeInt(listLinhThu.size());
            msg.writer().writeInt(listThuCuoi.size());

            for (int j = 0; j < listRuong.size(); j++) {
                RuongSuuTamTemplate ruong = listRuong.get(j);
                msg.writer().writeInt(ruong.id);
                msg.writer().writeByte(ruong.type);
                msg.writer().writeShort(ruong.id_item);
                msg.writer().writeByte(ruong.option_id);
                msg.writer().writeInt(ruong.param);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void SendAllRuong(Player player) {
        Send_RuongCaiTrang(player);
        Send_RuongPhuKien(player);
        Send_RuongPet(player);
        Send_RuongLinhThu(player);
        Send_RuongThuCuoi(player);
    }

    public void Send_RuongCaiTrang(Player pl) {
        InventoryService.gI().arrangeItems(pl.ruongSuuTam.RuongCaiTrang);
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(1);
            msg.writer().writeByte(pl.ruongSuuTam.RuongCaiTrang.size());
            for (int i = 0; i < pl.ruongSuuTam.RuongCaiTrang.size(); i++) {
                Item item = pl.ruongSuuTam.RuongCaiTrang.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
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
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_RuongPhuKien(Player pl) {
        InventoryService.gI().arrangeItems(pl.ruongSuuTam.RuongPhuKien);
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(2);
            msg.writer().writeByte(pl.ruongSuuTam.RuongPhuKien.size());
            for (int i = 0; i < pl.ruongSuuTam.RuongPhuKien.size(); i++) {
                Item item = pl.ruongSuuTam.RuongPhuKien.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
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
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_RuongPet(Player pl) {
        InventoryService.gI().arrangeItems(pl.ruongSuuTam.RuongPet);
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(3);
            msg.writer().writeByte(pl.ruongSuuTam.RuongPet.size());
            for (int i = 0; i < pl.ruongSuuTam.RuongPet.size(); i++) {
                Item item = pl.ruongSuuTam.RuongPet.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
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
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_RuongLinhThu(Player pl) {
        InventoryService.gI().arrangeItems(pl.ruongSuuTam.RuongLinhThu);
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(4);
            msg.writer().writeByte(pl.ruongSuuTam.RuongLinhThu.size());
            for (int i = 0; i < pl.ruongSuuTam.RuongLinhThu.size(); i++) {
                Item item = pl.ruongSuuTam.RuongLinhThu.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
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
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_RuongThuCuoi(Player pl) {
        InventoryService.gI().arrangeItems(pl.ruongSuuTam.RuongThuCuoi);
        Message msg = null;
        try {
            msg = new Message(109);
            msg.writer().writeByte(5);
            msg.writer().writeByte(pl.ruongSuuTam.RuongThuCuoi.size());
            for (int i = 0; i < pl.ruongSuuTam.RuongThuCuoi.size(); i++) {
                Item item = pl.ruongSuuTam.RuongThuCuoi.get(i);
                if (!item.isNotNullItem()) {
                    continue;
                }
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
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void mangItem(Player pl, byte type, int idTemp) {
        List<Item> listItem = new ArrayList<>();
        if (type == 0) {
            listItem = pl.ruongSuuTam.RuongCaiTrang;
        } else if (type == 1) {
            listItem = pl.ruongSuuTam.RuongPhuKien;
        } else if (type == 2) {
            listItem = pl.ruongSuuTam.RuongPet;
        } else if (type == 3) {
            listItem = pl.ruongSuuTam.RuongLinhThu;
        } else if (type == 4) {
            listItem = pl.ruongSuuTam.RuongThuCuoi;
        }
        Item item = InventoryService.gI().findItemBagByTemp(pl, (short) idTemp);
        if (item == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        Item itemCheck = InventoryService.gI().findItem(listItem, (short) idTemp);
        if (itemCheck != null) {
            Service.getInstance().sendThongBao(pl, "Đã có vật phẩm này trong Rương Sưu Tầm rồi!!!!");
            return;
        }
        int sizeNull = 0;
        for (int i = 0; i < listItem.size(); i++) {
            Item it = listItem.get(i);
            if (!it.isNotNullItem()) {
                sizeNull++;
            }
        }
        if (sizeNull == 0) {
            Service.getInstance().sendThongBao(pl, "Rương đã đầy rồi!!!!");
            return;
        }
        item.quantity = 1;
        InventoryService.gI().addItemList(listItem, item, 1);
        InventoryService.gI().subQuantityItemsBag(pl, item, 1);
        InventoryService.gI().sendItemBags(pl);
        pl.nPoint.calPoint();
        Service.getInstance().point(pl);
        SendAllRuong(pl);
    }

    public void thaoItem(Player pl, byte type, int idTemp) {
        List<Item> listItem = new ArrayList<>();
        if (type == 0) {
            listItem = pl.ruongSuuTam.RuongCaiTrang;
        } else if (type == 1) {
            listItem = pl.ruongSuuTam.RuongPhuKien;
        } else if (type == 2) {
            listItem = pl.ruongSuuTam.RuongPet;
        } else if (type == 3) {
            listItem = pl.ruongSuuTam.RuongLinhThu;
        } else if (type == 4) {
            listItem = pl.ruongSuuTam.RuongThuCuoi;
        }
        Item item = InventoryService.gI().findItem(listItem, (short) idTemp);
        if (item == null) {
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        item.quantity = 1;
        InventoryService.gI().addItemBag(pl, item, 1);
        InventoryService.gI().subQuantityItem(listItem, item, 1);
        InventoryService.gI().sendItemBags(pl);
        pl.nPoint.calPoint();
        Service.getInstance().point(pl);
        SendAllRuong(pl);
    }

    public void moRongRuong(Player pl, byte type) {
        pl.typeMoRuong = type;
        Item it = ItemService.gI().createNewItem((short) ID_TEMP);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.MO_RONG_RUONG_SUU_TAM, 0,
                "Bạn có muốn dùng\n"
                        + "|1|" + QUATITY + " " + it.template.name
                        + "\n|6|để mở thêm 1 ô " + nameRuong(pl, type) + " không?\n"
                                + "|7|Tối đa " + MAX_SIZE + " Ô",
                 "Đồng ý", "Đóng");
    }
    
    public String nameRuong(Player pl, byte type){
        if (type == 0) {
            return "Rương Cải Trang";
        } else if (type == 1) {
            return "Rương Phụ Kiện";
        } else if (type == 2) {
            return "Rương Pet";
        } else if (type == 3) {
            return "Rương Linh Thú";
        } else if (type == 4) {
            return "Rương Thú Cưỡi";
        }
        return "";
    }

    public void activeRuongSuuTam(Player pl, byte active) {
        pl.active_ruong_suu_tam = active;
        pl.nPoint.calPoint();
        Service.getInstance().point(pl);
        Send_RuongSuuTamTemplate(pl);
    }

}
