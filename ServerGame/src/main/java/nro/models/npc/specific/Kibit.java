package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ChangeMapService;

public class Kibit extends Npc {

   public Kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 50) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                  "Đến\nKaio", "Từ chối");
         } else {
            super.openBaseMenu(player);
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 50) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                     break;
               }
            }
         }
      }
   }
}
