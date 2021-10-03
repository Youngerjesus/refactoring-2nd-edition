package refactoring.app.chapter06.inlineMethod;

public class Before {

    public int rating(Driver driver) {
        return moreThanFiveLateDeliveries(driver) ? 2 : 1;
    }

    private boolean moreThanFiveLateDeliveries(Driver driver) {
        return driver.numberOfLateDeliveries > 5;
    }
}
