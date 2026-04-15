package nro.models.boss.boss_doanh_trai;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.services.func.ChangeMapService;
import nro.models.map.phoban.DoanhTrai;
import nro.models.player.Player;
import nro.services.SkillService;
import nro.utils.SkillUtil;
import nro.utils.Util;

import java.util.List;
import nro.consts.ConstItem;
import nro.lib.RandomCollection;
import nro.models.map.ItemMap;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class TrungUyThep extends BossDoanhTrai {

    private boolean activeAttack;

    public TrungUyThep(DoanhTrai doanhTrai) {
        super(BossFactory.TRUNG_UD_THEP, BossData.TRUNG_UY_THEP, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (activeAttack) {
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    SkillService.gI().useSkill(this, pl, null,null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            } else {
                List<Player> notBosses = this.zone.getNotBosses();
                for (Player pl : notBosses) {
                    if (pl.location.x >= 650 && !pl.effectSkin.isVoHinh) {
                        this.activeAttack = true;
                        break;
                    }
                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 457, Util.nextInt(100, 300), 10);// 1- 500 thỏi vàng
        super.itemDropCoTile(pl, 611, 1, 10);
        super.tileRoiDoThanLinh(pl, 10, 10, 5);
    }

    @Override
    public void joinMap() {
        try {
            this.zone = this.doanhTrai.getMapById(mapJoin[Util.nextInt(0, mapJoin.length - 1)]);
            ChangeMapService.gI().changeMap(this, this.zone, 900, this.zone.map.yPhysicInTop(900, 100));
        } catch (Exception e) {

        }
    }

    @Override
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (plAtt != null && !SkillUtil.isUseSkillDam(plAtt)) {
            return super.injured(plAtt, damage, piercing, isMobAttack);
        }
        damage = damage / 100.0;
        if (damage <= 0) {
            damage = 1;
        }
        return super.injured(plAtt, damage, piercing, isMobAttack);
    }

}
