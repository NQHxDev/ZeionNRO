package nro.server;

import lombok.NoArgsConstructor;

import java.time.ZoneId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.TimeUnit;

import nro.utils.Log;
import nro.services.Service;
import nro.core.GameScheduler;
import nro.services.ClanService;

@NoArgsConstructor
public class AutoMaintenance {

   public AutoMaintenance(int hours, int minutes, int seconds) {
      this.hours = hours;
      this.minutes = minutes;
      this.seconds = seconds;
   }

   private int hours, minutes, seconds;

   public void start() {
      LocalDateTime localNow = LocalDateTime.now();
      ZoneId currentZone = ZoneId.of("Asia/Ho_Chi_Minh");
      ZonedDateTime zonedNow = ZonedDateTime.of(localNow, currentZone);
      ZonedDateTime zonedNext5;
      zonedNext5 = zonedNow.withHour(hours).withMinute(minutes).withSecond(seconds);
      if (zonedNow.compareTo(zonedNext5) > 0) {
         zonedNext5 = zonedNext5.plusDays(1);
      }

      Duration duration = Duration.between(zonedNow, zonedNext5);

      long initalDelay = duration.getSeconds();
      Runnable runnable = new Runnable() {
         public void run() {
            execute();
         }
      };
      GameScheduler.SCHED.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
   }

   public void execute() {
      try {
         Maintenance.isRuning = true;
         int seconds = 60;
         Log.warning("Hệ thống sẽ bảo trì định kì sau " + seconds + " giây...");
         while (seconds > 0) {
            Log.log("Bảo trì trong: " + seconds + "s");
            Service.getInstance().sendThongBaoAllPlayer("Hệ thống sẽ bảo trì định kì sau " + seconds
                  + " giây nữa, vui lòng thoát game để tránh mất vật phẩm.");
            try {
               Thread.sleep(1000);
            } catch (Exception e) {
            }
            seconds--;
         }
         try {
            Client.gI().close();
         } catch (Exception e) {
            e.printStackTrace();
         }
         try {
            ClanService.gI().close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      } catch (Exception ex) {
         Logger.getLogger(AutoMaintenance.class.getName()).log(Level.SEVERE, null, ex);
         System.exit(1);
      } finally {
         Log.log("Tiến trình Game Server đang thoát để thực hiện bảo trì/khởi động lại...");
         System.exit(0);
      }
   }
}
