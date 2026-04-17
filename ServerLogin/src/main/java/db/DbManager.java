package db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import server.Config;
import server.Server;
import util.Log;

public class DbManager {

   private static DbManager instance = null;

   private HikariDataSource hikariDataSource;

   private Connection[] connections = new Connection[5];

   public static DbManager getInstance() {
      if (instance == null) {
         instance = new DbManager();
      }
      return instance;
   }

   private DbManager() {
   }

   public Connection getConnection() throws SQLException {
      return this.hikariDataSource.getConnection();
   }

   public boolean start() {
      if (this.hikariDataSource != null) {
         Log.warning("Kết nối Database đã tồn tại!");
         return false;
      }
      System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "error");
      try {
         Config serverConfig = Server.getInstance().getConfig();
         Log.info("Đang kết nối tới Database: " + serverConfig.getHost() + ":" + serverConfig.getPort() + "/"
               + serverConfig.getDbName());
         HikariConfig config = new HikariConfig();
         config.setJdbcUrl(serverConfig.getJdbcUrl());
         config.setDriverClassName(serverConfig.getDriver());
         config.setUsername(serverConfig.getUsername());
         config.setPassword(serverConfig.getPassword());
         config.addDataSourceProperty("minimumIdle", 2);
         config.addDataSourceProperty("maximumPoolSize", 10);
         config.addDataSourceProperty("cachePrepStmts", "true");
         config.addDataSourceProperty("prepStmtCacheSize", "250");
         config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         this.hikariDataSource = new HikariDataSource(config);
         Log.success("Kết nối Database thành công!");
         return true;
      } catch (Exception e) {
         Log.error("Kết nối Database thất bại: " + e.getMessage());
         return false;
      }
   }

   public Connection getConnectionForLogin() throws SQLException {
      if (this.connections[0] != null && !this.connections[0].isValid(10)) {
         this.connections[0].close();
      }
      if (this.connections[0] == null || this.connections[0].isClosed()) {
         this.connections[0] = this.getConnection();
         return this.getConnectionForLogin();
      }
      return this.connections[0];
   }

   public void shutdown() {
      try {
         if (this.hikariDataSource != null) {
            this.hikariDataSource.close();
            Log.info("Đang đóng kết nối Database...");
         }
         this.hikariDataSource = null;
      } catch (Exception e) {
         System.out.println("Error when shutting down DB Connection Pool");
      }
   }
}
