package nro.models.npc.special;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.NpcService;
import nro.services.func.TaiXiu;
import nro.utils.Util;

public class LyTieuNuong extends Npc {

   public LyTieuNuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         createOtherMenu(player, ConstNpc.BASE_MENU,
               "\b|8|Trò chơi Tài Xỉu đang được diễn ra\n\n|6|Thử vận may của bạn với trò chơi Tài Xỉu! Đặt cược và dự đoán đúng"
                     + "\n kết quả, bạn sẽ được nhận thưởng lớn. Hãy tham gia ngay và\n cùng trải nghiệm sự hồi hộp, thú vị trong trò chơi này!"
                     + "\n\n|7|(Điều kiện tham gia : Nhiệm vụ 24)\n\n|2|Đặt tối thiểu: 1.000 Hồng ngọc\n Tối đa: 100.000 Hồng ngọc"
                     + "\n\n|7| Lưu ý : Thoát game khi chốt Kết quả sẽ MẤT Tiền cược và Tiền thưởng",
               "Thể lệ", "Tham gia");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "|5|Có 2 nhà cái Tài và Xĩu, bạn chỉ được chọn 1 nhà để tham gia"
                              + "\n\n|6|Sau khi kết thúc thời gian đặt cược. Hệ thống sẽ tung xí ngầu để biết kết quả Tài Xỉu"
                              + "\n\nNếu Tổng số 3 con xí ngầu <=10 : XỈU\nNếu Tổng số 3 con xí ngầu >10 : TÀI\nNếu 3 Xí ngầu cùng 1 số : TAM HOA (Nhà cái lụm hết)"
                              + "\n\n|7|Lưu ý: Số Hồng ngọc nhận được sẽ bị nhà cái lụm đi 20%. Trong quá trình diễn ra khi đặt cược nếu thoát game sẽ bị MẤT TIỀN ĐẶT CƯỢC",
                        "Ok");
                  break;
               case 1:
                  String time = ((TaiXiu.gI().lastTimeEnd - System.currentTimeMillis()) / 1000)
                        + " giây";
                  if (TaiXiu.gI().baotri == false) {
                     if (player.goldTai == 0 && player.goldXiu == 0) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time,
                              "Cập nhập", "Theo TÀI", "Theo XỈU", "Đóng");
                     } else if (player.goldTai > 0) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time
                                    + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai)
                                    + " Hồng ngọc",
                              "Cập nhập", "Đóng");
                     } else {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time
                                    + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu)
                                    + " Hồng ngọc",
                              "Cập nhập", "Đóng");
                     }
                  } else {
                     if (player.goldTai == 0 && player.goldXiu == 0) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time
                                    + "\n\n|7|Hệ thống sắp bảo trì",
                              "Cập nhập", "Đóng");
                     } else if (player.goldTai > 0) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time
                                    + "\n\n|7|Bạn đã cược Tài : " + Util.format(player.goldTai)
                                    + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì",
                              "Cập nhập", "Đóng");
                     } else {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.TAIXIU, 11039,
                              "\n|7|---NHÀ CÁI TÀI-XỈU---\n\n|3|Kết quả kì trước:  " + TaiXiu.gI().x
                                    + " : " + TaiXiu.gI().y + " : " + TaiXiu.gI().z
                                    + "\n\n|6|Tổng nhà TÀI: " + Util.format(TaiXiu.gI().goldTai)
                                    + " Hồng ngọc"
                                    + "\n\nTổng nhà XỈU: " + Util.format(TaiXiu.gI().goldXiu)
                                    + " Hồng ngọc\n\n|5|Thời gian còn lại: " + time
                                    + "\n\n|7|Bạn đã cược Xỉu : " + Util.format(player.goldXiu)
                                    + " Hồng ngọc" + "\n\n|7|Hệ thống sắp bảo trì",
                              "Cập nhập", "Đóng");
                     }
                  }
                  break;
            }
         }
      }
   }
}
