package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.item.Item;
import nro.models.map.phoban.BanDoKhoBau;
import nro.models.npc.Npc;
import nro.models.npc.NpcFactory;
import nro.models.player.Player;
import nro.services.*;
import nro.services.func.ChangeMapService;
import nro.services.func.Input;
import nro.utils.Util;

public class QuyLaoKame extends Npc {
   public QuyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "|8| Tài khoản hiện đang có:"
                  + "\n- Số Coin: " + Util.format(player.getSession().vnd)
                  + "\n- Đã Săn: " + player.killboss + " Boss",
                  "Đổi\nThỏi Vàng", "Vào lãnh\n Địa Bang", "Bản đồ\nKho Báu", "Hồi Skill",
                  "Giải tán\nBang");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0:
                  this.createOtherMenu(player, ConstNpc.QUY_DOI_COIN,
                        "|7|ĐỔI TIỀN TỆ\n"
                              + "\n|6|Giới hạn đổi không quá 1.000.000 Coin"
                              + "\n|1|Coin hiện còn : " + " " + Util.format(player.getSession().vnd)
                              + "\n"
                              + "1000 Coin được 1000 thỏi vàng\n"
                              + "1000 Coin được 500 xu bạc\n"
                              + "1000 Coin được 100 xu vàng\n"
                              + "|3|Đổi tối thiểu 1k và số coin cần chia hết cho 1k",
                        "Đổi\nThỏi Vàng", "Đổi\nXu Bạc", "Đổi\nXu Vàng");
                  break;
               case 1:
                  if (player.clan != null) {
                     ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, -1);
                  } else {
                     Service.getInstance().sendThongBao(player, "Yêu cầu có bang hội !!!");
                  }
                  break;
               case 2:
                  if (player.clan != null) {
                     if (player.clan.banDoKhoBau != null) {
                        this.createOtherMenu(player, ConstNpc.MENU_OPENED_DBKB,
                              "Bang hội của con đang đi tìm kho báu dưới biển cấp độ "
                                    + player.clan.banDoKhoBau.level
                                    + "\nCon có muốn đi theo không?",
                              "Đồng ý", "Từ chối");
                     } else {
                        this.createOtherMenu(player, ConstNpc.MENU_OPEN_DBKB,
                              "Đây là bản đồ kho báu hải tặc tí hon\nCác con cứ yên tâm lên đường\n"
                                    + "Ở đây có ta lo\nNhớ chọn cấp độ vừa sức mình nhé",
                              "Chọn\ncấp độ", "Từ chối");
                     }
                  } else {
                     this.npcChat(player, "Con phải có bang hội ta mới có thể cho con đi");
                  }
                  break;
               case 3:
                  Item thoiVang = InventoryService.gI().findItemBagByTemp(player, 457);
                  if (thoiVang == null || thoiVang.quantity < 10) {
                     this.npcChat(player, "Cần 10 thỏi vàng để hồi skill");
                     return;
                  }
                  InventoryService.gI().subQuantityItemsBag(player, thoiVang, 10);
                  Service.getInstance().releaseCooldownSkill(player);
                  InventoryService.gI().sendItemBags(player);
                  Service.getInstance().sendThongBao(player,
                        "Bạn đã tiêu 10 thỏi vàng để hồi lại full skill");
                  break;
               case 4:
                  if (player.clan != null) {
                     ClanService.gI().RemoveClanAll(player);
                  } else {
                     Service.getInstance().sendThongBao(player,
                           "Bạn không có bang hội nào để giải tán.");
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPENED_DBKB) {
            switch (select) {
               case 0:
                  if (player.isAdmin()
                        || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                     ChangeMapService.gI().goToDBKB(player);
                  } else {
                     this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                           + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPEN_DBKB) {
            switch (select) {
               case 0:
                  if (player.isAdmin()
                        || player.nPoint.power >= BanDoKhoBau.POWER_CAN_GO_TO_DBKB) {
                     Input.gI().createFormChooseLevelBDKB(player);
                  } else {
                     this.npcChat(player, "Sức mạnh của con phải ít nhất phải đạt "
                           + Util.numberToMoney(BanDoKhoBau.POWER_CAN_GO_TO_DBKB));
                  }
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_ACCEPT_GO_TO_BDKB) {
            switch (select) {
               case 0:
                  BanDoKhoBauService.gI().openBanDoKhoBau(player,
                        Byte.parseByte(String.valueOf(NpcFactory.PLAYERID_OBJECT.get(player.id))));
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI_COIN) {
            switch (select) {
               case 0:
                  Input.gI().createFormDoiThoiVang(player);
                  break;
               case 1:
                  Input.gI().createFormDoiXuBac(player);
                  break;
               case 2:
                  Input.gI().createFormDoiXuVang(player);
                  break;
            }
         }
      }
   }
}
