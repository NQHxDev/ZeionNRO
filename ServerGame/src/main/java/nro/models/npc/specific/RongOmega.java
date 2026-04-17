package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstMap;
import nro.models.map.war.BlackBallWar;
import nro.services.NpcService;
import nro.services.func.ChangeMapService;
import nro.utils.Log;

public class RongOmega extends Npc {

   public RongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         BlackBallWar.gI().setTime();
         if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
            try {
               long now = System.currentTimeMillis();
               if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                  this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW,
                        "Đường đến với ngọc rồng sao đen đã mở, "
                              + "ngươi có muốn tham gia không?",
                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
               } else {
                  String[] optionRewards = new String[7];
                  int index = 0;
                  for (int i = 0; i < 7; i++) {
                     if (player.rewardBlackBall.timeOutOfDateReward[i] > System
                           .currentTimeMillis()) {
                        optionRewards[index] = "Nhận thưởng\n" + (i + 1) + " sao";
                        index++;
                     }
                  }
                  if (index != 0) {
                     String[] options = new String[index + 1];
                     for (int i = 0; i < index; i++) {
                        options[i] = optionRewards[i];
                     }
                     options[options.length - 1] = "Từ chối";
                     this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW,
                           "Ngươi có một vài phần thưởng ngọc " + "rồng sao đen đây!",
                           options);
                  } else {
                     this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                           "Bang chiếm được ngọc rồng đen sẽ nhận được:"
                                 + "\n Ngọc rồng đen 1s = 30pt sức đánh"
                                 + "\n Ngọc rồng đen 2s = 50pt hp max"
                                 + "\n Ngọc rồng đen 3s = 50pt ki max"
                                 + "\n Ngọc rồng đen 4s = 30pt chí mạng"
                                 + "\n Ngọc rồng đen 5s = 20pt giáp"
                                 + "\n Ngọc rồng đen 6s = 20pt né đòn"
                                 + "\n Ngọc rồng đen 7s = 10 Thỏi Vàng/ giờ"
                                 + "\n (Thỏi vàng cần nhận mỗi giờ nếu không sẽ mất)",
                           "Hướng dẫn", "Từ chối");
                  }
               }
            } catch (Exception ex) {
               Log.error("Lỗi mở menu rồng Omega");
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_REWARD_BDW:
               player.rewardBlackBall.getRewardSelect((byte) select);
               break;
            case ConstNpc.MENU_OPEN_BDW:
               if (select == 0) {
                  NpcService.gI().createTutorial(player, this.avartar,
                        ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
               } else if (select == 1) {
                  player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                  ChangeMapService.gI().openChangeMapTab(player);
               }
               break;
            case ConstNpc.MENU_NOT_OPEN_BDW:
               if (select == 0) {
                  NpcService.gI().createTutorial(player, this.avartar,
                        ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
               }
               break;
         }
      }
   }
}
