package nro.core.concurrent;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GameScheduler tối ưu — hỗ trợ CallerRunsPolicy, giảm nguy cơ nghẽn,
 * cấu hình chuẩn cho game server realtime.
 */
public final class GameScheduler {

    private static final AtomicInteger ID = new AtomicInteger(1);

    /**
     * Thread pool trung tâm cho toàn bộ server.
     * Dùng ScheduledThreadPoolExecutor để chạy được scheduleAtFixedRate.
     */
    public static final ScheduledThreadPoolExecutor SCHED =
            new ScheduledThreadPoolExecutor(
                    Math.max(6, Runtime.getRuntime().availableProcessors()),
                    r -> {
                        Thread t = new Thread(r);
                        t.setName("GameScheduler-" + ID.getAndIncrement());
                        t.setDaemon(true);
                        return t;
                    }
            );

    static {
        // Xóa task đã cancel để tránh memory leak
        SCHED.setRemoveOnCancelPolicy(true);

        // Không cho chạy các task delayed cũ khi shutdown
        SCHED.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        SCHED.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);

        // Quan trọng: Nếu pool tạm thời quá tải → thread gọi sẽ chạy luôn
        // => Không bị drop frame, không tạo backlog
        SCHED.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        // Cho phép threadpool tăng giảm linh hoạt
        SCHED.setKeepAliveTime(30, TimeUnit.SECONDS);
        SCHED.allowCoreThreadTimeOut(true);
    }

    private GameScheduler() {}

    /** Tắt toàn bộ scheduler */
    public static void shutdown() {
        SCHED.shutdownNow();
    }
}