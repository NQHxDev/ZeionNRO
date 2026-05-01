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

      int localCurR = session.getCurR();

      // Read Command
      byte rawCmd = in.readByte();
      byte cmd = rawCmd;
      if (session.isConnected()) {
         cmd = session.decodeKey(rawCmd, localCurR++);
      }

      // Read Length
      int size;
      if (session.isConnected()) {
         if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
         }
         byte b1 = in.readByte();
         byte b2 = in.readByte();
         size = (session.decodeKey(b1, localCurR++) & 0xFF) << 8 | (session.decodeKey(b2, localCurR++) & 0xFF);
      } else {
         if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
         }
         size = in.readUnsignedShort();
      }

      // Read Data
      if (size > 16384 || size < 0) {
         ctx.close();
         return;
      }

      if (in.readableBytes() < size) {
         in.resetReaderIndex();
         return;
      }

      byte[] decryptedData = new byte[size];
      if (session.isConnected()) {
         for (int i = 0; i < size; i++) {
            decryptedData[i] = session.decodeKey(in.readByte(), localCurR++);
         }
      } else {
         in.readBytes(decryptedData);
      }

      session.setCurR(localCurR);

      Message msg = new Message(cmd, decryptedData);
      out.add(msg);
   }

}

