package nro.models.boss.nappa;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;

public class Kuku extends FutureBoss {

   public Kuku() {
      super(BossFactory.KUKU, BossData.KUKU);
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   @Override
   public void rewards(Player pl) {
      TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
   }

   @Override
   public void idle() {
   }

   @Override
   public void checkPlayerDie(Player pl) {

   }

   @Override
   public void initTalk() {
      this.textTalkBefore = new String[] {};
      this.textTalkMidle = new String[] {};
      this.textTalkAfter = new String[] {};
   }

   @Override
   public void leaveMap() {
      ChangeMapService.gI().spaceShipArrive(this, (byte) 2, ChangeMapService.TENNIS_SPACE_SHIP);
      super.leaveMap();
   }

}
