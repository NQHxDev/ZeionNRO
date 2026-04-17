package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.Service;
import nro.services.func.ShopService;

public class MrPopo extends Npc {
   public MrPopo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 0) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ta là người đang giữ rương quà cho ngươi, nếu có bất kì món quà nào hãy tới gặp ta để nhận."
                        + "\n Nhớ nhận ngay để không bị mất khi có quà mới nhé!",
                  "Rương\nQuà tặng", "Bảng\n xếp hạng", "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 0) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ShopService.gI().openBoxItemReward(player);
                     break;
                  case 1:
                     Service.getInstance().showTopPower(player, Service.getInstance().TOP_SUCMANH);
                     break;
               }
            }
         }
      }
   }
}
