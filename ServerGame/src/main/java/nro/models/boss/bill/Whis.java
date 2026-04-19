package nro.models.boss.bill;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.utils.Util;

public class Whis extends Boss {

   public Whis() {
      super(BossFactory.WHIS, BossData.WHIS);
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   // @Override
   // public void joinMap() {
   // super.joinMap();
   // BossFactory.createBoss(BossFactory.BILL).zone = this.zone;
   // }

   @Override
   public void rewards(Player pl) {
      super.DoXungQuanh(pl, 457, Util.nextInt(20, 50), 1);
      super.tileRoiDoHD(pl, Util.nextInt(650, 662), 10, 15, 5);
      super.itemDropCoTile(pl, 1083, 1, 10);
   }

   @Override
   public void idle() {

   }

   // @Override
   // public void leaveMap() {
   // BossManager.gI().getBossById(BossFactory.BILL).changeToAttack();
   // super.leaveMap();
   // BossManager.gI().removeBoss(this);
   // }
   @Override
   public void leaveMap() {
      Boss bill = BossFactory.createBoss(BossFactory.BILL);
      bill.zone = this.zone;
      bill.location.x = this.location.x;
      bill.location.y = this.location.y;
      super.leaveMap();
      BossManager.gI().removeBoss(this);
      this.setJustRestToFuture();
   }

   @Override
   public void checkPlayerDie(Player pl) {

   }

   @Override
   public void initTalk() {

   }

}
