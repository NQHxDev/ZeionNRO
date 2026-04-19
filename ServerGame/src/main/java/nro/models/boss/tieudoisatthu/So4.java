package nro.models.boss.tieudoisatthu;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.services.TaskService;

public class So4 extends FutureBoss {

   public So4() {
      super(BossFactory.SO4, BossData.SO4);
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
      this.textTalkBefore = new String[] { "Thầy huấn đã bảo rồi..." };
      this.textTalkMidle = new String[] { "Sống trên đời..", "Không làm mà đòi có ăn...",
            "Chỉ có ăn cái đb... ăn cớt", "Cần cù thì bù siêng năng..." };
   }

   @Override
   public void leaveMap() {
      BossManager.gI().getBossById(BossFactory.SO3).changeToAttack();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }
}
