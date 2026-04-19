package nro.network;

import nro.network.io.Message;

public interface IController {

   void onMessage(ISession session, Message msg);

   void onConnectionReady(ISession session);

   void onDisconnected(ISession session);

}
