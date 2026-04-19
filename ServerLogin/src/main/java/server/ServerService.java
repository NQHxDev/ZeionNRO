package server;

import nro.network.io.Message;
import io.Session;

public class ServerService {

   private final ServerManager manager;

   public ServerService(ServerManager manager) {
      this.manager = manager;
   }

   public void disconnect(int userID, Session except) {
      try {
         Message ms = Message.create(3);
         ms.writeInt(userID);
         for (Session session : this.manager.getSessions()) {
            if (session != except) {
               session.sendMessage(ms);
            }
         }
         ms.cleanup();
      } catch (Exception ex) {
         ex.printStackTrace();
      }
   }

   public void sendMessage(Message ms) {
      for (Session session : this.manager.getSessions()) {
         session.sendMessage(ms);
      }
   }

}
