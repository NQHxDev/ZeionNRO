package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.CombineServiceNew;

public class LongNu extends Npc {

   public LongNu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Long Đế xuất hiện, Giết hắn để"
                     + "\n Nhận được các mảnh long ấn"
                     + "\n Và có cơ hội kí khế ước với hắn",
               "Kích hoạt\nẤn", "Kích hoạt\n Long Ấn");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 5 || this.mapId == 13) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.AN_TRANG_BI);
                     break;
                  case 1:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.LONG_AN_TRANG_BI);
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
               switch (player.combineNew.typeCombine) {
                  case CombineServiceNew.AN_TRANG_BI:
                  case CombineServiceNew.LONG_AN_TRANG_BI:
                     CombineServiceNew.gI().startCombine(player);
                     break;
               }
            }
         }
      }
   }
}
