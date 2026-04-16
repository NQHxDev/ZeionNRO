package nro.jdbc.daos.manager;

import nro.models.item.HeadAvatar;
import nro.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class HeadAvatarDAO {

   public static void load(Connection con, List<HeadAvatar> headAvatars) {
      String query = "SELECT * FROM head_avatar";
      try (PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery()) {

         while (rs.next()) {
            headAvatars.add(new HeadAvatar(rs.getInt("head_id"), rs.getInt("avatar_id")));
         }
         Log.success("Head avatars loaded successfully (" + headAvatars.size() + ")");

      } catch (Exception e) {
         Log.error("Error loading head_avatar table: " + e.getMessage());
      }
   }
}
