package nro.jdbc.daos.manager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import nro.consts.ConstPlayer;
import nro.models.skill.NClass;
import nro.models.skill.Skill;
import nro.models.skill.SkillTemplate;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class SkillDAO {

   public static void load(Connection con, List<NClass> nClasses) {
      String query = "SELECT * FROM skill_template ORDER BY nclass_id, slot";

      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         byte currentNClassId = -1;
         NClass currentNClass = null;
         int totalTemplates = 0;

         while (rs.next()) {
            byte nClassId = rs.getByte("nclass_id");

            // Group by NClass
            if (nClassId != currentNClassId) {
               currentNClassId = nClassId;
               currentNClass = new NClass();
               currentNClass.classId = nClassId;
               currentNClass.name = (nClassId == ConstPlayer.TRAI_DAT) ? "Trái Đất"
                     : (nClassId == ConstPlayer.NAMEC) ? "Namếc" : "Xayda";
               nClasses.add(currentNClass);
            }

            if (currentNClass == null)
               continue;

            SkillTemplate skillTemplate = new SkillTemplate();
            skillTemplate.classId = nClassId;
            skillTemplate.id = rs.getByte("id");
            skillTemplate.name = rs.getString("name");
            skillTemplate.maxPoint = rs.getByte("max_point");
            skillTemplate.manaUseType = rs.getByte("mana_use_type");
            skillTemplate.type = rs.getByte("type");
            skillTemplate.iconId = rs.getShort("icon_id");
            skillTemplate.damInfo = rs.getString("dam_info");
            skillTemplate.description = rs.getString("desc");
            currentNClass.skillTemplatess.add(skillTemplate);
            totalTemplates++;

            // Parse nested skills using GSON
            String skillsJson = rs.getString("skills");
            if (skillsJson != null && !skillsJson.isEmpty()) {
               try {
                  JsonArray skillsArray = JsonParser.parseString(skillsJson).getAsJsonArray();
                  for (JsonElement element : skillsArray) {
                     JsonObject skillObj = element.getAsJsonObject();
                     Skill skill = new Skill();
                     skill.template = skillTemplate;

                     skill.skillId = skillObj.get("id").getAsShort();
                     skill.point = skillObj.get("point").getAsByte();
                     skill.powRequire = skillObj.get("power_require").getAsLong();
                     skill.manaUse = skillObj.get("mana_use").getAsInt();
                     skill.coolDown = skillObj.get("cool_down").getAsInt();
                     skill.dx = skillObj.get("dx").getAsInt();
                     skill.dy = skillObj.get("dy").getAsInt();
                     skill.maxFight = skillObj.get("max_fight").getAsInt();
                     skill.damage = skillObj.get("damage").getAsShort();
                     skill.price = skillObj.get("price").getAsShort();
                     skill.moreInfo = skillObj.get("info").getAsString();

                     skillTemplate.skillss.add(skill);
                  }
               } catch (Exception e) {
                  Log.error("Error parsing skills JSON for template ID " + skillTemplate.id + ": " + e.getMessage());
               }
            }
         }

         Log.success("Skills loaded successfully (" + nClasses.size() + " classes, " + totalTemplates + " templates)");

      } catch (Exception e) {
         Log.error("Error loading skill_template table: " + e.getMessage());
      }
   }
}
