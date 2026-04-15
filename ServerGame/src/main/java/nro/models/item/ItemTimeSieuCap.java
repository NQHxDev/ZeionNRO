package nro.models.item;

import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.services.EffectSkillService;
import nro.services.ItemTimeService;
import nro.services.Service;
import nro.utils.Util;


public class ItemTimeSieuCap {

    //id item text
    public static final int TIME_ITEM_SC_10P = 600000;
    public static final int TIME_ITEM_SC_30P = 1800000;
    public static final int TIME_TRUNGTHU = 3600000;
    public static final int TIME_ITEM_SC_3000P = 180000000;

    private Player player;

    public boolean isDuoikhi;
    public boolean isDaNgucTu;
    public boolean isRongSieuCap;
    public boolean isRongBang;
    public boolean isUseCaRot;
    
    public boolean isChoido;
    public long lasttimeChoido;
    
    public long lastTimeDuoikhi;
    public long lastTimeDaNgucTu;
    public long lastTimeRongSieuCap;
    public long lastTimeRongBang;
    public long lastTimeCaRot;

    public boolean isUseXiMuoi;
    public long lastTimeUseXiMuoi;
    
    public boolean isUseTrungThu;
    public long lastTimeUseBanh;
    public int iconBanh;
    
    public boolean isKeo;
    public long lastTimeKeo;


    public boolean isEatMeal;
    public long lastTimeMeal;
    public int iconMeal;
    public boolean isBienhinhSc;
    public long lastTimeBienhinhSc;

    public ItemTimeSieuCap(Player player) {
        this.player = player;
    }

    public void update() {
        if (isDuoikhi) {
            if (Util.canDoWithTime(lastTimeDuoikhi, TIME_ITEM_SC_10P)) {
                isDuoikhi = false;
                Service.getInstance().point(player);
            }
        }
        
        if (isDaNgucTu) {
            if (Util.canDoWithTime(lastTimeDaNgucTu, TIME_ITEM_SC_10P)) {
                isDaNgucTu = false;
                Service.getInstance().point(player);
            }
        }
        if (isUseCaRot) {
            if (Util.canDoWithTime(lastTimeCaRot, TIME_ITEM_SC_10P)) {
                isUseCaRot = false;
                Service.getInstance().point(player);
            }
        }
       
        if (isKeo) {
            if (Util.canDoWithTime(lastTimeKeo, TIME_ITEM_SC_30P)) {
                isKeo = false;
                Service.getInstance().point(player);
            }
        }
        if (isUseXiMuoi) {
            if (Util.canDoWithTime(lastTimeUseXiMuoi, TIME_ITEM_SC_10P)) {
                isUseXiMuoi = false;
                Service.getInstance().point(player);
            }
        }
        if (isUseTrungThu) {
            if (Util.canDoWithTime(lastTimeUseBanh, TIME_TRUNGTHU)) {
                isUseTrungThu = false;
                Service.getInstance().point(player);
            }
        }
        if (isChoido) {
            if (Util.canDoWithTime(lasttimeChoido, TIME_ITEM_SC_10P)) {
                isChoido = false;
                EffectSkillService.gI().removeChoido(player);
                Service.getInstance().point(player);
            }
        }
        if (isEatMeal) {
            if (Util.canDoWithTime(lastTimeMeal, TIME_ITEM_SC_10P)) {
                isEatMeal = false;
                Service.getInstance().point(player);
            }
        }
        if (isRongSieuCap) {
            if (Util.canDoWithTime(lastTimeRongSieuCap, TIME_ITEM_SC_30P)) {
                isRongSieuCap = false;
                Service.getInstance().point(player);
            }
        }
        if (isRongBang) {
            if (Util.canDoWithTime(lastTimeRongBang, TIME_ITEM_SC_30P)) {
                isRongBang = false;
                Service.getInstance().point(player);
            }
        }
        if (isBienhinhSc) {
            if (Util.canDoWithTime(lastTimeBienhinhSc, TIME_ITEM_SC_10P)) {
                isBienhinhSc = false;
                Service.getInstance().point(player);
            }
        }
    }
    
    public void dispose(){
        this.player = null;
    }
}
