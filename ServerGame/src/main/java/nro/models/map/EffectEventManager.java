package nro.models.map;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EffectEventManager {

   private static final EffectEventManager i = new EffectEventManager();

   public static EffectEventManager gI() {
      return i;
   }

   @Getter
   private final List<EffectEventTemplate> templates = new ArrayList<>();

   public void add(EffectEventTemplate ee) {
      templates.add(ee);
   }
}
