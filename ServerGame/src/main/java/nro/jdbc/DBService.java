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
   private static final Connection[] connections = new Connection[10];

   private static DBService instance;
   public static String dbName;

   public static DBService gI() {
      if (instance == null) {
         instance = new DBService();
      }

      return instance;
   }

   public synchronized Connection getConnectionForLogin() throws SQLException {
      if (connections[0] != null) {
         if (!connections[0].isValid(10)) {
            connections[0].close();
         }
      }
      if (connections[0] == null || connections[0].isClosed()) {
         try {
            connections[0] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[0];
   }

   public synchronized Connection getConnectionForLogout() throws SQLException {
      if (connections[1] != null) {
         if (!connections[1].isValid(10)) {
            connections[1].close();
         }
      }
      if (connections[1] == null || connections[1].isClosed()) {
         try {
            connections[1] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[1];
   }

   public synchronized Connection getConnectionForSaveData() throws SQLException {
      if (connections[2] != null) {
         if (!connections[2].isValid(10)) {
            connections[2].close();
         }
      }
      if (connections[2] == null || connections[2].isClosed()) {
         try {
            connections[2] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[2];
   }

   public synchronized Connection getConnectionForGame() throws SQLException {
      if (connections[3] != null) {
         if (!connections[3].isValid(10)) {
            connections[3].close();
         }
      }
      if (connections[3] == null || connections[3].isClosed()) {
         try {
            connections[3] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[3];
   }

   public Connection getConnectionForClan() throws SQLException {
      if (connections[4] != null) {
         if (!connections[4].isValid(10)) {
            connections[4].close();
         }
      }
      if (connections[4] == null || connections[4].isClosed()) {
         try {
            connections[4] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[4];
   }

   public Connection getConnectionForAutoSave() throws SQLException {
      if (connections[5] != null) {
         if (!connections[5].isValid(10)) {
            connections[5].close();
         }
      }
      if (connections[5] == null || connections[5].isClosed()) {
         try {
            connections[5] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[5];
   }

   public Connection getConnectionForSaveHistory() throws SQLException {
      if (connections[6] != null) {
         if (!connections[6].isValid(10)) {
            connections[6].close();
         }
      }
      if (connections[6] == null || connections[6].isClosed()) {
         try {
            connections[6] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[6];
   }

   public Connection getConnectionForGetPlayer() throws SQLException {
      if (connections[7] != null) {
         if (!connections[7].isValid(10)) {
            connections[7].close();
         }
      }
      if (connections[7] == null || connections[7].isClosed()) {
         try {
            connections[7] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[7];
   }

   public Connection getConnectionCreatPlayer() throws SQLException {
      if (connections[8] != null) {
         if (!connections[8].isValid(10)) {
            connections[8].close();
         }
      }
      if (connections[8] == null || connections[8].isClosed()) {
         try {
            connections[8] = getConnection();
         } catch (Exception ex) {
            ex.printStackTrace();
         }
      }
      return connections[8];
   }

   public Connection getConnection() throws Exception {
      // return this.connPool.getConnection();
      return DBHika.getConnection();
   }

   public void release(Connection con) {
      // this.connPool.free(con);
   }

   public int currentActive() {
      return -1;
   }

   public int currentIdle() {
      return -1;
   }

}
