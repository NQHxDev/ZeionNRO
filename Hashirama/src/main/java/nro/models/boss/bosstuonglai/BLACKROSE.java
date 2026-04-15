package nro.models.boss.bosstuonglai;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import nro.consts.ConstItem;
import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.item.ItemOption;
import nro.models.map.ItemMap;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.RewardService;
import nro.services.Service;
import nro.utils.Util;

import nro.models.boss.BossManager;
import static nro.models.boss.fide.FideGold.ratiItemHuyDiet;
import static nro.models.boss.tramtau.Vados.ratiItemHuyDiet;
import nro.models.map.Zone;
import nro.services.TaskService;

/**
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 */
public class BLACKROSE extends Boss {

    public BLACKROSE() {
        super(BossFactory.SUPER_BLACK_ROSE, BossData.SUPER_BLACK_ROSE);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 4);
        super.itemDropCoTile(pl, 874, 1, 30);
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
        this.textTalkMidle = new String[]{"Oải rồi hả?", "Ê cố lên nhóc",
            "Chán", "Ta có nhầm không nhỉ"};

    }

    @Override
    public void leaveMap() {
        BossFactory.createBoss(BossFactory.SUPER_BLACK_ROSE).setJustRest();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
    }

}
