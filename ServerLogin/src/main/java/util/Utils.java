package util;

import java.util.concurrent.TimeUnit;

public class Utils {
   public static void setTimeout(Runnable runnable, int delay) {
      LoginScheduler.SCHED.schedule(runnable, delay, TimeUnit.MILLISECONDS);
   }
}
