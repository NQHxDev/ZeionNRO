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
import nro.utils.Util;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Hoàng Việt - 0857853150
 *
 */
public class PhongThiNghiem {

    public static final byte SIZE = 3;//số lọ được tạo khi vừa tạo acc
    public static final byte MAX_SIZE = 26;//số lượng tối đa khi mở thêm lọ thí nghiệm

    public static final int ID_ITEM_MO_RONG = 457;//ID Item tốn khi mở rộng
    public static final int SO_LUONG = 100;//Số lượng tốn khi mở rộng

    public static final int ID_ITEM_TANG_TOC = 457;//ID Item tốn khi tăng tốc
    public static final int SO_LUONG_TANG_TOC = 22;//Số lượng tốn khi tăng tốc
    public static final long TIME_TANG_TOC = 600000;//10phut là 60 * 10 * 1000 = 600000 miliGiay

    private static final byte START = 0;//0 là tắt chức năng, 1 là mở
    
    @Getter
    public int id;
    public String name_tab;
    public String name_binh;
    public final List<PhongThiNghiem_Template> items = new ArrayList<>();
    public int thoi_gian;
    public int idItem_Nhan;
    public String info;
    public byte color; //màu: 0(Xanh dương) || 1(Đỏ) || 2(Cam) || 3(Xanh lá) || 4(Tím) || 5(Vàng) || 6(Hồng) || 7(Xanh nhạt) || 8(Nâu) || 9(Đen)

    public static final List<PhongThiNghiem> PHONG_THI_NGHIEM = new ArrayList<>();
    private static PhongThiNghiem i;

    public static PhongThiNghiem gI() {
        if (i == null) {
            i = new PhongThiNghiem();
        }
        return i;
    }

    public long thoiGianDieuChe() {
        return this.thoi_gian * 1000 * 60;
    }

