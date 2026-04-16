package nro.models.boss.tramtau;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.utils.Util;

public class Vados extends Boss {

   public Vados() {
      super(BossFactory.VADOS, BossData.VADOS);
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   // @Override
   // public void joinMap() {
   // super.joinMap();
   // BossFactory.createBoss(BossFactory.CHAMPA).zone = this.zone;
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
   // BossManager.gI().getBossById(BossFactory.CHAMPA).changeToAttack();
   // super.leaveMap();
   // BossManager.gI().removeBoss(this);
   // }
   @Override
   public void leaveMap() {
      Boss champa = BossFactory.createBoss(BossFactory.CHAMPA);
      champa.zone = this.zone;
      champa.location.x = this.location.x;
      champa.location.y = this.location.y;
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }

   @Override
   public void checkPlayerDie(Player pl) {

   }

   @Override
   public void initTalk() {

   }

}
