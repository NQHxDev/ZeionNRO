package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.services.TaskService;

public class EmiFukada extends Npc {

   public EmiFukada(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|7|TÍCH LUỸ SĂN BOSS NHẬN XU BẠC\n"
                        + "|2|Tổng nhận từ mốc 10 đến 10000 Boss nhận về: 40.000 Xu bạc\n\n"
                        + "|7|LÀM NHIỆM VỤ CỐT TRUYỆN NHẬN XU BẠC\n",
                  "Quà Săn\nBoss");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  TaskService.gI().checkDoneAchivements(player);
                  TaskService.gI().sendAchivement(player);
                  break;
            }
         }
      }
   }
}
