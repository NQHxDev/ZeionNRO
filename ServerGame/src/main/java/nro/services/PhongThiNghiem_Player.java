package nro.services;

import lombok.Getter;
import lombok.Setter;

public class PhongThiNghiem_Player {

   @Getter
   @Setter
   public int id;

   @Getter
   @Setter
   public long time;

   public PhongThiNghiem_Player(int id, long time) {
      this.id = id;
      this.time = time;
   }

}
