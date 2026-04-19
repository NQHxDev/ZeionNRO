package nro.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartImage {

   public short icon;

   public byte dx;

   public byte dy;

   public PartImage(short icon, byte dx, byte dy) {
      this.icon = icon;
      this.dx = dx;
      this.dy = dy;
   }

}
