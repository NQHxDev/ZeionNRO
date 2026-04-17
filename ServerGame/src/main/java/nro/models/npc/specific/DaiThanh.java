package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;

public class DaiThanh extends Npc {

   public DaiThanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Hợp thành rương kí ức"
                     + "\nNguyên liệu cần thiết:"
                     + "\n1 Long Hồn"
                     + "\n1 Quả trứng (Đệ Bư)"
                     + "\n1 Hồn Berus (Đệ Berus)"
                     + "\n1 Vòng Kim Cô (Đệ ngộ không)"
                     + "\n1 Trứng Tiên (Đệ Tiên)"
                     + "\n1 Trứng itachi (Đệ itachi)"
                     + "\n1 Trứng Kaido (Đệ Kaido)",
               "Hợp thành Rương Kí Ức");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            if (select == 0) {
               handleHopThanhRuongKiUc(player);
            }
         }
      }
   }

   private void handleHopThanhRuongKiUc(Player player) {
      Item quatrung = null;
      Item honberus = null;
      Item trungitachi = null;
      Item trungkaido = null;
      Item trungtien = null;
      Item vongkimco = null;
      Item longhon = null;
      try {
         quatrung = InventoryService.gI().findItemBagByTemp(player, (short) 568);
         honberus = InventoryService.gI().findItemBagByTemp(player, (short) 1108);
         trungitachi = InventoryService.gI().findItemBagByTemp(player, (short) 1538);
         trungkaido = InventoryService.gI().findItemBagByTemp(player, (short) 1537);
         trungtien = InventoryService.gI().findItemBagByTemp(player, (short) 1539);
         vongkimco = InventoryService.gI().findItemBagByTemp(player, (short) 1569);
         longhon = InventoryService.gI().findItemBagByTemp(player, (short) 1726);
      } catch (Exception e) {
      }
      if (quatrung == null || quatrung.quantity < 1
            || honberus == null || honberus.quantity < 1
            || trungitachi == null || trungitachi.quantity < 1
            || trungkaido == null || trungkaido.quantity < 1
            || vongkimco == null || vongkimco.quantity < 1
            || longhon == null || longhon.quantity < 1
            || trungtien == null || trungtien.quantity < 1) {
         Service.getInstance().sendThongBao(player, "Ko đủ nguyên liệu kìa thằng ngu");
         return;
      }
      if (InventoryService.gI().getCountEmptyBag(player) == 0) {
         Service.getInstance().sendThongBao(player, "Túi đầy rồi thằng ngu");
      } else {
         Item ruongkiuc = ItemService.gI().createNewItem((short) 1719);
         InventoryService.gI().subQuantityItemsBag(player, quatrung, 1);
         InventoryService.gI().subQuantityItemsBag(player, honberus, 1);
         InventoryService.gI().subQuantityItemsBag(player, trungitachi, 1);
         InventoryService.gI().subQuantityItemsBag(player, trungkaido, 1);
         InventoryService.gI().subQuantityItemsBag(player, vongkimco, 1);
         InventoryService.gI().subQuantityItemsBag(player, longhon, 1);
         InventoryService.gI().subQuantityItemsBag(player, trungtien, 1);
         InventoryService.gI().addItemBag(player, ruongkiuc, 1);
         InventoryService.gI().sendItemBags(player);
         Service.getInstance().sendThongBao(player,
               "Mày đã nhận được " + ruongkiuc.template.name);
      }
   }
}
