package nro.models.boss.chill;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.utils.Util;

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
      super.tileRoiDoHD(pl, Util.nextInt(650, 662), 10, 15, 5);
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

      textTalkAfter = new String[] { "Ta đã giấu hết ngọc rồng rồi, các ngươi tìm vô ích hahaha" };
   }

   @Override
   public void leaveMap() {
      BossFactory.createBoss(BossFactory.CHILL).setJustRest();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
      BossManager.gI().removeBoss(this);
   }

}
