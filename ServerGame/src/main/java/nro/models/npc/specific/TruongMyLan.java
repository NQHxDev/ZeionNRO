package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.CombineServiceNew;

public class TruongMyLan extends Npc {

   public TruongMyLan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Ngươi tìm ta có việc gì?",
               "Gia Hạn\nvật phẩm",
               "Nhận\n Chân mệnh");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.GIA_HAN_VAT_PHAM);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
            switch (player.combineNew.typeCombine) {
               case CombineServiceNew.GIA_HAN_VAT_PHAM:
                  CombineServiceNew.gI().startCombine(player);
                  break;
            }
         }
      }
   }
}
