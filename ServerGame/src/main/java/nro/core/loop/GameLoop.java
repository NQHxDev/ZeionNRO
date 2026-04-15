package nro.core.loop;

import nro.core.concurrent.GameScheduler;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GameLoop tối ưu — không drift, không COW, không block loop.
 */
public final class GameLoop implements Runnable {
    /** tick khung: 50ms (20 FPS) */
    private static final int FRAME_MS = 50;

    private static final class Entry {
        final Tickable t;
        volatile long nextDue;
        Entry(Tickable t, long now) {
            this.t = t;
            this.nextDue = now + t.periodMs();
        }
    }

    /**
     * Queue nhẹ hơn CopyOnWriteArrayList rất nhiều.
     */
    private final Queue<Entry> entries = new ConcurrentLinkedQueue<>();

    private ScheduledFuture<?> loop;
    private final AtomicInteger slowWarn = new AtomicInteger(0);

    private GameLoop() {}

    private static class Holder { static final GameLoop I = new GameLoop(); }
    public static GameLoop gI() { return Holder.I; }

    public void start() {
        if (loop == null || loop.isDone()) {
            loop = GameScheduler.SCHED.scheduleAtFixedRate(this, 0, FRAME_MS, TimeUnit.MILLISECONDS);
        }
    }

    public void stop() {
        if (loop != null) {
            loop.cancel(false);
            loop = null;
        }
        entries.clear();
    }

    public void register(Tickable t) {
        entries.add(new Entry(t, System.currentTimeMillis()));
    }

    public void unregister(Tickable t) {
        entries.removeIf(e -> e.t == t);
    }

    @Override
    public void run() {
        long now = System.currentTimeMillis();
        long start = now;
        long budgetMs = FRAME_MS * 3L;

        for (Entry e : entries) {
            if (now >= e.nextDue) {

                int period = e.t.isActive() ? e.t.periodMs() : Math.max(e.t.periodMs(), 1000);

                try {
                    e.t.tick(now);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                } finally {
                    // reset đúng chuẩn, không accumulate drift
                    e.nextDue = now + period;
                }
            }

            if (System.currentTimeMillis() - start > budgetMs) break;
        }

        long spent = System.currentTimeMillis() - start;
        if (spent > budgetMs && slowWarn.incrementAndGet() % 20 == 0) {
            System.err.println("[GameLoop] frame spent " + spent + "ms, entries=" + entries.size());
        }
    }
}