package nro.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Part {

   public short id;

   byte type;

   public PartImage[] pi;

   public short getIcon(int index) {
      return pi[index].icon;
   }

}
