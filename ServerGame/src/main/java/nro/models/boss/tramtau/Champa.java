package nro.models.boss.tramtau;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.utils.Util;

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

   }

   @Override
   public void leaveMap() {
      BossFactory.createBoss(BossFactory.VADOS);
      this.setJustRestToFuture();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }
}
