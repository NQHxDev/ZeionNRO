package nro.jdbc.daos.manager;

import nro.models.item.HeadAvatar;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

public class HeadAvatarDAO {

   public static void load(Connection con, Map<Integer, HeadAvatar> headAvatars) {
      String query = "SELECT * FROM head_avatar";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
             HeadAvatar headAvatar = new HeadAvatar(rs.getInt("head_id"), rs.getInt("avatar_id"));
             headAvatars.put(headAvatar.headId, headAvatar);
         }
         Log.success("Head avatars loaded successfully (" + headAvatars.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading head_avatar table: " + e.getMessage());
      }
   }
}
