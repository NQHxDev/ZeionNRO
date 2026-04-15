package nro.models.boss.NgucTu;

import nro.models.boss.*;
import nro.models.player.Player;
import nro.utils.Util;

/**
 * @author 💖 Nothing 💖
 */
public class SuperCumber extends FutureBoss {

    public SuperCumber() {
        super(BossFactory.CUMBER2, BossData.CUMBER2);
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
        textTalkAfter = new String[]{"Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha"};
    }

    @Override
    public void leaveMap() {
        super.leaveMap();
        BossManager.gI().removeBoss(this);
        BossFactory.createBoss(BossFactory.CUMBER).setJustRest();
    }
}
