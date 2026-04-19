package nro.models.map.dungeon;

import nro.consts.Cmd;
import nro.models.map.dungeon.zones.ZDungeon;
import nro.models.player.Player;
import nro.network.io.Message;
import nro.services.Service;
import nro.utils.Log;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Dungeon {

   public static final int SNAKE_ROAD = 2;

   public final List<ZDungeon> zones = new ArrayList<>();
   public int id;
   public int type;
   public int level;
   public String name;
   public long createdAt;
   public int countDown;
   public String title;
   public boolean closed;
   public boolean finish;

   public Dungeon(int level) {
      createdAt = System.currentTimeMillis();
      this.level = level;
      init();
   }

   public abstract void init();

   public void addZone(ZDungeon zone) {
      synchronized (zones) {
         zones.add(zone);
      }
   }

   public void removeZone(ZDungeon zone) {
      synchronized (zones) {
         zones.remove(zone);
      }
   }

   public ZDungeon find(int mapID) {
      synchronized (zones) {
         for (ZDungeon zone : zones) {
            if (zone.map.mapId == mapID) {
               return zone;
            }
         }
      }
      return null;
   }

   public void update() {
      if (countDown > 0) {
         countDown--;
         if (countDown == 0) {
            close();
         }
      }
   }

   public abstract void finish();

   public abstract void join(Player player);

   public void setTime(int countDown) {
      this.countDown = countDown;
      synchronized (zones) {
         zones.forEach((z) -> {
            z.setTextTime();
         });
      }
   }

   public void close() {
      if (!closed) {
         closed = true;
         synchronized (zones) {
            zones.forEach((z) -> {
               z.close();
            });
            zones.clear();
         }
      }
   }

   public void sendNotification(String text) {
      try {
         Message ms = Message.create(Cmd.SERVER_MESSAGE);
         DataOutputStream ds = ms.writer();
         ds.writeUTF(text);
         ds.flush();
         sendMessage(ms);
         ms.cleanup();
      } catch (IOException ex) {
         Log.error(Dungeon.class, ex);
      }
   }

   public void sendMessage(Message ms) {
      synchronized (zones) {
         zones.forEach((zone) -> {
            Service.getInstance().sendMessAllPlayerInMap(zone, ms);
         });
      }
   }
}
