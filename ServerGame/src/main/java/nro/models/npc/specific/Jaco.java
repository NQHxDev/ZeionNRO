package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ChangeMapService;

public class Jaco extends Npc {

   public Jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 0) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|7| KHU VỰC BOSS NHÂN BẢN"
                        + "\n\n|6|Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu"
                        + "\nĐánh bại những kẻ giả mạo ngươi sẽ nhận được những phần thưởng hấp dẫn"
                        + "\n|3|Hạ Boss Nhân Bản sẽ nhận được Item Siêu cấp"
                        + "\n|2|Hãy đến đó ngay",
                  "Đến \nPotaufeu");
         } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Người muốn trở về?", "Quay về", "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 0) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  ChangeMapService.gI().goToPotaufeu(player);
               }
            }
         } else {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                     break;
               }
            }
         }
      }
   }
}
