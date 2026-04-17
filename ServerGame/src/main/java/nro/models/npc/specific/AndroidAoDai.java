package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstItem;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.utils.Util;

public class AndroidAoDai extends Npc {

   public AndroidAoDai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Xin chào " + player.name + "\nTôi có thể giúp gì cho bạn"
                     + "\n|5|-Đổi Capsule Bạc : Cần 99 chữ Vạn Sự Như Ý 2024 và 2 Thiệp chúc tết"
                     + "\n-Đổi Túi  Vàng :  Cần 50 chữ Vạn Sự Như Ý 2024 và 1 Thiệp chúc tết"
                     + "\n-Danh hiệu Thiên tử 1 Ngày (Chỉ số ngẫu nhiên) :  Cần 99 chữ Vạn Sự Như Ý 2024 và 10 Thiệp chúc tết"
                     + "\n-Danh hiệu Thiên tử Vĩnh viễn (Chỉ số Max):  Cần 99 chữ Vạn Sự Như Ý 2024 và 99 Thiệp chúc tết + 2000 Thỏi  Vàng",
               "Capsule 2024", "Túi  Vàng", "Danh hiệu 1 Ngày", "Danh hiệu Vĩnh viễn");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  handleDoiCapsule(player);
                  break;
               case 1:
                  handleDoiTuiVang(player);
                  break;
               case 2:
                  handleDoiDanhHieu(player, false);
                  break;
               case 3:
                  handleDoiDanhHieu(player, true);
                  break;
            }
         }
      }
   }

   private void handleDoiCapsule(Player player) {
      Item chuvan = InventoryService.gI().findItem(player, ConstItem.CHU_VAN, 99);
      Item chusu = InventoryService.gI().findItem(player, ConstItem.CHU_SU, 99);
      Item chunhu = InventoryService.gI().findItem(player, ConstItem.CHU_NHU, 99);
      Item chuy = InventoryService.gI().findItem(player, ConstItem.CHU_Y, 99);
      Item chu2024 = InventoryService.gI().findItem(player, ConstItem.CHU_2024, 99);
      Item thiep = InventoryService.gI().findItem(player, ConstItem.THIEP_CHUC_TET_2024, 2);

      if (chuvan != null && chusu != null && chunhu != null && chuy != null && chu2024 != null && thiep != null) {
         InventoryService.gI().subQuantityItemsBag(player, chuvan, 99);
         InventoryService.gI().subQuantityItemsBag(player, chusu, 99);
         InventoryService.gI().subQuantityItemsBag(player, chunhu, 99);
         InventoryService.gI().subQuantityItemsBag(player, chuy, 99);
         InventoryService.gI().subQuantityItemsBag(player, chu2024, 99);
         InventoryService.gI().subQuantityItemsBag(player, thiep, 2);

         Item capsule2024 = ItemService.gI().createNewItem((short) ConstItem.CAPSULE_BAC);
         capsule2024.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, capsule2024, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được " + capsule2024.template.name);
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
      }
   }

   private void handleDoiTuiVang(Player player) {
      Item chuvan = InventoryService.gI().findItem(player, ConstItem.CHU_VAN, 50);
      Item chusu = InventoryService.gI().findItem(player, ConstItem.CHU_SU, 50);
      Item chunhu = InventoryService.gI().findItem(player, ConstItem.CHU_NHU, 50);
      Item chuy = InventoryService.gI().findItem(player, ConstItem.CHU_Y, 50);
      Item chu2024 = InventoryService.gI().findItem(player, ConstItem.CHU_2024, 50);
      Item thiep = InventoryService.gI().findItem(player, ConstItem.THIEP_CHUC_TET_2024, 1);

      if (chuvan != null && chusu != null && chunhu != null && chuy != null && chu2024 != null && thiep != null) {
         InventoryService.gI().subQuantityItemsBag(player, chuvan, 50);
         InventoryService.gI().subQuantityItemsBag(player, chusu, 50);
         InventoryService.gI().subQuantityItemsBag(player, chunhu, 50);
         InventoryService.gI().subQuantityItemsBag(player, chuy, 50);
         InventoryService.gI().subQuantityItemsBag(player, chu2024, 50);
         InventoryService.gI().subQuantityItemsBag(player, thiep, 1);

         Item tuivang = ItemService.gI().createNewItem((short) ConstItem.TUI_VANG);
         tuivang.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, tuivang, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được " + tuivang.template.name);
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
      }
   }

   private void handleDoiDanhHieu(Player player, boolean isPermanent) {
      int thiepNeeded = isPermanent ? 99 : 10;
      Item chuvan = InventoryService.gI().findItem(player, ConstItem.CHU_VAN, 99);
      Item chusu = InventoryService.gI().findItem(player, ConstItem.CHU_SU, 99);
      Item chunhu = InventoryService.gI().findItem(player, ConstItem.CHU_NHU, 99);
      Item chuy = InventoryService.gI().findItem(player, ConstItem.CHU_Y, 99);
      Item chu2024 = InventoryService.gI().findItem(player, ConstItem.CHU_2024, 99);
      Item thiep = InventoryService.gI().findItem(player, ConstItem.THIEP_CHUC_TET_2024, thiepNeeded);
      Item thoivang = null;
      if (isPermanent) {
         thoivang = InventoryService.gI().findItem(player, ConstItem.THOI_VANG, 2000);
      }

      if (chuvan != null && chusu != null && chunhu != null && chuy != null && chu2024 != null && thiep != null
            && (!isPermanent || thoivang != null)) {
         InventoryService.gI().subQuantityItemsBag(player, chuvan, 99);
         InventoryService.gI().subQuantityItemsBag(player, chusu, 99);
         InventoryService.gI().subQuantityItemsBag(player, chunhu, 99);
         InventoryService.gI().subQuantityItemsBag(player, chuy, 99);
         InventoryService.gI().subQuantityItemsBag(player, chu2024, 99);
         InventoryService.gI().subQuantityItemsBag(player, thiep, thiepNeeded);
         if (isPermanent) {
            InventoryService.gI().subQuantityItemsBag(player, thoivang, 2000);
         }

         Item thientu = ItemService.gI().createNewItem((short) ConstItem.THIEN_TU);
         if (isPermanent) {
            thientu.itemOptions.add(new ItemOption(50, 40));
            thientu.itemOptions.add(new ItemOption(77, 55));
            thientu.itemOptions.add(new ItemOption(103, 55));
            thientu.itemOptions.add(new ItemOption(14, 15));
            thientu.itemOptions.add(new ItemOption(101, 1200));
            thientu.itemOptions.add(new ItemOption(30, 1));
         } else {
            thientu.itemOptions.add(new ItemOption(50, Util.nextInt(20, 40)));
            thientu.itemOptions.add(new ItemOption(77, Util.nextInt(20, 55)));
            thientu.itemOptions.add(new ItemOption(103, Util.nextInt(20, 55)));
            thientu.itemOptions.add(new ItemOption(14, Util.nextInt(5, 15)));
            thientu.itemOptions.add(new ItemOption(101, Util.nextInt(200, 1200)));
            thientu.itemOptions.add(new ItemOption(30, 1));
            thientu.itemOptions.add(new ItemOption(93, 1));
         }
         InventoryService.gI().addItemBag(player, thientu, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được " + thientu.template.name);
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
      }
   }
}
