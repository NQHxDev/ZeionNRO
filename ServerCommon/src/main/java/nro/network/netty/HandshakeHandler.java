package nro.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nro.network.io.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandshakeHandler extends ChannelInboundHandlerAdapter {

   private static final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session == null) return;

      if (!session.isConnected()) {
         // Initial message received, send session key
         sendSessionKey(session);
         session.setConnected(true);

         // Handshake done, remove this handler or it can stay if it doesn't interfere
         // Actually, we should probably remove it to keep pipeline clean
         ctx.pipeline().remove(this);

         // Pass the current message (usually just a dummy or handshake byte) to the next handler
         // or just discard it if it was just to trigger the handshake.
         // In NRO, the first byte is often ignored or treated as a trigger.
         if (msg instanceof Message m) {
               m.cleanup();
         }
      } else {
         ctx.fireChannelRead(msg);
      }
   }

   private void sendSessionKey(NettySession session) {
      byte[] keys = session.getKeys();
      if (keys == null || keys.length == 0) {
         return;
      }

      // Command -27 is GET_SESSION_ID / Send Key
      Message ms = new Message(-27);
      ms.writeByte(keys.length);
      ms.writeByte(keys[0]);
      for (int i = 1; i < keys.length; i++) {
         ms.writeByte(keys[i] ^ keys[i - 1]);
      }
      session.sendMessage(ms);
      logger.info("Sent session key to session {}", session.getId());
   }

}
