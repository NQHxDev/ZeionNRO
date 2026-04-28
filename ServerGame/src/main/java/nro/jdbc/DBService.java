package nro.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public class DBService {

   public static String DRIVER = "com.mysql.cj.jdbc.Driver";

   public static String URL = "jdbc:#0://#1:#2/#3";

   public static String DB_HOST = "gatewayservergame.nroacademy.online";

   public static int DB_PORT = 3306;

   public static String DB_NAME = "servergame";

   public static String DB_USER = "root";

   public static String DB_PASSWORD = "cdmnopqHIJKLMNOPQR@STUVWXYrstuEF@GZabxyz";

   public static int MAX_CONN = 2;

   private static DBService instance;

   public static String dbName;

   public static DBService gI() {
      if (instance == null) {
         instance = new DBService();
      }
      return instance;
   }

   public Connection getConnection() throws SQLException {
      return DBHika.getConnection();
   }

   public void release(Connection con) {
      if (con != null) {
         try {
            con.close();
         } catch (SQLException e) {
            e.printStackTrace();
         }
      }
   }

   public int currentActive() {
      return -1;
   }

   public int currentIdle() {
      return -1;
   }

}
