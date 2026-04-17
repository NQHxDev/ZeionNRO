package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;

public class Babiday extends Npc {

   public Babiday(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      // Babiday legacy logic was mostly empty or handled via other services
      super.openBaseMenu(player);
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         // Babiday legacy confirm logic was empty in the verified block
      }
   }
}