    public void loadPhongThiNghiem() {
        try {
            PreparedStatement ps = DBService.gI().getConnectionForGame().prepareStatement("SELECT * FROM `phong_thi_nghiem`");
            ResultSet rs = ps.executeQuery();
            try {
                while (rs.next()) {
                    PhongThiNghiem ptn = new PhongThiNghiem();
                    ptn.id = rs.getInt("id");
                    ptn.name_tab = rs.getString("name_tab");
                    ptn.name_binh = rs.getString("name_binh");
                    ptn.thoi_gian = rs.getInt("thoi_gian");
                    ptn.idItem_Nhan = rs.getInt("item_nhan");
                    ptn.info = rs.getString("info");
                    ptn.color = rs.getByte("color");
                    JSONArray jArr = new JSONArray(rs.getString("items"));
                    for (int i = 0; i < jArr.length(); i++) {
                        PhongThiNghiem_Template itemThuoc = new PhongThiNghiem_Template();
                        JSONObject obj = jArr.getJSONObject(i);
                        itemThuoc.tempId = obj.getInt("tempid");
                        itemThuoc.quantity = obj.getInt("quantity");
                        ptn.items.add(itemThuoc);
                    }
                    PHONG_THI_NGHIEM.add(ptn);
                }
                Log.success("Load Phong Thi Nghiem thanh cong (" + PHONG_THI_NGHIEM.size() + ")");
            } finally {
                rs.close();
                ps.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void Send_PhongThiNghiem_Template(Player pl) {
        Message msg = null;
        try {
            msg = new Message(110);
            msg.writer().writeByte(0);
            msg.writer().writeByte(START);
            msg.writer().writeByte(MAX_SIZE);
            msg.writer().writeByte(PHONG_THI_NGHIEM.size());

            for (int j = 0; j < PHONG_THI_NGHIEM.size(); j++) {
                PhongThiNghiem manager = PHONG_THI_NGHIEM.get(j);
                msg.writer().writeInt(manager.id);
                msg.writer().writeUTF(manager.name_tab);
                msg.writer().writeUTF(manager.name_binh);
                msg.writer().writeUTF(Util.msToTime(manager.thoiGianDieuChe()));
                msg.writer().writeInt(manager.idItem_Nhan);
                msg.writer().writeUTF(manager.info);
                msg.writer().writeByte(manager.color);
                msg.writer().writeByte(manager.items.size());
                for (int k = 0; k < manager.items.size(); k++) {
                    PhongThiNghiem_Template template = manager.items.get(k);
                    msg.writer().writeInt(template.tempId);
                    msg.writer().writeInt(template.quantity);
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void Send_PhongThiNghiem_Player(Player pl) {
        Message msg = null;
        try {
            msg = new Message(110);
            msg.writer().writeByte(1);
            msg.writer().writeByte(pl.phongThiNghiem.size());

            for (int j = 0; j < pl.phongThiNghiem.size(); j++) {
                PhongThiNghiem_Player manager = pl.phongThiNghiem.get(j);
                msg.writer().writeInt(manager.idBinh);
                msg.writer().writeLong(manager.timeCheTao - System.currentTimeMillis());
                if (manager.idBinh != - 1 && manager.timeCheTao > 0 && manager.timeCheTao - System.currentTimeMillis() > 0) {
                    msg.writer().writeUTF(Util.msToTime(manager.timeCheTao - System.currentTimeMillis()));
                } else if (manager.idBinh != - 1 && manager.timeCheTao - System.currentTimeMillis() <= 0) {
                    msg.writer().writeUTF("Chế tạo xong");
                } else {
                    msg.writer().writeUTF("");
                }
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void dieu_che(Player pl, int vitri, int type) {
        PhongThiNghiem ptn = PHONG_THI_NGHIEM.get(type);
        for (int i = 0; i < ptn.items.size(); i++) {
            Item item = InventoryService.gI().findItemBagByTemp(pl, (short) ptn.items.get(i).tempId);
            if (item == null || item.quantity < ptn.items.get(i).quantity) {
                Service.getInstance().sendThongBao(pl, "Không đủ nguyên liệu");
                return;
            }
        }
        String text = "";
        for (int i = 0; i < ptn.items.size(); i++) {
            Item it = ItemService.gI().createNewItem((short) ptn.items.get(i).tempId);
            it.quantity = ptn.items.get(i).quantity;
            text += "|5|-" + it.quantity + " " + it.template.name + (i == (ptn.items.size() - 1) ? "" : "\n");
        }
        pl.vitriBinhDieuChe = vitri;
        pl.typeBinhDieuChe = type;
        NpcService.gI().createMenuConMeo(pl, ConstNpc.DIEU_CHE, 0,
                "Bạn có muốn dùng\n"
                + text
                + "\n|6|Để điều chế " + ptn.name_binh + " không?"
                + "\n|7|Thời gian điều chế: " + Util.msToTime(ptn.thoiGianDieuChe()),
                "Đồng ý", "Đóng");
    }

    public void nhan_item(Player pl, int id, int vitri) {
        PhongThiNghiem ptn = PHONG_THI_NGHIEM.get(id);
        if (pl.phongThiNghiem.get(vitri).timeCheTao - System.currentTimeMillis() > 0) {
            Service.getInstance().sendThongBao(pl, "Chưa xong mà");
            return;
        }
        if (pl.phongThiNghiem.get(vitri).idBinh == -1 || pl.phongThiNghiem.get(vitri).timeCheTao == 0){
            Service.getInstance().sendThongBao(pl, "Không thể thực hiện");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(pl) > 0) {
            pl.phongThiNghiem.get(vitri).idBinh = -1;
            pl.phongThiNghiem.get(vitri).timeCheTao = 0;
            Item it = ItemService.gI().createNewItem((short) ptn.idItem_Nhan);
//            if (ptn.idItem_Nhan == 1274){
//                it.itemOptions.add(new ItemOption(50, 20));
//            }
            InventoryService.gI().addItemBag(pl, it, 0);
            InventoryService.gI().sendItemBags(pl);
            Service.getInstance().sendThongBao(pl, "Nhận thành công " + it.template.name);
            Send_PhongThiNghiem_Player(pl);
        } else {
            Service.getInstance().sendThongBao(pl, "Hành trang không đủ chổ trống");
        }
    }

    public void tangTocPtn(Player pl, int id, int vitri) {
        PhongThiNghiem_Player ptnPL = pl.phongThiNghiem.get(vitri);
        PhongThiNghiem ptn = PHONG_THI_NGHIEM.get(id);
        if (ptnPL.timeCheTao - System.currentTimeMillis() <= 0) {
            Service.getInstance().sendThongBao(pl, "Đã chế tạo xong. Không thể Tăng tốc");
            return;
        }
        pl.vitriBinhDieuChe = vitri;
        pl.typeBinhDieuChe = id;
        Item it = ItemService.gI().createNewItem((short) ID_ITEM_TANG_TOC);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.TANG_TOC, 0,
                "Bạn có muốn dùng"
                + "\n|1|" + SO_LUONG_TANG_TOC + " " + it.template.name
                + "\n|6|để Tăng tốc " + Util.msToTime(TIME_TANG_TOC) + " " + ptn.name_binh + " không?",
                "Đồng ý", "Đóng");
    }

    public void huyPtn(Player pl, int id, int vitri) {
        PhongThiNghiem_Player ptnPL = pl.phongThiNghiem.get(vitri);
        PhongThiNghiem ptn = PHONG_THI_NGHIEM.get(id);
        if (ptnPL.timeCheTao - System.currentTimeMillis() <= 0) {
            Service.getInstance().sendThongBao(pl, "Đã chế tạo xong. Không thể Hủy bỏ");
            return;
        }
        if (InventoryService.gI().getCountEmptyBag(pl) < ptn.items.size()) {
            Service.getInstance().sendThongBao(pl, "Hành trang không đủ chổ trống");
            return;
        }
        pl.vitriBinhDieuChe = vitri;
        pl.typeBinhDieuChe = id;
        String text = "";
        for (int i = 0; i < ptn.items.size(); i++) {
            Item it = ItemService.gI().createNewItem((short) ptn.items.get(i).tempId);
            it.quantity = ptn.items.get(i).quantity;
            text += "|5|-" + it.quantity + " " + it.template.name + (i == (ptn.items.size() - 1) ? "" : "\n");
        }
        NpcService.gI().createMenuConMeo(pl, ConstNpc.HUY_PTN, 0,
                "Bạn có muốn hủy Điều chế"
                + "\n|1|" + ptn.name_binh + " không?"
                + "\n|7|Hoàn trả toàn bộ nguyên liệu:"
                + "\n" + text,
                "Đồng ý", "Đóng");
    }

    public void mo_rong(Player pl) {
        Item it = ItemService.gI().createNewItem((short) ID_ITEM_MO_RONG);
        NpcService.gI().createMenuConMeo(pl, ConstNpc.MO_RONG_PHONG_THI_NGHIEM, 0,
                "Bạn có muốn dùng\n"
                + "|1|" + SO_LUONG + " " + it.template.name
                + "\n|6|để mở thêm 1 chổ trống không?\n"
                + "|2|Đang có: " + pl.phongThiNghiem.size() + " lọ\n"
                + "|7|Tối đa " + MAX_SIZE + " lọ",
                "Đồng ý", "Đóng");
    }
}
