package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.TopService;

public class TopRanking extends Npc {
   public TopRanking(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player pl) {
      if (canOpenNpc(pl)) {
         this.createOtherMenu(pl, ConstNpc.BASE_MENU,
               "|8|Bạn muốn xem bảng xếp hạng nào?", "Top\nSức Đánh", "Top\nHp", "Top\nKi",
               "Top\nSức Mạnh", "Top\nNhiệm Vụ", "Top\nNạp");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  TopService.gI().showTopSd(player);
                  break;
               case 1:
                  TopService.gI().showTopHP(player);
                  break;
               case 2:
                  TopService.gI().showTopKi(player);
                  break;
               case 3:
                  TopService.gI().showTopSucManh(player);
                  break;
               case 4:
                  TopService.gI().showTopNhiemVu(player);
                  break;
               case 5:
                  TopService.gI().showTopVnd(player);
                  break;
            }
         }
      }
   }
}
