package nro.models.boss.robotsatthu;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.player.Player;
import nro.services.TaskService;
import nro.utils.Util;

public class Poc extends Boss {

   public Poc() {
      super(BossFactory.POC, BossData.POC);
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   @Override
   public void rewards(Player pl) {
      TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
      super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 2);
      super.itemDropCoTile(pl, Util.nextInt(16, 18), 1, 20);
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
      super.leaveMap();
      BossManager.gI().removeBoss(this);
      BossManager.gI().getBossById(BossFactory.PIC).changeToAttack();
   }

}
