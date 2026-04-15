package nro.services;

import nro.consts.ConstNpc;
import nro.models.intrinsic.Intrinsic;
import nro.models.player.Player;
import nro.server.Manager;
import nro.server.io.Message;
import nro.utils.Util;

import java.util.List;
import nro.models.item.Item;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class IntrinsicService {

    private static IntrinsicService i;
    private static final int COST_OPEN = 1000;

    public static IntrinsicService gI() {
        if (i == null) {
            i = new IntrinsicService();
        }
        return i;
    }

    public List<Intrinsic> getIntrinsics(byte playerGender) {
        switch (playerGender) {
            case 0:
                return Manager.INTRINSIC_TD;
            case 1:
                return Manager.INTRINSIC_NM;
            default:
                return Manager.INTRINSIC_XD;
        }
    }

    public Intrinsic getIntrinsicById(int id) {
        for (Intrinsic intrinsic : Manager.INTRINSICS) {
            if (intrinsic.id == id) {
                return new Intrinsic(intrinsic);
            }
        }
        return null;
    }

    public void sendInfoIntrinsic(Player player) {
        Message msg;
        try {
            msg = new Message(112);
            msg.writer().writeByte(0);
            msg.writer().writeShort(player.playerIntrinsic.intrinsic.icon);
            msg.writer().writeUTF(player.playerIntrinsic.intrinsic.getName());
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void showAllIntrinsic(Player player) {
        List<Intrinsic> listIntrinsic = getIntrinsics(player.gender);
        Message msg;
        try {
            msg = new Message(112);
            msg.writer().writeByte(1);
            msg.writer().writeByte(1); //count tab
            msg.writer().writeUTF("Nội tại");
            msg.writer().writeByte(listIntrinsic.size() - 1);
            for (int i = 1; i < listIntrinsic.size(); i++) {
                msg.writer().writeShort(listIntrinsic.get(i).icon);
                msg.writer().writeUTF(listIntrinsic.get(i).getDescription());
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
    
    public void sattd(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_TD, -1,
                                "chọn lẹ đi để tau đi chơi với ny", "Set\nKaioken", "Set\nGenki", "Set\nKamejoko", "Từ chối");
                    
            }
    
    public void satnm(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_NM, -1,
                                "chọn lẹ đi để tau đi chơi với ny", "Set\nGod KI", "Set\nLiên hoàn", "Set\nTrứng", "Từ chối");
                    
            }
    
    public void setxd(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_XD, -1,
                                "chọn lẹ đi để tau đi chơi với ny", "Set\ngod Galick", "Set\nMonkey", "Set\nGod HP", "Từ chối");
                    
            }

    public void showMenu(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.INTRINSIC, -1,
                "Nội tại là một kỹ năng bị động hỗ trợ đặc biệt\nBạn có muốn mở hoặc thay đổi nội tại không?",
                "Xem\ntất cả\nNội Tại", "Mở\nNội Tại", "Mở VIP", "Từ chối");
    }

    public void showConfirmOpen(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_OPEN_INTRINSIC, -1, "Bạn muốn đổi Nội Tại khác\nvới giá là "
                + COST_OPEN + " Thỏi vàng ?", "Mở\nNội Tại", "Từ chối");
    }

    public void showConfirmOpenVip(Player player) {
        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP, -1,
                "Bạn có muốn mở Nội Tại\nvới giá là 500 xu bạc và\ntái lập giá vàng quay lại ban đầu không?", "Mở\nNội VIP", "Từ chối");
    }

    private void changeIntrinsic(Player player) {
        List<Intrinsic> listIntrinsic = getIntrinsics(player.gender);
        player.playerIntrinsic.intrinsic = new Intrinsic(listIntrinsic.get(Util.nextInt(1, listIntrinsic.size() - 1)));
        player.playerIntrinsic.intrinsic.param1 = (short) Util.nextInt(player.playerIntrinsic.intrinsic.paramFrom1, player.playerIntrinsic.intrinsic.paramTo1);
        player.playerIntrinsic.intrinsic.param2 = (short) Util.nextInt(player.playerIntrinsic.intrinsic.paramFrom2, player.playerIntrinsic.intrinsic.paramTo2);
        Service.getInstance().sendThongBao(player, "Bạn nhận được Nội tại:\n" + player.playerIntrinsic.intrinsic.getName().substring(0, player.playerIntrinsic.intrinsic.getName().indexOf(" [")));
        sendInfoIntrinsic(player);
    }

    public void open(Player player) {
        if (player.nPoint.power >= 10000000000L) {
            int goldRequire = COST_OPEN;
            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
            int soLuong = thoiVang == null ? 0 : thoiVang.quantity;
            if (thoiVang == null || thoiVang.quantity < goldRequire) {
                Service.getInstance().sendThongBao(player, "Bạn không đủ thỏi vàng, còn thiếu "
                        + Util.numberToMoney(goldRequire - soLuong) + " thỏi vàng nữa");
                return;
            }
//            player.inventory.gold -= goldRequire;
            InventoryService.gI().subQuantityItemsBag(player, thoiVang, goldRequire);
            PlayerService.gI().sendInfoHpMpMoney(player);
            changeIntrinsic(player);
            player.playerIntrinsic.countOpen++;
        } else {
            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh tối thiểu 10 tỷ");
        }
    }

    public void openVip(Player player) {
        if (player.nPoint.power >= 10000000000L) {
            int gemRequire = 500;
            Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 1567);
            int soLuong = thoiVang == null ? 0 : thoiVang.quantity;
            if (thoiVang == null || thoiVang.quantity < gemRequire) {
                Service.getInstance().sendThongBao(player, "Bạn không đủ xu bạc, còn thiếu "
                        + Util.numberToMoney(gemRequire - soLuong) + " xu bạc nữa");
                return;
            }
//            if (player.inventory.getGem() >= 100) {
//                player.inventory.subGem(gemRequire);
                InventoryService.gI().subQuantityItemsBag(player, thoiVang, gemRequire);
                PlayerService.gI().sendInfoHpMpMoney(player);
                changeIntrinsic(player);
                player.playerIntrinsic.countOpen = 0;
//            } else {
//                Service.getInstance().sendThongBao(player, "Bạn không có đủ ngọc, còn thiếu "
//                        + (gemRequire - player.inventory.getGem()) + " ngọc nữa");
//            }
        } else {
            Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh tối thiểu 10 tỷ");
        }
    }

}
