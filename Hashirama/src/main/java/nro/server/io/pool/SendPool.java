package nro.server.io.pool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Tuỳ Chỉnh Bởi Văn Tuấn 0337766460
 * @copyright 💖 GirlkuN 💖
 *
 */
public class SendPool {

    private static int MAX_THREAD = 10;
    private static SendPool i;
    private ExecutorService pool;

    private SendPool() {
        this.pool = Executors.newFixedThreadPool(MAX_THREAD);
    }

    public static SendPool gI() {
        if (i == null) {
            i = new SendPool();
        }
        return i;
    }

    public void send(SendTask sendTask) {
        this.pool.execute(sendTask);
    }

}
