package nro.models.boss.bill;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import static nro.models.boss.bill.Whis.ratiItemHuyDiet;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.map.Zone;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class Bill extends Boss {

    public Bill() {
        super(BossFactory.BILL, BossData.BILL);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 457, Util.nextInt(20, 50), 1);
        super.tileRoiDoHD(pl,Util.nextInt(650, 662), 10, 15, 5);
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

    @Override
    public void leaveMap() {
//        System.out.println(BossFactory.WHIS);
//        BossManager.gI().getBossById(BossFactory.WHIS).setJustRest();
//        super.leaveMap();
//        BossManager.gI().getBossById(BossFactory.WHIS).setJustRest();
        BossFactory.createBoss(BossFactory.WHIS);
//        whis.zone = this.zone;
        this.setJustRestToFuture();
        super.leaveMap();
        BossManager.gI().removeBoss(this);

    }
}
