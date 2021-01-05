package vexpress.scheduling;

import java.util.Collection;
import java.util.List;

public interface OrderRepository {
    Collection<SchedulingRequest> getOrders();

    SchedulingRequest getOrderByTrackingNumber(String trackingNumber);

    void store(SchedulingRequest order);
}
