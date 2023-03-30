package course.concurrency.m3_shared.immutable;

import java.util.List;

import static course.concurrency.m3_shared.immutable.Order.Status.IN_PROGRESS;
import static course.concurrency.m3_shared.immutable.Order.Status.NEW;

public final class Order {

    public enum Status { NEW, IN_PROGRESS, DELIVERED }

    private final Long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public Order(Long id, List<Item> items) {
        this(id, items, null, false, NEW);
    }

    private Order(Order oldOrderVersion, PaymentInfo paymentInfo) {
        this(oldOrderVersion.getId(), oldOrderVersion.getItems(), paymentInfo, oldOrderVersion.isPacked(), IN_PROGRESS);
    }

    private Order(Order oldOrderVersion, boolean isPacked) {
        this(oldOrderVersion.getId(), oldOrderVersion.getItems(), oldOrderVersion.getPaymentInfo(), isPacked, IN_PROGRESS);
    }

    private Order(Order oldOrderVersion, Status status) {
        this(oldOrderVersion.getId(), oldOrderVersion.getItems(), oldOrderVersion.getPaymentInfo(), oldOrderVersion.isPacked(), status);
    }

    private Order(Long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = items;
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public boolean checkStatus() {
        if (items != null && !items.isEmpty() && paymentInfo != null && isPacked) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new Order(this, paymentInfo);
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Order withPacked(boolean packed) {
        return new Order(this, packed);
    }

    public Status getStatus() {
        return status;
    }

    public Order withStatus(Status status) {
        return new Order(this, status);
    }
}
