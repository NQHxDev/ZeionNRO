package nro.network.netty;

import io.netty.channel.Channel;
import nro.network.ISession;
import nro.network.io.Message;

import java.net.InetSocketAddress;

public class NettySession implements ISession {

   private final int id;
   private final Channel channel;

   private byte[] keys;
   private int curR, curW;
   private boolean connected;
   private Object handler;

   public int getId() {
      return id;
   }

   public byte[] getKeys() {
      return keys;
   }

   public void setKeys(byte[] keys) {
      this.keys = keys;
   }

   public int getCurR() {
      return curR;
   }

   public void setCurR(int curR) {
      this.curR = curR;
   }

   public int getCurW() {
      return curW;
   }

   public void setCurW(int curW) {
      this.curW = curW;
   }

   public boolean isConnected() {
      return connected;
   }

   public void setConnected(boolean connected) {
      this.connected = connected;
      // if (connected) {
      //    System.out.println(String.format("<<< [SESSION CONNECTED] Session %d | IP: %s", id, getIPString()));
      // }
   }

   public Object getHandler() {
      return handler;
   }

   public void setHandler(Object handler) {
      this.handler = handler;
   }

   public NettySession(Channel channel, int id) {
      this.channel = channel;
      this.id = id;
   }

   @Override
   public void sendMessage(Message msg) {
      if (channel.isActive()) {
         channel.writeAndFlush(msg);
      }
   }

   public void doSendMessage(Message msg) {
      sendMessage(msg);
   }

   @Override
   public void disconnect() {
      if (channel.isActive()) {
         channel.close();
      }
   }

   public void close() {
      disconnect();
   }

   @Override
   public String getIPString() {
      if (channel.remoteAddress() instanceof InetSocketAddress addr) {
         return addr.getAddress().getHostAddress();
      }
      return channel.remoteAddress().toString();
   }

   public byte decodeKey(byte b, int keyIndex) {
      return (byte) ((keys[(keyIndex & 0xFFFFFFFF) % keys.length] & 0xFF) ^ (b & 0xFF));
   }

   public byte readKey(byte b) {
      byte i = (byte) ((keys[curR % keys.length] & 0xFF) ^ (b & 0xFF));
      curR = (curR + 1) % keys.length;
      return i;
   }

   public byte writeKey(byte b) {
      byte i = (byte) ((keys[curW % keys.length] & 0xFF) ^ (b & 0xFF));
      curW = (curW + 1) % keys.length;
      return i;
   }

   private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

   public static String bytesToHex(byte[] bytes) {
      if (bytes == null || bytes.length == 0) return "";
      // Giới hạn log tối đa 100 byte để tránh làm chậm hệ thống
      int len = Math.min(bytes.length, 100);
      char[] hexChars = new char[len * 3];
      for (int j = 0; j < len; j++) {
         int v = bytes[j] & 0xFF;
         hexChars[j * 3] = HEX_ARRAY[v >>> 4];
         hexChars[j * 3 + 1] = HEX_ARRAY[v & 0x0F];
         hexChars[j * 3 + 2] = ' ';
      }
      String result = new String(hexChars).trim();
      if (bytes.length > 100) {
         result += "... (" + bytes.length + " bytes)";
      }
      return result;
   }

   public static String byteToHex(byte b) {
      int v = b & 0xFF;
      return new String(new char[]{HEX_ARRAY[v >>> 4], HEX_ARRAY[v & 0x0F]});
   }

}
