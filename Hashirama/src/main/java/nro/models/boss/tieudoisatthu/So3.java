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
public class So3 extends FutureBoss {

    public So3() {
        super(BossFactory.SO3, BossData.SO3);
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
        BossManager.gI().getBossById(BossFactory.SO2).changeToAttack();
        BossManager.gI().getBossById(BossFactory.SO1).changeToAttack();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
