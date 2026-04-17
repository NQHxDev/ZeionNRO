package nro.models.npc.specific;

import nro.models.boss.Boss;
import nro.models.boss.BossFactory;
import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.Service;

public class Vados extends Npc {

   public Vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Ngước mặt lên trời... Hận đời vô đối!!!",
               "Triệu Hồi Boss");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  this.createOtherMenu(player, ConstNpc.MENU_TRIEU_HOI_BOSS,
                        "|7|Muốn triệu hồi BOSS nào!!!\n"
                              + "|8|Heart Gold rơi thuốc tăng sức đánh gốc\n"
                              + "|3|Triệu hồi cần 20 Mảnh Phong Ấn Boss\n\n"
                              + "|8|Cooler Gold rơi thuốc tăng hp-ki gốc\n"
                              + "|3|Triệu hồi cần 10 Mảnh Phong Ấn Boss\n",
                        "HEART\nGOLD", "COOLER\nGOLD");
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_TRIEU_HOI_BOSS) {
            Item phonganboss = InventoryService.gI().findItemBagByTemp(player, (short) 1557);
            switch (select) {
               case 0:
                  if (phonganboss == null || phonganboss.quantity < 20) {
                     Service.getInstance().sendThongBaoOK(player, "Chưa đủ mảnh phong ấn boss\n"
                           + "Boss này triệu hồi cần có 20 mảnh phong ấn boss\n"
                           + "Mảnh phong ấn kiếm được từ săn các boss mới");
                     return;
                  }
                  InventoryService.gI().subQuantityItemsBag(player, phonganboss, 20);
                  InventoryService.gI().sendItemBags(player);
                  Boss.trieuHoiBoss(player, BossFactory.HEART_GOLD);
                  Service.getInstance().sendThongBaoAllPlayer(
                        "Người chơi " + player.name + " Đã triệu hồi boss Heart Gold");
                  Service.getInstance().sendThongBao(player, "Triệu hồi thành công boss Heart Gold");
                  break;
               case 1:
                  if (phonganboss == null || phonganboss.quantity < 10) {
                     Service.getInstance().sendThongBaoOK(player, "Chưa đủ mảnh phong ấn boss\n"
                           + "Boss này triệu hồi cần có 10 mảnh phong ấn boss\n"
                           + "Mảnh phong ấn kiếm được từ săn các boss mới");
                     return;
                  }
                  InventoryService.gI().subQuantityItemsBag(player, phonganboss, 10);
                  InventoryService.gI().sendItemBags(player);
                  Boss.trieuHoiBoss(player, BossFactory.COOLER_GOLD);
                  Service.getInstance().sendThongBaoAllPlayer(
                        "Người chơi " + player.name + " Đã triệu hồi boss Cooler Gold");
                  Service.getInstance().sendThongBao(player, "Triệu hồi thành công boss Cooler Gold");
                  break;
            }
         }
      }
   }
}
