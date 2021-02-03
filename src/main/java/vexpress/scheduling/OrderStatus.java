package vexpress.scheduling;

public class OrderStatus extends SchedulingRequest {
  private String status;

  public OrderStatus() {}

  public OrderStatus(final SchedulingRequest sr) {
    super(
        sr.getFromZip(),
        sr.getToZip(),
        sr.getWeight(),
        sr.getTrackingNumber(),
        sr.getTimeSubmitted());
    status = System.currentTimeMillis() - sr.getTimeSubmitted() > 30000 ? "Delivered" : "Shipped";
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(final String status) {
    this.status = status;
  }
}
