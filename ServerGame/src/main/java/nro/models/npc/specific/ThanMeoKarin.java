package nro.models.npc.specific;

import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstEvent;
import nro.consts.ConstMap;
import nro.models.map.dungeon.zones.ZSnakeRoad;
import nro.services.TaskService;
import nro.services.func.Input;
import nro.models.map.dungeon.SnakeRoad;
import nro.server.ServerManager;
import nro.services.Service;

import static nro.server.Manager.EVENT_SEVER;
import static nro.models.npc.NpcFactory.PLAYERID_OBJECT;

public class ThanMeoKarin extends Npc {

   public ThanMeoKarin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (mapId == ConstMap.THAP_KARIN) {
            if (player.zone instanceof ZSnakeRoad) {
               this.createOtherMenu(player, ConstNpc.BASE_MENU,
                     "Hãy cầm lấy hai hạt đậu cuối cùng ở đây\nCố giữ mình nhé "
                           + player.name,
                     "Cảm ơn\nsư phụ");
            } else if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
               this.createOtherMenu(player, ConstNpc.BASE_MENU,
                     "Chào con, con muốn ta giúp gì nào?", getMenuSuKien(EVENT_SEVER));
            }
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (mapId == ConstMap.THAP_KARIN) {
            if (player.iDMark.isBaseMenu()) {
               if (player.zone instanceof ZSnakeRoad) {
                  switch (select) {
                     case 0:
                        Input.gI().createFormChooseLevelCDRD(player);
                        break;
                  }
               } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_CDRD) {
                  switch (select) {
                     case 0:
                        if (player.clan != null && player.clan.maxMember > 100) {
                           Service.getInstance().sendThongBao(player,
                                 "Bạn không được vào khi đang ở bang hội này!!!");
                           return;
                        }
                        if (player.clan != null) {
                           synchronized (player.clan) {
                              if (player.clan.snakeRoad == null) {
                                 int level = Byte.parseByte(
                                       String.valueOf(PLAYERID_OBJECT.get(player.id)));
                                 SnakeRoad road = new SnakeRoad(level);
                                 ServerManager.gI().getDungeonManager().addDungeon(road);
                                 road.join(player);
                                 player.clan.snakeRoad = road;
                              } else {
                                 player.clan.snakeRoad.join(player);
                              }
                           }
                        }
                        break;
                  }
               }
            }
         }
      }
   }

   public static String getMenuSuKien(int id) {
      switch (id) {
         case ConstEvent.KHONG_CO_SU_KIEN:
            return "Chưa có\n Sự Kiện";
         case ConstEvent.SU_KIEN_HALLOWEEN:
            return "Sự Kiện\nHaloween";
         case ConstEvent.SU_KIEN_20_11:
            return "Sự Kiện\n 20/11";
         case ConstEvent.SU_KIEN_NOEL:
            return "Sự Kiện\n Giáng Sinh";
         case ConstEvent.SU_KIEN_TET:
            return "Sự Kiện\n Tết Nguyên\nĐán 2024";
         case ConstEvent.SU_KIEN_8_3:
            return "Sự Kiện\n 8/3";
      }
      return "Chưa có\n Sự Kiện";
   }
}
