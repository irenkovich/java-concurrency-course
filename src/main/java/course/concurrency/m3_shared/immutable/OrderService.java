package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class OrderService {

    private final AtomicReferenceArray<Order> currentOrders = new AtomicReferenceArray<>(1_000_000);
    private final AtomicLong nextId = new AtomicLong(0);

    private long nextId() {
        return nextId.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        Long id = nextId();
        Order order = new Order(id, items);
        currentOrders.set(id.intValue(), order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        int id = ((Long) orderId).intValue();
        Order updated = currentOrders.updateAndGet(id, oldValue -> oldValue.setPaymentInfo(paymentInfo));
        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    public void setPacked(long orderId) {
        int id = ((Long) orderId).intValue();
        Order updated = currentOrders.updateAndGet(id, oldValue -> oldValue.setPacked(true));
        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    private void deliver(Order order) {
        currentOrders.set(order.getId().intValue(), order.setStatus(Order.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        int id = ((Long) orderId).intValue();
        return currentOrders.get(id).getStatus().equals(Order.Status.DELIVERED);
    }
}
