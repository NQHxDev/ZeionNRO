package nro.models.boss.NguHanhSon;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.services.SkillService;
import nro.utils.Log;
import nro.utils.SkillUtil;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
public class NgoKhong extends Boss {

    public NgoKhong() {
        super(BossFactory.NGO_KHONG, BossData.NGO_KHONG);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
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
                    SkillService.gI().useSkill(this, pl, null, null);
                    checkPlayerDie(pl);
                } else {
                    goToPlayer(pl, false);
                }
            }
        } catch (Exception ex) {
            Log.error(NgoKhong.class, ex);
        }
    }


    @Override
    public void rewards(Player pl)  {
        super.DoXungQuanh(pl, 457, Util.nextInt(1, 10), 5);
        tileRoiCT_DeTu_RandomChiSo(pl, 547, 10, 60, 150, 60, 180, 100, 500, 1, 3);
        super.itemDropCoTile(pl, 1569, 1, 1);  
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
        this.textTalkMidle = new String[]{"Yêu quái! Chạy đi đâu?!", "Mi khá đấy nhưng so với Lão Tôn chỉ là tép riu",
            "Tất cả nhào vô hết đi", "Lão Tôn là Tề thiên đại thánh 500 năm trước từng đại náo thiên cung.", "Các ngươi yếu thế này sao hạ được Lão Tôn đây. haha",
            "Lão Tôn ta đến đây!!!", "Yêu quái ăn một gậy của lão Tôn ta!"};
        this.textTalkAfter = new String[]{"Các ngươi được lắm", "Hãy đợi đấy thời gian tới Lão Tôn sẽ quay lại.."};
    }
    
    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossFactory.createBoss(BossFactory.BAT_GIOI).setJustRest();
    }
}
