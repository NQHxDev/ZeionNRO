package server;

import io.Session;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerManager {

   private final List<Session> sessions = new CopyOnWriteArrayList<>();

   public void add(Session session) {
      this.sessions.add(session);
   }

   public void remove(Session session) {
      this.sessions.remove(session);
   }

   public Session find(int id) {
      for (Session session : this.sessions) {
         if (session.id == id) {
            return session;
         }
      }
      return null;
   }

   public Session findWithServerID(int serverID) {
      for (Session session : this.sessions) {
         if (session.getServerID() == serverID) {
            return session;
         }
      }
      return null;
   }

   public List<Session> getSessions() {
      return this.sessions;
   }
}
