package nro.models.boss.fide;

import nro.models.boss.BossData;
import nro.models.boss.BossFactory;
import nro.models.boss.BossManager;
import nro.models.boss.FutureBoss;
import nro.models.player.Player;
import nro.server.ServerNotify;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;

public class FideDaiCa3 extends FutureBoss {

   public FideDaiCa3() {
      super(BossFactory.FIDE_DAI_CA_3, BossData.FIDE_DAI_CA_3);
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
      this.textTalkMidle = new String[] { "Xem bản lĩnh của ngươi như nào đã", "Các ngươi tới số mới gặp phải ta" };

   }

   @Override
   public void leaveMap() {
      BossFactory.createBoss(BossFactory.FIDE_DAI_CA_1).setJustRest();
      super.leaveMap();
      BossManager.gI().removeBoss(this);
   }

   @Override
   public void joinMap() {
      if (this.zone != null) {
         ChangeMapService.gI().changeMap(this, zone, this.location.x, this.location.y);
         ServerNotify.gI().notify("Boss " + this.name + " vừa xuất hiện tại " + this.zone.map.mapName);
      }
   }
}
