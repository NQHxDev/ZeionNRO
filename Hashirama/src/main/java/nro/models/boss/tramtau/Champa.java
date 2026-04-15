package nro.models.boss.tramtau;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import static nro.models.boss.tramtau.Vados.ratiItemHuyDiet;
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
public class Champa extends Boss {

    public Champa() {
        super(BossFactory.CHAMPA, BossData.CHAMPA);
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

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.VADOS);
        this.setJustRestToFuture();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }
}
