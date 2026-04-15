package nro.models.boss.robotsatthu;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 *
 * @author ❤Girlkun75❤
 * @copyright ❤Trần Lại❤
 */
public class KingKong extends Boss {

    public KingKong() {
        super(BossFactory.KINGKONG, BossData.KINGKONG);
    }

    @Override
    public void joinMap() {
        super.joinMap();
        BossFactory.createBoss(BossFactory.POC).zone = this.zone;
        BossFactory.createBoss(BossFactory.PIC).zone = this.zone;
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
    public void idle() {
    }

    @Override
    public void checkPlayerDie(Player pl) {
    }

    @Override
    public void initTalk() {
    }

}
