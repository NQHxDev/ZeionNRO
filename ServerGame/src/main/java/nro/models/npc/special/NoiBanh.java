package nro.models.npc.special;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstItem;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

public class NoiBanh extends Npc {

   public NoiBanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Xin chào " + player.name + "\nTôi là nồi nấu bánh\nTôi có thể giúp gì cho bạn"
                     + "\n|5|-Làm Bánh Tét: (99)Thịt ba chỉ, Gạo nếp, Đỗ xanh, Lá chuối"
                     + "\n-Làm Bánh Chưng: (99)Thịt Heo, Gạo nếp, Đỗ xanh, Lá dong"
                     + "\n-Nấu Bánh Tét: (1)Bánh tét, Phụ gia tạo màu, Gia vị tổng hộp"
                     + "\n-Nấu Bánh Chưng: (1)Bánh chưng, Phụ gia tạo màu, Gia vị tổng hộp"
                     + "\n-Đổi Hộp quà: (5)Bánh chưng Chín, Bánh tét Chín"
                     + "\n|3|(Làm bánh Thành công sẽ nhận 1 Điểm Sự kiện)",
               "Làm\nBánh Tét", "Làm\nBánh Chưng", getMenuLamBanh(player, 0),
               getMenuLamBanh(player, 1), "Đổi Hộp\nQuà Tết");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  handleLamBanhTet(player);
                  break;
               case 1:
                  handleLamBanhChung(player);
                  break;
               case 2:
                  handleNauBanhTet(player);
                  break;
               case 3:
                  handleNauBanhChung(player);
                  break;
               case 4:
                  handleDoiHopQua(player);
                  break;
            }
         }
      }
   }

   private void handleLamBanhTet(Player player) {
      Item thitBaChi = InventoryService.gI().findItem(player, ConstItem.THIT_BA_CHI, 99);
      Item gaoNep = InventoryService.gI().findItem(player, ConstItem.GAO_NEP, 99);
      Item doXanh = InventoryService.gI().findItem(player, ConstItem.DO_XANH, 99);
      Item laChuoi = InventoryService.gI().findItem(player, ConstItem.LA_CHUOI, 99);
      if (thitBaChi != null && gaoNep != null && doXanh != null && laChuoi != null) {
         InventoryService.gI().subQuantityItemsBag(player, thitBaChi, 99);
         InventoryService.gI().subQuantityItemsBag(player, gaoNep, 99);
         InventoryService.gI().subQuantityItemsBag(player, doXanh, 99);
         InventoryService.gI().subQuantityItemsBag(player, laChuoi, 99);
         Item banhtet = ItemService.gI().createNewItem((short) ConstItem.BANH_TET_2023);
         banhtet.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, banhtet, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh Tét");
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
      }
   }

   private void handleLamBanhChung(Player player) {
      Item thitHeo = InventoryService.gI().findItem(player, ConstItem.THIT_HEO_2023, 99);
      Item gaoNep = InventoryService.gI().findItem(player, ConstItem.GAO_NEP, 99);
      Item doXanh = InventoryService.gI().findItem(player, ConstItem.DO_XANH, 99);
      Item laDong = InventoryService.gI().findItem(player, ConstItem.LA_DONG_2023, 99);
      if (thitHeo != null && gaoNep != null && doXanh != null && laDong != null) {
         InventoryService.gI().subQuantityItemsBag(player, thitHeo, 99);
         InventoryService.gI().subQuantityItemsBag(player, gaoNep, 99);
         InventoryService.gI().subQuantityItemsBag(player, doXanh, 99);
         InventoryService.gI().subQuantityItemsBag(player, laDong, 99);
         Item banhChung = ItemService.gI().createNewItem((short) ConstItem.BANH_CHUNG_2023);
         banhChung.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, banhChung, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh Chưng");
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
      }
   }

   private void handleNauBanhTet(Player player) {
      if (!player.event.isCookingTetCake()) {
         Item banhTet = InventoryService.gI().findItem(player, ConstItem.BANH_TET_2023, 1);
         Item phuGia = InventoryService.gI().findItem(player, ConstItem.PHU_GIA_TAO_MAU, 1);
         Item giaVi = InventoryService.gI().findItem(player, ConstItem.GIA_VI_TONG_HOP, 1);

         if (banhTet != null && phuGia != null && giaVi != null) {
            InventoryService.gI().subQuantityItemsBag(player, banhTet, 1);
            InventoryService.gI().subQuantityItemsBag(player, phuGia, 1);
            InventoryService.gI().subQuantityItemsBag(player, giaVi, 1);
            InventoryService.gI().sendItemBags(player);
            player.event.setTimeCookTetCake(300);
            player.event.setCookingTetCake(true);
            Service.getInstance().sendThongBao(player, "Bắt đầu nấu bánh, thời gian nấu bánh là 5 phút");
         } else {
            Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
         }
      } else if (player.event.isCookingTetCake() && player.event.getTimeCookTetCake() == 0) {
         Item cake = ItemService.gI().createNewItem((short) ConstItem.BANH_TET_CHIN, 1);
         cake.itemOptions.add(new ItemOption(77, 20));
         cake.itemOptions.add(new ItemOption(103, 20));
         cake.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, cake, 0);
         InventoryService.gI().sendItemBags(player);
         player.event.setCookingTetCake(false);
         player.event.addEventPoint(1);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh Tét (đã chín) và 1 điểm sự kiện");
      }
   }

   private void handleNauBanhChung(Player player) {
      if (!player.event.isCookingChungCake()) {
         Item banhChung = InventoryService.gI().findItem(player, ConstItem.BANH_CHUNG_2023, 1);
         Item phuGia = InventoryService.gI().findItem(player, ConstItem.PHU_GIA_TAO_MAU, 1);
         Item giaVi = InventoryService.gI().findItem(player, ConstItem.GIA_VI_TONG_HOP, 1);

         if (banhChung != null && phuGia != null && giaVi != null) {
            InventoryService.gI().subQuantityItemsBag(player, banhChung, 1);
            InventoryService.gI().subQuantityItemsBag(player, phuGia, 1);
            InventoryService.gI().subQuantityItemsBag(player, giaVi, 1);
            InventoryService.gI().sendItemBags(player);
            player.event.setTimeCookChungCake(300);
            player.event.setCookingChungCake(true);
            Service.getInstance().sendThongBao(player, "Bắt đầu nấu bánh, thời gian nấu bánh là 5 phút");
         } else {
            Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu");
         }
      } else if (player.event.isCookingChungCake() && player.event.getTimeCookChungCake() == 0) {
         Item cake = ItemService.gI().createNewItem((short) ConstItem.BANH_CHUNG_CHIN, 1);
         cake.itemOptions.add(new ItemOption(50, 20));
         cake.itemOptions.add(new ItemOption(5, 15));
         cake.itemOptions.add(new ItemOption(74, 0));
         InventoryService.gI().addItemBag(player, cake, 0);
         InventoryService.gI().sendItemBags(player);
         player.event.setCookingChungCake(false);
         player.event.addEventPoint(1);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Bánh Chưng (đã chín) và 1 điểm sự kiện");
      }
   }

   private void handleDoiHopQua(Player player) {
      Item tetCake = InventoryService.gI().findItem(player, ConstItem.BANH_TET_CHIN, 5);
      Item chungCake = InventoryService.gI().findItem(player, ConstItem.BANH_CHUNG_CHIN, 5);
      if (chungCake != null && tetCake != null) {
         Item hopQua = ItemService.gI().createNewItem((short) ConstItem.HOP_QUA_TET_2023, 1);
         hopQua.itemOptions.add(new ItemOption(30, 0));
         hopQua.itemOptions.add(new ItemOption(74, 0));

         InventoryService.gI().subQuantityItemsBag(player, tetCake, 5);
         InventoryService.gI().subQuantityItemsBag(player, chungCake, 5);
         InventoryService.gI().addItemBag(player, hopQua, 0);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp quà tết");
      } else {
         Service.getInstance().sendThongBao(player, "Không đủ nguyên liệu để đổi");
      }
   }

   public String getMenuLamBanh(Player player, int type) {
      String name = type == 0 ? "Bánh Tét" : "Bánh Chưng";
      boolean isCooking = type == 0 ? player.event.isCookingTetCake() : player.event.isCookingChungCake();
      int time = type == 0 ? player.event.getTimeCookTetCake() : player.event.getTimeCookChungCake();

      if (isCooking) {
         if (time > 0) {
            return "Nấu " + name + "\n" + time + " giây";
         } else {
            return "Nhận " + name;
         }
      }
      return "Nấu " + name;
   }
}
