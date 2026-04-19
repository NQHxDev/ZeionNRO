package nro.power;

import lombok.Getter;
import lombok.Setter;

public class Caption {

   @Getter
   @Setter
   public int id;

   @Getter
   @Setter
   public String earth, saiya, namek;

   @Getter
   @Setter
   public long power;

   public String getCaption(int planet) {
      String caption = earth;
      if (planet == 1) {
         caption = namek;
      } else if (planet == 2) {
         caption = saiya;
      }
      return caption;
   }

}
