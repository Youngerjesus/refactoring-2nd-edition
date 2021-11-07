package refactoring.app.chapter12.replaceSubclassWithDelegate.oneSubClass;

import java.time.LocalDateTime;

public class Client {
    public void example(Show show, LocalDateTime date) {
        Booking booking = createBooking(show, date);
    }

    public void example2(Show show, LocalDateTime date, Extras extras) {
        Booking premiumBooking = createPremiumBooking(show, date, extras);
    }

    private Booking createBooking(Show show, LocalDateTime date) {
        return new Booking(show, date);
    }

    private Booking createPremiumBooking(Show show, LocalDateTime date, Extras extras) {
        Booking result = new Booking(show, date);
        result.bePremium(extras);
        return result;
    }
}
