package vexpress.scheduling;

public class SchedulingRequest {
    private int fromZip;

    private int toZip;

    private double weight;

    private String trackingNumber;

    private long timeSubmitted;

    public SchedulingRequest() {}

    public SchedulingRequest(int fromZip, int toZip, double weight, String trackingNumber, long timeSubmitted) {
        this.fromZip = fromZip;
        this.toZip = toZip;
        this.weight = weight;
        this.trackingNumber = trackingNumber;
        this.timeSubmitted = timeSubmitted;
    }

    public int getFromZip() {
        return fromZip;
    }

    public void setFromZip(int fromZip) {
        this.fromZip = fromZip;
    }

    public int getToZip() {
        return toZip;
    }

    public void setToZip(int toZip) {
        this.toZip = toZip;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public long getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(long timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }
}
