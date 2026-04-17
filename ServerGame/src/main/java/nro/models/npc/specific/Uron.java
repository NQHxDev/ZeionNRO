package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;

public class Uron extends Npc {

   public Uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player pl) {
      if (canOpenNpc(pl)) {
         this.openShopWithGender2(pl, ConstNpc.SHOP_URON_0, 0);
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         // Uron shop handled by openShopWithGender2
      }
   }
}
