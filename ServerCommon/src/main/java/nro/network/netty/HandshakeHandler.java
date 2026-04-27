package nro.network.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import nro.network.io.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;

public class HandshakeHandler extends ChannelInboundHandlerAdapter {

   private static final Logger logger = LoggerFactory.getLogger(HandshakeHandler.class);

   // Configuration for Handshake Response (IP/Port)
   private final String host;
   private final int port;
   private final boolean isRedirect;

   public HandshakeHandler(String host, int port, boolean isRedirect) {
      this.host = host;
      this.port = port;
      this.isRedirect = isRedirect;
   }

   @Override
   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session == null) {
         ctx.fireChannelRead(msg);
         return;
      }

      // Check if the message is a trigger byte (usually -27)
      if (msg instanceof Message m && m.command == -27) {
         // logger.info("Received handshake trigger from session {}", session.getId());

         sendSessionKey(ctx, session);

         // CLEANUP: Release the message that triggered the handshake to prevent memory leak
         m.cleanup();

         // Handshake done, remove this handler to keep the pipeline clean
         ctx.pipeline().remove(this);
      } else {
         ctx.fireChannelRead(msg);
      }
   }

   private void sendSessionKey(ChannelHandlerContext ctx, NettySession session) throws IOException {
      byte[] keys = session.getKeys();
      Message ms = new Message(-27);
      DataOutputStream ds = ms.writer();
      ds.writeByte(keys.length);
      ds.writeByte(keys[0]);
      for (int i = 1; i < keys.length; i++) {
         ds.writeByte(keys[i] ^ keys[i - 1]);
      }

      // Restore these to ensure client reads the correct number of bytes
      ds.writeUTF(host != null ? host : "");
      ds.writeInt(port);
      ds.writeByte(isRedirect ? 1 : 0); // Trigger redirect if true

      ds.flush();

      // Set connected BEFORE writing to prevent race conditions with next message
      session.setConnected(true);

      io.netty.channel.ChannelFuture f = ctx.channel().writeAndFlush(ms);
      f.addListener(future -> {
         if (!future.isSuccess()) {
            logger.error("Failed to send session key!", future.cause());
         }
         // else {
         //    logger.info("Session key flushed to socket successfully!");
         // }
      });

      byte[] fullData = ms.getData();
      // logger.info("Sent session key to session {}. Redirect: {} Hex: {} {}",
      //       session.getId(), isRedirect, NettySession.byteToHex((byte) -27), NettySession.bytesToHex(fullData));
   }

}
