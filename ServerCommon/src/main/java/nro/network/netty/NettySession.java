package nro.network.netty;

import io.netty.channel.Channel;
import nro.network.ISession;
import nro.network.io.Message;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public class NettySession implements ISession {

   @Getter
   private final int id;
   private final Channel channel;

   @Getter @Setter
   private byte[] keys;
   @Getter @Setter
   private byte curR, curW;

   @Getter @Setter
   private boolean connected;

   @Getter @Setter
   private Object handler;

   public NettySession(Channel channel, int id) {
      this.channel = channel;
      this.id = id;
   }

   @Override
   public void sendMessage(Message msg) {
      if (channel.isActive()) {
         channel.writeAndFlush(msg);
      }
   }

   @Override
   public void disconnect() {
      if (channel.isActive()) {
         channel.close();
      }
   }

   @Override
   public String getIPString() {
      if (channel.remoteAddress() instanceof InetSocketAddress addr) {
         return addr.getAddress().getHostAddress();
      }
      return channel.remoteAddress().toString();
   }

   public byte readKey(byte b) {
      byte i = (byte) ((keys[curR++] & 255) ^ (b & 255));
      if (curR >= keys.length) {
         curR %= keys.length;
      }
      return i;
   }

   public byte writeKey(byte b) {
      byte i = (byte) ((keys[curW++] & 255) ^ (b & 255));
      if (curW >= keys.length) {
         curW %= keys.length;
      }
      return i;
   }

}
