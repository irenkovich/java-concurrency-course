package course.concurrency.exams.auction;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Notifier {
    private static final int AVAILABLE_PROCS = Runtime.getRuntime().availableProcessors();
    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(
            AVAILABLE_PROCS, AVAILABLE_PROCS, 10L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(Integer.MAX_VALUE), new ThreadPoolExecutor.DiscardOldestPolicy());
    public void sendOutdatedMessage(Bid bid) {
        CompletableFuture.runAsync(this::imitateSending, executor);
    }

    private void imitateSending() {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                //do nothing
            }
    }

    public void shutdown() {
        executor.shutdownNow();
    }
}
