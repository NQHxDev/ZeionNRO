package nro.network;

import io.netty.channel.Channel;

public interface ISessionFactory {

   ISession createSession(Channel channel, int id);

}
