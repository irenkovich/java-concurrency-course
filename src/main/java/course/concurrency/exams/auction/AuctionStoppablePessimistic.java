package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionStoppablePessimistic implements AuctionStoppable {

    private final Notifier notifier;
    private volatile Bid latestBid;
    private volatile boolean updatable;
    private final ReentrantLock lock;

    public AuctionStoppablePessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new Bid(-1L, -1L, -1L);
        this.lock = new ReentrantLock();
        this.updatable = true;
    }

    public boolean propose(Bid bid) {
        if (updatable && bid.getPrice() > latestBid.getPrice()) {
            try {
                lock.lock();
                if (updatable && bid.getPrice() > latestBid.getPrice()) {
                    notifier.sendOutdatedMessage(latestBid);
                    latestBid = bid;
                    return true;
                }
                return false;
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public Bid getLatestBid() {
        return latestBid;
    }

    public Bid stopAuction() {
        this.updatable = false;
        return latestBid;
    }
}
