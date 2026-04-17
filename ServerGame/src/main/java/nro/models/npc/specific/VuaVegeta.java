package nro.models.npc.specific;

import nro.consts.ConstNpc;
import nro.models.npc.Npc;
import nro.models.player.Player;
import nro.models.skill.Skill;
import nro.services.Service;
import nro.services.SkillService;
import nro.utils.Log;
import nro.utils.SkillUtil;

public class VuaVegeta extends Npc {
   public VuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
      super(mapId, status, cx, cy, tempId, avartar);
   }

   @Override
   public void openBaseMenu(Player player) {
      if (canOpenNpc(player)) {
         Service.getInstance().sendThongBaoOK(player, "Chức năng tạm đóng");
      }
   }

   @Override
   public void confirmMenu(Player player, int select) {
      if (canOpenNpc(player)) {
         if (player.iDMark.isBaseMenu()) {
            switch (select) {
               case 0: // shop
                  if (player.gender == 0) {
                     this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_TD, "|8| Skill Super Kame\n\n"
                           + "|7|Chú ý chọn đúng cấp độ cần học"
                           + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                           + "\n Căng con mắt lên mà chọn cho đúng"
                           + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                           "Nâng Skill\nThêm 1 Cấp");
                  } else if (player.gender == 1) {
                     this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_NM, "|8| Skill Ma Phong Ba\n\n"
                           + "|7|Chú ý chọn đúng cấp độ cần học"
                           + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                           + "\n Căng con mắt lên mà chọn cho đúng"
                           + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                           "Nâng Skill\nThêm 1 Cấp");
                  } else {
                     this.createOtherMenu(player, ConstNpc.HOC_SKILL_9_XD,
                           "|8| Skill Liên Hoàn Chưởng\n\n"
                                 + "|7|Chú ý chọn đúng cấp độ cần học"
                                 + "\n Cố ý chọn sai bị trừ tiền ngu ráng chịu"
                                 + "\n Căng con mắt lên mà chọn cho đúng"
                                 + "\n Admin không chịu trách nhiệm đâu nhé kkk",
                           "Nâng Skill\nThêm 1 Cấp");
                  }
                  break;
               case 1:
               case 2:
                  Service.getInstance().sendThongBao(player, "Skill này tạm chưa mở");
                  break;
            }
         } else if (player.iDMark.getIndexMenu() == 7520042) { // Super Kamejoko
            if (select == 0) {
               upgradeSkill(player, Skill.SUPER_KAME);
            }
         } else if (player.iDMark.getIndexMenu() == 7520043) { // Ma Phong Ba
            if (select == 0) {
               upgradeSkill(player, Skill.MA_PHONG_BA);
            }
         }
      }
   }

   private void upgradeSkill(Player player, int skillId) {
      try {
         Skill curSkill = SkillUtil.getSkillbyId(player, skillId);
         if (curSkill == null)
            return;
         int level = curSkill.point;
         if (curSkill.point == 9) {
            Service.getInstance().sendThongBao(player, "Kỹ năng đã đạt tối đa!");
            return;
         } else if (player.nPoint.tiemNang < 999_999_999_999L * level) {
            Service.getInstance().sendThongBao(player, "Không đủ tiềm năng");
            return;
         } else {
            player.nPoint.tiemNang -= 999_999_999_999L * level;
            Service.getInstance().point(player);
            curSkill.point += 1;
            SkillService.gI().upgradeSkillSpecial(player, 25, (byte) curSkill.point);
            if (skillId == Skill.SUPER_KAME) {
               SkillService.gI().sendCurrLevelSpecial(player, curSkill);
            }
            Service.getInstance().sendThongBao(player, "Nâng cấp kỹ năng thành công lên cấp " + curSkill.point);
         }
      } catch (Exception e) {
         Log.error(VuaVegeta.class, e);
      }
   }
}
