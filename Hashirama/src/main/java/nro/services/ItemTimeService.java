package nro.services;

import nro.consts.ConstPlayer;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.map.phoban.DoanhTrai;
import nro.models.player.Fusion;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.utils.Log;

import static nro.models.item.ItemTime.*;
import static nro.models.item.ItemTimeSieuCap.*;
import nro.services.func.ChangeMapService;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class ItemTimeService {

    private static ItemTimeService i;

    public static ItemTimeService gI() {
        if (i == null) {
            i = new ItemTimeService();
        }
        return i;
    }

    //gửi cho client
    public void sendAllItemTime(Player player) {
        sendTextRongSieuCap(player);
        sendTextRongBang(player);
        sendTextDoanhTrai(player);
        sendTextBanDoKhoBau(player);
        if (player.fusion.typeFusion == ConstPlayer.LUONG_LONG_NHAT_THE) {
            sendItemTime(player, player.gender == ConstPlayer.NAMEC ? 3901 : 3790,
                    (int) ((Fusion.TIME_FUSION - (System.currentTimeMillis() - player.fusion.lastTimeFusion)) / 1000));
        }
        if (player.itemTime.isUseBoHuyet) {
            sendItemTime(player, 2755, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet)) / 1000));
        }
        if (player.itemTime.isUseBoKhi) {
            sendItemTime(player, 2756, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi)) / 1000));
        }
        if (player.itemTime.isUseGiapXen) {
            sendItemTime(player, 2757, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen)) / 1000));
        }
        if (player.itemTime.isUseCuongNo) {
            sendItemTime(player, 2754, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo)) / 1000));
        }
        if (player.itemTime.isUseAnDanh) {
            sendItemTime(player, 2760, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeAnDanh)) / 1000));
        }
        if (player.itemTime.isOpenPower) {
            sendItemTime(player, 3783, (int) ((TIME_OPEN_POWER - (System.currentTimeMillis() - player.itemTime.lastTimeOpenPower)) / 1000));
        }
        if (player.itemTime.isUseMayDo) {
            sendItemTime(player, 2758, (int) ((TIME_MAY_DO - (System.currentTimeMillis() - player.itemTime.lastTimeUseMayDo)) / 1000));
        }
        if (player.itemTime.isEatMeal) {
            sendItemTime(player, player.itemTime.iconMeal, (int) ((TIME_EAT_MEAL - (System.currentTimeMillis() - player.itemTime.lastTimeEatMeal)) / 1000));
        }
        if (player.itemTime.isUseBanhChung) {
            sendItemTime(player, 15528, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBanhChung)) / 1000));
        }
        if (player.itemTime.isUseBanhTet) {
            sendItemTime(player, 15526, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBanhTet)) / 1000));
        }
        if (player.itemTime.isUseBoHuyet2) {
            sendItemTime(player, 10714, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoHuyet2)) / 1000));
        }
        if (player.itemTime.isUseBoKhi2) {
            sendItemTime(player, 10715, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeBoKhi2)) / 1000));
        }
        if (player.itemTime.isUseGiapXen2) {
            sendItemTime(player, 10712, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeGiapXen2)) / 1000));
        }
        if (player.itemTime.isUseCuongNo2) {
            sendItemTime(player, 10716, (int) ((TIME_ITEM - (System.currentTimeMillis() - player.itemTime.lastTimeCuongNo2)) / 1000));
        }
        if (player.itemTimesieucap.isDaNgucTu) {
            sendItemTime(player, 11274, (int) ((TIME_ITEM_SC_10P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeDaNgucTu)) / 1000));
        }
        if (player.itemTimesieucap.isDuoikhi) {
            sendItemTime(player, 5072, (int) ((TIME_ITEM_SC_10P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeDuoikhi)) / 1000));
        }
        if (player.itemTimesieucap.isUseCaRot) {
            sendItemTime(player, 16128, (int) ((TIME_ITEM_SC_10P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeCaRot)) / 1000));
        }
        if (player.itemTimesieucap.isKeo) {
            sendItemTime(player, 8243, (int) ((TIME_ITEM_SC_30P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeKeo)) / 1000));
        }
        if (player.itemTimesieucap.isUseXiMuoi) {
            sendItemTime(player, 10904, (int) ((TIME_ITEM_SC_10P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeUseXiMuoi)) / 1000));
        }
        if (player.itemTimesieucap.isUseTrungThu) {
            sendItemTime(player, player.itemTimesieucap.iconBanh, (int) ((TIME_TRUNGTHU - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeUseBanh)) / 1000));
        }
        if (player.itemTimesieucap.isChoido) {
        }
        if (player.itemTimesieucap.isBienhinhSc) {
            int lv = player.effectSkill.levelBienHinhSc;
            int iconLvFirst = player.gender == 0 ? 27841 : player.gender == 1 ? 27846 : 27836;
            sendItemTime(player, (iconLvFirst + lv), (int) ((TIME_ITEM_SC_10P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeBienhinhSc)) / 1000));        
        }
