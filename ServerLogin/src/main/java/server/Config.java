package server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import util.Log;

public class Config {

   private short listen;

   private String driver;

   private String username;

   private String password;

   private String dbName;

   private String host;

   private short port;

   private short testmode;

   private int secondWaitLogin;

   private String gameHost;

   private int gamePort;

   public Config(String path) {
      try (FileInputStream input = new FileInputStream(new File(path))) {
         Log.success("Tải cấu hình hệ thống thành công!");
         Properties props = new Properties();
         props.load(new InputStreamReader((InputStream) input, StandardCharsets.UTF_8));
         this.listen = Short.parseShort(props.getProperty("server.port"));
         this.driver = props.getProperty("db.driver");
         this.username = props.getProperty("db.user");
         this.password = props.getProperty("db.password");
         this.dbName = props.getProperty("db.name");
         this.host = props.getProperty("db.host");
         this.port = Short.parseShort(props.getProperty("db.port"));
         this.testmode = Short.parseShort(props.getProperty("admin.mode"));
         this.secondWaitLogin = props.containsKey("wait.login")
               ? (int) Short.parseShort(props.getProperty("wait.login"))
               : 5;
         this.gameHost = props.getProperty("game.host", "127.0.0.1");
         this.gamePort = Integer.parseInt(props.getProperty("game.port", "14445"));
      } catch (IOException ex) {
         Log.error("Lỗi khi tải cấu hình: " + ex.getMessage());
      }
   }

   public void reload() {
      try (FileInputStream input = new FileInputStream(new File("server.ini"))) {
         Properties props = new Properties();
         props.load(new InputStreamReader((InputStream) input, StandardCharsets.UTF_8));
         this.listen = Short.parseShort(props.getProperty("server.port"));
         this.driver = props.getProperty("db.driver");
         this.username = props.getProperty("db.user");
         this.password = props.getProperty("db.password");
         this.dbName = props.getProperty("db.name");
         this.host = props.getProperty("db.host");
         this.port = Short.parseShort(props.getProperty("db.port"));
         this.testmode = Short.parseShort(props.getProperty("admin.mode"));
         this.secondWaitLogin = props.containsKey("wait.login")
               ? (int) Short.parseShort(props.getProperty("wait.login"))
               : 5;
         this.gameHost = props.getProperty("game.host", "127.0.0.1");
         this.gamePort = Integer.parseInt(props.getProperty("game.port", "14445"));
         Log.success("Cấu hình đã được tải lại thành công!");
      } catch (IOException ex) {
         Log.error("Lỗi khi tải lại cấu hình: " + ex.getMessage());
      }
   }

   public String getJdbcUrl() {
      return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.dbName;
   }

   public short getListen() {
      return this.listen;
   }

   public String getDriver() {
      return this.driver;
   }

   public String getUsername() {
      return this.username;
   }

   public String getPassword() {
      return this.password;
   }

   public String getDbName() {
      return this.dbName;
   }

   public String getHost() {
      return this.host;
   }

   public short getPort() {
      return this.port;
   }

   public short getTestmode() {
      return this.testmode;
   }

   public int getSecondWaitLogin() {
      return this.secondWaitLogin;
   }

   public void setListen(short listen) {
      this.listen = listen;
   }

   public void setDriver(String driver) {
      this.driver = driver;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public void setDbName(String dbName) {
      this.dbName = dbName;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public void setPort(short port) {
      this.port = port;
   }

   public void setTestmode(short testmode) {
      this.testmode = testmode;
   }

   public void setSecondWaitLogin(int secondWaitLogin) {
      this.secondWaitLogin = secondWaitLogin;
   }

   public String getGameHost() {
      return gameHost;
   }

   public int getGamePort() {
      return gamePort;
   }

}
