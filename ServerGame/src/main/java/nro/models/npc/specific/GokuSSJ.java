package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ChangeMapService;

public class GokuSSJ extends Npc {

   public GokuSSJ(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 80) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart",
                  "Từ chối");
         } else if (this.mapId == 131) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
         } else {
            super.openBaseMenu(player);
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.BASE_MENU:
               if (this.mapId == 80) {
                  if (select == 0) {
                     ChangeMapService.gI().changeMapBySpaceShip(player, 131, -1, 940);
                  }
               } else if (this.mapId == 131) {
                  if (select == 0) {
                     ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                  }
               }
               break;
         }
      }
   }
}
