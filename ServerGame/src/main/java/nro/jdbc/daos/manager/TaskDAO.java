package nro.jdbc.daos.manager;

import nro.models.task.SubTaskMain;
import nro.models.task.TaskMain;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TaskDAO {

   public static void load(Connection con, List<TaskMain> tasks) {
      String query = "SELECT id, task_main_template.name, detail, "
            + "task_sub_template.name AS 'sub_name', max_count, notify, npc_id, map "
            + "FROM task_main_template JOIN task_sub_template ON task_main_template.id = "
            + "task_sub_template.task_main_id";

      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         int currentTaskId = -1;
         TaskMain currentTask = null;

         while (rs.next()) {
            int id = rs.getInt("id");
            if (id != currentTaskId) {
               currentTaskId = id;
               currentTask = new TaskMain();
               currentTask.id = id;
               currentTask.name = rs.getString("name");
               currentTask.detail = rs.getString("detail");
               tasks.add(currentTask);
            }

            if (currentTask != null) {
               SubTaskMain subTask = new SubTaskMain();
               subTask.name = rs.getString("sub_name");
               subTask.maxCount = rs.getShort("max_count");
               subTask.notify = rs.getString("notify");
               subTask.npcId = rs.getByte("npc_id");
               subTask.mapId = rs.getShort("map");
               currentTask.subTasks.add(subTask);
            }
         }
         Log.success("Main tasks loaded successfully (" + tasks.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading task_main_template table: " + e.getMessage());
      }
   }
}
