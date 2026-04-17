package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.func.ChangeMapService;

public class GiumaDauBo extends Npc {

   public GiumaDauBo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 6 || this.mapId == 25 || this.mapId == 26) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay",
                  "Đến \nPotaufeu");
         } else if (this.mapId == 139) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Người muốn trở về?", "Quay về", "Từ chối");
         } // lãnh địa bang
         else if (this.mapId == 153) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Theo ta, ta sẽ đưa ngươi đến Khu vực Thánh địa\nNơi đây ngươi sẽ truy tìm mảnh bông tai cấp 2 và Hồn bông tai để mở chỉ số Bông tai Cấp 3."
                        + "\n|7|Ngươi có muốn đến đó không?",
                  "Đến\nThánh địa", "Từ chối");
         } else if (this.mapId == 156) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Người muốn trở về?", "Quay về", "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  // đến potaufeu
                  ChangeMapService.gI().goToPotaufeu(player);
               }
            }
         } else if (this.mapId == 139) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  // về trạm vũ trụ
                  case 0:
                     ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                     break;
               }
            }
         } else if (this.mapId == 153) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  // lãnh địa bang
                  case 0:
                     ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, -1);
                     break;
               }
            }
         } else if (this.mapId == 156) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  // về trạm vũ trụ
                  case 0:
                     ChangeMapService.gI().changeMapBySpaceShip(player, 21 + player.gender, -1, -1);
                     break;
               }
            }
         }
      }
   }
}
