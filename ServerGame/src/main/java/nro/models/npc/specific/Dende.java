package nro.models.npc.specific;

import nro.models.map.war.NamekBallWar;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.services.TaskService;

public class Dende extends Npc {

   public Dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            if (player.isHoldNamecBall) {
               this.createOtherMenu(player, ConstNpc.ORTHER_MENU,
                     "Ô,ngọc rồng Namek,anh thật may mắn,nếu tìm đủ 7 viên ngọc có thể triệu hồi Rồng Thần Namek,",
                     "Gọi rồng", "Từ chối");
            } else {
               this.createOtherMenu(player, ConstNpc.BASE_MENU,
                     "Ta có bán một số thứ mà có thế ngươi cũng del cần"
                           + "\nMua thì mua ko mua thì mua",
                     "Cửa\nhàng");
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:// Shop
                  if (player.gender == ConstPlayer.NAMEC) {
                     this.openShopWithGender(player, ConstNpc.SHOP_DENDE_0, 0);
                  } else {
                     this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                           "Ta có bán một số thứ mà có thế ngươi cũng del cần"
                                 + "\nNhưng ta ko thích bán cho ngươi đó",
                           "Đóng");
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.ORTHER_MENU) {
            NamekBallWar.gI().summonDragon(player, this);
         }
      }
   }
}
