package nro.models.npc.special;

import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

public class HungVuong extends Npc {

   public HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "|8|DÙ AI ĐI NGƯỢC VỀ XUÔI\n"
                     + "NHỚ NGÀY GIỖ TỔ MÙNG 10 THÁNG 9\n\n"
                     + "\nThu thập đặc sản để đổi dược hộp quà"
                     + "\n x99 Ngà voi"
                     + "\n x99 Cựa Gà"
                     + "\n x99 Lmao Ngựa\n"
                     + "|4|Đổi được hộp quà 10 tháng 3\n"
                     + "\n x9 Ngà voi"
                     + "\n x9 Cựa Gà"
                     + "\n x9 Mao Ngựa\n"
                     + "x999 Xu vàng\n"
                     + "x999 Xu bạc\n"
                     + "|7|Đổi được hộp quà 10 tháng 3 VIP\n",
               "Đổi\nQuà", "Mua\nHộp VIP");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  handleDoiQua(player);
                  break;
               case 1:
                  handleMuaHopVIP(player);
                  break;
            }
         }
      }
   }

   private void handleDoiQua(Player player) {
      Item ngavoi = InventoryService.gI().findItemBagByTemp(player, 1546);
      Item cuaga = InventoryService.gI().findItemBagByTemp(player, 1547);
      Item maongua = InventoryService.gI().findItemBagByTemp(player, 1548);

      if (ngavoi == null || ngavoi.quantity < 99
            || cuaga == null || cuaga.quantity < 99
            || maongua == null || maongua.quantity < 99) {
         Service.getInstance().sendThongBao(player, "Ngươi cần có x99 mỗi loại Ngà voi, Cựa gà, Mao ngựa");
         return;
      }

      if (InventoryService.gI().getCountEmptyBag(player) > 0) {
         InventoryService.gI().subQuantityItemsBag(player, ngavoi, 99);
         InventoryService.gI().subQuantityItemsBag(player, cuaga, 99);
         InventoryService.gI().subQuantityItemsBag(player, maongua, 99);
         Item hopqua = ItemService.gI().createNewItem((short) 1551);
         InventoryService.gI().addItemBag(player, hopqua, 99);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp quà 10 tháng 3");
      } else {
         Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
      }
   }

   private void handleMuaHopVIP(Player player) {
      Item ngavoi = InventoryService.gI().findItemBagByTemp(player, 1546);
      Item cuaga = InventoryService.gI().findItemBagByTemp(player, 1547);
      Item maongua = InventoryService.gI().findItemBagByTemp(player, 1548);
      Item xuvang = InventoryService.gI().findItemBagByTemp(player, 1567);
      Item xubac = InventoryService.gI().findItemBagByTemp(player, 1568);

      if (ngavoi == null || ngavoi.quantity < 9
            || cuaga == null || cuaga.quantity < 9
            || maongua == null || maongua.quantity < 9
            || xuvang == null || xuvang.quantity < 999
            || xubac == null || xubac.quantity < 999) {
         Service.getInstance().sendThongBao(player, "Chưa đủ vật phẩm");
         return;
      }

      if (InventoryService.gI().getCountEmptyBag(player) > 0) {
         InventoryService.gI().subQuantityItemsBag(player, ngavoi, 9);
         InventoryService.gI().subQuantityItemsBag(player, cuaga, 9);
         InventoryService.gI().subQuantityItemsBag(player, maongua, 9);
         InventoryService.gI().subQuantityItemsBag(player, xuvang, 999);
         InventoryService.gI().subQuantityItemsBag(player, xubac, 999);
         Item hopquaVip = ItemService.gI().createNewItem((short) 1552);
         InventoryService.gI().addItemBag(player, hopquaVip, 99);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player, "Bạn nhận được Hộp quà 10 tháng 3 VIP");
      } else {
         Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
      }
   }
}
