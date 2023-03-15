package course.concurrency.exams.auction;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AuctionStoppableOptimistic implements AuctionStoppable {

    private final Notifier notifier;

    private final AtomicMarkableReference<Bid> latestBid;

    public AuctionStoppableOptimistic(Notifier notifier) {
        this.notifier = notifier;
        this.latestBid = new AtomicMarkableReference<>(new Bid(-1L, -1L, -1L), true);
    }

    public boolean propose(Bid bid) {
        Bid currentBid;
        boolean isNotUpdatable;
        do {
            currentBid = latestBid.getReference();
            isNotUpdatable = !(latestBid.isMarked());
            if (isNotUpdatable || bid.getPrice() <= currentBid.getPrice()) {
                return false;
            }
        } while (!(latestBid.compareAndSet(currentBid, bid, true, true)));

        notifier.sendOutdatedMessage(currentBid);
        return true;
    }

    public Bid getLatestBid() {
        return latestBid.getReference();
    }

    public Bid stopAuction() {
        setUpdatableToFalse();
        return getLatestBid();
    }

    private void setUpdatableToFalse() {
        Bid currentBid;
        do {
            currentBid = latestBid.getReference();
        } while (!latestBid.attemptMark(currentBid, false));
    }
}