//        if (player.itemTimesieucap.isRongBang) {
//            sendItemTime(player, 8579, (int) ((TIME_ITEM_SC_30P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeRongBang)) / 1000));
//        }
    }

    //bật tđlt
    public void turnOnTDLT(Player player, Item item) {
        int min = 0;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 1) {
                min = io.param;
                io.param = 0;
                break;
            }
        }
        player.itemTime.isUseTDLT = true;
        player.itemTime.timeTDLT = min * 60 * 1000;
        player.itemTime.lastTimeUseTDLT = System.currentTimeMillis();
//        sendCanAutoPlay(player);
        player.autoDoiKhu = true;
        ChangeMapService.gI().startAutoZoneChange(player);
        sendItemTime(player, 31412, player.itemTime.timeTDLT / 1000);
        InventoryService.gI().sendItemBags(player);
    }

    //tắt tđlt
    public void turnOffTDLT(Player player, Item item) {
        if (player.isBot) {
            return; // ⛔ BOT KHÔNG BỊ TRỪ TIME
        }
        player.itemTime.isUseTDLT = false;
        for (ItemOption io : item.itemOptions) {
            if (io.optionTemplate.id == 1) {
                io.param = (short) ((player.itemTime.timeTDLT - (System.currentTimeMillis() - player.itemTime.lastTimeUseTDLT)) / 60 / 1000);
                break;
            }
        }
//        sendCanAutoPlay(player);
        player.autoDoiKhu = false;
        removeItemTime(player, 31412);
        InventoryService.gI().sendItemBags(player);
    }

    public void sendCanAutoPlay(Player player) {
        Message msg;
        try {
            msg = new Message(-116);
            msg.writer().writeByte(player.itemTime.isUseTDLT ? 1 : 0);
            player.sendMessage(msg);
        } catch (Exception e) {
            Log.error(ItemTimeService.class, e);
        }
    }

    public void sendTextRongBang(Player player) {
        if (player.itemTimesieucap.isRongBang) {
            int secondsLeft = (int) ((TIME_ITEM_SC_30P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeRongBang)) / 1000);
            sendTextTime(player, RONG_BANG, "Rồng Băng: +15%HP,KI,SD còn", secondsLeft);
        }
    }

    public void sendTextRongSieuCap(Player player) {
        if (player.itemTimesieucap.isRongSieuCap) {
            int secondsLeft = (int) ((TIME_ITEM_SC_30P - (System.currentTimeMillis() - player.itemTimesieucap.lastTimeRongSieuCap)) / 1000);
            sendTextTime(player, RONG_SIEU_CAP, "Rồng Siêu Cấp: +50%HP,KI,SD còn", secondsLeft);
        }
    }

    public void sendTextDoanhTrai(Player player) {
        if (player.clan != null && !player.clan.haveGoneDoanhTrai
                && player.clan.timeOpenDoanhTrai != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.timeOpenDoanhTrai) / 1000);
            int secondsLeft = (DoanhTrai.TIME_DOANH_TRAI / 1000) - secondPassed;
            sendTextTime(player, DOANH_TRAI, "Doanh trại độc nhãn", secondsLeft);
        }
    }

    public void sendTextBanDoKhoBau(Player player) {
        if (player.clan != null
                && player.clan.timeOpenBanDoKhoBau != 0) {
            int secondPassed = (int) ((System.currentTimeMillis() - player.clan.timeOpenBanDoKhoBau) / 1000);
            int secondsLeft = (BanDoKhoBau.TIME_BAN_DO_KHO_BAU / 1000) - secondPassed;
            sendTextTime(player, BAN_DO_KHO_BAU, "Bản đồ kho báu", secondsLeft);
        }
    }

    public void removeTextDoanhTrai(Player player) {
        removeTextTime(player, DOANH_TRAI);
    }

    public void removeTextTime(Player player, byte id) {
        sendTextTime(player, id, "", 0);
    }

    private void sendTextTime(Player player, byte id, String text, int seconds) {
        Message msg;
        try {
            msg = new Message(65);
            msg.writer().writeByte(id);
            msg.writer().writeUTF(text);
            msg.writer().writeShort(seconds);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void sendItemTime(Player player, int itemId, int time) {
        Message msg;
        try {
            msg = new Message(-106);
            msg.writer().writeShort(itemId);
            msg.writer().writeShort(time);
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void removeItemTime(Player player, int itemTime) {
        sendItemTime(player, itemTime, 0);
    }    
    
    public void sendItemTimeBienHinhSc(Player player, int level) {
        int iconLvFirst = player.gender == 0 ? 27841 : player.gender == 1 ? 27846 : 27836;
        int timeIcon = player.effectSkill.timeBienHinhSc / 1000;
        if (level == 1) {
            sendItemTime(player, iconLvFirst, timeIcon);
        } else {
            removeItemTime(player, iconLvFirst + level - 2);
            sendItemTime(player, iconLvFirst + level - 1, timeIcon);
        }
    }

}
