package nro.models.npc.specific;

import nro.models.item.Item;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.consts.ConstNpc;
import nro.services.InventoryService;
import nro.services.NpcService;
import nro.services.Service;
import nro.services.func.CombineServiceNew;
import nro.services.func.ShopService;
import nro.server.io.Message;
import nro.utils.SkillUtil;

public class Whis extends Npc {

   public Whis(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         if (this.mapId == 48) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?"
                  + "\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!"
                  + "\n\b|7| Điều kiện học Tuyệt kỹ"
                  + "\b|5| -Khi lần đầu học skill cần: x999 Bí kiếp tuyệt kỹ và SM trên 60 Tỷ"
                  + "\n -Mỗi một cấp yêu cầu: x999 Bí kiếp tuyệt kỹ và Thông thạo đạt MAX 100%",
                  "Học\ntuyệt kĩ", "Từ Chối");
         } else if (this.mapId == 154) {
            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                  "|7|NÂNG CẤP ĐỒ THIÊN SỨ\n|6| Mang cho ta Công thức + Đá cầu vòng và 999 Mảnh thiên sứ ta sẽ chế tạo đồ Thiên sứ cho ngươi"
                        + "\nĐồ Thiên sứ khi chế tạo sẽ random chỉ số 0-15%"
                        + "\n|2|(Khi mang đủ 5 món Hủy diệt ngươi hãy theo Osin qua Hành tinh ngục tù tìm kiếm Mảnh thiên sứ và săn BOSS Thiên sứ để thu thập Đá cầu vòng)"
                        + "\n|1| Ngươi có muốn nâng cấp không?",
                  "Hướng dẫn", "Nâng Cấp \nĐồ Thiên Sứ", "Shop\n Thiên sứ");
         }
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (this.mapId == 154) {
            if (player.iDMark.isBaseMenu()) {
               switch (select) {
                  case 0:
                     NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DO_TS);
                     break;
                  case 1:
                     CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                     break;
                  case 2:
                     ShopService.gI().openShopWhisThienSu(player, ConstNpc.SHOP_WHIS_THIEN_SU, 0);
                     break;
               }
            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE
                  || player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS
                  || player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DO_SKH_TS
                  || player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_RA_TS) {
               if (select == 0) {
                  CombineServiceNew.gI().startCombine(player);
               }
            }
         } else if (this.mapId == 48) {
            if (player.iDMark.isBaseMenu()) {
               if (select == 0) {
                  handleHocTuyetKy(player);
               }
            }
         }
      }
   }

   private void handleHocTuyetKy(Player player) {
      try {
         Item sachTuyetki = InventoryService.gI().findItemBagByTemp(player, 1215);
         short skillId = -1;
         switch (player.gender) {
            case 0:
               skillId = Skill.SUPER_KAME;
               break;
            case 1:
               skillId = Skill.MA_PHONG_BA;
               break;
            case 2:
               skillId = Skill.LIEN_HOAN_CHUONG;
               break;
         }

         if (skillId == -1)
            return;

         Skill curSkill = SkillUtil.getSkillbyId(player, skillId);
         if (curSkill.point == 0) {
            if (player.nPoint.power >= 60000000000L) {
               if (sachTuyetki == null || sachTuyetki.quantity < 999) {
                  this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
                  return;
               }
               InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
               InventoryService.gI().sendItemBags(player);
               curSkill = SkillUtil.createSkill(skillId, 1);
               SkillUtil.setSkill(player, curSkill);
               Message msg = Service.getInstance().messageSubCommand((byte) 23);
               msg.writer().writeShort(curSkill.skillId);
               player.sendMessage(msg);
               msg.cleanup();
            } else {
               Service.getInstance().sendThongBao(player, "Yêu cầu sức mạnh trên 60 Tỷ");
            }
         } else if (curSkill.point > 0 && curSkill.point < 9) {
            if (sachTuyetki == null || sachTuyetki.quantity < 999) {
               this.npcChat(player, "Bạn không đủ 999 bí kíp tuyệt kĩ");
               return;
            }
            if (curSkill.currLevel == 100) {
               InventoryService.gI().subQuantityItemsBag(player, sachTuyetki, 999);
               InventoryService.gI().sendItemBags(player);
               curSkill = SkillUtil.createSkill(skillId, curSkill.point + 1);
               SkillUtil.setSkill(player, curSkill);
               Message msg = Service.getInstance().messageSubCommand((byte) 62);
               msg.writer().writeShort(curSkill.skillId);
               player.sendMessage(msg);
               msg.cleanup();
            } else {
               Service.getInstance().sendThongBao(player, "Thông thạo của bạn chưa đủ 100%");
            }
         } else {
            Service.getInstance().sendThongBao(player, "Tuyệt kĩ của bạn đã đạt cấp tối đa");
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }
}
