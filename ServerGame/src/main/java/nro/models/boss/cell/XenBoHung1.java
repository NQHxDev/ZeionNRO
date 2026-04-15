package nro.models.boss.cell;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;
import nro.consts.ConstAchive;
import nro.models.boss.BossManager;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class XenBoHung1 extends FutureBoss {

    public XenBoHung1() {
        super(BossFactory.XEN_BO_HUNG_1, BossData.XEN_BO_HUNG_1);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }


    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
        super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 2);
        super.itemDropCoTile(pl,Util.nextInt(16, 18), 1, 20);
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
            Log.error(XenBoHung1.class, ex);
        }
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
        this.textTalkMidle = new String[]{"Tất cả nhào vô", "Mình ta cũng đủ để hủy diệt các ngươi"};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        Boss xbh2 = BossFactory.createBoss(BossFactory.XEN_BO_HUNG_2);
        xbh2.zone = this.zone;
        this.setJustRestToFuture();
    }

}
