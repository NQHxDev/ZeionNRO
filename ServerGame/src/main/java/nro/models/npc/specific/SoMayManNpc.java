package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.services.func.Input;
import nro.services.func.SoMayMan;

public class SoMayManNpc extends Npc {
   public SoMayManNpc(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         String time = ((SoMayMan.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
         StringBuilder stringBuilder = new StringBuilder();
         for (Player mem : SoMayMan.gI().TrungGiai) {
            if (stringBuilder.length() > 0) {
               stringBuilder.append(", ");
            }
            stringBuilder.append(mem.name);
         }
         String NamePl = stringBuilder.toString();
         createOtherMenu(player, ConstNpc.BASE_MENU,
               "Kết quả giải trước: " + SoMayMan.gI().SoGiaiTruoc
                     + "\nNgười trúng giải trước: " + NamePl
                     + "\nTham gia: " + SoMayMan.gI().PlayerThamGia.size() + " người"
                     + "\nĐoán trúng sẽ nhận thưởng 100.000 Hồng ngọc"
                     + "\nThời gian quay số: " + time,
               "1 số\n1.000 Hồng ngọc", "Hướng dẫn", "Đóng");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  Input.gI().ChonSo(player);
                  break;
               case 1:
                  createOtherMenu(player, ConstNpc.IGNORE_MENU,
                        "Sau khi hết thời gian đếm ngược"
                              + "\nHệ thống sẽ quay số cho ra kết quả"
                              + "\nNgười thắng sẽ nhận được 100.000 Hồng ngọc"
                              + "\n(Mỗi 1 số dự đoán sẽ mất 1.000 Hồng ngọc)",
                        "Ok");
                  break;
            }
         }
      }
   }
}
