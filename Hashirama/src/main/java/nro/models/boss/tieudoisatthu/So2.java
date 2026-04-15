package nro.models.boss.tieudoisatthu;

import nro.models.boss.*;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class So2 extends FutureBoss {

    public So2() {
        super(BossFactory.SO2, BossData.SO2);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
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
        if (BossManager.gI().getBossById(BossFactory.SO1) == null) {
            BossManager.gI().getBossById(BossFactory.TIEU_DOI_TRUONG).changeToAttack();
        }
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
