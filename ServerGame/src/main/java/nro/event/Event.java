package nro.event;

import nro.models.item.Item;
import nro.models.map.ItemMap;
import nro.models.mob.Mob;
import nro.models.player.Player;
import nro.utils.Log;

import java.util.List;

public abstract class Event {

   private static Event instance;

   public static Event getInstance() {
      return instance;
   }

   public static void initEvent(String event) {
      if (event != null) {
         try {
            instance = (Event) Class.forName(event).getDeclaredConstructor().newInstance();
            Log.success("Event " + event);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static boolean isEvent() {
      return instance != null;
   }

   public abstract void init();

   public abstract void initNpc();

   public abstract void initMap();

   public abstract void dropItem(Player player, Mob mob, List<ItemMap> list, int x, int yEnd);

   public abstract boolean useItem(Player player, Item item);
}
