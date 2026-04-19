package io;

import nro.network.io.Message;
import model.User;

public class Service {

   private Session session;

   public Service(Session session) {
      this.session = session;
   }

   public void loginSuccessful(User user) {
      try {
         Message ms = Message.create(1);
         ms.writeInt(user.getClientID());
         ms.writeByte(0);
         ms.writeInt(user.getUserID());
         ms.writeBoolean(user.isActived());
         ms.writeBoolean(user.isAdmin());
         ms.writeInt(user.getGoldBar());
         ms.writeLong(user.getLastTimeLogin());
         ms.writeLong(user.getLastTimeLogout());
         ms.writeUTF(user.getRewards());
         ms.writeInt(user.getRuby());
         ms.writeInt(user.getMocNap());
         ms.writeInt(user.getServer());
         this.sendMessage(ms);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void disconnect(int userID) {
      try {
         Message ms = Message.create(3);
         ms.writeInt(userID);
         this.sendMessage(ms);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void updateTimeLogout(int userID) {
      try {
         Message ms = Message.create(6);
         ms.writeInt(userID);
         this.sendMessage(ms);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void serverMessage(int clientID, String text) {
      try {
         Message ms = Message.create(4);
         ms.writeInt(clientID);
         ms.writeUTF(text);
         this.sendMessage(ms);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void loginFailed(int clientID, String text) {
      try {
         Message ms = Message.create(1);
         ms.writeInt(clientID);
         ms.writeByte(1);
         ms.writeUTF(text);
         this.sendMessage(ms);
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void sendMessage(Message ms) {
      this.session.sendMessage(ms);
   }

}
