package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;

public class Calick extends Npc {

   public Calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
      if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
         Service.getInstance().hideWaitDialog(player);
         Service.getInstance().sendThongBao(player, "Không thể thực hiện");
         return;
      }
      if (this.mapId != player.zone.map.mapId) {
         Service.getInstance().sendThongBao(player, "Calích đã rời khỏi map!");
         Service.getInstance().hideWaitDialog(player);
         return;
      }

      if (this.mapId == 102) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
               "Kể\nChuyện", "Quay về\nQuá khứ");
      } else {
         this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào chú, cháu có thể giúp gì?",
               "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (this.mapId == 102) {
         if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
               // kể chuyện
               NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
            } else if (select == 1) {
               // về quá khứ
               ChangeMapService.gI().goToQuaKhu(player);
            }
         }
      } else if (player.iDMark.isBaseMenu()) {
         if (select == 0) {
            // kể chuyện
            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
         } else if (select == 1) {
            // đến tương lai
            if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
               ChangeMapService.gI().goToTuongLai(player);
            }
         } else {
            Service.getInstance().sendThongBao(player, "Không thể thực hiện");
         }
      }
   }
}
