package nro.models.player;

import nro.consts.ConstPlayer;
import nro.models.mob.Mob;
import nro.services.EffectSkillService;
import nro.services.ItemTimeService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.func.ChangeMapService;
import nro.utils.SkillUtil;
import nro.utils.Util;
import nro.models.skill.Skill;
import nro.server.Manager;

/**
 *
 * @author Văn Tuấn - 0337766460
 * @copyright 💖 Ytb: @Tuan_To_Mo 💖
 *
 */
public class EffectSkill {

    private Player player;

    //thái dương hạ san
    public boolean isStun;
    public long lastTimeStartStun;
    public int timeStun;

    //khiên năng lượng
    public boolean isShielding;
    public long lastTimeShieldUp;
    public int timeShield;

    //biến khỉ
    public boolean isMonkey;
    public byte levelMonkey;
    public long lastTimeUpMonkey;
    public int timeMonkey;

    //tái tạo năng lượng
    public boolean isCharging;
    public int countCharging;

    //huýt sáo
    public int tiLeHPHuytSao;
    public long lastTimeHuytSao;

    //thôi miên
    public boolean isThoiMien;
    public long lastTimeThoiMien;
    public int timeThoiMien;

    //trói
    public boolean useTroi;
    public boolean anTroi;
    public long lastTimeTroi;
//    public long lastTimeAnTroi;
    public int timeTroi;
//    public int timeAnTroi;
    public Player plTroi;
    public Player plAnTroi;
    public Mob mobAnTroi;

    //dịch chuyển tức thời
    public boolean isBlindDCTT;
    public long lastTimeBlindDCTT;
    public int timeBlindDCTT;

    //socola
    public boolean isSocola;
    public long lastTimeSocola;
    public int timeSocola;
    public int countPem1hp;

    //mabu hold
    public byte isTaskHoldMabu;
    public boolean isHoldMabu;
    public long lastTimeHoldMabu;
    public long lastTimeChat;

    //Bien Hinh Sc
    public boolean isBienHinhSc;
    public long lastTimeBienHinhSc;
    public int timeBienHinhSc;
    public int levelBienHinhSc = 0;
    public int time_hoi_BienHinhSc = 0;

    //Bien Hinh Sc
    public boolean isPhanThan;
    public long lastTimePhanThan;
    public int timePhanThan;
    public int levelPhanThan = 0;

    public boolean isBinh;
    public long lastTimeBinh;
    public int timeBinh;

    public EffectSkill(Player player) {
        this.player = player;
    }

    public void removeSkillEffectWhenDie() {
        if (isMonkey) {
            EffectSkillService.gI().monkeyDown(player);
        }
        if (isBienHinhSc) {
            EffectSkillService.gI().downBienHinh(player);
        }
        if (isShielding) {
            EffectSkillService.gI().removeShield(player);
            ItemTimeService.gI().removeItemTime(player, 3784);
        }
        if (useTroi) {
            EffectSkillService.gI().removeUseTroi(this.player);
        }
        if (isStun) {
            EffectSkillService.gI().removeStun(this.player);
        }
        if (isThoiMien) {
            EffectSkillService.gI().removeThoiMien(this.player);
        }
        if (isBlindDCTT) {
            EffectSkillService.gI().removeBlindDCTT(this.player);
        }
    }

    public void update() {
        if (isBienHinhSc && (Util.canDoWithTime(lastTimeBienHinhSc, timeBienHinhSc))) {
            EffectSkillService.gI().downBienHinh(player);
        }
        if (isMonkey && (Util.canDoWithTime(lastTimeUpMonkey, timeMonkey))) {
            EffectSkillService.gI().monkeyDown(player);
        }
        if (isShielding && (Util.canDoWithTime(lastTimeShieldUp, timeShield))) {
            EffectSkillService.gI().removeShield(player);
        }
        if (useTroi && Util.canDoWithTime(lastTimeTroi, timeTroi)
                || plAnTroi != null && plAnTroi.isDie()
                || useTroi && isHaveEffectSkill()
                || player.isDie()) {
            EffectSkillService.gI().removeUseTroi(this.player);
        }
//        if (anTroi && (Util.canDoWithTime(lastTimeAnTroi, timeAnTroi) || player.isDie())) {
//            EffectSkillService.gI().removeAnTroi(this.player);
//        }
        if (isStun && Util.canDoWithTime(lastTimeStartStun, timeStun)) {
            EffectSkillService.gI().removeStun(this.player);
        }
        if (isThoiMien && (Util.canDoWithTime(lastTimeThoiMien, timeThoiMien))) {
            EffectSkillService.gI().removeThoiMien(this.player);
        }
        if (isBlindDCTT && (Util.canDoWithTime(lastTimeBlindDCTT, timeBlindDCTT))) {
            EffectSkillService.gI().removeBlindDCTT(this.player);
        }
        if (isSocola && (Util.canDoWithTime(lastTimeSocola, timeSocola))) {
            EffectSkillService.gI().removeSocola(this.player);
        }
        if (isBinh && (Util.canDoWithTime(lastTimeBinh, timeBinh))) {
            EffectSkillService.gI().removeBinh(this.player);
        }
        if (tiLeHPHuytSao != 0 && Util.canDoWithTime(lastTimeHuytSao, 30000)) {
            EffectSkillService.gI().removeHuytSao(this.player);
        }
        if (player.isBoss && player.playerSkill.prepareQCKK && Util.canDoWithTime(player.playerSkill.lastTimeUseQCKK, 4000)) {
            SkillService.gI().useSkill(player, null, null, null);
        }
        if (player.isPl()) {
            if (this.isTaskHoldMabu == 1 && Util.canDoWithTime(this.lastTimeHoldMabu, 2000)) {
                if (player.zone.map.mapId != 128) {
                    player.pet.goHome();
                    ChangeMapService.gI().changeMapInYard(player, 128, -1, -1);
                }
                this.isTaskHoldMabu = 2;
                lastTimeHoldMabu = System.currentTimeMillis();
            }
            if (this.isTaskHoldMabu == 2 && player.zone.map.mapId == 128 && Util.canDoWithTime(this.lastTimeHoldMabu, 600)) {
                if (!this.isShielding) {
                    short[] point = player.zone.getXYMabuMap();
                    Service.getInstance().sendMabuEat(player, point[0], point[1]);
                    PlayerService.gI().changeAndSendTypePK(player, ConstPlayer.PK_ALL);
                    lastTimeChat = System.currentTimeMillis();
                    this.isHoldMabu = true;
                }
                this.isTaskHoldMabu = -1;
            }
            if (player.zone.map.mapId == 128 && this.isHoldMabu && Util.canDoWithTime(lastTimeChat, 6000)) {
                player.nPoint.subHP(player.nPoint.hp / 100 * 5);
                PlayerService.gI().sendInfoHp(player);
                lastTimeChat = System.currentTimeMillis();
            }
        }
    }

    public boolean isHaveEffectSkill() {
        return isStun || isBlindDCTT || anTroi || isThoiMien || isHoldMabu;
    }

    public void dispose() {
        this.player = null;
    }
}
