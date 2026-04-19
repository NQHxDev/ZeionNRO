package nro.models.item;

import lombok.Getter;

public class MinipetTemplate {

   public int id;

   @Getter
   public short head;

   @Getter
   public short body;

   @Getter
   public short leg;

   public MinipetTemplate(int id, short head, short body, short leg) {
      this.id = id;
      this.head = head;
      this.body = body;
      this.leg = leg;
   }

}
