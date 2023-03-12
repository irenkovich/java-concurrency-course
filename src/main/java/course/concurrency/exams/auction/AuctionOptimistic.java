package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicReference;

public class AuctionOptimistic implements Auction {

    private Notifier notifier;
    private final AtomicReference<Bid> atomicLatestBid;


    public AuctionOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.atomicLatestBid = new AtomicReference<>(new Bid(-1L, -1L, -1L));
    }

    public boolean propose(Bid bid) {
        Bid latestBid;

        do {
            latestBid = atomicLatestBid.get();
            if (bid.getPrice() <= latestBid.getPrice()) {
                return false;
            }
        } while (!(atomicLatestBid.compareAndSet(latestBid, bid)));

        notifier.sendOutdatedMessage(latestBid);
        return true;
    }

    public Bid getLatestBid() {
        return atomicLatestBid.get();
    }
}
