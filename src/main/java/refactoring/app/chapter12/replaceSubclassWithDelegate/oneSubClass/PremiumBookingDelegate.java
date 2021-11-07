package refactoring.app.chapter12.replaceSubclassWithDelegate.oneSubClass;

public class PremiumBookingDelegate {
    Extras extras;
    Booking booking;

    public PremiumBookingDelegate(Extras extras, Booking booking) {
        this.extras = extras;
        this.booking = booking;
    }

    public boolean hasTalkback() {
        return true;
    }

    public int extendPrice() {
        return Math.round(booking.basePrice() + extras.premiumFee);
    }

    public boolean hasDinner() {
        return extras.dinner && !booking.isPeakDay();
    }
}
