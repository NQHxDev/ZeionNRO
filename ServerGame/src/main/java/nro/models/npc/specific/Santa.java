package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ShopService;

public class Santa extends Npc {

   public Santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         createOtherMenu(player, ConstNpc.BASE_MENU,
               "Xem con mẹ gì, có tiền không mà xem ?",
               "Shop\n Tạp Hóa", "Shop\n Phụ Kiện", "Item\n Hỗ Trợ", "Phụ Kiện\nĐệ Tử",
               "Shop\n Sự Kiện");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0: // shop vàng ngọc
                     ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_SANTA_0, 0, -1);
                     break;
                  case 1: // shop thỏi vàng
                     ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_1, 1, -1);
                     break;
                  case 2:// shop quy đổi
                     ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_5, 5, -1);
                     break;
                  case 3:// shop vat pham
                     ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_3, 3, -1);
                     break;
                  case 4: // Shop Vật phẩm
                     ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_SANTA_4, 4, -1);
                     break;
               }
            }
         }
      }
   }
}
