package vexpress.scheduling;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class InMemoryOrderRepository implements OrderRepository {
    private Map<String, SchedulingRequest> orders = new HashMap<>();

    @Override
    public synchronized Collection<SchedulingRequest> getOrders() {
        return orders.values();
    }

    @Override
    public synchronized SchedulingRequest getOrderByTrackingNumber(String trackingNumber) {
        return null;
    }

    @Override
    public synchronized void store(SchedulingRequest order) {
        orders.put(order.getTrackingNumber(), order);
    }
}
