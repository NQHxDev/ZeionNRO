package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.services.TaskService;

public class Appule extends Npc {

   public Appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ta có bán một số thứ mà có thế ngươi cũng del cần" + "\nMua thì mua ko mua thì mua",
                  "Cửa\nhàng");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:// Shop
                  if (player.gender == ConstPlayer.XAYDA) {
                     this.openShopWithGender(player, ConstNpc.SHOP_APPULE_0, 0);
                  } else {
                     this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                           "Ta có bán một số thứ mà có thế ngươi cũng del cần"
                                 + "\nNhưng ta ko thích bán cho ngươi đó",
                           "Đóng");
                  }
                  break;
            }
         }
      }
   }
}
