package nro.models.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Achivement {

   public int id;

   public String name;

   public String detail;

   public int money;

   public int count;

   public int maxCount;

   public boolean isFinish;

   public boolean isReceive;

   public boolean isDone() {
      return this.count >= this.maxCount;
   }

   public boolean isDone(int divisor) {
      return this.count / divisor >= this.maxCount / divisor;
   }

   public int getCount() {
      return this.count;
   }

   public int getMaxCount() {
      return this.maxCount;
   }
}
