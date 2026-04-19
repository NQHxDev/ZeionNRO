package nro.models.boss.robotsatthu;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.PlayerService;
import nro.services.TaskService;
import nro.utils.Util;

public class Android20 extends Boss {

   public Android20() {
      super(BossFactory.ANDROID_20, BossData.ANDROID_20);
   }

   @Override
   public void joinMap() {
      super.joinMap();
      BossFactory.createBoss(BossFactory.ANDROID_19).zone = this.zone;
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   @Override
   public void rewards(Player pl) {
      TaskService.gI().checkDoneTaskKillBoss(pl, this);// check nhiệm vụ
      super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 2);
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
   public double injured(Player plAtt, double damage, boolean piercing, boolean isMobAttack) {
      if (plAtt != null) {
         switch (plAtt.playerSkill.skillSelect.template.id) {
            case Skill.KAMEJOKO:
            case Skill.MASENKO:
            case Skill.ANTOMIC:
               PlayerService.gI().hoiPhuc(this, damage, 0);
               return 0;
         }
      }
      return super.injured(plAtt, damage, piercing, isMobAttack);
   }

}
