package nro.models.boss.iboss;

import nro.models.player.Player;

public interface IBossStatus extends IBossInit {

   void attack();

   void idle();

   void checkPlayerDie(Player pl);

   void die();

   void respawn();

}
