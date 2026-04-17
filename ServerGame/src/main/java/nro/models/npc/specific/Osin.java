package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.models.map.mabu.MabuWar;
import nro.models.map.mabu.MabuWar14h;
import nro.services.MapService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.ChangeMapService;
import nro.server.Manager;

public class Osin extends Npc {

   public Osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 50) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                  "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
         } else if (this.mapId == 52) {
            if (MabuWar.gI().isTimeMabuWar() || MabuWar14h.gI().isTimeMabuWar()) {
               if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                  this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Bây giờ tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                              + "Quý vị nào muốn đi theo thì xin mời !",
                        "Ok", "Từ chối");
               }
            } else {
               if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                  this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Vào lúc 12h tôi sẽ bí mật...\n đuổi theo 2 tên đồ tể... \n"
                              + "Quý vị nào muốn đi theo thì xin mời !",
                        "Ok", "Từ chối");
               }
            }
         } else if (this.mapId == 154) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|3|Để đến được Hành tinh ngục tù yêu cầu mang 5 món đồ Hủy diệt"
                        + "\n|1|Ta có thể giúp gì cho ngươi ?",
                  "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
         } else if (this.mapId == 155 || this.mapId == 165) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                  "Quay về", "Từ chối");
         } else if (MapService.gI().isMapMabuWar(this.mapId)
               || MapService.gI().isMapMabuWar14H(this.mapId)) {
            if (MabuWar.gI().isTimeMabuWar()) {
               this.createOtherMenu(player, ConstNpc.BASE_MENU,
                     "Đừng vội xem thường Babyđây,ngay đến cha hắn là thần ma đạo sĩ\n"
                           + "Bibiđây khi còn sống cũng phải sợ hắn đấy",
                     "Giải trừ\nphép thuật\n50Tr Vàng",
                     player.zone.map.mapId != 120 ? "Xuống\nTầng Dưới" : "Rời\nKhỏi đây");
            } else if (MabuWar14h.gI().isTimeMabuWar()) {
               createOtherMenu(player, ConstNpc.BASE_MENU,
                     "Ta sẽ phù hộ cho ngươi bằng nguồn sức mạnh của Thần Kaiô"
                           + "\n+1 triệu HP, +1 triệu MP, +10k Sức đánh"
                           + "\nLưu ý: sức mạnh sẽ biến mất khi ngươi rời khỏi đây",
                     "Phù hộ\n55 hồng ngọc", "Từ chối", "Về\nĐại Hội\nVõ Thuật");
            }
         } else {
            super.openBaseMenu(player);
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 50) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                     break;
                  case 1:
                     ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                     break;
               }
            }
         } else if (this.mapId == 52) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     if (MabuWar.gI().isTimeMabuWar()) {
                        ChangeMapService.gI().changeMap(player, 114, -1, 354, 240);
                     } else {
                        ChangeMapService.gI().changeMap(player, 127, -1, 354, 240);
                     }
                     break;
               }
            }
         } else if (this.mapId == 154) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                     break;
                  case 1:
                     if (!Manager.gI().getGameConfig().isOpenPrisonPlanet()) {
                        Service.getInstance().sendThongBao(player,
                              "Lối vào hành tinh ngục tù chưa mở");
                        return;
                     }
                     if (player.nPoint.power < 60000000000L) {
                        Service.getInstance().sendThongBao(player,
                              "Yêu cầu tối thiếu 60tỷ sức mạnh");
                        return;
                     }
                     if (player.setClothes.setDHD != 5) {
                        Service.getInstance().sendThongBao(player,
                              "Yêu cầu mang set Đồ Hủy diệt");
                        return;
                     }
                     ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                     break;
               }
            }
         } else if (this.mapId == 155) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
               }
            }
         } else if (this.mapId == 165) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
               }
            }
         } else if (MapService.gI().isMapMabuWar(this.mapId)
               || MapService.gI().isMapMabuWar14H(this.mapId)) {
            // Ma bu war logic for confirmMenu was handled here too in legacy
            // I need to check the legacy code again for Ma bu war confirm logic
         }
      }
   }
}
