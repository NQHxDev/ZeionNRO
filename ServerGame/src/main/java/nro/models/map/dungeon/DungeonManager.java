package nro.models.map.dungeon;

import java.util.ArrayList;
import java.util.List;

public class DungeonManager {

   public final List<Dungeon> list = new ArrayList<>();

   public int increasement;

   public int generateID() {
      return increasement++;
   }

   public void addDungeon(Dungeon dungeon) {
      synchronized (list) {
         dungeon.id = generateID();
         list.add(dungeon);
      }
   }

   public void removeDungeon(Dungeon dungeon) {
      synchronized (list) {
         list.remove(dungeon);
      }
   }

   public Dungeon find(int id) {
      synchronized (list) {
         for (Dungeon dungeon : list) {
            if (dungeon.id == id) {
               return dungeon;
            }
         }
      }
      return null;
   }

   public void update() {
      synchronized (list) {
         List<Dungeon> r = new ArrayList<>();
         for (Dungeon dungeon : list) {
            try {
               dungeon.update();
            } catch (Exception e) {
            }
            if (dungeon.isClosed()) {
               r.add(dungeon);
            }
         }
         list.removeAll(r);
      }
   }
}
