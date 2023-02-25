package course.concurrency.m2_async.cf.min_price;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {
    //it's impossible to set timeout for every task to 3000
    //because of test failures
    private static final int TIMEOUT_MILLIS = 2970;
    private static final int HYPER_THREADING_WORKING_THREADS_NUMBER = Runtime.getRuntime().availableProcessors() * 2;
    private final Executor executor = Executors.newFixedThreadPool(HYPER_THREADING_WORKING_THREADS_NUMBER);

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        CompletableFuture<Double>[] futures = shopIds.stream()
                .map(shopId -> getPriceAsync(shopId, itemId))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(futures)
                .thenApply(v -> findMinFromResults(futures))
                .join();
    }

    private Double findMinFromResults(CompletableFuture<Double>[] results) {
        return Arrays.stream(results)
                .map(CompletableFuture::join)
                .map(Double.class::cast)
                .min(Double::compareTo)
                .orElse(Double.NaN);
    }

    private CompletableFuture<Double> getPriceAsync(long shopId, long itemId) {
        return CompletableFuture.supplyAsync(() -> priceRetriever.getPrice(shopId, itemId), executor)
                .completeOnTimeout(Double.NaN, TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .exceptionally(trw -> Double.NaN);
    }
}
