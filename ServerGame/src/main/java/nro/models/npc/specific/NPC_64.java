package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.Service;

public class NPC_64 extends Npc {

   public NPC_64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         createOtherMenu(player, ConstNpc.BASE_MENU, "Ngươi muốn xem thông tin gì?",
               "Top\nsức mạnh", "Đóng");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
               Service.getInstance().showTopPower(player, Service.getInstance().TOP_SUCMANH);
            }
         }
      }
   }
}
