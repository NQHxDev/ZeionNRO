package nro.models.boss.BossMoiLam;

import nro.consts.ConstRatio;
import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.player.Player;
import nro.utils.Util;

import nro.models.boss.BossManager;
import nro.services.SkillService;
import nro.utils.SkillUtil;

public class Bido extends Boss {

   public Bido() {
      super(BossFactory.BIDO, BossData.BIDO);
   }

   @Override
   protected boolean useSpecialSkill() {
      this.playerSkill.skillSelect = this.getSkillSpecial();
      if (SkillService.gI().canUseSkillWithCooldown(this)) {
         SkillService.gI().useSkill(this, null, null, null);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void attack() {
      try {
         Player pl = getPlayerAttack();
         if (pl != null) {
            if (!useSpecialSkill()) {
               this.playerSkill.skillSelect = this.getSkillAttack();
               if (Util.getDistance(this, pl) <= this.getRangeCanAttackWithSkillSelect()) {
                  if (Util.isTrue(15, ConstRatio.PER100) && SkillUtil.isUseSkillChuong(this)) {
                     goToXY(pl.location.x + (Util.getOne(-1, 1) * Util.nextInt(20, 80)),
                           Util.nextInt(10) % 2 == 0 ? pl.location.y : pl.location.y - Util.nextInt(0, 50), false);
                  }
                  SkillService.gI().useSkill(this, pl, null, null);
                  checkPlayerDie(pl);
               } else {
                  goToPlayer(pl, false);
               }
            }
         }
      } catch (Exception ex) {
      }
   }

   @Override
   public void idle() {

   }

   @Override
   public void rewards(Player pl) {
      super.tileRoiCT_SuPhu_RandomChiSo(pl, 426, 5, 20, 30, 20, 30, 10, 50, 1, 2);
      super.itemDropCoTile(pl, 1187, 1, 1);
      super.DoXungQuanh(pl, 457, Util.nextInt(5, 20), 1);
      super.DoXungQuanh(pl, 16, 1, 1);
   }

   @Override
   public void checkPlayerDie(Player pl) {
   }

   @Override
   public void initTalk() {
      this.textTalkMidle = new String[] { "Sống trên đời..", "Không làm mà đòi có ăn...",
            "Chỉ có ăn cái đb... ăn cớt", "Cần cù thì bù siêng năng..." };

   }

   @Override
   public void leaveMap() {
      BossFactory.createBoss(BossFactory.BIDO).setJustRest();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }
}
