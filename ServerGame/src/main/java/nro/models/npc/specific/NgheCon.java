package nro.models.npc.specific;

import nro.consts.ConstItem;
import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ShopService;
import nro.utils.Util;

public class NgheCon extends Npc {

   public NgheCon(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         createOtherMenu(player, ConstNpc.BASE_MENU,
               "|8|Ở đây có bán phụ kiện dành cho đệ tử!!!\n"
                     + "|3|Mình cũng có bán lại lễ bao tân thủ giá: 49k tv\n"
                     + "|2|Và bán phụng thiên kích (hạn dùng 7 ngày) giá:1999k tv\n"
                     + "|4|Gậy như ý FAKE giá:1999k tv\n"
                     + "|7|Đổi 100k thỏi vàng để nhận về 99k thỏi vàng khoá",
               "Phụ Kiện\nĐệ Tử", "Mua Lại\nLễ Bao Tân Thủ", "Mua\nPhụng Thiên Kích", "Mua\nGậy Fake",
               "Đổi 100k\n Thỏi Vàng Khoá");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0: // shop
                  ShopService.gI().openShopSpecial(player, this, ConstNpc.NGHE_CON_1, 0, -1);
                  break;
               case 1: // shop
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item thoivang = null;
                     try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                     } catch (Exception e) {
                     }
                     if (thoivang == null || thoivang.quantity < 49999) {
                        Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                        return;
                     } else {
                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 49999);
                        Item lebao = ItemService.gI().createNewItem((short) 1787);
                        InventoryService.gI().addItemBag(player, lebao, 99);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được lễ bao tân thủ");
                     }
                  } else {
                     this.npcChat(player, "Hành trang không đủ chổ trống");
                  }
                  break;
               case 2: // shop
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item thoivang = null;
                     try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                     } catch (Exception e) {
                     }
                     if (thoivang == null || thoivang.quantity < 1999999) {
                        Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                        return;
                     } else {
                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 1999999);
                        Item phungthienkich = ItemService.gI()
                              .createNewItem((short) ConstItem.PHUNG_THIEN_KICH);
                        phungthienkich.itemOptions.add(new ItemOption(0, Util.nextInt(300, 1000)));
                        phungthienkich.itemOptions.add(new ItemOption(3, Util.nextInt(1, 5)));
                        phungthienkich.itemOptions.add(new ItemOption(50, Util.nextInt(50, 150)));
                        phungthienkich.itemOptions.add(new ItemOption(77, Util.nextInt(50, 150)));
                        phungthienkich.itemOptions.add(new ItemOption(103, Util.nextInt(50, 150)));
                        phungthienkich.itemOptions.add(new ItemOption(101, Util.nextInt(50, 150)));
                        phungthienkich.itemOptions.add(new ItemOption(117, Util.nextInt(5, 15)));
                        phungthienkich.itemOptions.add(new ItemOption(93, 7));
                        phungthienkich.itemOptions.add(new ItemOption(199, 1));
                        InventoryService.gI().addItemBag(player, phungthienkich, 1);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được phụng thiên kích(HSD 7 ngày)");
                     }
                  } else {
                     this.npcChat(player, "Hành trang không đủ chổ trống");
                  }
                  break;
               case 3: // shop
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item thoivang = null;
                     try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                     } catch (Exception e) {
                     }
                     if (thoivang == null || thoivang.quantity < 1999999) {
                        Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                        return;
                     } else {
                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 1999999);
                        Item gayfake = ItemService.gI().createNewItem((short) ConstItem.GAY_FAKE);
                        gayfake.itemOptions.add(new ItemOption(50, Util.nextInt(40, 140)));
                        gayfake.itemOptions.add(new ItemOption(77, Util.nextInt(40, 140)));
                        gayfake.itemOptions.add(new ItemOption(103, Util.nextInt(40, 140)));
                        gayfake.itemOptions.add(new ItemOption(101, Util.nextInt(250, 500)));
                        gayfake.itemOptions.add(new ItemOption(93, 7));
                        gayfake.itemOptions.add(new ItemOption(199, 1));
                        gayfake.itemOptions.add(new ItemOption(30, 1));
                        InventoryService.gI().addItemBag(player, gayfake, 1);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được gậy như ý fake (HSD 7 ngày)");
                     }
                  } else {
                     this.npcChat(player, "Hành trang không đủ chổ trống");
                  }
                  break;
               case 4: // shop
                  if (InventoryService.gI().getCountEmptyBag(player) > 0) {
                     Item thoivang = null;
                     try {
                        thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                     } catch (Exception e) {
                     }
                     if (thoivang == null || thoivang.quantity < 100000) {
                        Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                        return;
                     } else {
                        InventoryService.gI().subQuantityItemsBag(player, thoivang, 100000);
                        Item thoivangkhoa = ItemService.gI().createNewItem((short) ConstItem.THOI_VANG);
                        thoivangkhoa.quantity = 99000;
                        thoivangkhoa.itemOptions.add(new ItemOption(30, 1));
                        InventoryService.gI().addItemBag(player, thoivangkhoa, 99999999);
                        Service.getInstance().sendMoney(player);
                        InventoryService.gI().sendItemBags(player);
                        this.npcChat(player, "|1|Bạn nhận được 99k Thỏi Vàng Khoá");
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
