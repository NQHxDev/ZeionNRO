package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstTask;
import nro.services.TaskService;

public class BoMong extends Npc {

   public BoMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 47 || this.mapId == 84) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8|LÀM NHIỆM VỤ HÀNG NGÀY\n"
                  + "|3|Tối đa " + ConstTask.MAX_SIDE_TASK + " nhiệm vụ mỗi ngày\n"
                  + "|2|Nv dễ: " + ConstTask.GOLD_EASY + "  Thỏi vàng"
                  + "\nNv trung bình: " + ConstTask.GOLD_NORMAL + "  Thỏi vàng"
                  + "\nNv khó: " + ConstTask.GOLD_HARD + "  Thỏi vàng"
                  + "\nNv siêu khó: " + ConstTask.GOLD_VERY_HARD + "  Thỏi vàng"
                  + "\nNv địa ngục: " + ConstTask.GOLD_HELL + "  Thỏi vàng"
                  + "\n|1|Chọn loại nhiệm vụ nào?",
                  "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa Ngục", "Từ chối");
         } else {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Chào cậu, tôi có thể giúp gì cho cậu?",
                  "Nhiệm vụ\nhàng ngày", "Thành tích", "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.BASE_MENU:
               if (this.mapId == 47 || this.mapId == 84) {
                  if (select >= 0 && select <= 4) {
                     TaskService.gI().changeSideTask(player, (byte) select);
                  }
               } else {
                  switch (select) {
                     case 0:
                        if (player.playerTask.sideTask.template == null) {
                           this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                 "Cậu muốn nhận nhiệm vụ cấp độ nào?",
                                 "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa Ngục", "Từ chối");
                        } else {
                           this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                 "Nhiệm vụ hàng ngày: " + player.playerTask.sideTask.getName() + " (" + player.playerTask.sideTask.getLevel() + ")"
                                       + "\nTiến độ: " + player.playerTask.sideTask.count + "/" + player.playerTask.sideTask.maxCount
                                       + "\nCậu muốn làm gì?",
                                 "Trả nhiệm vụ", "Hủy bỏ", "Từ chối");
                        }
                        break;
                     case 1:
                        TaskService.gI().checkDoneAchivements(player);
                        TaskService.gI().sendAchivement(player);
                        break;
                  }
               }
               break;
            case ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK:
               if (select >= 0 && select <= 4) {
                  TaskService.gI().changeSideTask(player, (byte) select);
               }
               break;
            case ConstNpc.MENU_OPTION_PAY_SIDE_TASK:
               if (select == 0) {
                  TaskService.gI().paySideTask(player);
               } else if (select == 1) {
                  TaskService.gI().removeSideTask(player);
               }
               break;
         }
      }
   }
}
