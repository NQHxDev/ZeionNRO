package nro.power;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

public class PowerLimitManager {

   private static final PowerLimitManager instance = new PowerLimitManager();

   public static PowerLimitManager getInstance() {
      return instance;
   }

   @Getter
   private List<PowerLimit> powers;

   public PowerLimitManager() {
      powers = new ArrayList<>();
   }

   public void add(PowerLimit powerLimit) {
      powers.add(powerLimit);
   }

   public void remove(PowerLimit powerLimit) {
      powers.remove(powerLimit);
   }

   public PowerLimit get(int index) {
      if (index < 0 || index >= powers.size()) {
         return null;
      }
      return powers.get(index);
   }
}
