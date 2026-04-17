package nro.models.npc.specific;

import nro.models.consignment.ConsignmentShop;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.Service;
import nro.server.Manager;

public class CuaHangKyGui extends Npc {

   public CuaHangKyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         this.createOtherMenu(player, ConstNpc.BASE_MENU,
               "Của hàng chúng tôi chuyên bán hàng hiệu,hàng độc,nếu bạn không chê thì mại đzô",
               "Không có\nHướng dẫn", "Mua bán", "Danh sách\nHết Hạn", "Hủy");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 1:
                  if (!Manager.gI().getGameConfig().isOpenSuperMarket()) {
                     Service.getInstance().sendThongBao(player, "Chức năng kí gửi chưa mở,vui lòng quay lại sau");
                     return;
                  }
                  ConsignmentShop.getInstance().show(player);
                  break;
               case 2:
                  ConsignmentShop.getInstance().showExpiringItems(player);
                  break;
            }
         }
      }
   }
}
