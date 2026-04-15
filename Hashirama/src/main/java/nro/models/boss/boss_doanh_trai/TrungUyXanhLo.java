package nro.models.boss.boss_doanh_trai;

import nro.consts.ConstRatio;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.services.func.ChangeMapService;
import nro.models.map.phoban.DoanhTrai;
import nro.models.player.Player;
import nro.services.SkillService;
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
public class TrungUyXanhLo extends BossDoanhTrai {

    private boolean activeAttack;

    public TrungUyXanhLo(DoanhTrai doanhTrai) {
        super(BossFactory.TRUNG_UY_XANH_LO, BossData.TRUNG_UY_XANH_LO, doanhTrai);
    }

    @Override
    public void attack() {
        try {
            if (activeAttack) {
                if (!useSpecialSkill()) {
                    Player pl = getPlayerAttack();
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(10, ConstRatio.PER100)) {
                            goToXY(pl.location.x + Util.nextInt(-20, 20),
                                    Util.nextInt(pl.location.y - 80, this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                        }
                        SkillService.gI().useSkill(this, pl, null,null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            } else {
                List<Player> notBosses = this.zone.getNotBosses();
                for (Player pl : notBosses) {

                    if (pl.location.x >= 820 && !pl.effectSkin.isVoHinh) {
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
    protected boolean useSpecialSkill() {
        //boss này chỉ có chiêu thái dương hạ san
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null,null);
            return true;
        } else {
            return false;
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
            ChangeMapService.gI().changeMap(this, this.zone, 1065, this.zone.map.yPhysicInTop(1065, 0));
        } catch (Exception e) {

        }
    }

}
