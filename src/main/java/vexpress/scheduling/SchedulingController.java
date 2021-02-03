package vexpress.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulingController {
  private final OrderRepository orderRepository;

  public SchedulingController(final InMemoryOrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  @GetMapping(value = "/orders", produces = "application/json")
  public Collection<SchedulingRequest> getOrders() throws JsonProcessingException {
    return orderRepository.getOrders().stream()
        .map(v -> new OrderStatus(v))
        .collect(Collectors.toList());
  }
}
