package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.Service;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;

public class ToSuKaio extends Npc {

   public ToSuKaio(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         Service.getInstance().sendThongBaoOK(player, "Chức năng tạm chưa mở");
         return;
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_KIM_DAN);
                  break;
               case 1:
                  CombineServiceNew.gI().openTabCombine(player,
                        CombineServiceNew.TANG_PHAM_CHAT_KIM_DAN);
                  break;
               case 2:
                  ShopService.gI().openShopSpecial(player, this, ConstNpc.TRUNG_THU_SHOP, 0, -1);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
            switch (player.combineNew.typeCombine) {
               case CombineServiceNew.NANG_CAP_KIM_DAN:
               case CombineServiceNew.TANG_PHAM_CHAT_KIM_DAN:
                  CombineServiceNew.gI().startCombine(player);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_KIM_DAN) {
            if (select == 0) {
               CombineServiceNew.gI().startCombine(player);
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TANG_PHAM_CHAT_KIM_DAN) {
            if (select == 0) {
               CombineServiceNew.gI().startCombine(player);
            }
         }
      }
   }
}
