package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.utils.Util;
import java.util.Random;

public class MayGapThu extends Npc {

   private static final int ID_XU = 1567;
   private static final int GACHA_TV = 999_999;
   private static final int GACHA_XU = 10000;

   public MayGapThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8|MÁY GẮP THÚ\n"
               + "|7|TRANG BỊ SIÊU CẤP VIP PRO\n"
               + "|3|Chỉ số đồ sẽ cao hơn đồ shop rất nhiều\n"
               + "gắp bằng xu sẽ có thể nhận về trang bị có chỉ số cao hơn\n\n"
               + "|2|CHÚC BẠN MAY MẮN",
               "Thỏi\nVàng", "XU");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  this.createOtherMenu(player, 123457, "|8|GACHA BẰNG THỎI VÀNG\n"
                        + "|3|CHỈ SỐ ĐỒ NHẬN VỀ SẼ NGẪU NHIÊN\n"
                        + "|2|- Sức Đánh - Hp Ki trong khoảng 10-20%\n"
                        + "- Sdcm, Chí mạng trong khoảng 1-5%\n"
                        + "- Đẹp trong khoảng 30-45%\n"
                        + "- Tiềm năng sức mạnh trong khoảng 50-100%\n"
                        + "|3|*** Mỗi lần gacha tốn " + GACHA_TV + " Thỏi vàng",
                        "Cải\nTrang", "Linh\nThú", "Pet", "Thú Cưỡi");
                  break;
               case 1:
                  this.createOtherMenu(player, 123456, "|8|GACHA BẰNG XU\n"
                        + "|3|CHỈ SỐ ĐỒ NHẬN VỀ SẼ NGẪU NHIÊN\n"
                        + "|2|- Sức Đánh - Hp Ki trong khoảng 20-30%\n"
                        + "- Sdcm, Chí mạng trong khoảng 5-9%\n"
                        + "- Tiềm năng sức mạnh trong khoảng 100-200%\n"
                        + "|3|*** Mỗi lần gacha tốn " + GACHA_XU + " Xu",
                        "Cải\nTrang", "Linh\nThú", "Pet", "Thú Cưỡi");
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == 123457) { // GACHA THOI VANG
            handleGacha(player, select, true);
         } else if (player.iDMark.getIndexMenu() == 123456) { // GACHA XU
            handleGacha(player, select, false);
         }
      }
   }

   private void handleGacha(Player player, int select, boolean isGold) {
      Item resource = null;
      int needed = isGold ? GACHA_TV : GACHA_XU;
      int tempId = isGold ? 457 : ID_XU;

      try {
         resource = InventoryService.gI().findItemBagByTemp(player, tempId);
      } catch (Exception e) {
      }

      if (resource == null || resource.quantity < needed) {
         Service.getInstance().sendThongBaoOK(player, "Không đủ " + needed + (isGold ? " thỏi vàng" : " xu"));
         return;
      }
      if (InventoryService.gI().getCountEmptyBag(player) == 0) {
         Service.getInstance().sendThongBaoOK(player, "Túi đầy vui lòng dọn dẹp");
         return;
      }

      Item itemGenerated = null;
      switch (select) {
         case 0: // Cai trang
            itemGenerated = generateCaiTrang(player, isGold);
            break;
         case 1: // Linh thu
            itemGenerated = generateLinhThu(player, isGold);
            break;
         case 2: // Pet
            itemGenerated = generatePet(player, isGold);
            break;
         case 3: // Thu cuoi
            itemGenerated = generateThuCuoi(player, isGold);
            break;
      }

      if (itemGenerated != null) {
         InventoryService.gI().subQuantityItemsBag(player, resource, needed);
         InventoryService.gI().addItemBag(player, itemGenerated, 1);
         InventoryService.gI().sendItemBags(player);
         if (select == 2)
            Service.getInstance().sendMoney(player);
         Service.getInstance().sendThongBaoOK(player, "Ngươi nhận được: " + itemGenerated.template.name);
      }
   }

   private Item generateCaiTrang(Player player, boolean isGold) {
      int[] ids = isGold
            ? new int[] { 1251, 1252, 1253, 1625, 1627, 1641, 1642, 1643, 1631, 1620, 1632, 1626, 1411, 1410, 1409,
                  1416, 1600, 1624, 1628, 1412, 1638, 1629, 1686, 1214, 1384, 1636, 1639, 1634, 1635, 1319, 1618, 1619 }
            : new int[] { 1251, 1252, 1253, 1625, 1627, 1641, 1642, 1643, 1631, 1620, 1632, 1626, 1628, 1412, 1638,
                  1629, 1686, 1214, 1384, 1636, 1639, 1634, 1635, 1319, 1618, 1619 };

      Item item = ItemService.gI().createNewItem((short) ids[new Random().nextInt(ids.length)]);
      if (isGold) {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(10, 20)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(10, 20)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(10, 20)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(1, 5)));
         item.itemOptions.add(new ItemOption(101, Util.nextInt(50, 100)));
         item.itemOptions.add(new ItemOption(117, Util.nextInt(1, 5)));
         item.itemOptions.add(new ItemOption(94, Util.nextInt(10, 20)));
         item.itemOptions.add(new ItemOption(106, 1));
         item.itemOptions.add(new ItemOption(116, 1));
         item.itemOptions.add(new ItemOption(33, 1));
         item.itemOptions.add(new ItemOption(76, 1));
         item.itemOptions.add(new ItemOption(73, 1));
      } else {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(400, 500)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(400, 500)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(400, 500)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(60, 75)));
         item.itemOptions.add(new ItemOption(117, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(94, Util.nextInt(170, 200)));
         item.itemOptions.add(new ItemOption(116, 1));
         item.itemOptions.add(new ItemOption(73, 1));
         item.itemOptions.add(new ItemOption(30, 1));
      }
      return item;
   }

   private Item generateLinhThu(Player player, boolean isGold) {
      int[] ids = isGold
            ? new int[] { 1295, 1344, 1374, 1375, 1376, 1377, 1378, 1379, 1380, 1381, 1382, 1540, 1541, 1542, 1543,
                  1544, 1545, 1546, 1547, 1548, 1589, 1598, 1599, 1601, 1614, 1615, 1616 }
            : new int[] { 1295, 1344, 1374, 1375, 1376, 1377, 1378, 1379, 1380, 1381, 1382, 1540, 1541, 1542, 1543,
                  1544, 1545, 1546, 1547, 1548, 1549, 1550, 1551, 1552, 1573, 1574, 1575, 1576, 1577, 1578, 1589, 1598,
                  1599, 1601, 1614, 1615, 1616 };

      Item item = ItemService.gI().createNewItem((short) ids[new Random().nextInt(ids.length)]);
      if (isGold) {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(10, 400)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(10, 400)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(101, Util.nextInt(100, 150)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
         item.itemOptions.add(new ItemOption(173, Util.nextInt(3, 5)));
         item.itemOptions.add(new ItemOption(209, Util.nextInt(3, 5)));
         item.itemOptions.add(new ItemOption(76, 1));
         item.itemOptions.add(new ItemOption(73, 1));
      } else {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(101, Util.nextInt(200, 300)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(40, 50)));
         item.itemOptions.add(new ItemOption(173, Util.nextInt(4, 7)));
         item.itemOptions.add(new ItemOption(209, Util.nextInt(4, 7)));
         item.itemOptions.add(new ItemOption(73, 1));
      }
      return item;
   }

   private Item generatePet(Player player, boolean isGold) {
      short[] ids = { 1644, 1645, 1646, 1647, 1648, 2036, 1196, 1197, 1198, 1221, 1222, 1223 };
      Item item = ItemService.gI().createNewItem(ids[Util.nextInt(0, ids.length - 1)]);
      if (isGold) {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(14, Util.nextInt(25, 35)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
         item.itemOptions.add(new ItemOption(97, Util.nextInt(25, 35)));
         item.itemOptions.add(new ItemOption(80, Util.nextInt(25, 35)));
         item.itemOptions.add(new ItemOption(81, Util.nextInt(25, 35)));
         item.itemOptions.add(new ItemOption(76, 1));
         item.itemOptions.add(new ItemOption(73, 1));
      } else {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(14, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(40, 50)));
         item.itemOptions.add(new ItemOption(97, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(80, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(81, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(73, 1));
      }
      return item;
   }

   private Item generateThuCuoi(Player player, boolean isGold) {
      int[] ids = isGold ? new int[] { 1502, 1503, 1142, 1092, 1227 }
            : new int[] { 1502, 1503, 1142, 1227 };
      Item item = ItemService.gI().createNewItem((short) ids[new Random().nextInt(ids.length)]);
      if (isGold) {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(300, 400)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(35, 45)));
         item.itemOptions.add(new ItemOption(95, Util.nextInt(30, 45)));
         item.itemOptions.add(new ItemOption(96, Util.nextInt(30, 45)));
         item.itemOptions.add(new ItemOption(16, 50));
         item.itemOptions.add(new ItemOption(76, 0));
         item.itemOptions.add(new ItemOption(73, 0));
      } else {
         item.itemOptions.add(new ItemOption(50, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(77, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(103, Util.nextInt(320, 450)));
         item.itemOptions.add(new ItemOption(5, Util.nextInt(40, 55)));
         item.itemOptions.add(new ItemOption(95, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(96, Util.nextInt(35, 55)));
         item.itemOptions.add(new ItemOption(16, 50));
         item.itemOptions.add(new ItemOption(73, 0));
      }
      return item;
   }
}
