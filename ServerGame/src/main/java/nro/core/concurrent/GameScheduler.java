package nro.core.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public final class GameScheduler {

   private static final AtomicInteger ID = new AtomicInteger(1);

   public static final ScheduledThreadPoolExecutor SCHED = new ScheduledThreadPoolExecutor(
         Math.max(8, Runtime.getRuntime().availableProcessors()),
         r -> {
            Thread t = new Thread(r);
            t.setName("GameScheduler-" + ID.getAndIncrement());
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

   private GameScheduler() {
   }

   public static void shutdown() {
      SCHED.shutdownNow();
   }

}
