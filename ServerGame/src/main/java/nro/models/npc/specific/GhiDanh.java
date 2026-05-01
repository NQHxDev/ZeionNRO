package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.item.ItemOption;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.consts.ConstNpc;
import nro.consts.ConstMap;
import nro.consts.ConstItem;
import nro.models.map.challenge.MartialCongressService;
import nro.services.InventoryService;
import nro.services.ItemService;
import nro.services.PlayerService;
import nro.services.Service;
import nro.services.func.ChangeMapService;
import nro.utils.Util;

public class GhiDanh extends Npc {

   public GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Chào mừng bạn đến với đại hội võ thuật", "Đại Hội\nVõ Thuật\nLần Thứ\n23",
                  "Giải siêu hạng");
         } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
            String[] menuselect;
            int goldchallenge = player.goldChallenge;
            if (player.levelWoodChest == 0) {
               menuselect = new String[] {
                     "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng",
                     "Về\nĐại Hội\nVõ Thuật" };
            } else {
               menuselect = new String[] {
                     "Thi đấu\n" + Util.numberToMoney(goldchallenge) + " vàng",
                     "Nhận thưởng\nRương cấp\n" + player.levelWoodChest,
                     "Về\nĐại Hội\nVõ Thuật" };
            }
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "Đại hội võ thuật lần thứ 23\nDiễn ra bất kể ngày đêm,ngày nghỉ ngày lễ\nPhần thưởng vô cùng quý giá\nNhanh chóng tham gia nào",
                  menuselect, "Từ chối");
         } else {
            super.openBaseMenu(player);
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            if (this.mapId == ConstMap.DAI_HOI_VO_THUAT) {
               switch (select) {
                  case 0:
                     ChangeMapService.gI().changeMapNonSpaceship(player,
                           ConstMap.DAI_HOI_VO_THUAT_129, player.location.x, 360);
                     break;
                  case 1:
                     ChangeMapService.gI().changeMapNonSpaceship(player, 113, player.location.x, 360);
                     break;
               }
            } else if (this.mapId == ConstMap.DAI_HOI_VO_THUAT_129) {
               handleTournament129(player, select);
            }
         }
      }
   }

   private void handleTournament129(Player player, int select) {
      int goldchallenge = player.goldChallenge;
      if (player.levelWoodChest == 0) {
         switch (select) {
            case 0:
               startChallenge(player, goldchallenge);
               break;
            case 1:
               ChangeMapService.gI().changeMapNonSpaceship(player,
                     ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
               break;
         }
      } else {
         switch (select) {
            case 0:
               startChallenge(player, goldchallenge);
               break;
            case 1:
               claimWoodChest(player);
               break;
            case 2:
               ChangeMapService.gI().changeMapNonSpaceship(player,
                     ConstMap.DAI_HOI_VO_THUAT, player.location.x, 336);
               break;
         }
      }
   }

   private void startChallenge(Player player, int goldchallenge) {
      if (InventoryService.gI().finditemWoodChest(player)) {
         if (player.inventory.getGold() >= goldchallenge) {
            MartialCongressService.gI().startChallenge(player);
            player.inventory.subGold(goldchallenge);
            PlayerService.gI().sendInfoHpMpMoney(player);
            player.goldChallenge += 2000000000;
         } else {
            Service.getInstance().sendThongBao(player,
                  "Không đủ vàng, còn thiếu "
                        + Util.numberToMoney(goldchallenge - player.inventory.gold)
                        + " vàng kìa thằng lòn");
         }
      } else {
         Service.getInstance().sendThongBao(player, "Hãy mở rương báu vật trước");
      }
   }

   private void claimWoodChest(Player player) {
      if (!player.receivedWoodChest) {
         if (InventoryService.gI().getCountEmptyBag(player) > 0) {
            Item it = ItemService.gI().createNewItem((short) ConstItem.RUONG_GO);
            it.itemOptions.add(new ItemOption(72, player.levelWoodChest));
            it.itemOptions.add(new ItemOption(30, 0));
            it.createTime = System.currentTimeMillis();
            InventoryService.gI().addItemBag(player, it, 0);
            InventoryService.gI().sendItemBags(player);

            player.receivedWoodChest = true;
            player.levelWoodChest = 0;
            Service.getInstance().sendThongBao(player, "Bạn nhận được rương gỗ");
         } else {
            this.npcChat(player, "Hành trang đã đầy");
         }
      } else {
         Service.getInstance().sendThongBao(player, "Mỗi ngày chỉ có thể nhận quà một lần");
      }
   }

}
