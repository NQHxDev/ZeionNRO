package nro.models.boss.cell;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.consts.ConstPlayer;
import nro.consts.ConstRatio;
import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.server.Manager;
import nro.services.PlayerService;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.SkillService;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
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
public class XenBoHung extends FutureBoss {

    public XenBoHung() {
        super(BossFactory.XEN_BO_HUNG, BossData.XEN_BO_HUNG);
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
//        if (BossManager.gI().getBossById(BossFactory.XEN_CON) != null) {
//            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
//            this.changeIdle();
//            return;
//        }
        if (this.isDie()) {
            tuSat();
            die();
            return;
        }
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
    public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
        if (this.isDie() || this.playerSkill.prepareTuSat) {
            return 0;
        } else {
            double dame = super.injuredNotCheckDie(plAtt, damage, piercing);
            if (this.isDie()) {
                rewards(plAtt);
            }
            return dame;
        }
    }

    private void tuSat() {
        try {
            this.nPoint.hpg = 10000;
            this.nPoint.hp = 1;
            ChangeMapService.gI().changeMap(this, this.zone, this.location.x, this.location.y);
            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.NON_PK);
            PlayerService.gI().changeTypePK(this, ConstPlayer.PK_ALL);
            this.playerSkill.skillSelect = this.getSkillById(Skill.TU_SAT);
            SkillService.gI().useSkill(this, null, null, null);
            Thread.sleep(3000);
            SkillService.gI().useSkill(this, null, null, null);
        } catch (Exception e) {
        }
    }

    @Override
    public void joinMap() {
        if (BossManager.gI().getBossById(BossFactory.SIEU_BO_HUNG) == null) {
            BossFactory.createBoss(BossFactory.XEN_CON);
            super.joinMap();
        }
    }

    @Override
    public void idle() {
        if (BossManager.gI().getBossById(BossFactory.XEN_CON) == null) {
            PlayerService.gI().changeAndSendTypePK(this, ConstPlayer.PK_ALL);
            changeAttack();
        }
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
        super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 2);
        super.itemDropCoTile(pl,15, 1, 50);
        super.DoXungQuanh(pl, Util.nextInt(15, 18), 1, 1);
        super.tileRoiDoThanLinh(pl, 10, 10,5);
    }
    

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        this.textTalkBefore = new String[]{"Ta cho các ngươi 5 giây để chuẩn bị", "Cuộc chơi bắt đầu.."};
        this.textTalkMidle = new String[]{"Kame Kame Haaaaa!!", "Mi khá đấy nhưng so với ta chỉ là hạng tôm tép",
            "Tất cả nhào vô hết đi", "Cứ chưởng tiếp đi. haha", "Các ngươi yếu thế này sao hạ được ta đây. haha",
            "Khi công pháo!!", "Cho mi biết sự lợi hại của ta"};
        this.textTalkAfter = new String[]{};
    }

    @Override
    public void leaveMap() {
        Boss sieuBoHung = BossFactory.createBoss(BossFactory.SIEU_BO_HUNG);
        sieuBoHung.zone = this.zone;
        super.leaveMap();
        this.changeToIdle();
        BossManager.gI().removeBoss(this);
    }
}
