package nro.models.boss.BossMoiLam;

import nro.models.boss.BossMoi.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.jdbc.daos.PlayerDAO;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;

import nro.models.boss.BossManager;
import nro.models.map.Zone;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.utils.SkillUtil;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class Heart_Gold extends Boss {

    public Heart_Gold() {
        super(BossFactory.HEART_GOLD, BossData.HEART_GOLD);
    }

    @Override
    protected boolean useSpecialSkill() {
        this.playerSkill.skillSelect = this.getSkillSpecial();
        if (SkillService.gI().canUseSkillWithCooldown(this)) {
            SkillService.gI().useSkill(this, null, null, null);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void attack() {
        try {
            Player pl = getPlayerAttack();
            if (pl != null) {
                if (!useSpecialSkill()) {
                    this.playerSkill.skillSelect = this.getSkillAttack();
                    if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                        if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                            goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                                    Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                        }
                        SkillService.gI().useSkill(this, pl, null, null);
                        checkPlayerDie(pl);
                    } else {
                        goToPlayer(pl, false);
                    }
                }
            }
        } catch (Exception ex) {
        }
    }

    @Override
    public void idle() {

    }

    @Override
    public void rewards(Player pl)  {
    super.itemDropCoTile(pl, 700, 1, 30);//tăng nguyên đan
    super.DoXungQuanh(pl, 1479, Util.nextInt(1, 10), 1);
    }


    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Sống trên đời..", "Không làm mà đòi có ăn...",
            "Chỉ có ăn cái đb... ăn cớt", "Cần cù thì bù siêng năng..."};
    }

      @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
