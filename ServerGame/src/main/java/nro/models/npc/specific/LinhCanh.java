package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.clan.ClanMember;
import nro.models.map.phoban.DoanhTrai;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.DoanhTraiService;
import nro.services.NpcService;
import nro.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

public class LinhCanh extends Npc {

   public LinhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         // TODO: Remove this bypass before release
         boolean isAdminBypass = player.isAdmin();

         if (player.clan == null) {
            this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                  "Ngươi không có Bang hội à?\nHãy tìm một Bang hội rồi hãy đến đây gặp ta!",
                  "Đóng");
            return;
         }

         // Check Clan Level
         if (player.clan.maxMember <= 19 && !isAdminBypass) {
            this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                  "Bang hội của bạn chưa đủ cấp độ!\n" + "Bang hội cần tối thiểu đạt cấp 10 mới có thể tham gia.",
                  "Đóng");
            return;
         }

         // Check Number of Members in Clan (Admin Bypass)
         if (player.clan.getMembers().size() < 3 && !isAdminBypass) {
            this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                  "Hãy tập hợp đủ 3 thành viên Bang hội tại đây rồi mới được vào!", "Đóng");
         } else {
            int days = 5;
            ClanMember clanMember = player.clan.getClanMember((int) player.id);
            if (clanMember != null) {
               days = (int) (((System.currentTimeMillis() / 1000) - clanMember.joinTime) / 60 / 60 / 24);
            }

            // Check Join Time (Admin Bypass)
            if (days < 5 && !isAdminBypass) {
               NpcService.gI().createTutorial(player, avartar,
                     "Chỉ những thành viên gia nhập bang hội tối thiểu 5 ngày mới có thể tham gia");
               return;
            }

            // Check if Doanh Trai is already open
            if (!player.clan.haveGoneDoanhTrai && player.clan.timeOpenDoanhTrai != 0) {
               createOtherMenu(player, ConstNpc.MENU_VAO_DT,
                     "Doanh trại đã được mở. Bạn có muốn tham gia không?\n" + "Thời gian còn lại: "
                           + TimeUtil.getSecondLeft(player.clan.timeOpenDoanhTrai,
                                 DoanhTrai.TIME_DOANH_TRAI / 1000)
                           + " giây",
                     "Tham gia", "Đóng", "Hướng dẫn");
            } else {
               // Normal entry logic
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

               // Admin bypass allows entering alone (plSameClans.size() < 2 is the requirement
               // for 3 people total)
               if (plSameClans.size() < 2 && !isAdminBypass) {
                  this.createOtherMenu(player, ConstNpc.MENU_KHONG_CHO_VAO_DT,
                        "Hãy tập hợp đủ ít nhất 3 thành viên Bang hội đứng tại đây mới có thể tham gia", "Đóng");
               } else {
                  this.createOtherMenu(player, ConstNpc.MENU_CHO_PHEP_VAO_DT,
                        "Bang hội của ngươi đã đủ điều kiện để tham gia doanh trại độc nhãn\n"
                              + "Ngươi có muốn tham gia không?",
                        "Tham gia", "Đóng");
               }
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.MENU_CHO_PHEP_VAO_DT:
               if (select == 0) {
                  DoanhTraiService.gI().openDoanhTrai(player);
               }
               break;
            case ConstNpc.MENU_VAO_DT:
               if (select == 0) {
                  DoanhTraiService.gI().joinDoanhTrai(player);
               }
               break;
         }
      }
   }
}
