package server;

import db.DbManager;
import io.Session;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.Log;

public class Server {
   private static final Server instance = new Server();
   private ServerSocket listen;
   private Config config = new Config("server.ini");
   private ServerManager manager = new ServerManager();
   private ServerService service = new ServerService(this.manager);
   private boolean running;
   private long startTime;

   public static Server getInstance() {
      return instance;
   }

   public void start() {
      this.startTime = System.currentTimeMillis();
      Log.banner();
      Log.info("Hệ điều hành: " + System.getProperty("os.name"));
      Log.info("Phiên bản Java: " + System.getProperty("java.version"));
      block3: {
         this.activeCommandLine();
         DbManager.getInstance().start();
         this.running = true;
         try {
            this.listen = new ServerSocket(this.config.getListen());
            Log.success("Server đang lắng nghe tại cổng: " + this.config.getListen());
            int i = 0;
            while (this.running) {
               Session session = new Session(this.listen.accept(), i++);
               Log.info("Client connected! IP: " + session.sc.getInetAddress().getHostAddress() + " | Session Name: "
                     + session.sessionName);
            }
         } catch (IOException ex) {
            if (!this.running)
               break block3;
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
         }
      }
   }

   public void shutdown(int seconds) {
      if (seconds > 0) {
         Log.warning("Hệ thống sẽ bảo trì sau " + seconds + " giây...");
         try {
            while (seconds > 0) {
               Log.info("Bảo trì trong: " + seconds + "s");
               Thread.sleep(1000L);
               seconds--;
            }
         } catch (InterruptedException e) {
            Log.error("Lỗi khi đang đếm ngược bảo trì.");
         }
      }
      Log.warning("Đang đóng các kết nối và dừng Server...");
      this.running = false;
      try {
         if (this.listen != null) {
            this.listen.close();
         }
      } catch (IOException iOException) {
         // empty catch block
      }
      DbManager.getInstance().shutdown();
   }

   private void activeCommandLine() {
      new Thread(new CommandLine(), "Active line").start();
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
