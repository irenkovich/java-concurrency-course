package course.concurrency.exams.auction;

import java.util.concurrent.locks.ReentrantLock;

public class AuctionPessimistic implements Auction {

    private Notifier notifier;
    private volatile Bid latestBid;

    private final ReentrantLock lock = new ReentrantLock();

    public AuctionPessimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new Bid(-1L, -1L, -1L);
    }

    public boolean propose(Bid bid) {
        if (bid.getPrice() > latestBid.getPrice()) {
            try {
                lock.lock();
                if (bid.getPrice() > latestBid.getPrice()) {
                    latestBid = bid;
                    notifier.sendOutdatedMessage(latestBid);
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
}
