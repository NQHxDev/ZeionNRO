/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nro.models.item.Item;
import nro.models.player.Player;
import nro.server.Client;
import nro.server.io.Message;
import nro.services.func.Input;
import nro.utils.TimeUtil;
import nro.utils.Util;

/**
 *
 * @author Hoàng Việt - 0857853150
 * @copyright VIET
 *
 */
public class GameDuDoan implements Runnable {

    private static final byte START = 1;//0 là tắt chức năng, 1 là mở
    public static final int ID_ITEM_CUOC = 1567;//id item cược
    public static final String NAME_ITEM_CUOC = "Xu Bạc";//tên item cược
    public static final long TIME_SHOW = 5000;//thời gian show kết quả
    public static final long TIME_TAI_XIU = 50000;//thời gian 1 ván
    public static final long TIME_NAN = 10000;//thời gian nặn
    public static final long TIME_THONG_BAO = 15000;//thời gian xuất hiện dòng thông báo
    public static final int TI_LE_BIP = 50;//tỉ lệ bịp
    public static final int TI_LE_AN = 90;//tỉ lệ ăn
    
    public static final String LON = "Lớn";
    public static final String NHO = "Nhỏ";

    public int soPhien = 1;
    public long goldTai;
    public long goldXiu;
    public boolean ketquaTai = false;
    public boolean ketquaXiu = false;

    public boolean baotri = false;
    public long lastTimeEnd;
    public List<Player> PlayersTai = new ArrayList<>();
    public List<Player> PlayersXiu = new ArrayList<>();
    private static GameDuDoan instance;
    public int xucxac1, xucxac2, xucxac3;
    public List<Integer> List_KetQua = new ArrayList<>();
    public List<String> noiDungChat = new ArrayList<>();
    public boolean showKQ = false;
    public long lastTimeShow;
    public long lastTimeThongBao;

    public boolean nanKG = false;
    public long lastTimeNan;

    public static GameDuDoan gI() {
        if (instance == null) {
            instance = new GameDuDoan();
        }
        instance.setTime();
        return instance;
    }

    public void addPlayerXiu(Player pl) {
        if (!PlayersXiu.contains(pl)) {
            PlayersXiu.add(pl);
        }
    }

    public void addPlayerTai(Player pl) {
        if (!PlayersTai.contains(pl)) {
            PlayersTai.add(pl);
        }
    }
    public static long TIME_OPEN;
    public static long TIME_CLOSE;

    public static final byte HOUR_OPEN = 0;//giờ mở
    public static final byte MIN_OPEN = 0;
    public static final byte SECOND_OPEN = 0;

    public static final byte HOUR_CLOSE = 23;//giờ đóng
    public static final byte MIN_CLOSE = 0;
    public static final byte SECOND_CLOSE = 0;

    private int day = -1;

