package server;

import io.Session;
import util.Log;
import java.util.Scanner;

public class CommandLine implements Runnable {

   @Override
   public void run() {
      Scanner sc = new Scanner(System.in);
      while (true) {
         try {
            String line = sc.nextLine();
            if (line == null || line.trim().isEmpty()) {
               continue;
            }

            String command = line.split(" ")[0].toLowerCase();

            switch (command) {
               case "help":
                  showHelp();
                  break;
               case "status":
                  showStatus();
                  break;
               case "reload":
                  Server.getInstance().getConfig().reload();
                  break;
               case "baotri":
                  handleMaintenance(line);
                  sc.close();
                  return;
               case "kick":
                  handleKick(line);
                  break;
               default:
                  Log.warning("Lệnh không hợp lệ. Gõ 'help' để xem danh sách lệnh.");
                  break;
            }
         } catch (Exception e) {
            Log.error("Lỗi xử lý lệnh: " + e.getMessage());
         }
      }
   }

   private void showHelp() {
      Log.info("--- DANH SÁCH LỆNH ---");
      Log.info("status  : Xem tình trạng server");
      Log.info("reload  : Tải lại file server.ini");
      Log.info("kick <id>: Đá một session ra khỏi server");
      Log.info("baotri  : Tắt server an toàn");
      Log.info("help    : Hiển thị danh sách này");
      Log.info("-----------------------");
   }

   private void showStatus() {
      Server server = Server.getInstance();
      long uptimeMillis = System.currentTimeMillis() - server.getStartTime();
      long uptime = uptimeMillis / 1000;
      long hours = uptime / 3600;
      long minutes = (uptime % 3600) / 60;
      long seconds = uptime % 60;

      Log.info("--- SERVER STATUS ---");
      Log.info(String.format("Uptime: %02d:%02d:%02d", hours, minutes, seconds));
      Log.info("Online Sessions: " + server.getManager().getSessions().size());
      Log.info("Memory usage: " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024
            + " MB");
      Log.info("---------------------");
   }

   private void handleKick(String line) {
      String[] parts = line.split(" ");
      if (parts.length < 2) {
         Log.error("Thiếu ID. Sử dụng: kick <id>");
         return;
      }
      try {
         int id = Integer.parseInt(parts[1]);
         Session s = Server.getInstance().getManager().find(id);
         if (s != null) {
            s.disconnect();
            Log.success("Đã kick session ID: " + id);
         } else {
            Log.error("Không tìm thấy session ID: " + id);
         }
      } catch (NumberFormatException e) {
         Log.error("ID không hợp lệ. Sử dụng: kick <id>");
      }
   }

   private void handleMaintenance(String line) {
      String[] parts = line.split(" ");
      int seconds = 1;
      if (parts.length >= 2) {
         try {
            seconds = Integer.parseInt(parts[1]);
         } catch (NumberFormatException e) {
            Log.warning("Thời gian không hợp lệ, sử dụng mặc định 3 giây.");
         }
      }
      Server.getInstance().shutdown(seconds);
   }

}
