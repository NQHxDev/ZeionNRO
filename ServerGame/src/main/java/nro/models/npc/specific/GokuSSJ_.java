package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

public class GokuSSJ_ extends Npc {

   public GokuSSJ_(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 133) {
            Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
            int soLuong = (biKiep != null) ? biKiep.quantity : 0;

            if (soLuong >= 9999) {
               this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mày có " + soLuong
                     + " bí kiếp.\n"
                     + "Hãy kiếm đủ 9999 bí kiếp tao sẽ đổi cho mày cải trang yadart",
                     "Đổi", "Đóng");
            } else {
               this.createOtherMenu(player, ConstNpc.BASE_MENU, "Mày có " + soLuong
                     + " bí kiếp.\n"
                     + "Hãy kiếm đủ 9999 bí kíp rồi tìm tao\n"
                     + "Còn giờ thì cook =))",
                     "Đóng");
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 133 && player.iDMark.isBaseMenu()) {
            if (select == 0) {
               handleDoiCaitrangYadart(player);
            }
         }
      }
   }

   private void handleDoiCaitrangYadart(Player player) {
      Item biKiep = InventoryService.gI().findItem(player.inventory.itemsBag, 590);
      int soLuong = (biKiep != null) ? biKiep.quantity : 0;

      if (soLuong >= 9999 && InventoryService.gI().getCountEmptyBag(player) > 0) {
         Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
         yardart.itemOptions.add(new ItemOption(50, 100));
         yardart.itemOptions.add(new ItemOption(77, 120));
         yardart.itemOptions.add(new ItemOption(103, 120));
         yardart.itemOptions.add(new ItemOption(101, 1000));
         yardart.itemOptions.add(new ItemOption(108, 10));
         yardart.itemOptions.add(new ItemOption(33, 1));
         yardart.itemOptions.add(new ItemOption(30, 1));

         InventoryService.gI().addItemBag(player, yardart, 0);
         InventoryService.gI().subQuantityItemsBag(player, biKiep, 9999);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
      } else if (soLuong < 9999) {
         Service.getInstance().sendThongBao(player, "Không đủ bí kiếp");
      } else if (InventoryService.gI().getCountEmptyBag(player) == 0) {
         Service.getInstance().sendThongBao(player, "Hành trang đầy rồi");
      }
   }
}
