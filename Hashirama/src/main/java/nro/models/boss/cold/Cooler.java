package nro.models.boss.cold;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.consts.ConstPlayer;
import nro.models.boss.Boss;
import static nro.models.boss.Boss.ATTACK;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.PlayerService;
import nro.services.RewardService;
import nro.services.Service;
import nro.services.TaskService;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class Cooler extends FutureBoss {

    public Cooler() {
        super(BossFactory.COOLER, BossData.COOLER);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 457, Util.nextInt(20, 50), 1);
        super.tileRoiDoHD(pl,Util.nextInt(650, 662), 10, 15, 5);
        super.itemDropCoTile(pl, 1083, 1, 10);
    }

    @Override
    public void idle() {

    }    
    

    @Override
    public void checkPlayerDie(Player pl) {

    }

    @Override
    public void initTalk() {
        textTalkMidle = new String[]{"Ta chính là đệ nhất vũ trụ cao thủ"};
        textTalkAfter = new String[]{"Ác quỷ biến hình aaa..."};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        Boss cooler2 = BossFactory.createBoss(BossFactory.COOLER2);
        cooler2.zone = this.zone;
        this.setJustRestToFuture();
        BossManager.gI().removeBoss(this);
    }

}
