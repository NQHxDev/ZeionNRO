package io;

import io.netty.channel.Channel;
import nro.network.netty.NettySession;
import nro.network.io.Message;
import model.User;
import model.UserManager;
import server.Server;

public class Session extends NettySession {

   public String sessionName;
   private int serverID;
   private Controller controller;
   private Service service;
   public boolean isLoginSuccess;
   public boolean isClosed;

   public Session(Channel channel, int id) {
      super(channel, id);
      this.sessionName = channel.remoteAddress().toString();
      this.controller = new Controller(); // Controller is now stateless/shared or instantiated per session
      this.service = new Service(this);
      Server.getInstance().getManager().add(this);
   }

   @Override
   public void sendMessage(Message message) {
      super.sendMessage(message);
   }

   public void setServer(Message ms) {
      try {
         this.serverID = ms.readInt();
         UserManager.getInstance().removeAllUserWithServerID(this.serverID);
         int size = ms.readInt();
         for (int i = 0; i < size; ++i) {
            int clientID = ms.readInt();
            int userID = ms.readInt();
            String username = ms.readUTF();
            String password = ms.readUTF();
            User user = new User(username, password, this.serverID, clientID, this);
            user.setUserID(userID);
            UserManager.getInstance().add(user);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void close() {
      try {
         Server.getInstance().getManager().remove(this);
         disconnect();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public void closeMessage() {
      try {
         if (this.isClosed) {
            return;
         }
         this.isClosed = true;
         this.controller.onDisconnected(this);
         this.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public int getServerID() {
      return this.serverID;
   }

   public Service getService() {
      return this.service;
   }

   public Controller getController() {
      return this.controller;
   }

}
