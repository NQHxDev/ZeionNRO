package nro.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import nro.network.io.Message;

public class NettyEncoder extends MessageToByteEncoder<Message> {

   @Override
   protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session == null) return;

      byte cmd = msg.getCommand();
      ByteBuf data = msg.getBuffer();
      int size = data.readableBytes();

      // 1. Write Command
      if (session.isConnected()) {
         out.writeByte(session.writeKey(cmd));
      } else {
         out.writeByte(cmd);
      }

      // 2. Write Length
      if (session.isConnected()) {
         out.writeByte(session.writeKey((byte) (size >> 8)));
         out.writeByte(session.writeKey((byte) (size & 0xFF)));
      } else {
         out.writeShort(size);
      }

      // 3. Write Data
      if (session.isConnected()) {
         for (int i = 0; i < size; i++) {
               byte b = data.readByte();
               out.writeByte(session.writeKey(b));
         }
      } else {
         out.writeBytes(data);
      }

      // Reference counting: Release the message buffer as it belongs to the Pooled allocator
      msg.cleanup();
   }

}
