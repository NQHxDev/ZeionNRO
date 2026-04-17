package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.services.Service;
import nro.utils.Util;

public class QuaTrung extends Npc {

   private final int COST_AP_TRUNG_NHANH = 2000000000;

   public QuaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         player.mabuEgg.sendMabuEgg();
         if (player.mabuEgg.getSecondDone() != 0) {
            this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG,
                  "Muốn triệu hồi ta sao, cần có 2 tỉ vàng...",
                  "Hủy bỏ\ntrứng",
                  "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + "  Vàng", "Đóng");
         } else {
            this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở",
                  "Hủy bỏ\ntrứng", "Đóng");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.CAN_NOT_OPEN_EGG:
               if (select == 0) {
                  this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                        "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
               } else if (select == 1) {
                  if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                     player.inventory.gold -= COST_AP_TRUNG_NHANH;
                     player.mabuEgg.timeDone = 0;
                     Service.getInstance().sendMoney(player);
                     player.mabuEgg.sendMabuEgg();
                  } else {
                     Service.getInstance().sendThongBao(player,
                           "Bạn không đủ  Vàng để thực hiện, còn thiếu "
                                 + Util.numberToMoney(
                                       (COST_AP_TRUNG_NHANH - player.inventory.gold))
                                 + "  Vàng");
                  }
               }
               break;
            case ConstNpc.CAN_OPEN_EGG:
               switch (select) {
                  case 0:
                     this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                           "Bạn có chắc chắn cho trứng nở?\n"
                                 + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                           "Đệ\nTrái Đất", "Đệ\nNamếc", "Đệ\nXayda",
                           "Từ chối");
                     break;
                  case 1:
                     this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                           "Bạn có chắc chắn muốn hủy bỏ trứng bí ẩn?", "Đồng ý",
                           "Từ chối");
                     break;
               }
               break;
            case ConstNpc.CONFIRM_OPEN_EGG:
               switch (select) {
                  case 0:
                     player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                     break;
                  case 1:
                     player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                     break;
                  case 2:
                     player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                     break;
               }
               break;
            case ConstNpc.CONFIRM_DESTROY_EGG:
               if (select == 0) {
                  player.mabuEgg.destroyEgg();
               }
               break;
         }
      }
   }
}
