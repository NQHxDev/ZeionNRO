package nro.models.npc.specific;

import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.Service;
import nro.services.func.ChangeMapService;

public class Itachi extends Npc {

   public Itachi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Ngước mặt lên trời... Hận đời vô đối!!!",
               "Đến map\nVar itachi");// , "Triệu Hồi Boss"
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  Item thoivang = null;
                  try {
                     thoivang = InventoryService.gI().findItemBagByTemp(player, (short) 457);
                  } catch (Exception e) {
                  }
                  if (thoivang == null || thoivang.quantity < 99) {
                     Service.getInstance().sendThongBao(player, "|3|Chưa đủ mảnh phong ấn boss");
                     return;
                  }
                  if (player.nPoint.dame < 10000000) {
                     Service.getInstance().sendThongBao(player,
                           "|7|Sức đánh tối thiểu 10 triệu mới đủ tuổi");
                     return;
                  } else {
                     InventoryService.gI().subQuantityItemsBag(player, thoivang, 99);
                     Service.getInstance().sendMoney(player);
                     InventoryService.gI().sendItemBags(player);
                     ChangeMapService.gI().changeMapBySpaceShip(player, 213, -1, -1);
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TRIEU_HOI_BOSS) {
            switch (select) {
               case 0:
                  Item phonganboss = InventoryService.gI().findItemBagByTemp(player, (short) 1557);
                  if (phonganboss == null || phonganboss.quantity < 99) {
                     Service.getInstance().sendThongBao(player, "|3|Chưa đủ mảnh phong ấn boss");
                     return;
                  } else {
                     Boss nhatvi = BossFactory.createBoss(BossFactory.BLACKGOKU);
                     nhatvi.zone = player.zone;
                     nhatvi.location.x = player.location.x;
                     nhatvi.location.y = player.location.y;
                     InventoryService.gI().subQuantityItemsBag(player, phonganboss, 99);
                     InventoryService.gI().sendItemBags(player);
                     Service.getInstance().sendMoney(player);
                     Service.getInstance().sendThongBao(player, "|2|Triệu hồi thành công boss Nhất Vĩ");
                  }
                  break;
            }
         }
      }
   }
}
