package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.models.map.war.BlackBallWar;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

public class BlackDragon extends Npc {

   public BlackDragon(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (player.isHoldBlackBall) {
            this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?",
                  "Phù hộ", "Từ chối");
         } else {
            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME,
                  "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
            if (select == 0) {
               this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                     "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                     "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                     "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                     "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                     "Từ chối");
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
            if (select == 0) {
               ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
            switch (select) {
               case 0:
                  BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                  break;
               case 1:
                  BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                  break;
               case 2:
                  BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                  break;
               case 3:
                  this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                  break;
            }
         }
      }
   }
}
