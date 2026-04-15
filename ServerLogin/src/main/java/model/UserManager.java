package model;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class UserManager {

   private static final UserManager instance = new UserManager();
   private final List<User> users = new CopyOnWriteArrayList<>();

   public static UserManager getInstance() {
      return instance;
   }

   public void add(User user) {
      this.users.add(user);
   }

   public void remove(User user) {
      this.users.remove(user);
   }

   public User find(int userID) {
      for (User user : this.users) {
         if (user.getUserID() == userID) {
            return user;
         }
      }
      return null;
   }

   public void removeAllUserWithServerID(int serverID) {
      this.users.removeIf(t -> t.getServerID() == serverID);
   }
}
