package nro.network.netty;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import nro.network.IController;
import nro.network.io.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class CommonHandler extends SimpleChannelInboundHandler<Message> {

   private static final Logger logger = LoggerFactory.getLogger(CommonHandler.class);

   private final IController controller;

   public CommonHandler(IController controller) {
      this.controller = controller;
   }

   @Override
   public void channelActive(ChannelHandlerContext ctx) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session != null) {
         controller.onConnectionReady(session);
      }
   }

   @Override
   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session != null) {
         controller.onDisconnected(session);
      }
   }

   @Override
   protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session != null) {
         controller.onMessage(session, msg);
      }
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      logger.error("Exception in networking layer for channel {}", ctx.channel(), cause);
      ctx.close();
   }

}
