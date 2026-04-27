package nro.models.boss.dhvt;

import nro.models.boss.Boss;
import nro.models.boss.BossData;
import nro.models.player.Player;
import nro.models.skill.Skill;

public class SieuHangBoss extends Boss {

   public Player challenger;
   public long opponentId;

   public SieuHangBoss(Player challenger, Player opponent) throws Exception {
      super(-(int) System.currentTimeMillis(), new BossData(
            opponent.name,
            opponent.gender,
            Boss.DAME_NORMAL,
            Boss.HP_NORMAL,
            (long) opponent.nPoint.dameg,
            new double[][] { { opponent.nPoint.hpMax } },
            new short[] { opponent.getHead(), opponent.getBody(), opponent.getLeg() },
            new short[] { 113 },
            new int[][] {
                  { Skill.DRAGON, 7, 1000 },
                  { Skill.KAMEJOKO, 7, 1000 },
                  { Skill.GALICK, 7, 1000 },
                  { Skill.ANTOMIC, 7, 1000 },
                  { Skill.DEMON, 7, 1000 },
                  { Skill.MASENKO, 7, 1000 },
                  { Skill.KAMEJOKO, 7, 1000 }
            },
            10000));
      this.challenger = challenger;
      this.opponentId = opponent.id;
      this.nPoint.dameg = opponent.nPoint.dameg;
      this.nPoint.defg = opponent.nPoint.defg;
      this.nPoint.critg = opponent.nPoint.critg;
      this.nPoint.hpMax = opponent.nPoint.hpMax;
      this.nPoint.hp = opponent.nPoint.hpMax;
   }

   @Override
   public void active() {
      super.active();
   }

   @Override
   public void joinMap() {
      super.joinMap();
   }

   @Override
   public void rewards(Player pl) {
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
   protected boolean useSpecialSkill() {
      return false;
   }

}
