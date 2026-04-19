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

      // 1. Encrypt and Write Command
      byte encryptedCmd;
      boolean isEncrypted = session.isConnected() && cmd != -27;
      if (isEncrypted) {
         encryptedCmd = session.writeKey(cmd);
      } else {
         encryptedCmd = cmd;
      }
      out.writeByte(encryptedCmd);

      // 2. Encrypt and Write Length
      if (cmd == -32 || cmd == -66 || cmd == 11 || cmd == -67 || cmd == -74 || cmd == -87 || cmd == 66 || cmd == -28) {
         // "Big Message" for this specific client uses 3 bytes Little Endian
         byte b1 = (byte) (size & 0xFF);
         byte b2 = (byte) ((size >> 8) & 0xFF);
         byte b3 = (byte) ((size >> 16) & 0xFF);

         if (isEncrypted) {
            out.writeByte(session.writeKey((byte) (b1 - 128)));
            out.writeByte(session.writeKey((byte) (b2 - 128)));
            out.writeByte(session.writeKey((byte) (b3 - 128)));
         } else {
            out.writeByte(b1);
            out.writeByte(b2);
            out.writeByte(b3);
         }
      } else if (isEncrypted) {
         byte b1 = (byte) (size >> 8);
         byte b2 = (byte) (size & 0xFF);
         byte eb1 = session.writeKey(b1);
         byte eb2 = session.writeKey(b2);
         out.writeByte(eb1);
         out.writeByte(eb2);
      } else {
         out.writeShort(size);
      }

      // 3. Write Data
      byte[] rawData = msg.getData();
      if (rawData != null && rawData.length > 0) {
         if (isEncrypted) {
            for (int i = 0; i < rawData.length; i++) {
               out.writeByte(session.writeKey(rawData[i]));
            }
         } else {
            out.writeBytes(rawData);
         }
      }

      // Minimal Logging for performance
      if (size > 1024) {
         System.out.println(String.format("[SEND] Session %d | Cmd: %d | Size: %d (Large Packet)", session.getId(), cmd, size));
      } else {
         System.out.println(String.format("[SEND] Session %d | Cmd: %d | Size: %d", session.getId(), cmd, size));
      }

      msg.cleanup();
   }

}
