package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.ShopService;

public class Toribot extends Npc {
   public Toribot(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      this.createOtherMenu(player, ConstNpc.BASE_MENU,
            "Chào mừng bạn đến với cửa hàng đá qúy số 1 thời đại", "Cửa Hàng");
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_TORIBOT, 0, -1);
      }
   }
}
