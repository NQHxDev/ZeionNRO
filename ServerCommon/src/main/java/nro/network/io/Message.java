package nro.network.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

public class Message {

   public byte command;
   private ByteBuf buffer;

   private DataOutputStream dos;
   private DataInputStream dis;

   private boolean isBigMsg;

   public byte getCommand() {
      return command;
   }

   public boolean isBigMsg() {
      return isBigMsg;
   }

   public void setBigMsg(boolean bigMsg) {
      isBigMsg = bigMsg;
   }

   public Message() {
      this((byte) 0);
   }

   public Message(int command) {
      this((byte) command);
   }

   public Message(byte command) {
      this.command = command;
      this.buffer = PooledByteBufAllocator.DEFAULT.buffer();
   }

   public Message(byte command, ByteBuf data) {
      this.command = command;
      this.buffer = data;
   }

   public Message(byte command, byte[] data) {
      this.command = command;
      this.buffer = Unpooled.wrappedBuffer(data);
   }

   public static Message create() {
      return new Message();
   }

   public static Message create(int command) {
      return new Message(command);
   }

   public static Message create(byte command) {
      return new Message(command);
   }

   public static Message create(byte command, byte[] data) {
      return new Message(command, data);
   }

   public ByteBuf getBuffer() {
      return buffer;
   }

   // --- Writing Methods (Compatibility) ---

   public DataOutputStream writer() {
      if (dos == null) {
         dos = new DataOutputStream(new ByteBufOutputStream(buffer));
      }
      return dos;
   }

   public void flush() {
      try {
         if (dos != null) {
               dos.flush();
         }
      } catch (Exception ignored) {
      }
   }

   public void transformData() {
      // No-op for compatibility with legacy code.
      // Encryption/Transformation is now handled in the Netty pipeline (NettyEncoder).
   }

   public void writeByte(int b) {
      buffer.writeByte(b);
   }

   public void writeBoolean(boolean b) {
      buffer.writeBoolean(b);
   }

   public void writeShort(int s) {
      buffer.writeShort(s);
   }

   public void writeInt(int i) {
      buffer.writeInt(i);
   }

   public void writeLong(long l) {
      buffer.writeLong(l);
   }

   public void writeDouble(double d) {
      buffer.writeDouble(d);
   }

   public void writeUTF(String s) {
      if (s == null) s = "";
      byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
      buffer.writeShort(bytes.length);
      buffer.writeBytes(bytes);
   }

   public void writeBytes(byte[] bytes) {
      buffer.writeBytes(bytes);
   }

   // --- Reading Methods (Compatibility) ---

   public DataInputStream reader() {
      if (dis == null) {
         dis = new DataInputStream(new ByteBufInputStream(buffer));
      }
      return dis;
   }

   public byte readByte() {
      return buffer.readByte();
   }

   public boolean readBoolean() {
      return buffer.readBoolean();
   }

   public short readShort() {
      return buffer.readShort();
   }

   public int readInt() {
      return buffer.readInt();
   }

   public long readLong() {
      return buffer.readLong();
   }

   public String readUTF() {
      short len = buffer.readShort();
      byte[] bytes = new byte[len];
      buffer.readBytes(bytes);
      return new String(bytes, StandardCharsets.UTF_8);
   }

   public int available() {
      return buffer.readableBytes();
   }

   public int readUnsignedByte() {
      return buffer.readUnsignedByte();
   }

   public int readerIndex() {
      return buffer.readerIndex();
   }

   public void readerIndex(int index) {
      buffer.readerIndex(index);
   }

   public byte[] getData() {
      if (buffer.hasArray()) {
         return buffer.array();
      }
      byte[] bytes = new byte[buffer.readableBytes()];
      buffer.getBytes(buffer.readerIndex(), bytes);
      return bytes;
   }

   public void cleanup() {
      if (buffer != null && buffer.refCnt() > 0) {
         buffer.release();
      }
   }

   public void dispose() {
      cleanup();
   }

}
