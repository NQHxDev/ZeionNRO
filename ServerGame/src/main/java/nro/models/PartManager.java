package nro.models;

import java.util.ArrayList;
import java.util.List;

public class PartManager {

   public static class PartPot {

      public int id;

      public int type;

      public List<PartDetail> partDetails;

      public PartPot() {
         this.partDetails = new ArrayList<>();
      }
   }

   public static class PartDetail {

      public short iconId;

      public byte dx;

      public byte dy;

      public PartDetail(short iconId, byte dx, byte dy) {
         this.iconId = iconId;
         this.dx = dx;
         this.dy = dy;
      }
   }

}
