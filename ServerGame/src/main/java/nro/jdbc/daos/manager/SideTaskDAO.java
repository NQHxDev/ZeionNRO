package nro.jdbc.daos.manager;

import nro.models.task.SideTaskTemplate;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class SideTaskDAO {

   public static void load(Connection con, List<SideTaskTemplate> sideTasks) {
      String query = "SELECT * FROM side_task_template";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            SideTaskTemplate sideTask = new SideTaskTemplate();
            sideTask.id = rs.getInt("id");
            sideTask.name = rs.getString("name");

            // Parse max_count levels (hyphen-separated strings)
            for (int i = 1; i <= 5; i++) {
               String mcStr = rs.getString("max_count_lv" + i);
               if (mcStr != null && mcStr.contains("-")) {
                  String[] mcv = mcStr.split("-");
                  sideTask.count[i - 1][0] = Integer.parseInt(mcv[0]);
                  sideTask.count[i - 1][1] = Integer.parseInt(mcv[1]);
               }
            }
            sideTasks.add(sideTask);
         }
         Log.success("Side tasks loaded successfully (" + sideTasks.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading side_task_template table: " + e.getMessage());
      }
   }
}
