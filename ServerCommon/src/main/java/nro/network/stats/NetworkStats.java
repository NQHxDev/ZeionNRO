package nro.network.stats;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class NetworkStats {

   private static final NetworkStats instance = new NetworkStats();
   public static NetworkStats gI() { return instance; }

   private final AtomicInteger activeSessions = new AtomicInteger(0);
   private final AtomicLong bytesIn = new AtomicLong(0);
   private final AtomicLong bytesOut = new AtomicLong(0);

   public void sessionCreated() { activeSessions.incrementAndGet(); }
   public void sessionClosed() { activeSessions.decrementAndGet(); }
   public void addBytesIn(long bytes) { bytesIn.addAndGet(bytes); }
   public void addBytesOut(long bytes) { bytesOut.addAndGet(bytes); }

   public int getActiveSessions() { return activeSessions.get(); }
   public long getBytesIn() { return bytesIn.get(); }
   public long getBytesOut() { return bytesOut.get(); }

}
