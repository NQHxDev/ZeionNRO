package nro.models.map;

import nro.models.player.NPoint;
import nro.models.player.Player;
import nro.utils.Util;

public class SatelliteDefense extends Satellite {

   public SatelliteDefense(Zone zone, int itemID, int x, int y, Player player) {
      super(zone, itemID, x, y, player);
      this.delayBuff = 1000;
   }

   @Override
   public void buff(Player pl) {
      int r = Util.getDistance(pl.location.x, pl.location.y, x, y);
      NPoint n = pl.nPoint;
      if (n != null) {
         if (r <= range) {
            n.buffDefenseSatellite = true;
         } else {
            n.buffDefenseSatellite = false;
         }
      }
   }

}
