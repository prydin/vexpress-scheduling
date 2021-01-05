package vexpress.scheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
  private InMemoryOrderRepository orderRepository;

  public MessageReceiver(InMemoryOrderRepository orderRepository) {
    this.orderRepository = orderRepository;
  }

  public void receiveMessage(String message) throws JsonProcessingException {
    System.out.println(message);
    ObjectMapper om = new ObjectMapper();
    SchedulingRequest order = om.readValue(message, SchedulingRequest.class);
    orderRepository.store(order);
  }
}
