package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.phuban.DragonNamecWar.TranhNgoc;
import nro.services.Service;
import nro.services.TaskService;

public class TruongLaoGuru extends Npc {
   public TruongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Ngọc rồng Namếc đang bị 2 thế lực tranh giành\nHãy chọn cấp độ tham gia tùy theo sức mạnh bản thân",
                  "Từ chối");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         switch (player.iDMark.getIndexMenu()) {
            case ConstNpc.BASE_MENU:
               // Currently only has "Từ chối" option in the switch-case logic provided
               break;
            case ConstNpc.REGISTER_TRANH_NGOC:
               switch (select) {
                  case 0:
                     if (!player.getSession().actived) {
                        Service.getInstance().sendThongBao(player,
                              "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                        return;
                     }
                     player.iDMark.setTranhNgoc((byte) 1);
                     TranhNgoc.gI().addPlayersCadic(player);
                     Service.getInstance().sendThongBao(player, "Đăng ký vào phe Cadic thành công");
                     break;
                  case 1:
                     if (!player.getSession().actived) {
                        Service.getInstance().sendThongBao(player,
                              "Vui lòng kích hoạt tài khoản để sửa dụng chức năng này!");
                        return;
                     }
                     player.iDMark.setTranhNgoc((byte) 2);
                     TranhNgoc.gI().addPlayersFide(player);
                     Service.getInstance().sendThongBao(player, "Đăng ký vào phe Fide thành công");
                     break;
               }
               break;
            case ConstNpc.LOG_OUT_TRANH_NGOC:
               switch (select) {
                  case 0:
                     player.iDMark.setTranhNgoc((byte) -1);
                     TranhNgoc.gI().removePlayersCadic(player);
                     TranhNgoc.gI().removePlayersFide(player);
                     Service.getInstance().sendThongBao(player, "Hủy đăng ký thành công");
                     break;
               }
               break;
         }
      }
   }
}
