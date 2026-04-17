package nro.models.npc.specific;

import nro.models.map.phoban.DoanhTrai;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.models.clan.ClanMember;
import nro.services.DoanhTraiService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.TimeUtil;
import nro.utils.Util;

import java.util.ArrayList;
import java.util.List;

public class LinhCanh extends Npc {

   public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (player.clan != null && player.clan.maxMember <= 19) {
            Service.getInstance().sendThongBao(player,
                  "Bang hội của bạn chưa đủ cấp độ!!!" + "\nBang hội cần tối thiểu đạt cấp 10"
                        + "\nHãy cùng các thành viên khác cố gắng nâng cấp bang");
            return;
         }
         if (player.clan == null) {
            this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                  "Đừng chơi game 1 mình nữa, kiếm đồng đội đi"
                        + "\n Trong box zalo có gái xinh, vào chơi kiếm bán đê",
                  "Đóng");
         } else if (player.clan.getMembers().size() < 5) {
            this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                  "Cần ít nhất 5 thằng đứng cùng nhau mới được tham gia", "Đóng");
         } else {
            ClanMember clanMember = player.clan.getClanMember((int) player.id);
            int days = (int) (((System.currentTimeMillis() / 1000) - clanMember.joinTime) / 60
                  / 60 / 24);
            if (days < 5) {
               NpcService.gI().createTutorial(player, avartar,
                     "Chỉ những thành viên gia nhập bang hội tối thiểu 5 ngày mới có thể tham gia");
               return;
            }
            if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
               createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                     "Có thằng lòn nào mở dt rồi kìa\n" + "Còn : "
                           + TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai,
                                 DoanhTrai.TIME_DOANH_TRAI / 1000)
                           + "Bấm tham gia nhanh không nó lại bú hết thỏi vàng",
                     "Vào nhanh\n Còn kịp", "Chê", "Hướng\ndẫn\nthêm");
            } else {
               List<Player> plSameClans = new ArrayList<>();
               List<Player> playersMap = player.zone.getPlayers();
               synchronized (playersMap) {
                  for (Player pl : playersMap) {
                     if (!pl.equals(player) && pl.clan != null
                           && pl.clan.id == player.clan.id && pl.location.x >= 1285
                           && pl.location.x <= 1645) {
                        plSameClans.add(pl);
                     }

                  }
               }
               if (plSameClans.size() >= 4) {
                  if (!player.isAdmin() && player.clanMember
                        .getNumDateFromJoinTimeToToday() < DoanhTrai.DATE_WAIT_FROM_JOIN_CLAN) {
                     createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                           "Cân có 5 thành viên đứng gần để có thể đi",
                           "OK", "Hướng\ndẫn\nthêm");
                  } else if (player.clan.haveGoneDoanhTrai) {
                     createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                           "Đã có con chó nào đi lúc sớm rồi\n "
                                 + Util.formatTime(player.clan.timeOpenDoanhTrai)
                                 + " hôm nay. " + "\nNgười mở" + "("
                                 + player.clan.playerOpenDoanhTrai.name
                                 + ")" + "Ae cái bầu đuồi",
                           "OK", "Hướng\ndẫn\nthêm");

                  } else {
                     createOtherMenu(player, ConstNpc.MENU_CHO_VAO_DT,
                           "Hôm nay chưa bị con lợn nào phá, mày có thể mở DT\n"
                                 + "Một chuyến nếu kill toàn bộ boss sẽ rơi từ 1-2k thỏi vàng",
                           "Chiến\n thôi", "Chê", "Hướng\ndẫn\nthêm");
                  }
               } else {
                  createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                        "Cần ít nhất 5 thành viên đứng gần đây mới vào được\n"
                              + "Gọi mấy thằng trong bang lại đây đứng đi\n"
                              + "Nhanh mẹ lên.....\n",
                        "OK", "Hướng\ndẫn\nthêm");
               }
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 27) {
            switch (player.iDMark.getIndexMenu()) {
               case ConstNpc.MENU_KHONG_CHO_VAO_DT:
                  if (select == 1) {
                     NpcService.gI().createTutorial(player, this.avartar,
                           ConstNpc.HUONG_DAN_DOANH_TRAI);
                  }
                  break;
               case ConstNpc.MENU_CHO_VAO_DT:
                  switch (select) {
                     case 0:
                        DoanhTraiService.gI().openDoanhTrai(player);
                        break;
                     case 2:
                        NpcService.gI().createTutorial(player, this.avartar,
                              ConstNpc.HUONG_DAN_DOANH_TRAI);
                        break;
                  }
                  break;
               case ConstNpc.MENU_VAO_DT:
                  switch (select) {
                     case 0:
                        ChangeMapService.gI().changeMap(player, 53, 0, 35, 432);
                        break;
                     case 2:
                        NpcService.gI().createTutorial(player, this.avartar,
                              ConstNpc.HUONG_DAN_DOANH_TRAI);
                        break;
                  }
                  break;
            }
         }
      }
   }
}
