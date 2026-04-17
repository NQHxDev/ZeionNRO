package io;

import java.io.IOException;
import java.io.DataOutputStream;

import model.User;

public class Service {

   private Session session;

   public Service(Session session) {
      this.session = session;
   }

   public void loginSuccessful(User user) {
      try {
         Message ms = new Message(1);
         DataOutputStream ds = ms.writer();
         ds.writeInt(user.getClientID());
         ds.writeByte(0);
         ds.writeInt(user.getUserID());
         ds.writeBoolean(user.isAdmin());
         ds.writeBoolean(user.isActived());
         ds.writeInt(user.getGoldBar());
         ds.writeLong(user.getLastTimeLogin());
         ds.writeLong(user.getLastTimeLogout());
         ds.writeUTF(user.getRewards());
         ds.writeInt(user.getRuby());
         ds.writeInt(user.getMocNap());
         ds.writeInt(user.getServer());
         ds.flush();
         this.sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void disconnect(int userID) {
      try {
         Message ms = new Message(3);
         DataOutputStream ds = ms.writer();
         ds.writeInt(userID);
         ds.flush();
         this.sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void updateTimeLogout(int userID) {
      try {
         Message ms = new Message(6);
         DataOutputStream ds = ms.writer();
         ds.writeInt(userID);
         ds.flush();
         this.sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void serverMessage(int clientID, String text) {
      try {
         Message ms = new Message(4);
         DataOutputStream ds = ms.writer();
         ds.writeInt(clientID);
         ds.writeUTF(text);
         ds.flush();
         this.sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void loginFailed(int clientID, String text) {
      try {
         Message ms = new Message(1);
         DataOutputStream ds = ms.writer();
         ds.writeInt(clientID);
         ds.writeByte(1);
         ds.writeUTF(text);
         ds.flush();
         this.sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void sendMessage(Message ms) {
      this.session.sendMessage(ms);
   }
}
