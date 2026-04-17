package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.TaskService;

public class Berry extends Npc {

   public Berry(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi có thể giúp gì cho bạn??", "Đóng");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  break;
            }
         }
      }
   }
}
