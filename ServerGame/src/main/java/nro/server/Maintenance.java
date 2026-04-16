package nro.server;

import nro.services.Service;
import nro.utils.Log;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class Maintenance extends Thread {

   public static boolean isRuning = false;

   private static Maintenance i;

   private int seconds;

   private Maintenance() {

   }

   public static Maintenance gI() {
      if (i == null) {
         i = new Maintenance();
      }
      return i;
   }

   public void start(int seconds) {
      if (!isRuning) {
         isRuning = true;
         this.seconds = seconds;
         this.start();
      }
   }

   @Override
   public void run() {
      Log.warning("Hệ thống sẽ bảo trì sau " + seconds + " giây...");
      while (this.seconds > 0) {
         Log.log("Bảo trì trong: " + seconds + "s");
         Service.getInstance().sendThongBaoAllPlayer("Hệ thống sẽ bảo trì sau " + seconds
               + " giây nữa, vui lòng thoát game để tránh mất vật phẩm");
         try {
            Thread.sleep(1000);
         } catch (Exception e) {
         }
         this.seconds--;
      }
      Log.warning("----------------------------------------------------");
      Log.warning("      >>> SYSTEM MAINTENANCE STARTED <<<      ");
      Log.warning("----------------------------------------------------");
      ServerManager.gI().close(100);
   }

}
