package nro.network.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import nro.network.io.Message;
import java.util.List;

public class NettyDecoder extends ByteToMessageDecoder {

   @Override
   protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
      NettySession session = ctx.channel().attr(NettyServer.SESSION_KEY).get();
      if (session == null) return;

      if (in.readableBytes() < 1) return;

      in.markReaderIndex();

      // 1. Read Command
      byte cmd = in.readByte();
      if (session.isConnected()) {
         cmd = session.readKey(cmd);
      }

      // 2. Read Length
      int size;
      if (session.isConnected()) {
         if (in.readableBytes() < 2) {
               in.resetReaderIndex();
               return;
         }
         byte b1 = in.readByte();
         byte b2 = in.readByte();
         size = (session.readKey(b1) & 0xFF) << 8 | session.readKey(b2) & 0xFF;
      } else {
         if (in.readableBytes() < 2) {
               in.resetReaderIndex();
               return;
         }
         size = in.readUnsignedShort();
      }

      // 3. Read Data
      if (in.readableBytes() < size) {
         in.resetReaderIndex();
         return;
      }

      ByteBuf data = in.readBytes(size);
      if (session.isConnected()) {
         for (int i = 0; i < data.readableBytes(); i++) {
               byte b = data.getByte(i);
               data.setByte(i, session.readKey(b));
         }
      }

      Message msg = new Message(cmd, data);
      out.add(msg);
   }

}
