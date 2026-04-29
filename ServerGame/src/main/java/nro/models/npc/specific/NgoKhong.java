package nro.models.npc.specific;

import nro.consts.ConstItem;
import nro.consts.ConstNpc;
import nro.utils.RandomCollection;
import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.server.Manager;
import nro.services.InventoryService;
import nro.services.Service;
import nro.utils.Util;

public class NgoKhong extends Npc {
   public NgoKhong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chu mi nga", "Tặng quả\nHồng đào\nChín");
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         int itemNeed = ConstItem.QUA_HONG_DAO_CHIN;
         Item item = InventoryService.gI().findItemBagByTemp(player, itemNeed);
         if (item != null) {
            RandomCollection<Integer> rc = Manager.HONG_DAO_CHIN;
            int itemID = rc.next();
            int x = cx + Util.nextInt(-50, 50);
            int y = player.zone.map.yPhysicInTop(x, cy - 24);
            int quantity = 1;
            if (itemID == ConstItem.HONG_NGOC) {
               quantity = Util.nextInt(1, 2);
            }
            InventoryService.gI().subQuantityItemsBag(player, item, 1);
            InventoryService.gI().sendItemBags(player);
            ItemMap itemMap = new ItemMap(player.zone, itemID, quantity, x, y, player.id);
            Service.getInstance().dropItemMap(player.zone, itemMap);
            npcChat(player.zone, "Xie xie");
         } else {
            Service.getInstance().sendThongBao(player, "Không tìm thấy!");
         }
      }
   }
}
