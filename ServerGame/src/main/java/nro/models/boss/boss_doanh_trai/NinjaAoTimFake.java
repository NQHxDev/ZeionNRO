package nro.models.boss.boss_doanh_trai;

import nro.consts.ConstItem;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.event.Event;
import nro.lib.RandomCollection;
import nro.models.boss.BossData;
import nro.models.boss.nappa.Kuku;
import nro.models.boss.nappa.MapDauDinh;
import nro.models.boss.nappa.Rambo;
import nro.models.map.ItemMap;
import nro.models.map.phoban.DoanhTrai;
import nro.models.player.Player;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;
import nro.services.SkillService;
import nro.services.TaskService;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class NinjaAoTimFake extends NinjaAoTim {

    public NinjaAoTimFake(int id, DoanhTrai doanhTrai) {
        super(id, BossData.NINJA_AO_TIM_FAKE, doanhTrai);
        this.typePk = ConstPlayer.PK_ALL;
    }

    @Override
    public void attack() {
        try {
            if (!useSpecialSkill()) {
                if (Util.isTrue(30, ConstRatio.PER100)) {
                    this.talk();
                }
                Player pl = getPlayerAttack();
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(50, ConstRatio.PER100)) {
                        goToXY(pl.location.x + Util.nextInt(-20, 20), Util.nextInt(pl.location.y - 80,
                                this.zone.map.yPhysicInTop(pl.location.x, 0)), false);
                    }
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
//            ex.printStackTrace();
        }
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 457, Util.nextInt(10, 30), 10);// 1- 500 thỏi vàng
    }
}