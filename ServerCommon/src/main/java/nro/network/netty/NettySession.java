package nro.network.netty;

import io.netty.channel.Channel;
import nro.network.ISession;
import nro.network.io.Message;

import java.net.InetSocketAddress;

public class NettySession implements ISession {

    private final int id;
    private final Channel channel;

    private byte[] keys;
    private byte curR, curW;
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

    public byte getCurR() {
        return curR;
    }

    public void setCurR(byte curR) {
        this.curR = curR;
    }

    public byte getCurW() {
        return curW;
    }

    public void setCurW(byte curW) {
        this.curW = curW;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
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

    public byte readKey(byte b) {
        byte i = (byte) ((keys[curR++] & 255) ^ (b & 255));
        if (curR >= keys.length) {
            curR %= keys.length;
        }
        return i;
    }

    public byte writeKey(byte b) {
        byte i = (byte) ((keys[curW++] & 255) ^ (b & 255));
        if (curW >= keys.length) {
            curW %= keys.length;
        }
        return i;
    }

}
