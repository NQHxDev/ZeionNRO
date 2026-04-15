package nro.models.boss.BossMoi;

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
import nro.utils.SkillUtil;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class GiamNguc extends Boss {

    public GiamNguc() {
        super(BossFactory.GIAM_NGUC, BossData.GIAM_NGUC);
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
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkMidle = new String[]{"Sống trên đời..", "Không làm mà đòi có ăn...",
            "Chỉ có ăn cái đb... ăn cớt", "Cần cù thì bù siêng năng..."};

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.GIAM_NGUC).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 1533, Util.nextInt(1, 3), Util.nextInt(1, 2));// mảnh vỡ kí ức
        super.DoXungQuanh(pl, 1688, Util.nextInt(1, 3), Util.nextInt(1, 2));// nguyên tố bí ẩn
        super.itemDropCoTile(pl, 1479, Util.nextInt(1, 5), 5);
        super.itemDropCoTile(pl, Util.nextInt(1190, 1191), Util.nextInt(1, 3), 10);//10% 1-> 3 viên ngọc rồng vip
        super.itemDropCoTile(pl, 1687, 1, 1);
        PlayerDAO.CongKillBoss(pl, 9);
    }
}
