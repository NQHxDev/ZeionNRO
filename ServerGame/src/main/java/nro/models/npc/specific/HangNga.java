package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;

public class HangNga extends Npc {

   public HangNga(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         createOtherMenu(player, ConstNpc.BASE_MENU,
               "|7|SHOP VIP\n"
                     + "Cải trang, phụ kiện chỉ số cực cao",
               "SHOP\nVIP");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 5) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  ShopService.gI().openShopSpecial(player, this, ConstNpc.HANG_NGA_SHOP, 0, -1);
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
               switch (player.combineNew.typeCombine) {
                  case CombineServiceNew.NANG_SKH_THANH_TON:
                     if (select == 0) {
                        CombineServiceNew.gI().startCombine(player);
                     }
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_THAN_LINH) {
               if (select == 0) {
                  CombineServiceNew.gI().startCombine(player);
               }
            }
         }
      }
   }
}
