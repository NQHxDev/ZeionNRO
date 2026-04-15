package nro.models.boss.fide;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 * @copyright 💖 GirlkuN 💖
 */
public class FideGold extends Boss {

    public FideGold() {
        super(BossFactory.FIDEGOLD, BossData.FIDEGOLD);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl)  {
        super.DoXungQuanh(pl, 457, Util.nextInt(70, 450), 5);// 1- 500 thỏi vàng
        super.itemDropCoTile(pl, 1567, Util.nextInt(2, 10), 50);
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

    public void leaveMap() {
        BossFactory.createBoss(BossFactory.FIDEGOLD).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
