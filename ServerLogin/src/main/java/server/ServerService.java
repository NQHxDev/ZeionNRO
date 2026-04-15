package server;

import io.Message;
import io.Session;
import java.io.IOException;
import java.io.DataOutputStream;

public class ServerService {
   private final ServerManager manager;

   public ServerService(ServerManager manager) {
      this.manager = manager;
   }

   public void disconnect(int userID, Session except) {
      try {
         Message ms = new Message(3);
         DataOutputStream ds = ms.writer();
         ds.writeInt(userID);
         ds.flush();
         for (Session session : this.manager.getSessions()) {
            if (session != except) {
               session.sendMessage(ms);
            }
         }
         ms.cleanup();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   public void sendMessage(Message ms) {
      for (Session session : this.manager.getSessions()) {
         session.sendMessage(ms);
      }
   }
}
