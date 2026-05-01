package nro.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import nro.utils.Log;

import java.sql.Connection;
import java.sql.SQLException;

public class DBHika {

   private static HikariConfig config = new HikariConfig();

   private static HikariDataSource ds;

   static {
      config.setDriverClassName(DBService.DRIVER);
      config.setJdbcUrl(DBService.URL
            .replaceAll("#0", "mysql")
            .replaceAll("#1", DBService.DB_HOST)
            .replaceAll("#2", DBService.DB_PORT + "")
            .replaceAll("#3", DBService.DB_NAME));
      config.setUsername(DBService.DB_USER);
      config.setPassword(DBService.DB_PASSWORD);
      config.setMinimumIdle(10);
      config.setMaximumPoolSize(50);
      config.setConnectionTimeout(30000); // 30s
      config.setIdleTimeout(600000); // 10 mins
      config.setMaxLifetime(1800000); // 30 mins
      config.setInitializationFailTimeout(-1); // Đợi DB khởi động, không crash ngay
      config.setLeakDetectionThreshold(30000); // 30s để phát hiện rò rỉ kết nối
      config.setValidationTimeout(5000); // 5s

      config.addDataSourceProperty("cachePrepStmts", "true");
      config.addDataSourceProperty("prepStmtCacheSize", "250");
      config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
      config.addDataSourceProperty("useServerPrepStmts", "true");
      config.addDataSourceProperty("useLocalSessionState", "true");
      config.addDataSourceProperty("rewriteBatchedStatements", "true");
      config.addDataSourceProperty("cacheResultSetMetadata", "true");
      config.addDataSourceProperty("cacheServerConfiguration", "true");
      config.addDataSourceProperty("elideSetAutoCommits", "true");
      config.addDataSourceProperty("maintainTimeStats", "false");
      config.addDataSourceProperty("characterEncoding", "utf8");
      config.addDataSourceProperty("useUnicode", "true");

      ds = new HikariDataSource(config);
      Log.info("Đang khởi tạo kết nối Database ...");

      new Thread(() -> {
         try {
            Connection conn = ds.getConnection();
            if (conn != null) {
               Log.success("Kết nối Database thành công!");
               conn.close();
            }
         } catch (SQLException e) {
         }
      }).start();
   }

   public static Connection getConnection() throws SQLException {
      return ds.getConnection();
   }

   public static void close() {
      DBHika.ds.close();
   }

}
