package nro.models.boss.nappa;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.consts.ConstAchive;
import nro.utils.Util;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Rambo extends FutureBoss {

    public Rambo() {
        super(BossFactory.RAMBO, BossData.RAMBO);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }



    @Override
    public void rewards(Player pl)  {
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

    }

}
