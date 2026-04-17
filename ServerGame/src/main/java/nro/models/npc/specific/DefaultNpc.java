package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;

public class DefaultNpc extends Npc {
   public DefaultNpc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void confirmMenu(Player player, int select) {
      // Mặc định không làm gì hoặc xử lý chung
   }
}
