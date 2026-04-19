package nro.models.boss.chill;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.utils.Util;

public class Chill extends FutureBoss {

   public Chill() {
      super(BossFactory.CHILL, BossData.CHILL);
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
      textTalkMidle = new String[] { "Ta chính là đệ nhất vũ trụ cao thủ" };
      textTalkAfter = new String[] { "Ác quỷ biến hình aaa..." };
   }

   @Override
   public void leaveMap() {
      Boss chill2 = BossFactory.createBoss(BossFactory.CHILL2);
      chill2.zone = this.zone;
      this.setJustRestToFuture();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }

}