    public void setTime() {
        if (instance.day == -1 || instance.day != TimeUtil.getCurrDay()) {
            instance.day = TimeUtil.getCurrDay();
            try {
                TIME_OPEN = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_OPEN + ":" + MIN_OPEN + ":" + SECOND_OPEN, "dd/MM/yyyy HH:mm:ss");
                TIME_CLOSE = TimeUtil.getTime(TimeUtil.getTimeNow("dd/MM/yyyy") + " " + HOUR_CLOSE + ":" + MIN_CLOSE + ":" + SECOND_CLOSE, "dd/MM/yyyy HH:mm:ss");
            } catch (Exception e) {
            }
        }
    }

    public static boolean isTimeOpen() {
        long now = System.currentTimeMillis();
        if (now > TIME_OPEN && now < TIME_CLOSE) {
            return true;
        }
        return false;
    }

    @Override
    public void run() {
        try {
            while (true) {
                if ((this.lastTimeShow - System.currentTimeMillis()) / 1000 <= 0) {
                    this.showKQ = false;
                }
                if ((this.lastTimeThongBao - System.currentTimeMillis()) / 1000 <= 0) {
                    thongbao("Đây là minigame tranh tài ai chiến thắng, vật phẩm trong trò chơi đều là ảo, KHÔNG tổ chức buôn bán trái phép. Admin chúc mọi người chơi game vui vẻ !");//thông báo chạy phía dưới
                    this.lastTimeThongBao = System.currentTimeMillis() + TIME_THONG_BAO;
                }
                int xx1, xx2, xx3;
                if (!this.nanKG && (this.lastTimeEnd - System.currentTimeMillis()) / 1000 <= 0) {
                    this.nanKG = true;
                    this.lastTimeNan = System.currentTimeMillis() + TIME_NAN;
                    this.lastTimeEnd = System.currentTimeMillis() + TIME_TAI_XIU;
                    if (this.goldTai >= this.goldXiu) {
                        if (Util.isTrue(TI_LE_BIP, 100)) {
                            xx1 = Util.nextInt(1, 4);
                            xx2 = Util.nextInt(1, 3);
                            xx3 = Util.nextInt(1, 3);
                        } else {
                            xx1 = Util.nextInt(4, 6);
                            xx2 = Util.nextInt(4, 6);
                            xx3 = Util.nextInt(3, 6);
                        }
                        if (xx1 == xx2 && xx2 == xx3 && xx1 == xx3) {
                            if (Util.isTrue(1,200)) {
                                if (Util.isTrue(TI_LE_BIP, 100)) {
                                    xx1 = Util.nextInt(1, 4);
                                    xx2 = Util.nextInt(1, 3);
                                    xx3 = Util.nextInt(1, 3);
                                } else {
                                    xx1 = Util.nextInt(4, 6);
                                    xx2 = Util.nextInt(4, 6);
                                    xx3 = Util.nextInt(3, 6);
                                }
                            }
                        }
                    } else {
                        if (Util.isTrue(TI_LE_BIP, 100)) {
                            xx1 = Util.nextInt(4, 6);
                            xx2 = Util.nextInt(4, 6);
                            xx3 = Util.nextInt(3, 6);
                        } else {
                            xx1 = Util.nextInt(1, 4);
                            xx2 = Util.nextInt(1, 3);
                            xx3 = Util.nextInt(1, 3);
                        }
                        if (xx1 == xx2 && xx2 == xx3 && xx1 == xx3) {
                            if (Util.isTrue(1, 200)) {
                                if (Util.isTrue(TI_LE_BIP, 100)) {
                                    xx1 = Util.nextInt(4, 6);
                                    xx2 = Util.nextInt(4, 6);
                                    xx3 = Util.nextInt(3, 6);
                                } else {
                                    xx1 = Util.nextInt(1, 4);
                                    xx2 = Util.nextInt(1, 3);
                                    xx3 = Util.nextInt(1, 3);
                                }
                            }
                        }
                    }
                    this.xucxac1 = xx1;
                    this.xucxac2 = xx2;
                    this.xucxac3 = xx3;
                    this.soPhien++;
                } else {
                    xx1 = this.xucxac1;
                    xx2 = this.xucxac2;
                    xx3 = this.xucxac3;
                }

                int tong = (xx1 + xx2 + xx3);
                if (this.nanKG && (this.lastTimeNan - System.currentTimeMillis()) / 1000 <= 0) {
                    this.nanKG = false;
                    this.showKQ = true;
                    this.lastTimeShow = System.currentTimeMillis() + TIME_SHOW;

                    if (tong <= 10) {
                        ketquaXiu = true;
                        ketquaTai = false;
                        if (List_KetQua.size() >= 20) {
                            List_KetQua.remove(0);
                            List_KetQua.add(1);
                        } else {
                            List_KetQua.add(1);
                        }
                    } else {
                        this.ketquaXiu = false;
                        this.ketquaTai = true;
                        if (this.List_KetQua.size() >= 20) {
                            this.List_KetQua.remove(0);
                            this.List_KetQua.add(0);
                        } else {
                            this.List_KetQua.add(0);
                        }
                    }

                    if (this.ketquaTai == true && this.ketquaXiu == false) {
                        if (!this.PlayersTai.isEmpty()) {
                            for (int i = 0; i < this.PlayersTai.size(); i++) {
                                int nohu;
                                if (xx1 == xx2 && xx2 == xx3 && xx1 == xx3) {
                                    nohu = 2;//nổ hủ
                                } else {
                                    nohu = 1;
                                }
                                String text = "";
                                text += "Số hệ thống quay ra\n" + xx1 + " : " + xx2 + " : " + xx3
                                        + "\nTổng là : " + tong + "\n " + LON
                                        + "\n\nBạn đã chiến thắng!!";
                                if (nohu > 1) {
                                    text += "\nBạn đã nổ hũ nhận x" + nohu + " Lần số tiến cược";
                                }
                                Player pl = this.PlayersTai.get(i);
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    double goldC = (double) (pl.goldTai + nohu * pl.goldTai * (TI_LE_AN / 100D));
                                    int tienAn = Util.DoubleToInter(goldC);
                                    Service.getInstance().sendThongBaoOK(pl, text);
                                    Item thoivang = ItemService.gI().createNewItem((short) ID_ITEM_CUOC, tienAn);
                                    InventoryService.gI().addItemBag(pl, thoivang, 1);
                                    Service.getInstance().sendMoney(pl);
                                    InventoryService.gI().sendItemBags(pl);
                                    Service.getInstance().sendThongBaoOK(pl, "Chúc mừng bạn đã dành chiến thắng và nhận được " + Util.format(tienAn) + " " + NAME_ITEM_CUOC);
                                }
                            }
                        }
                        if (!this.PlayersXiu.isEmpty()) {
                            for (int i = 0; i < this.PlayersXiu.size(); i++) {
                                Player pl = this.PlayersXiu.get(i);
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    Service.getInstance().sendThongBaoOK(pl, "Số hệ thống quay ra\n"
                                            + xx1 + " : " + xx2 + " : " + xx3
                                            + "\nTổng là : " + tong + "\n " + LON
                                            + "\n\nTrắng tay gòi, chơi lại đi!!!");
                                }
                            }
                        }
                    } else {
                        if (!this.PlayersXiu.isEmpty()) {
                            for (int i = 0; i < this.PlayersXiu.size(); i++) {
                                Player pl = this.PlayersXiu.get(i);
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    int nohu;
                                    if (xx1 == xx2 && xx2 == xx3 && xx1 == xx3) {
                                        nohu = 2;//nổ hủ
                                    } else {
                                        nohu = 1;
                                    }
                                    String text = "";
                                    text += "Số hệ thống quay ra\n" + xx1 + " : " + xx2 + " : " + xx3
                                            + "\nTổng là : " + tong + "\n " + NHO
                                            + "\n\nBạn đã chiến thắng!!";
                                    if (nohu > 1) {
                                        text += "\nBạn đã nổ hũ nhận x" + nohu + " Lần số tiền cược";
                                    }
                                    double goldC = (double) (pl.goldXiu + nohu * pl.goldXiu * (TI_LE_AN / 100D));
                                    int tienAn = Util.DoubleToInter(goldC);
                                    Service.getInstance().sendThongBaoOK(pl, text);
                                    Item thoivang = ItemService.gI().createNewItem((short) ID_ITEM_CUOC, tienAn);
                                    InventoryService.gI().addItemBag(pl, thoivang, 1);
                                    Service.getInstance().sendMoney(pl);
                                    InventoryService.gI().sendItemBags(pl);
                                    Service.getInstance().sendThongBaoOK(pl, "Chúc mừng bạn đã dành chiến thắng và nhận được " + Util.format(tienAn) + " " + NAME_ITEM_CUOC);
                                }
                            }
                        }
                        if (!this.PlayersTai.isEmpty()) {
                            for (int i = 0; i < this.PlayersTai.size(); i++) {
                                Player pl = this.PlayersTai.get(i);
                                if (pl != null && Client.gI().getPlayer(pl.name) != null) {
                                    Service.getInstance().sendThongBaoOK(pl, "Số hệ thống quay ra\n" + xx1 + " : " + xx2 + " : " + xx3
                                            + "\nTổng là : " + tong + "\n " + NHO
                                            + "\n\nTrắng tay gòi, chơi lại đi!!!");
                                }
                            }
                        }
                    }
                    for (int i = 0; i < this.PlayersTai.size(); i++) {
                        Player pl = this.PlayersTai.get(i);
                        if (pl != null) {
                            pl.goldTai = 0;
                        }
                    }
                    for (int i = 0; i < this.PlayersXiu.size(); i++) {
                        Player pl = this.PlayersXiu.get(i);
                        if (pl != null) {
                            pl.goldXiu = 0;
                        }
                    }
                    this.ketquaXiu = false;
                    this.ketquaTai = false;
                    this.goldTai = 0;
                    this.goldXiu = 0;
                    this.PlayersTai.clear();
                    this.PlayersXiu.clear();
                    this.lastTimeEnd = System.currentTimeMillis() + TIME_TAI_XIU;
                }
                Thread.sleep(500);
            }
        } catch (Exception e) {
        }
    }

    public void Send_TaiXiu(Player pl) {
        Message msg = null;
        try {
            msg = new Message(111);
            msg.writer().writeByte(0);
            msg.writer().writeByte(START);
            msg.writer().writeLong((this.lastTimeEnd - System.currentTimeMillis()) / 1000);//thời gian còn lại
            msg.writer().writeInt(this.soPhien);//số phiên
            msg.writer().writeInt(this.PlayersTai.size());//tổng pl tài
            msg.writer().writeInt(this.PlayersXiu.size());//tổng pl xỉu
            msg.writer().writeLong(this.goldTai);//tổng cược tài
            msg.writer().writeLong(this.goldXiu);//tổng cược xỉu
            msg.writer().writeInt(this.xucxac1);
            msg.writer().writeInt(this.xucxac2);
            msg.writer().writeInt(this.xucxac3);
            msg.writer().writeBoolean(this.nanKG);
            msg.writer().writeBoolean(this.showKQ);
            msg.writer().writeInt(this.List_KetQua.size());
            for (int i = 0; i < this.List_KetQua.size(); i++) {
                msg.writer().writeInt(this.List_KetQua.get(i));
            }
            msg.writer().writeInt(this.noiDungChat.size());
            for (int k = 0; k < this.noiDungChat.size(); k++) {
                msg.writer().writeUTF(this.noiDungChat.get(k));
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (IOException e) {
        }
    }

    public void thongbao(String text) {
        Message msg;
        try {
            msg = new Message(111);
            msg.writer().writeByte(4);
            msg.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void DatTai(Player pl) {
        if (this.nanKG){
            Service.getInstance().sendThongBaoOK(pl, "Ngoài thời gian đặt cược");
            return;
        }
        if (!isTimeOpen()) {
            Service.getInstance().sendThongBaoOK(pl, "Chưa mở");
            return;
        }
        if (!pl.getSession().actived) {
            Service.getInstance().sendThongBaoOK(pl, "Yêu cầu mở thành viên để trải nghiệm.");
            return;
        }
        if (PlayersXiu.contains(pl)) {
            Service.getInstance().sendThongBaoOK(pl, "Bạn chỉ được trợ giúp thêm 1 bên thôi.");
            return;
        }
        Input.gI().input_Tai(pl);
    }

    public void DatXiu(Player pl) {
        if (this.nanKG){
            Service.getInstance().sendThongBaoOK(pl, "Ngoài thời gian đặt cược");
            return;
        }
        if (!isTimeOpen()) {
            Service.getInstance().sendThongBaoOK(pl, "Chưa mở");
            return;
        }
        if (!pl.getSession().actived) {
            Service.getInstance().sendThongBaoOK(pl, "Yêu cầu mở thành viên để trải nghiệm.");
            return;
        }
        if (PlayersTai.contains(pl)) {
            Service.getInstance().sendThongBaoOK(pl, "Bạn chỉ được trợ giúp thêm 1 bên thôi.");
            return;
        }
        Input.gI().input_Xiu(pl);
    }

    public void chat(Player pl) {
        if (!pl.getSession().actived) {
            Service.getInstance().sendThongBaoOK(pl, "Yêu cầu mở thành viên để trải nghiệm.");
            return;
        }
        Input.gI().chat_TaiXiu(pl);
    }
}
