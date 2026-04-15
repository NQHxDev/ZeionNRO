package nro.models.boss.broly;

import nro.consts.ConstRatio;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.ServerNotify;
import nro.services.Service;
import nro.services.*;
import nro.services.SkillService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;


/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class SuperBroly extends Broly {

    public SuperBroly() {
        super(BossFactory.SUPER_BROLY, BossData.SUPER_BROLY);
        this.nPoint.defg = (short) (this.nPoint.hpg / 1000);
        if (this.nPoint.defg < 0) {
            this.nPoint.defg = (short) -this.nPoint.defg;
        }
    }

    public SuperBroly(byte id, BossData data) {
        super(id, data);
        this.nPoint.defg = (short) (this.nPoint.hpg / 1000);
        if (this.nPoint.defg < 0) {
            this.nPoint.defg = (short) -this.nPoint.defg;
        }
    }

    @Override
    public synchronized double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie()) {
            return 0D;
        }
        /* ================== KHÁNG SKILL ================== */
        if (plAtt != null
                && plAtt.playerSkill != null
                && plAtt.playerSkill.skillSelect != null) {

            int skill = plAtt.playerSkill.skillSelect.template.id;

            if (skill == Skill.KAMEJOKO
                    || skill == Skill.ANTOMIC
                    || skill == Skill.MASENKO
                    || skill == Skill.LIEN_HOAN) {

                damage = 1D;
                Service.getInstance().chat(plAtt,
                        "Trời ơi, chưởng hoàn toàn vô hiệu lực với hắn..");
            }
        }
        // Chuẩn hoá input: loại bỏ NaN/Infinity và âm
        if (Double.isNaN(damage) || Double.isInfinite(damage)) {
            damage = 1D;
        }

        // Clamp: tối thiểu 1, tối đa 
        final double MAX_HIT = 200_000d;
        
        double applied = damage;
        if (applied < 1D) {
            applied = 1D;
        }
        if (applied > MAX_HIT) {
            applied = MAX_HIT;
        }

        // Trừ HP
        if (applied >= this.nPoint.hp) {
            applied = this.nPoint.hp; // tránh trừ âm và đảm bảo hiển thị đúng cú chốt
        }
        this.nPoint.hp -= applied;

        // Kết liễu & thưởng
        if (this.isDie()) {
            try {
                rewards(plAtt);
            } catch (Exception ignore) {
            }
            try {
                notifyPlayeKill(plAtt); // nếu bạn có hàm notify
            } catch (Exception ignore) {
            }
            try {
                die();
            } catch (Exception ignore) {
            }
        }

        // Trả về lượng dame để client hiển thị (đúng yêu cầu khi < 696M)
        return applied;
    }


    @Override
    public void attack() {
        try {
            if (!charge()) {
                Player pl = getPlayerAttack();
                if (pl != null) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
//                        this.effectCharger();
                        try {
                            SkillService.gI().useSkill(this, pl, null,null);
                        } catch (Exception e) {
                            Log.error(SuperBroly.class, e);
                        }
                    } else {
                        goToPlayer(pl, false);
                    }
                    if (Util.isTrue(5, ConstRatio.PER100)) {
                        this.changeIdle();
                    }
                }
            }
        } catch (Exception ex) {
            Log.error(SuperBroly.class, ex);
        }
    }

    @Override
    public Player getPlayerAttack() throws Exception {
        if (countChangePlayerAttack < targetCountChangePlayerAttack
                && plAttack != null && plAttack.zone != null && plAttack.zone.equals(this.zone)
                && !plAttack.effectSkin.isVoHinh) {
            if (!plAttack.isDie()) {
                this.countChangePlayerAttack++;
                return plAttack;
            } else {
                plAttack = null;
            }
        } else {
            this.targetCountChangePlayerAttack = Util.nextInt(10, 20);
            this.countChangePlayerAttack = 0;
            plAttack = this.zone.getRandomPlayerInMap();
        }
        return plAttack;
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
    }

    @Override
    public void die() {
        this.secondTimeRestToNextTimeAppear = 900; //15p
        super.die();
    }

    @Override       
    public void rewards(Player pl)  {
        super.DoXungQuanh(pl, 457, Util.nextInt(10, 20), 1);
        super.itemDropCoTile(pl, 568, 1, 30);
//        if(pl.pet == null){
//            PetService.gI().createNormalPet(pl, pl.gender);
//            Service.getInstance().sendThongBaoOK(pl, "Chúc mừng bạn vừa tiêu diệt boss " + this.name + " nhận được đệ tử");
//            ServerNotify.gI().notify(pl.name + " vừa tiêu diệt " + this.name + "tại " + this.zone.map.mapName + " khu vực " + this.zone.zoneId);
//        } else {
//            ServerNotify.gI().notify(pl.name + " vừa phá boss " + this.name + " tại " + this.zone.map.mapName + " khu vực " + this.zone.zoneId);
//        }
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

}
