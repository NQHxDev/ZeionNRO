package nro.network;

import nro.network.io.Message;

public interface ISession {

   int getId();

   void sendMessage(Message msg);

   void disconnect();

   String getIPString();

   boolean isConnected();

   void setHandler(Object handler);

   Object getHandler();

}
