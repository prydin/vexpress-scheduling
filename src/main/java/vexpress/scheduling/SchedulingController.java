package vexpress.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulingController {
    private OrderRepository orderRepository;

    public SchedulingController(InMemoryOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    @GetMapping(value = "/orders", produces = "application/json")
    public Collection<SchedulingRequest> getOrders()
            throws JsonProcessingException {
       return orderRepository.getOrders();
    }
}
