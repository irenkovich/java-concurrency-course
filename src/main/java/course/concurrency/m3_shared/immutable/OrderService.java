package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class OrderService {
    private final int ONE_ITERATION_BUFF_SIZE = 1_000_000;

    private final AtomicReferenceArray<Order> ordersBuffer = new AtomicReferenceArray<>(ONE_ITERATION_BUFF_SIZE);
    private final AtomicLong nextId = new AtomicLong(0);

    private long nextId() {
        return nextId.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        Long id = nextId();
        Order order = new Order(id, items);
        ordersBuffer.set(getIndexFromBufferById(id), order);
        return id;
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        int id = getIndexFromBufferById(orderId);
        Order updated = ordersBuffer.updateAndGet(id, oldValue -> oldValue.setPaymentInfo(paymentInfo));
        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    public void setPacked(long orderId) {
        int id = getIndexFromBufferById(orderId);
        Order updated = ordersBuffer.updateAndGet(id, oldValue -> oldValue.setPacked(true));
        if (updated.checkStatus()) {
            deliver(updated);
        }
    }

    private void deliver(Order order) {
        ordersBuffer.set(getIndexFromBufferById(order.getId()), order.setStatus(Order.Status.DELIVERED));
    }

    public boolean isDelivered(long orderId) {
        int id = getIndexFromBufferById(orderId);
        return ordersBuffer.get(id).getStatus().equals(Order.Status.DELIVERED);
    }

    private int getIndexFromBufferById(Long id) {
        long l = id - (id / ONE_ITERATION_BUFF_SIZE) * ONE_ITERATION_BUFF_SIZE;
        return (int) l;
    }

}
