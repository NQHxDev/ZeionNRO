package nro.models.boss.cell;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import nro.consts.ConstAchive;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class XenCon extends FutureBoss {

    public XenCon() {
        super(BossFactory.XEN_CON, BossData.XEN_CON);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }



    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
        super.DoXungQuanh(pl, 1567, Util.nextInt(1, 10), 3);
        super.DoXungQuanh(pl, Util.nextInt(15, 17), 1, 2);
        super.tileRoiDoThanLinh(pl, 10, 10, 5);
    }
    

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                this.playerSkill.skillSelect = this.getSkillAttack();
                if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                    if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                        goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                    }
                    SkillService.gI().useSkill(this, pl, null,null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(XenCon.class, ex);
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossManager.gI().getBossById(BossFactory.XEN_BO_HUNG).changeToAttack();
    }

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void joinMap() {
        if (BossManager.gI().getBossById(BossFactory.SIEU_BO_HUNG) == null) {
            Boss xenBoHung = BossManager.gI().getBossById(BossFactory.XEN_BO_HUNG);
            this.zone = xenBoHung.zone;
            super.joinMap();
        }
    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{};
        this.textTalkMidle = new String[]{};
        this.textTalkAfter = new String[]{};
    }

}
