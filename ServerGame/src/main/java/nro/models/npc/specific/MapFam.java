package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.Service;
import nro.services.func.ChangeMapService;

public class MapFam extends Npc {

   public MapFam(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "|7|MAP FAM\n"
                     + "|2|Phí vào map:\n"
                     + "- MAP FAM THỎI VÀNG - 1000 Xu bạc\n"
                     + "|1|Các map fam mảnh vỡ bông tai, mảnh hồn vào miễn phí\n",
               "FAM\nMảnh\nChân Mệnh", "FAM\nThỏi Vàng", "FAM\nĐá Cường\nHoá", "FAM\nMảnh Vỡ\nBt 2",
               "FAM\nMảnh Vỡ\nBt 3", "FAM\nMảnh Vỡ\nBt 4", "FAM\nMảnh Hồn\nBt");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         Item xuBac = InventoryService.gI().findItemBagByTemp(player, 457);
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 215, -1, -1);
                  break;
               case 1:
                  if (xuBac == null || xuBac.quantity < 1000) {
                     Service.getInstance().sendThongBaoOK(player,
                           "Không đủ xu bạc, cần 1000 xu bạc làm phí vào map");
                     return;
                  }
                  InventoryService.gI().subQuantityItemsBag(player, xuBac, 1000);
                  InventoryService.gI().sendItemBags(player);
                  ChangeMapService.gI().changeMapBySpaceShip(player, 214, -1, -1);
                  break;
               case 2:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 216, -1, -1);
                  break;
               case 3:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 219, -1, -1);
                  break;
               case 4:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 220, -1, -1);
                  break;
               case 5:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 221, -1, -1);
                  break;
               case 6:
                  ChangeMapService.gI().changeMapBySpaceShip(player, 211, -1, -1);
                  break;
            }
         }
      }
   }
}
