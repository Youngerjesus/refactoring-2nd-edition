package refactoring.app.chapter07.inlineClass;

public class TrackingInformation {
    protected String shippingCompany;
    protected String trackingNumber;

    public String display() {
        return String.format("%s: %s", shippingCompany, trackingNumber);
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
