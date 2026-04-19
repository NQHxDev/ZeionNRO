package nro.network.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutorGroup;
import nro.network.stats.NetworkStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NettyServer {

   private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

   public static final AttributeKey<NettySession> SESSION_KEY = AttributeKey.valueOf("session");
   private static final AtomicInteger sessionCounter = new AtomicInteger(1);

   private final int port;
   private final byte[] defaultKeys;
   private final ChannelHandler businessHandler;
   private EventExecutorGroup businessGroup;

   private EventLoopGroup bossGroup;
   private EventLoopGroup workerGroup;

   private int idleTimeSeconds = 300; // 5 minutes default

   public NettyServer(int port, byte[] defaultKeys, ChannelHandler businessHandler) {
      this.port = port;
      this.defaultKeys = defaultKeys;
      this.businessHandler = businessHandler;
   }

   public void setBusinessGroup(EventExecutorGroup businessGroup) {
      this.businessGroup = businessGroup;
   }

   public void start() throws Exception {
      bossGroup = new NioEventLoopGroup(1);
      workerGroup = new NioEventLoopGroup(); // Default to cores * 2

      try {
         ServerBootstrap b = new ServerBootstrap();
         b.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)
            .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
            .childHandler(new ChannelInitializer<SocketChannel>() {
               @Override
               protected void initChannel(SocketChannel ch) {
                  // 1. Create Session
                  NettySession session = new NettySession(ch, sessionCounter.getAndIncrement());
                  session.setKeys(defaultKeys);
                  ch.attr(SESSION_KEY).set(session);

                  NetworkStats.gI().sessionCreated();

                  ChannelPipeline p = ch.pipeline();

                  // 2. Add Handlers
                  p.addLast(new IdleStateHandler(idleTimeSeconds, 0, 0, TimeUnit.SECONDS));
                  p.addLast(new NettyDecoder());
                  p.addLast(new NettyEncoder());
                  p.addLast(new HandshakeHandler());

                  if (businessGroup != null) {
                      p.addLast(businessGroup, businessHandler);
                  } else {
                      p.addLast(businessHandler);
                  }

                  ch.closeFuture().addListener(future -> NetworkStats.gI().sessionClosed());
               }
            });

         ChannelFuture f = b.bind(port).sync();
         logger.info("Netty Server started on port {}", port);
         f.channel().closeFuture().sync();
      } finally {
         stop();
      }
   }

   public void stop() {
      if (bossGroup != null) bossGroup.shutdownGracefully();
      if (workerGroup != null) workerGroup.shutdownGracefully();
      logger.info("Netty Server stopped gracefully.");
   }

   public void setIdleTime(int seconds) {
      this.idleTimeSeconds = seconds;
   }

}
