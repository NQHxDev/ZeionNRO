package nro.models.boss.bosstuonglai;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;
import nro.utils.Util;

import nro.models.boss.BossManager;

public class BLACKROSE extends Boss {

   public BLACKROSE() {
      super(BossFactory.SUPER_BLACK_ROSE, BossData.SUPER_BLACK_ROSE);
   }

   @Override
   protected boolean useSpecialSkill() {
      return false;
   }

   @Override
   public void rewards(Player pl) {
      super.DoXungQuanh(pl, 1567, Util.nextInt(1, 5), 4);
      super.itemDropCoTile(pl, 874, 1, 30);
      super.tileRoiDoThanLinh(pl, 10, 10, 5);
   }

   @Override
   public void idle() {

   }

   @Override
   public void checkPlayerDie(Player pl) {

   }

   @Override
   public void initTalk() {
      this.textTalkMidle = new String[] { "Oải rồi hả?", "Ê cố lên nhóc",
            "Chán", "Ta có nhầm không nhỉ" };

   }

   @Override
   public void leaveMap() {
      BossFactory.createBoss(BossFactory.SUPER_BLACK_ROSE).setJustRest();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }

}
