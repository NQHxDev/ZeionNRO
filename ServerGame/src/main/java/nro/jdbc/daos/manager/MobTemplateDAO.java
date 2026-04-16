package nro.jdbc.daos.manager;

import nro.models.mob.MobTemplate;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class MobTemplateDAO {

   public static void load(Connection con, List<MobTemplate> mobTemplates) {
      String query = "SELECT * FROM mob_template";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            MobTemplate mobTemp = new MobTemplate();
            mobTemp.id = rs.getByte("id");
            mobTemp.type = rs.getByte("type");
            mobTemp.name = rs.getString("name");
            mobTemp.hp = rs.getInt("hp");
            mobTemp.rangeMove = rs.getByte("range_move");
            mobTemp.speed = rs.getByte("speed");
            mobTemp.dartType = rs.getByte("dart_type");
            mobTemp.percentDame = rs.getByte("percent_dame");
            mobTemp.percentTiemNang = rs.getByte("percent_tiem_nang");
            mobTemplates.add(mobTemp);
         }
         Log.success("Mob templates loaded successfully (" + mobTemplates.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading mob_template table: " + e.getMessage());
      }
   }
}
