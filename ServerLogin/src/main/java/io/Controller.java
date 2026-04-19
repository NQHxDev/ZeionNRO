package io;

import nro.network.IController;
import nro.network.ISession;
import nro.network.io.Message;
import util.Log;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import model.User;
import model.UserManager;

public class Controller implements IController {

   private final Lock lock = new ReentrantLock();

   @Override
   public void onMessage(ISession session, Message ms) {
      Session s = (Session) session;
      switch (ms.getCommand()) {
         case 1: {
            this.login(s, ms);
            break;
         }
         case 2: {
            this.logout(ms);
            break;
         }
         case 5: {
            s.setServer(ms);
            break;
         }
         default: {
            Log.info("Unknown cmd: " + ms.getCommand());
         }
      }
   }

   @Override
   public void onConnectionReady(ISession session) {
      Log.info("Client connection ready! IP: " + session.getIPString());
   }

   @Override
   public void onDisconnected(ISession session) {
      Session s = (Session) session;
      UserManager.getInstance().removeAllUserWithServerID(s.getServerID());
      Log.info("Client disconnected: " + s.sessionName);
   }

   public void logout(Message ms) {
      try {
         int userID = ms.readInt();
         User user = UserManager.getInstance().find(userID);
         if (user != null) {
            UserManager.getInstance().remove(user);
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void login(Session session, Message ms) {
      try {
         byte serverID = ms.readByte();
         int clientID = ms.readInt();
         String username = ms.readUTF();
         String password = ms.readUTF();
         this.lock.lock();
         try {
            User user = new User(username, password, serverID, clientID, session);
            boolean result = user.login();
            if (result) {
               UserManager.getInstance().add(user);
            }
         } finally {
            this.lock.unlock();
         }
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

}
