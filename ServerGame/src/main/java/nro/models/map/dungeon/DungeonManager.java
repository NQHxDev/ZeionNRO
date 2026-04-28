package nro.models.map.dungeon;

import nro.core.Tickable;
import java.util.ArrayList;
import java.util.List;

public class DungeonManager implements Tickable {

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

   @Override
   public void tick(long nowMillis) throws Exception {
      update();
   }

   @Override
   public int periodMs() {
      return 1000;
   }

   @Override
   public boolean isActive() {
      return !list.isEmpty();
   }
}
