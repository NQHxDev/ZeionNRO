package nro.models.npc.specific;

import nro.jdbc.daos.PlayerDAO;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ShopService;
import nro.utils.Util;

public class MiNuong extends Npc {

   public MiNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "|7|SHOP SỰ KIỆN\n\n"
                     + "Shop sẽ bán một số đệ tử và item even\n"
                     + "Cải trang mị nương bán với giá 10k Xu Vàng - và Xu bạc\n"
                     + " ***Yêu cầu tối thiểu 999 điểm sự kiện mới có thể mua\n"
                     + "|8|Để tri ân ae đã ủng hộ sv ae đã từng nạp tiền có thể bấm nhận quà tri ân",
               "Shop\nMị Nương", "Mua Ct\nMị Nương", "Nhận quà\nTri ân");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0: // shop mị nương
                  ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_MINUONG, 0, -1);
                  break;
               case 1:
                  handleMuaCaitrangMinuong(player);
                  break;
               case 2:
                  handleNhanQuaTrian(player);
                  break;
            }
         }
      }
   }

   private void handleMuaCaitrangMinuong(Player player) {
      Item xuvang = null;
      Item xubac = null;
      int diemsk = player.evenpoint;
      try {
         xuvang = InventoryService.gI().findItemBagByTemp(player, (short) 1568);
         xubac = InventoryService.gI().findItemBagByTemp(player, (short) 1567);
      } catch (Exception e) {
      }
      if (xuvang == null || xuvang.quantity < 9999
            || xubac == null || xubac.quantity < 9999
            || diemsk < 999) {
         Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu hoặc điểm sự kiện");
         return;
      }
      if (InventoryService.gI().getCountEmptyBag(player) == 0) {
         Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
      } else {
         Item caitrangminuong = ItemService.gI().createNewItem((short) 994);
         caitrangminuong.itemOptions.add(new ItemOption(0, Util.nextInt(1234, 6666)));
         caitrangminuong.itemOptions.add(new ItemOption(6, Util.nextInt(6666, 34567)));
         caitrangminuong.itemOptions.add(new ItemOption(7, Util.nextInt(6666, 34567)));
         caitrangminuong.itemOptions.add(new ItemOption(50, Util.nextInt(300, 500)));
         caitrangminuong.itemOptions.add(new ItemOption(77, Util.nextInt(300, 500)));
         caitrangminuong.itemOptions.add(new ItemOption(103, Util.nextInt(300, 500)));
         caitrangminuong.itemOptions.add(new ItemOption(101, Util.nextInt(500, 2000)));
         caitrangminuong.itemOptions.add(new ItemOption(117, Util.nextInt(20, 45)));
         caitrangminuong.itemOptions.add(new ItemOption(5, Util.nextInt(20, 69)));
         caitrangminuong.itemOptions.add(new ItemOption(116, 1));
         caitrangminuong.itemOptions.add(new ItemOption(106, 1));
         caitrangminuong.itemOptions.add(new ItemOption(199, 1));
         InventoryService.gI().subQuantityItemsBag(player, xuvang, 9999);
         InventoryService.gI().subQuantityItemsBag(player, xubac, 9999);
         InventoryService.gI().addItemBag(player, caitrangminuong, 1);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player,
               "Mày đã nhận được " + caitrangminuong.template.name);
      }
   }

   private void handleNhanQuaTrian(Player player) {
      if (player.hoivienvip != 0) {
         Service.getInstance().sendThongBao(player, "Cảm ơn bạn đã ủng hộ sv"
               + "Phần quà lần này đã hết và bạn đang là hội viên VIP cấp: "
               + player.hoivienvip + " \n"
               + "Sẽ sớm cập nhật thêm quà theo cấp hội viên của bạn.");
         return;
      }
      if (player.tichluynap < 50000) {
         Service.getInstance().sendThongBao(player,
               "Phần quà này chỉ dành cho ae đã ủng hộ server");
         return;
      }
      if (InventoryService.gI().getCountEmptyBag(player) < 3) {
         Service.getInstance().sendThongBao(player, "Hành trang của bạn phải có ít nhất 3 ô trống");
         return;
      } else {
         int levelVip = 0;
         int quantity = 0;
         if (player.tichluynap >= 50000 && player.tichluynap < 500000) {
            levelVip = 1;
            quantity = 1;
         } else if (player.tichluynap >= 500000 && player.tichluynap < 2000000) {
            levelVip = 2;
            quantity = 5;
         } else if (player.tichluynap >= 2000000 && player.tichluynap < 5000000) {
            levelVip = 3;
            quantity = 10;
         } else if (player.tichluynap >= 5000000 && player.tichluynap < 7500000) {
            levelVip = 4;
            quantity = 15;
         } else if (player.tichluynap >= 7500000 && player.tichluynap < 10000000) {
            levelVip = 5;
            quantity = 20;
         } else {
            levelVip = 6;
            quantity = 25;
         }

         if (levelVip > 0) {
            Item HopQua10thang3 = ItemService.gI().createNewItem((short) 1371);
            HopQua10thang3.quantity = quantity;
            InventoryService.gI().addItemBag(player, HopQua10thang3, 1);
            PlayerDAO.NangCapHoiVienVIP(player, levelVip);
            InventoryService.gI().sendItemBags(player);
            Service.getInstance().sendMoney(player);
            Service.getInstance().sendThongBao(player,
                  "Mày đã nhận được " + HopQua10thang3.template.name + " (" + quantity + ")");
         }
      }
   }
}
