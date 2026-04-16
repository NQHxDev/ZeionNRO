package nro.notification;

import nro.consts.Cmd;
import nro.models.player.Player;
import nro.server.io.Message;
import nro.services.Service;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NotiManager {

   private static NotiManager instance;

   public static NotiManager getInstance() {
      if (instance == null) {
         instance = new NotiManager();
      }

      return instance;
   }

   private static List<Notification> notifications = new ArrayList<>();

   public List<Notification> getNotifications() {
      return notifications;
   }

   private static Alert alert;

   public static void setAlert(Alert alert) {
      NotiManager.alert = alert;
   }

   // private void addNoti(Notification noti) {
   // notifications.add(noti);
   // }

   public void sendAlert(Player player) {
      Service.getInstance().sendThongBaoFromAdmin(player, alert.content);
   }

   public void sendNoti(Player player) {
      Message m = new Message(Cmd.GAME_INFO);
      try {
         DataOutputStream ds = m.writer();
         ds.writeByte(notifications.size());
         for (Notification notification : notifications) {
            ds.writeShort(notification.getId());
            ds.writeUTF(notification.getTitle());
            ds.writeUTF(notification.getContent());
         }
         ds.flush();
         player.sendMessage(m);
         m.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }
}
