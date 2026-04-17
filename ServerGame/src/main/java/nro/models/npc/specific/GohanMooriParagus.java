package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstPlayer;
import nro.consts.ConstTask;
import nro.jdbc.daos.PlayerDAO;
import nro.services.PetService;
import nro.services.Service;
import nro.services.TaskService;
import nro.services.func.Input;

public class GohanMooriParagus extends Npc {

   public GohanMooriParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         String mtv;
         if (player.getSession().actived) {
            mtv = "Cảm ơn ae đã ghé chơi sv free của mình"
                  + "\nChúc ae cày game vui vẻ!!!";
         } else {
            mtv = "Hãy bấm mở thành viên miễn phí để!!!"
                  + "kích hoạt đầy đủ tính năng";
         }
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|8|Server miễn phí, muốn có đồ thì cày\n\n"
                        + "|3|Nếu Lười không muốn cày cũng có thể nạp tiền\n"
                        + "|3|Để ủng hộ ad có kinh phí duy trì lâu dài nhaaa ^^"
                              .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                    : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru"
                                          : "Vua Vegeta")
                        + "\n\n|2| ***" + mtv + "***",
                  "Giftcode",
                  "Nhận đệ tử",
                  "Nhận Ngọc",
                  "Mở Tv",
                  "Nhập Code\nRiêng",
                  "Đổi\nMật Khẩu");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  Input.gI().createFormGiftCode(player);
                  break;
               case 1:
                  if (player.pet == null) {
                     PetService.gI().createNormalPet(player);
                     Service.getInstance().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                  } else {
                     this.npcChat(player, "Tham Lam");
                  }
                  break;
               case 2:
                  if (player.inventory.gem >= 100_000) {
                     Service.getInstance().sendThongBao(player,
                           "Ngọc nhận 100k ngọc là sài chán rồi\n"
                                 + "khi nào gần hết thì quay lại nhận sau nhá");
                     return;
                  } else {
                     player.inventory.gem += 100_000;
                  }
                  break;
               case 3:
                  if (player.getSession().actived == true) {
                     this.createOtherMenu(player, 53747,
                           "|7|Đã mtv không cần mở lại nữa",
                           "Ố kê");
                  } else {
                     this.createOtherMenu(player, 1456,
                           "|7|MỞ THÀNH VIÊN"
                                 + "\n|5|Mở thành viên để có thể giao dịch và chat thế giới"
                                 + "\n|3|Mtv sẽ cần 20.000 coin để mở ngay\n"
                                 + "và free nếu đã qua nhiệm vụ fide\n"
                                 + "\n|7|Bạn muốn mở thành viên theo cách nào?",
                           "Miễn Phí", "Mở Ngay\n20.000\nCoin", "Đóng");
                  }
                  break;
               case 4:
                  Input.gI().createGiftMember(player);
                  break;
               case 5:
                  Input.gI().createFormChangePassword(player);
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == 1456) {
            switch (select) {
               case 0:
                  if (player.getSession().actived == true) {
                     Service.getInstance().sendThongBao(player,
                           "|4|Bạn đã mở thành viên rồi mà. Tiếp tục chơi game thui nào!!!!");
                     return;
                  }
                  if (TaskService.gI().getIdTask(player) < ConstTask.TASK_22_0) {
                     Service.getInstance().sendThongBao(player, "|7|Bạn cần hoàn thành nhiệm vụ Fide"
                           + "\nĐể có thể  mtv free!!");
                     return;
                  } else {
                     PlayerDAO.subActive(player, 1);
                     player.getSession().actived = true;
                     Service.getInstance().sendThongBao(player, "|2|Bạn đã mở thành viên Thành công."
                           + "\nĐã mở khóa chức năng Giao dịch và Chat thế giới !!");
                  }
                  break;
               case 1:
                  if (player.getSession().actived == true) {
                     Service.getInstance().sendThongBao(player,
                           "|4|Bạn đã mở thành viên rồi mà. Tiếp tục chơi game thui nào!!!!");
                     return;
                  }
                  if (player.getSession().vnd < 20_000) {
                     Service.getInstance().sendThongBao(player, "|7|Thiếu coin\n"
                           + "bạn cần 20.000 coin để mở thành viên ngay\n"
                           + "Hãy nạp thêm tiền tại trang chủ hoặc thoát ra vào lại\n"
                           + "để cập nhật số coin nếu bạn đã nạp");
                     return;
                  } else {
                     PlayerDAO.subVnd(player, 20_000);
                     PlayerDAO.subActive(player, 1);
                     player.getSession().actived = true;
                     Service.getInstance().sendThongBao(player, "|2|Bạn đã mở thành viên Thành công."
                           + "\nĐã mở khóa chức năng Giao dịch và Chat thế giới !!");
                  }
                  break;
            }
         }
      }
   }
}
