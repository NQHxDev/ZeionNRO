package nro.data.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DbManager {

   private static DbManager instance;

   private HikariDataSource dataSource;

   public static DbManager getInstance() {
      if (instance == null) {
         instance = new DbManager();
      }
      return instance;
   }

   private DbManager() {
   }

   public void init(String url, String user, String password, String driver) {
      if (this.dataSource != null) {
         return;
      }

      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(url);
      config.setUsername(user);
      config.setPassword(password);
      config.setDriverClassName(driver);

      // Cấu hình tối ưu cho MySQL
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

      // Cấu hình Pool
      config.setMinimumIdle(5);
      config.setMaximumPoolSize(20);
      config.setIdleTimeout(300000);
      config.setConnectionTimeout(10000);

      this.dataSource = new HikariDataSource(config);
   }

   public Connection getConnection() throws SQLException {
      if (this.dataSource == null) {
         throw new SQLException("DbManager chưa được khởi tạo!");
      }
      return this.dataSource.getConnection();
   }

   public void shutdown() {
      if (this.dataSource != null) {
         this.dataSource.close();
         this.dataSource = null;
      }
   }

}
