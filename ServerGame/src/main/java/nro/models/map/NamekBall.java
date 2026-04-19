package nro.models.map;

import lombok.Getter;
import lombok.Setter;
import nro.services.ItemMapService;
import nro.services.Service;

@Getter
@Setter
public class NamekBall extends ItemMap {

   public boolean isHolding;
   public boolean isCleaning;
   public boolean isStone;
   public long cleaningTime;
   public int index;
   public String holderName;

   public NamekBall(Zone zone, int tempId, int quantity, int x, int y, long playerId) {
      super(zone, tempId, quantity, x, y, playerId);
      holderName = "";
   }

   @Override
   public void update() {
      if (isCleaning && cleaningTime > 0) {
         cleaningTime--;
      }
   }

   public void setZone(Zone newZone) {
      this.zone.removeItemMap(this);
      this.zone = newZone;
      this.zone.addItem(this);
   }

   @Override
   public void reAppearItem() {
      if (isHolding) {
         ItemMapService.gI().sendItemMapDisappear(this);
      } else {
         Service.getInstance().dropItemMap(this.zone, this);
      }
   }

}
