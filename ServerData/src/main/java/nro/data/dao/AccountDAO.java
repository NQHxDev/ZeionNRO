package nro.data.dao;

import nro.data.db.DbManager;
import nro.data.model.Account;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO {

   public static Account findAccount(String username, String password) {
      String query = "SELECT * FROM account WHERE username = ? AND password = ? LIMIT 1";

      try (Connection conn = DbManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {

         ps.setString(1, username);
         ps.setString(2, password);

         try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
               return Account.builder()
                     .id(rs.getInt("id"))
                     .username(rs.getString("username"))
                     .password(rs.getString("password"))
                     .serverLogin(rs.getInt("server_login"))
                     .isAdmin(rs.getBoolean("is_admin"))
                     .active(rs.getBoolean("active"))
                     .goldBar(rs.getInt("thoi_vang"))
                     .lastTimeLogin(rs.getTimestamp("last_time_login"))
                     .lastTimeLogout(rs.getTimestamp("last_time_logout"))
                     .reward(rs.getString("reward"))
                     .ruby(rs.getInt("ruby"))
                     .countCard(rs.getInt("count_card"))
                     .ban(rs.getBoolean("ban"))
                     .build();
            }
         }
      } catch (SQLException e) {
         e.printStackTrace();
      }

      return null;
   }

   public static void updateLogoutTime(int accountId) {
      String query = "UPDATE account SET last_time_logout = CURRENT_TIMESTAMP WHERE id = ?";

      try (Connection conn = DbManager.getInstance().getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {

         ps.setInt(1, accountId);
         ps.executeUpdate();
      } catch (SQLException e) {
         e.printStackTrace();
      }
   }

}
