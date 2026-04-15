package nro.models.boss.chill;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.models.boss.*;
import static nro.models.boss.tramtau.Vados.ratiItemHuyDiet;
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
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Chill2 extends FutureBoss {

    public Chill2() {
        super(BossFactory.CHILL2, BossData.CHILL2);
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

        textTalkAfter = new String[]{"Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha"};
    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.CHILL).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossManager.gI().removeBoss(this);
    }

}

