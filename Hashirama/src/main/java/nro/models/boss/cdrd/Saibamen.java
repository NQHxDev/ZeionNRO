/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.models.boss.cdrd;

import nro.consts.ConstItem;
import nro.lib.RandomCollection;
import nro.models.boss.BossData;
import nro.models.map.ItemMap;
import nro.models.map.dungeon.SnakeRoad;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.EffectSkillService;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Log;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class Saibamen extends CBoss {

    private boolean selfExplosion;

    public Saibamen(long id, short x, short y, SnakeRoad dungeon, BossData data) {
        super(id, x, y, dungeon, data);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
    }


    @Override
    public void idle() {

    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        double hp = nPoint.hp;
        if (!selfExplosion) {
            if (hp > 1) {
                if (damage > hp) {
                    damage = hp - 1;
                    selfExplosion = true;
                    chat("Nhìn gì thằng nguuuu");
                    if (plAtt != null) {
                        Service.getInstance().chat(plAtt, "Mày dám đấm vào mặt bố mày à...");
                        Service.getInstance().sendThongBao(plAtt, plAtt.name + " Tao nổ chết đuỹ moẹ mày nha con chó..");
                        EffectSkillService.gI().setBlindDCTT(plAtt, System.currentTimeMillis(), 3000);
                        EffectSkillService.gI().sendEffectPlayer(this, plAtt, EffectSkillService.TURN_ON_EFFECT, EffectSkillService.BLIND_EFFECT);
                    }
                    selfExplosion();
                }
            } else {
                damage = 0;
            }
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

    private void selfExplosion() {
        try {
            this.nPoint.hpMax = 1000000000;
            this.playerSkill.skillSelect = this.getSkillById(Skill.TU_SAT);
            SkillService.gI().useSkill(this, null, null,null);
            Util.setTimeout(() -> {
                SkillService.gI().useSkill(this, null, null,null);
            }, 2000);
        } catch (Exception e) {
            Log.error(Saibamen.class, e);
        }
    }

}
