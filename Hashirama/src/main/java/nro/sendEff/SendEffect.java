package nro.sendEff;

import java.io.IOException;
import nro.models.player.Player;
import nro.server.Manager;
import nro.server.io.Message;
import nro.services.Service;
import nro.utils.Util;

/**
 *
 * @author Louis Goku
 */
public class SendEffect {

    private static SendEffect instance;

    public static SendEffect getInstance() {
        if (instance == null) {
            instance = new SendEffect();
        }
        return instance;
    }

    public void sendChanThienTu(Player player, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);

            int shortValue = -1; // Default value in case none of the conditions match

            switch (id) {
                case 1216:
                    shortValue = 73;
                    break;
                case 1217:
                    shortValue = 61;
                    break;
                case 1218:
                    shortValue = 62;
                    break;
                case 1219:
                    shortValue = 63;
                    break;
                case 1220:
                    shortValue = 64;
                    break;
                case 1221:
                    shortValue = 67;
                    break;
                case 1222:
                    shortValue = 70;
                    break;
                case 1223:
                    shortValue = 71;
                    break;
                case 1224:
                    shortValue = 72;
                    break;
                default:
                    break;
            }

            me.writer().writeShort(shortValue);
            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(0);
            Service.getInstance().sendMessAllPlayerInMap(player, me);
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendChanThienTuAll(Player player, Player p2, int id) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(0);
            me.writer().writeInt((int) player.id);
            if (id == 1216) {
                me.writer().writeShort(73);
            }
            if (id == 1217) {
                me.writer().writeShort(61);
            }
            if (id == 1218) {
                me.writer().writeShort(62);
            }
            if (id == 1219) {
                me.writer().writeShort(63);
            }
            if (id == 1220) {
                me.writer().writeShort(64);
            }
            if (id == 1221) {
                me.writer().writeShort(67);
            }
            if (id == 1222) {
                me.writer().writeShort(70);
            }
            if (id == 1223) {
                me.writer().writeShort(71);
            }
            if (id == 1224) {
                me.writer().writeShort(72);
            }
            me.writer().writeByte(0);
            me.writer().writeByte(-1);
            me.writer().writeShort(1);
            me.writer().writeByte(0);
            p2.sendMessage(me);
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDanhHieu(Player player, int id) {
        Message me = null;
        try {
            int ycongdanhhieu = 0;

            switch (id) {
                case 1:
                    if (player.DH1 == true && player.IdDanhHieu_1 >= 100) {
                        me = createMessage(player, player.IdDanhHieu_1, ycongdanhhieu);
                    }
                    break;
                case 2:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true && player.IdDanhHieu_2 >= 100) {
                        me = createMessage(player, player.IdDanhHieu_2, ycongdanhhieu);
                    }
                    break;
                case 3:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true && player.IdDanhHieu_3 >= 100) {
                        me = createMessage(player, player.IdDanhHieu_3, ycongdanhhieu);
                    }
                    break;
                case 4:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH4 == true && player.IdDanhHieu_4 >= 100) {
                        me = createMessage(player, player.IdDanhHieu_4, ycongdanhhieu);
                    }
                    break;
                case 5:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH4 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH5 == true && player.IdDanhHieu_5 >= 100) {
                        me = createMessage(player, player.IdDanhHieu_5, ycongdanhhieu);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendDanhHieuAll(Player player, Player p2, int id) {
        Message me;
        try {
            int ycongdanhhieu = 0;

            switch (id) {
                case 1:
                    if (player.DH1 == true && player.IdDanhHieu_1 >= 100) {
                        me = createMessage2(player, p2, player.IdDanhHieu_1, ycongdanhhieu);
                    }
                    break;
                case 2:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true && player.IdDanhHieu_2 >= 100) {
                        me = createMessage2(player, p2, player.IdDanhHieu_2, ycongdanhhieu);
                    }
                    break;
                case 3:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true && player.IdDanhHieu_3 >= 100) {
                        me = createMessage2(player, p2, player.IdDanhHieu_3, ycongdanhhieu);
                    }
                    break;
                case 4:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH4 == true && player.IdDanhHieu_4 >= 100) {
                        me = createMessage2(player, p2, player.IdDanhHieu_4, ycongdanhhieu);
                    }
                    break;
                case 5:
                    if (player.DH1 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH2 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH3 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH4 == true) {
                        ycongdanhhieu -= 20;
                    }
                    if (player.DH5 == true && player.IdDanhHieu_5 >= 100) {
                        me = createMessage2(player, p2, player.IdDanhHieu_5, ycongdanhhieu);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Message createMessage2(Player player, Player p2, int danhHieuCode, int ycongdanhhieu) throws IOException {
        Message me = new Message(-128);
        me.writer().writeByte(0);
        me.writer().writeInt((int) player.id);
        me.writer().writeShort(danhHieuCode);
        me.writer().writeByte(1);
        me.writer().writeByte(-1);
        me.writer().writeShort(50);
        me.writer().writeByte(-1);
        if (ycongdanhhieu != 0) {
            me.writer().writeByte(ycongdanhhieu);
        }
        me.writer().writeByte(-1);
        p2.sendMessage(me);
        me.cleanup();
        return me;
    }

    private Message createMessage(Player player, int danhHieuCode, int ycongdanhhieu) throws IOException {
        Message me = new Message(-128);
        me.writer().writeByte(0);
        me.writer().writeInt((int) player.id);
        me.writer().writeShort(danhHieuCode);
        me.writer().writeByte(1);
        me.writer().writeByte(-1);
        me.writer().writeShort(50);
        me.writer().writeByte(-1);
        if (ycongdanhhieu != 0) {
            me.writer().writeByte(ycongdanhhieu);
        }
        me.writer().writeByte(-1);
        Service.getInstance().sendMessAllPlayerInMap(player, me);
        me.cleanup();
        return me;
    }

    public void removeTitle(Player player) {
        Message me;
        try {
            me = new Message(-128);
            me.writer().writeByte(2);
            me.writer().writeInt((int) player.id);
            player.getSession().sendMessage(me);
            Service.getInstance().sendMessAllPlayerInMap(player, me);
            SendThreadEffDanhHieu(player);
            if (player.inventory.itemsBody.get(9).isNotNullItem()) {
                sendChanThienTu(player, player.inventory.itemsBody.get(9).template.id);
            }
            me.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SendEffDanhHieu(Player player) {
        for (int i = 0; i < 5; i++) {
            this.sendDanhHieu(player, i);
        }
    }

    public String Name_Danh_Hieu(int iddanhhieu) {
        switch (iddanhhieu) {
            case 200:
                return "Bất phục";
            case 201:
                return "Top 1";
            case 202:
                return "Top 2";
            case 203:
                return "Top 3";
            case 204:
                return "Top 4";
            case 205:
                return "Thần thoại";
            case 206:
                return "Tuổi thơ";
            case 207:
                return "Thiên tử";
            case 208:
                return "Bé ngoan";
            case 209:
                return "Phong ba";
            case 210:
                return "Đại gia";
            case 211:
                return "Dân chơi +6";
            case 212:
                return "Dân chơi +7";
            case 213:
                return "Dân chơi +8";
            case 214:
                return "Cày cuốc";
            case 215:
                return "Cày cuốc V2";
            case 216:
                return "Tân binh";
        }
        return null;
    }

    public void send_danh_hieu(Player player, int iddanhhieu, int ngày) {
        Service.getInstance().sendThongBao(player, "Bạn nhận được danh hiệu " + Name_Danh_Hieu(iddanhhieu) + " " + ngày + " ngày");
        if (player.lastTimeTitle1 == 0 && player.IdDanhHieu_1 != iddanhhieu) {
            if (player.lastTimeTitle1 == 0) {
                player.lastTimeTitle1 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ngày);
            }
            player.isTitleUse1 = true;
            player.IdDanhHieu_1 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
            return;
        }
        if (player.IdDanhHieu_1 == iddanhhieu) { // 1_1

            player.lastTimeTitle1 += (1000 * 60 * 60 * 24 * ngày);

            player.isTitleUse1 = true;
            player.IdDanhHieu_1 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 == 0 && player.IdDanhHieu_1 != iddanhhieu && player.IdDanhHieu_2 != iddanhhieu) {
            if (player.lastTimeTitle2 == 0) {
                player.lastTimeTitle2 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ngày);
            }
            player.isTitleUse2 = true;
            player.IdDanhHieu_2 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.IdDanhHieu_2 == iddanhhieu) {
            player.lastTimeTitle2 += (1000 * 60 * 60 * 24 * ngày);
            player.isTitleUse2 = true;
            player.IdDanhHieu_2 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0 && player.lastTimeTitle3 == 0 && player.IdDanhHieu_1 != iddanhhieu && player.IdDanhHieu_2 != iddanhhieu && player.IdDanhHieu_3 != iddanhhieu) {
            if (player.lastTimeTitle3 == 0) {
                player.lastTimeTitle3 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ngày);
            }
            player.isTitleUse3 = true;
            player.IdDanhHieu_3 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.IdDanhHieu_3 == iddanhhieu) {

            player.lastTimeTitle3 += (1000 * 60 * 60 * 24 * ngày);
            player.isTitleUse3 = true;
            player.IdDanhHieu_3 = iddanhhieu;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0 && player.lastTimeTitle3 != 0 && player.lastTimeTitle4 == 0 && player.IdDanhHieu_1 != iddanhhieu && player.IdDanhHieu_2 != iddanhhieu && player.IdDanhHieu_3 != iddanhhieu && player.IdDanhHieu_4 != iddanhhieu) {

            if (player.lastTimeTitle4 == 0) {
                player.lastTimeTitle4 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ngày);
            }
            player.IdDanhHieu_4 = iddanhhieu;
            player.isTitleUse4 = true;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.IdDanhHieu_4 == iddanhhieu) {
            player.lastTimeTitle4 += (1000 * 60 * 60 * 24 * ngày);
            player.IdDanhHieu_4 = iddanhhieu;
            player.isTitleUse4 = true;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.lastTimeTitle1 != 0 && player.lastTimeTitle2 != 0 && player.lastTimeTitle3 != 0 && player.lastTimeTitle4 != 0 && player.lastTimeTitle5 == 0 && player.IdDanhHieu_1 != iddanhhieu && player.IdDanhHieu_2 != iddanhhieu && player.IdDanhHieu_3 != iddanhhieu && player.IdDanhHieu_4 != iddanhhieu && player.IdDanhHieu_5 != iddanhhieu) {
            if (player.lastTimeTitle5 == 0) {
                player.lastTimeTitle5 += System.currentTimeMillis() + (1000 * 60 * 60 * 24 * ngày);
            }
            player.IdDanhHieu_5 = iddanhhieu;
            player.isTitleUse5 = true;
            Service.getInstance().point(player);
            removeTitle(player);
        } else if (player.IdDanhHieu_5 == iddanhhieu) {

            player.lastTimeTitle5 += (1000 * 60 * 60 * 24 * ngày);
            player.IdDanhHieu_5 = iddanhhieu;
            player.isTitleUse5 = true;
            Service.getInstance().point(player);
            removeTitle(player);
        }
    }

    public void SendThreadEffDanhHieu(Player player) {
        new Thread(()-> {

            try {
                Thread.sleep(1000);
                if (player.DH1 = true) {
                    this.sendDanhHieu(player, (short) 1);
                }
                if (player.DH2 = true) {
                    this.sendDanhHieu(player, (short) 2);
                }
                if (player.DH3 = true) {
                    this.sendDanhHieu(player, (short) 3);
                }
                if (player.DH4 = true) {
                    this.sendDanhHieu(player, (short) 4);
                }
                if (player.DH5 = true) {
                    this.sendDanhHieu(player, (short) 5);
                }
            } catch (Exception e) {

            }

        }).start();
    }

    public void update(Player player) {
        if (player.lastTimeTitle1 > 0) {
            player.DH1 = true;
        }
        if (player.lastTimeTitle2 > 0) {
            player.DH2 = true;
        }
        if (player.lastTimeTitle3 > 0) {
            player.DH3 = true;
        }
        if (player.lastTimeTitle4 > 0) {
            player.DH4 = true;
        }
        if (player.lastTimeTitle5 > 0) {
            player.DH5 = true;
        }
        if (player.lastTimeTitle1 != 0 && Util.canDoWithTime(player.lastTimeTitle1, 6000)) {
            player.lastTimeTitle1 = 0;
            player.isTitleUse1 = false;
            player.IdDanhHieu_1 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
        if (player.lastTimeTitle2 != 0 && Util.canDoWithTime(player.lastTimeTitle2, 6000)) {
            player.lastTimeTitle2 = 0;
            player.isTitleUse2 = false;
            player.IdDanhHieu_2 = 0;
            SendEffect.getInstance().removeTitle(player);
        }

        if (player.lastTimeTitle3 != 0 && Util.canDoWithTime(player.lastTimeTitle3, 6000)) {
            player.lastTimeTitle3 = 0;
            player.isTitleUse3 = false;
            player.IdDanhHieu_3 = 0;
            SendEffect.getInstance().removeTitle(player);
        }

        if (player.lastTimeTitle4 != 0 && Util.canDoWithTime(player.lastTimeTitle4, 6000)) {
            player.lastTimeTitle4 = 0;
            player.isTitleUse4 = false;
            player.IdDanhHieu_4 = 0;
            SendEffect.getInstance().removeTitle(player);
        }

        if (player.lastTimeTitle5 != 0 && Util.canDoWithTime(player.lastTimeTitle5, 6000)) {
            player.lastTimeTitle5 = 0;
            player.isTitleUse5 = false;
            player.IdDanhHieu_5 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
        if (player.lastTimeTitle1 == 0 && player.lastTimeTitle2 > 0) {
            player.lastTimeTitle1 = player.lastTimeTitle2;
            player.isTitleUse1 = true;
            player.IdDanhHieu_1 = player.IdDanhHieu_2;

            player.lastTimeTitle2 = 0;
            player.isTitleUse2 = false;
            player.IdDanhHieu_2 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
        if (player.lastTimeTitle2 == 0 && player.lastTimeTitle3 > 0) {
            player.lastTimeTitle2 = player.lastTimeTitle3;
            player.isTitleUse2 = true;
            player.IdDanhHieu_2 = player.IdDanhHieu_3;

            player.lastTimeTitle3 = 0;
            player.isTitleUse3 = false;
            player.IdDanhHieu_3 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
        if (player.lastTimeTitle3 == 0 && player.lastTimeTitle4 > 0) {
            player.lastTimeTitle3 = player.lastTimeTitle4;
            player.isTitleUse3 = true;
            player.IdDanhHieu_3 = player.IdDanhHieu_4;

            player.lastTimeTitle4 = 0;
            player.isTitleUse4 = false;
            player.IdDanhHieu_4 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
        if (player.lastTimeTitle4 == 0 && player.lastTimeTitle5 > 0) {
            player.lastTimeTitle4 = player.lastTimeTitle5;
            player.isTitleUse4 = true;
            player.IdDanhHieu_4 = player.IdDanhHieu_5;

            player.lastTimeTitle5 = 0;
            player.isTitleUse5 = false;
            player.IdDanhHieu_5 = 0;
            SendEffect.getInstance().removeTitle(player);
        }
    }
}
