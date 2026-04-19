package nro.models.map;

import lombok.Getter;

public class EffectEventTemplate {

   @Getter
   public int mapId;

   @Getter
   public int eventId;

   @Getter
   public int effId;

   @Getter
   public int layer;

   @Getter
   public int x;

   @Getter
   public int y;

   @Getter
   public int loop;

   @Getter
   public int delay;

   public EffectEventTemplate(int mapId, int eventId, int effId, int layer, int x, int y, int loop, int delay) {
      this.mapId = mapId;
      this.eventId = eventId;
      this.effId = effId;
      this.layer = layer;
      this.x = x;
      this.y = y;
      this.loop = loop;
      this.delay = delay;
   }

}
