package server;

import db.DbManager;
import io.Controller;
import io.Session;
import nro.network.netty.CommonHandler;
import nro.network.netty.NettyServer;
import util.Log;
import util.LoginScheduler;

public class Server {

   private static final Server instance = new Server();

   private NettyServer nettyServer;

   private final Config config = new Config("server.ini");

   private final ServerManager manager = new ServerManager();

   private final ServerService service = new ServerService(this.manager);

   private boolean running;

   private long startTime;

   private final Controller controller = new Controller();

   public static Server getInstance() {
      return instance;
   }

   public void start() {
      this.startTime = System.currentTimeMillis();
      Log.banner();
      Log.info("Hệ điều hành: " + System.getProperty("os.name"));
      Log.info("Phiên bản Java: " + System.getProperty("java.version"));

      this.activeCommandLine();
      DbManager.getInstance().start();
      this.running = true;

      try {
         byte[] key = new byte[] { 0 };
         CommonHandler handler = new CommonHandler(controller);
         nettyServer = new NettyServer(config.getListen(), key, handler);
         nettyServer.setPublicConfig(config.getGameHost(), config.getGamePort());
         nettyServer.setRedirect(true);

         // Set session factory to create io.Session instead of default NettySession
         nettyServer.setSessionFactory((channel, id) -> new Session(channel, id));

         Log.success("Netty Server đang lắng nghe tại cổng: " + this.config.getListen());
         nettyServer.start();

      } catch (Exception ex) {
         if (this.running) {
            Log.error("Lỗi khi khởi động Netty Server: " + ex.getMessage());
            ex.printStackTrace();
         }
      }
   }

   public void shutdown(int seconds) {
      if (seconds > 0) {
         Log.warning("Hệ thống sẽ bảo trì sau " + seconds + " giây ...");
         try {
            while (seconds > 0) {
               Log.info("Bảo trì trong: " + seconds + "s");
               Thread.sleep(1000L);
               seconds--;
            }
         } catch (InterruptedException e) {
            Log.error("Lỗi khi đang đếm ngược bảo trì ...");
         }
      }
      Log.warning("Đang đóng các kết nối và dừng Server ...");
      this.running = false;

      if (nettyServer != null) {
         nettyServer.stop();
      }

      DbManager.getInstance().shutdown();
   }

   private void activeCommandLine() {
      LoginScheduler.SCHED.execute(new CommandLine());
   }

   public static void main(String[] args) {
      instance.start();
   }

   public long getStartTime() {
      return this.startTime;
   }

   public Config getConfig() {
      return this.config;
   }

   public ServerManager getManager() {
      return this.manager;
   }

   public ServerService getService() {
      return this.service;
   }

}
