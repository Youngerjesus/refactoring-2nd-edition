package refactoring.app.chapter12.replaceSubclassWithDelegate.oneSubClass;

import java.time.LocalDateTime;

public class Booking {
    Show show;
    LocalDateTime date;
    PremiumBookingDelegate premiumDelegate;

    public Booking(Show show, LocalDateTime date) {
        this.show = show;
        this.date = date;
    }

    public boolean hasTalkback() {
        return show.talkback && isPeakDay();
    }

    public boolean isPeakDay() {
        return false;
    }

    public int basePrice() {
        int result = show.price;
        if (isPeakDay()) result += Math.round(result * 0.15);
        return premiumDelegate != null ? premiumDelegate.extendPrice() : result;
    }

    public void bePremium(Extras extras) {
        premiumDelegate = new PremiumBookingDelegate(extras, this);
    }
}
