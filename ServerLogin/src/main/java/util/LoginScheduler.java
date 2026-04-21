package util;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public final class LoginScheduler {

   private static final AtomicInteger ID = new AtomicInteger(1);

   /**
    * Global scheduler for recurring and background tasks.
    * Core size 2 is plenty for basic timers in Login Server.
    */
   public static final ScheduledThreadPoolExecutor SCHED = new ScheduledThreadPoolExecutor(
         2,
         r -> {
            Thread t = new Thread(r);
            t.setName("LoginScheduler-" + ID.getAndIncrement());
            t.setDaemon(true);
            return t;
         });

   /**
    * Dedicated pool for connection-related blocking tasks (Message Collectors).
    * Uses a cached pool or a larger fixed pool to prevent starving the SCHED pool.
    */
   public static final ExecutorService CONNECTIONS = new ThreadPoolExecutor(
         0, 100, // Max 100 concurrent login sessions handled
         60L, TimeUnit.SECONDS,
         new SynchronousQueue<>(),
         r -> {
            Thread t = new Thread(r);
            t.setName("Connection-Worker-" + ID.getAndIncrement());
            t.setDaemon(true);
            return t;
         });

   static {
      SCHED.setRemoveOnCancelPolicy(true);
      SCHED.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
      SCHED.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
      SCHED.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

      SCHED.setKeepAliveTime(30, TimeUnit.SECONDS);
      SCHED.allowCoreThreadTimeOut(true);
   }

   private LoginScheduler() {
   }

   public static void shutdown() {
      SCHED.shutdownNow();
      CONNECTIONS.shutdownNow();
   }

}
