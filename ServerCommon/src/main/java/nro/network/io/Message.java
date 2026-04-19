package nro.network.io;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.PooledByteBufAllocator;
import lombok.Getter;
import lombok.Setter;
import java.nio.charset.StandardCharsets;

public class Message {

   @Getter
   public byte command;
   private ByteBuf buffer;

   @Getter @Setter
   private boolean isBigMsg;

   /**
    * Constructor for writing a message with a command.
    * Allocates a pooled buffer.
    */
   public Message(int command) {
      this((byte) command);
   }

   public Message(byte command) {
      this.command = command;
      this.buffer = PooledByteBufAllocator.DEFAULT.buffer();
   }

   /**
    * Constructor for reading a message with a command and data buffer.
    */
   public Message(byte command, ByteBuf data) {
      this.command = command;
      this.buffer = data;
   }

   /**
    * Constructor for compatibility with byte array data.
    */
   public Message(byte command, byte[] data) {
      this.command = command;
      this.buffer = Unpooled.wrappedBuffer(data);
   }

   public ByteBuf getBuffer() {
      return buffer;
   }

   // --- Writing Methods ---

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

   public void writeUTF(String s) {
      if (s == null) s = "";
      byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
      buffer.writeShort(bytes.length);
      buffer.writeBytes(bytes);
   }

   public void writeBytes(byte[] bytes) {
      buffer.writeBytes(bytes);
   }

   // --- Reading Methods ---

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

   public int readerIndex() {
      return buffer.readerIndex();
   }

   public void readerIndex(int index) {
      buffer.readerIndex(index);
   }

   /**
    * Returns the underlying data as a byte array.
    * Note: This might involve a copy if the buffer is pooled or direct.
    */
   public byte[] getData() {
      if (buffer.hasArray()) {
         return buffer.array();
      }
      byte[] bytes = new byte[buffer.readableBytes()];
      buffer.getBytes(buffer.readerIndex(), bytes);
      return bytes;
   }

   /**
    * Cleanup and release the underlying buffer.
    * Essential to avoid memory leaks with Pooled ByteBufs.
    */
   public void cleanup() {
      if (buffer != null && buffer.refCnt() > 0) {
         buffer.release();
      }
   }

   /**
    * Alias for cleanup to match legacy code disposing.
    */
   public void dispose() {
      cleanup();
   }

}
