package nro.power;

import nro.models.player.Player;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class CaptionManager {

   private static final CaptionManager instance = new CaptionManager();

   public static CaptionManager getInstance() {
      return instance;
   }

   @Getter
   private List<Caption> captions;

   public CaptionManager() {
      captions = new ArrayList<>();
   }

   public void add(Caption caption) {
      captions.add(caption);
   }

   public void remove(Caption caption) {
      captions.remove(caption);
   }

   public Caption find(int id) {
      for (Caption caption : captions) {
         if (caption.getId() == id) {
            return caption;
         }
      }
      return null;
   }

   public int getLevel(Player player) {
      try {
         double power = player.nPoint.power;
         int size = captions.size();
         int level = 0;
         for (int i = size - 1; i >= 0; i--) {
            double p = captions.get(i).getPower();
            if (power >= p) {
               level = i;
               break;
            }
         }
         return level;
      } catch (Exception e) {

      }
      return 0;
   }
}
