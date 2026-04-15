package nro.models.boss.NgucTu;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.utils.Util;

/**
 * @author 💖 Nothing 💖
 */
public class Cumber extends FutureBoss {

    public Cumber() {
        super(BossFactory.CUMBER, BossData.CUMBER);
    }

    @Override
    protected boolean useSpecialSkill() {
        return false;
    }

    @Override
    public void rewards(Player pl) {
        super.DoXungQuanh(pl, 457, Util.nextInt(1, 50), 2);// 1- 500 thỏi vàng
        super.itemDropCoTile(pl, Util.nextInt(1232, 1234), Util.nextInt(1, 10), 50);
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
    }

    @Override
    public void leaveMap() {
        this.setJustRestToFuture();
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        Boss cumber2 = BossFactory.createBoss(BossFactory.CUMBER2);
        cumber2.zone = this.zone;
    }

}
