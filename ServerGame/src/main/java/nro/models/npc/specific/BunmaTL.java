package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.TaskService;
import nro.services.func.ShopService;

public class BunmaTL extends Npc {

   public BunmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?",
                  "Cửa hàng", "Đóng");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
               ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0,
                     player.gender);
            }
         }
      }
   }
}
