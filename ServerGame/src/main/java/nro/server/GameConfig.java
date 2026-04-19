package nro.server;

import lombok.Getter;
import lombok.Setter;
import nro.utils.Log;
import nro.utils.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Setter
@Getter
public class GameConfig {

   private boolean isOpenPrisonPlanet;
   private boolean isOpenSuperMarket;
   private String event;

   public GameConfig() {
      load();
   }

   public void load() {
      Properties properties = new Properties();
      String fileName = "config/game.properties";
      File file = new File(fileName);
      try {
         Log.log("Kiểm tra file: " + file.getAbsolutePath());
         if (!file.exists()) {
            Log.error("Không tìm thấy file: " + fileName);
         } else {
            Log.log("File tồn tại. Đang mở FileInputStream...");
         }

         try (FileInputStream fis = new FileInputStream(file)) {
            Log.log("Đã mở FileInputStream. Đang nạp Properties (Giới hạn 30s)...");
            try {
               Util.runWithTimeout(() -> {
                  try {
                     properties.load(fis);
                  } catch (IOException e) {
                     throw new RuntimeException(e);
                  }
               }, 30, TimeUnit.SECONDS);
               Log.log("Nạp Properties thành công.");
            } catch (Exception e) {
               Log.error("Lỗi nạp game.properties (có thể do treo): " + e.getMessage());
               throw new IOException("Timeout hoặc lỗi khi nạp game.properties", e);
            }
         }

         properties.forEach((key, value) -> {
            System.out.println("[Gameconfig]: " + key + " : " + value);
         });
         isOpenPrisonPlanet = Boolean.parseBoolean(properties.getProperty("open.prisonplanet"));
         isOpenSuperMarket = Boolean.parseBoolean(properties.getProperty("open.supermarket"));
         event = String.valueOf(properties.getProperty("event"));
      } catch (IOException e) {
         Log.error("Lỗi khi load game.properties: " + e.getMessage());
         throw new RuntimeException(e);
      }
   }
}
