package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

public class Kaido extends Npc {

   public Kaido(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Vô địch chán voãi ò...!!!"
                     + "Lại đây để tao gõ mày một cái nào",
               "Đến\nVar Kaido", "Giải tán\nbang", "Danh hiệu đệ\n 49k H.Ngọc",
               "Danh hiệu đệ\n 199k H.Ngọc");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  if (player.nPoint.dame < 100000000) {
                     Service.getInstance().sendThongBao(player,
                           "|7|Sức đánh tối thiểu 100 triệu mới đủ tuổi");
                     return;
                  } else {
                     ChangeMapService.gI().changeMapBySpaceShip(player, 218, -1, -1);
                  }
                  break;
               case 2:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     if (player.inventory.ruby < 49999) {
                        Service.getInstance().sendThongBao(player, "Không đủ Hồng Ngọc");
                     } else {
                        player.inventory.ruby -= 49999;
                        Item daithan = ItemService.gI().createNewItem((short) 1325);
                        daithan.itemOptions.add(new ItemOption(50, Util.nextInt(10, 33)));
                        daithan.itemOptions.add(new ItemOption(77, Util.nextInt(10, 39)));
                        daithan.itemOptions.add(new ItemOption(103, Util.nextInt(10, 39)));
                        daithan.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
                        daithan.itemOptions.add(new ItemOption(93, Util.nextInt(1, 3)));
                        InventoryService.gI().addItemBag(player, daithan, 1);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được Danh Hiệu Đại Thần");
                     }
                  } else {
                     this.npcChat(player, "Hành trang không đủ chổ trống");
                  }
                  break;
               case 3:
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     if (player.inventory.ruby < 199999) {
                        Service.getInstance().sendThongBao(player, "Không đủ Hồng Ngọc");
                     } else {
                        player.inventory.ruby -= 199999;
                        Item thientu = ItemService.gI().createNewItem((short) 1326);
                        thientu.itemOptions.add(new ItemOption(50, Util.nextInt(20, 55)));
                        thientu.itemOptions.add(new ItemOption(77, Util.nextInt(20, 59)));
                        thientu.itemOptions.add(new ItemOption(103, Util.nextInt(20, 59)));
                        thientu.itemOptions.add(new ItemOption(101, Util.nextInt(100, 345)));
                        thientu.itemOptions.add(new ItemOption(93, Util.nextInt(1, 5)));
                        InventoryService.gI().addItemBag(player, thientu, 1);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được Danh Hiệu Thiên Tử");
                     }
                  } else {
                     this.npcChat(player, "Hành trang không đủ chổ trống");
                  }
                  break;
            }
         }
      }
   }
}
