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

      // DEBUG TRACE: In ra 10 byte đầu tiên để xác định header thực tế
      int traceLen = Math.min(in.readableBytes(), 10);
      byte[] trace = new byte[traceLen];
      in.getBytes(in.readerIndex(), trace);
      System.out.println(String.format("[TRACE] Session %d | IP: %s | Hex: %s", 
         session.getId(), session.getIPString(), NettySession.bytesToHex(trace)));

      // Use a local copy of curR to simulate decryption
      int localCurR = session.getCurR();

      // 1. Read Command
      byte rawCmd = in.readByte();
      byte cmd = rawCmd;
      if (session.isConnected()) {
         cmd = session.decodeKey(rawCmd, localCurR++);
      }

      // 2. Read Length
      int size;
      if (session.isConnected()) {
         // Client ALWAYS sends standard 2-byte encrypted size header to server
         if (in.readableBytes() < 2) {
            in.resetReaderIndex();
            return;
         }
         byte b1 = in.readByte();
         byte b2 = in.readByte();
         size = (session.decodeKey(b1, localCurR++) & 0xFF) << 8 | (session.decodeKey(b2, localCurR++) & 0xFF);
      } else {
         // Chưa kết nối (Handshake): Vẫn là 2 byte size mặc định
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

      // All data is available, now we can commit the changes
      byte[] rawData = new byte[size];
      in.readBytes(rawData);

      byte[] decryptedData = new byte[size];
      if (session.isConnected()) {
         for (int i = 0; i < size; i++) {
            decryptedData[i] = session.decodeKey(rawData[i], localCurR++);
         }
      } else {
         System.arraycopy(rawData, 0, decryptedData, 0, size);
      }

      // Minimal LOGGING
      System.out.println(String.format("[RECV] Session %d | Cmd: %d | Size: %d",
         session.getId(), cmd, size));

      // COMMIT the session state
      session.setCurR(localCurR);

      Message msg = new Message(cmd, decryptedData);
      out.add(msg);
   }

}

