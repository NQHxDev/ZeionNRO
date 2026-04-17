package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ChangeMapService;

public class TienHacAm extends Npc {

   public TienHacAm(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Lũ sâu kiến... !!!",
               "Đến\n Khiêu Chiến ");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 217, -1, -1);
                  break;
            }
         }
      }
   }
}
