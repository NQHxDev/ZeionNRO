/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nro.server;

import lombok.NoArgsConstructor;
import nro.services.ClanService;
import nro.services.Service;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.AllArgsConstructor;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 */
@AllArgsConstructor
@NoArgsConstructor
public class AutoMaintenance {

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
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, initalDelay, 1 * 24 * 60 * 60, TimeUnit.SECONDS);
    }

    public void execute() {
        try {
            Maintenance.isRuning = true;
            int seconds = 60;
            while (seconds > 0) {
                seconds--;
                Service.getInstance().sendThongBaoAllPlayer("Hệ thống sẽ bảo trì định kì sau " + seconds
                        + " giây nữa, vui lòng thoát game để tránh mất vật phẩm.");
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                }
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
            ServerManager.listenSocket.close();
            String executeCommand = Manager.executeCommand;
            if (executeCommand != null) {
                openCmd(executeCommand);
            }
        } catch (IOException ex) {
            Logger.getLogger(AutoMaintenance.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } finally {
            System.exit(0);
        }
    }

    private void openCmd(String cmd) {
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec("cmd /c start cmd.exe /K \"dir && " + cmd);
        } catch (IOException ex) {
            Logger.getLogger(AutoMaintenance.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
