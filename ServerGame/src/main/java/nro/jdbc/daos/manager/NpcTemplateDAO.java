package nro.jdbc.daos.manager;

import nro.models.npc.NpcTemplate;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class NpcTemplateDAO {

   public static void load(Connection con, Map<Integer, NpcTemplate> npcTemplates) {
      String query = "SELECT * FROM npc_template";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            NpcTemplate npcTemp = new NpcTemplate();
            npcTemp.id = rs.getByte("id");
            npcTemp.name = rs.getString("name");
            npcTemp.head = rs.getShort("head");
            npcTemp.body = rs.getShort("body");
            npcTemp.leg = rs.getShort("leg");
            npcTemplates.put(npcTemp.id, npcTemp);
         }
         Log.success("NPC templates loaded successfully (" + npcTemplates.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading npc_template table: " + e.getMessage());
      }
   }
}
