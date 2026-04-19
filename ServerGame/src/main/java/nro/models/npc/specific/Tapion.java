package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.map.SantaCity;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.MapService;
import nro.services.Service;

public class Tapion extends Npc {
   public Tapion(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 19) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ác quỷ truyền thuyết Hirudegarn\nđã thoát khỏi phong ấn ngàn năm\nHãy giúp tôi chế ngự nó",
                  "OK", "Từ chối");
         }
         if (this.mapId == 126) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Tôi sẽ đưa bạn về", "OK",
                  "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 19) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                     if (santaCity != null) {
                        if (!santaCity.isOpened || santaCity.isClosed) {
                           Service.getInstance().sendThongBao(player,
                                 "Hẹn gặp bạn lúc 22h mỗi ngày");
                           return;
                        }
                        santaCity.enter(player);
                     } else {
                        Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                     }
                     break;
               }
            }
         }
         if (this.mapId == 126) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     SantaCity santaCity = (SantaCity) MapService.gI().getMapById(126);
                     if (santaCity != null) {
                        santaCity.leave(player);
                     } else {
                        Service.getInstance().sendThongBao(player, "Có lỗi xảy ra!");
                     }
                     break;
               }
            }
         }
      }
   }
}
