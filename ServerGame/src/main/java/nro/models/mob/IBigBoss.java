package nro.models.mob;

import nro.models.player.Player;

public interface IBigBoss {

   public void attack(Player player);

   public void move(int x, int y);

}
