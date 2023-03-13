package course.concurrency.exams.auction;

import java.util.concurrent.*;

public class Notifier {
    private static final int AVAILABLE_PROCS = Runtime.getRuntime().availableProcessors();
    private final Executor executor = new ThreadPoolExecutor(
            AVAILABLE_PROCS, AVAILABLE_PROCS, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(50000), new ThreadPoolExecutor.DiscardOldestPolicy());
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
    }
}
