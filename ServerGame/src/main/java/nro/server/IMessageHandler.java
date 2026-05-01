package nro.server;

import nro.network.io.Message;
import nro.models.player.Player;

public interface IMessageHandler {

   void handle(Player player, Message msg) throws Exception;

}
