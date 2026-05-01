package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.item.Item;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;
import nro.manager.SieuHangManager;
import nro.manager.TopManager;

public class TrongTai extends Npc {

   public TrongTai(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player pl) {
      this.createOtherMenu(pl, ConstNpc.BASE_MENU, "Đại hội võ thuật Siêu Hạng\nChào mừng bạn đến với võ đài!",
            "Thi đấu", "Xem Top", "Nhận vé", "Cửa hàng", "Quay về đại hội");
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (player.iDMark.isBaseMenu()) {
         switch (select) {
            case 0: // Thi đấu
               SieuHangManager.gI().showOpponents(player);
               break;
            case 1: // Xem Top
               TopManager.getInstance().loadSieuHang();
               SieuHangManager.gI().sendTopSieuHang(player);
               break;
            case 2: // Nhận vé
               if (Util.isAfterDay(player.lastTimeReceivedTicket)) {
                  player.lastTimeReceivedTicket = System.currentTimeMillis();
                  Item ticket = ItemService.gI().createNewItem((short) 2025, 5);
                  InventoryService.gI().addItemBag(player, ticket, 5);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player, "Bạn nhận được 5 vé thi đấu!");
               } else {
                  Service.getInstance().sendThongBao(player, "Hôm nay bạn đã nhận vé rồi, hãy quay lại vào ngày mai!");
               }
               break;
            case 3: // Cửa hàng
               Service.getInstance().sendThongBao(player, "Cửa hàng đang được cập nhật!");
               break;
            case 4: // Quay về đại hội
               ChangeMapService.gI().changeMapNonSpaceship(player, 52, 500, 336);
               break;
         }
      } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_SIEU_HANG) {
         if (player.opponentsSieuHang != null && select >= 0 && select < player.opponentsSieuHang.size()) {
            Player opponent = player.opponentsSieuHang.get(select);
            SieuHangManager.gI().challenge(player, (int) opponent.id);
         }
      }
   }

}
