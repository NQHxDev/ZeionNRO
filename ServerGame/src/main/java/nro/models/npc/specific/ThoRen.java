package nro.models.npc.specific;

import nro.jdbc.daos.PlayerDAO;
import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.server.Client;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;
import nro.utils.Util;

public class ThoRen extends Npc {

   public ThoRen(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "|8|Số coin mà bạn đang có: " + player.getSession().vnd + " Coin\n"
                     + "Số người đang onl game: " + Client.gI().getPlayers().size() + " Người\n"
                     + "Nơi này chuyên cung cấp các dịch vụ về trang bị\n"
                     + "Bạn cần hỗ trợ gì ?",
               "Nâng Đồ\nTL->HD",
               "Chế SKH\nVải Thô",
               "Cướng Hoá\nTrang Bị", "Chế Tạo\nThánh Tôn", "Shop Đá\nCường Hoá", "Gacha\nĐá Cường Hoá",
               "Thu Mua\nTrang Bị");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_THAN_LINH);
                  break;
               case 1:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VAI_THO);
                  break;
               case 2:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_TRANG_BI);
                  break;
               case 3:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.CHE_TAO_THANH_TON);
                  break;
               case 4:
                  ShopService.gI().openShopSpecial(player, this, ConstNpc.SHOP_THOREN, 0, -1);
                  break;
               case 5:
                  this.createOtherMenu(player, ConstNpc.MENU_GACHA_DA_CUONG_HOA,
                        "|8|SẼ CÓ 2 LOẠI GACHA"
                              + "\n- GACHA 500K COIN 1 NHÁT"
                              + "\n- HOẶC GACHA 1M THỎI VÀNG 1 NHÁT"
                              + "\n GACHA COIN: TỪ ĐÁ CẤP 7 ĐẾN CẤP 12!!!"
                              + "\n GACHA THỎI VÀNG: TỪ ĐÁ CẤP 6 ĐẾN CẤP 11!!!"
                              + "\n lưu ý: chỉ có thể dùng thỏi vàng không khoá!!!",
                        "Coin", "Thỏi\nVàng");
                  break;
               case 6:
                  CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.THU_HOI_DO_TL);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_GACHA_DA_CUONG_HOA) {
            switch (select) {
               case 0:
                  if (player.getSession().vnd < 50000) {
                     Service.getInstance().sendThongBao(player, "Không đủ coin");
                     return;
                  } else if (InventoryService.gI().getCountEmptyBag(player) == 0) {
                     Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
                     return;
                  } else {
                     PlayerDAO.subVnd(player, 50000);
                     PlayerDAO.CongTongNap(player, 50000);
                     short[] dacuonghoa7den12 = { 1559, 1560, 1561, 1563, 1564, 1565 };
                     byte index = (byte) Util.nextInt(0, dacuonghoa7den12.length - 1);
                     Item dacuonghoa = ItemService.gI().createNewItem(dacuonghoa7den12[index]);
                     InventoryService.gI().addItemBag(player, dacuonghoa, 9999);
                     InventoryService.gI().sendItemBags(player);
                     Service.getInstance().sendMoney(player);
                     Service.getInstance().sendThongBao(player,
                           "Mày đã nhận được " + dacuonghoa.template.name);
                  }
                  break;
               case 1:
                  Item thoivang = null;
                  try {
                     thoivang = InventoryService.gI().findItemBagByTemp(player, 457);
                  } catch (Exception e) {
                  }
                  if (thoivang == null || thoivang.quantity < 999) {
                     Service.getInstance().sendThongBao(player, "Không đủ Thỏi Vàng");
                  } else {
                     InventoryService.gI().subQuantityItemsBag(player, thoivang, 999999);
                     short[] dacuonghoa7den11 = { 1559, 1560, 1561, 1563, 1564, 1565 };
                     byte index = (byte) Util.nextInt(0, dacuonghoa7den11.length - 1);
                     Item dacuonghoa = ItemService.gI().createNewItem(dacuonghoa7den11[index]);
                     dacuonghoa.itemOptions.add(new ItemOption(30, 1));
                     InventoryService.gI().addItemBag(player, dacuonghoa, 9999);
                     InventoryService.gI().sendItemBags(player);
                     Service.getInstance().sendMoney(player);
                     this.npcChat(player, "|1|Bạn nhận được: " + dacuonghoa.getName());
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
            switch (player.combineNew.typeCombine) {
               case CombineServiceNew.NANG_CAP_TRANG_BI:
               case CombineServiceNew.NANG_CAP_SKH_VAI_THO:
               case CombineServiceNew.NANG_CAP_THAN_LINH:
               case CombineServiceNew.CHE_TAO_THANH_TON:
               case CombineServiceNew.THU_HOI_DO_TL:
                  if (select == 0) {
                     CombineServiceNew.gI().startCombine(player);
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_THAN_LINH
               || player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_SKH_VAI_THO
               || player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_TRANG_BI
               || player.iDMark.getIndexMenu() == ConstNpc.MENU_CHE_TAO_THANH_TON
               || player.iDMark.getIndexMenu() == ConstNpc.MENU_THU_HOI_DO_TL) {
            if (select == 0) {
               CombineServiceNew.gI().startCombine(player);
            }
         }
      }
   }
}
