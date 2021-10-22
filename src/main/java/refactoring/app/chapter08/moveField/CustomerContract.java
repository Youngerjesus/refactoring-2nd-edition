package refactoring.app.chapter08.moveField;

import java.time.LocalDateTime;

public class CustomerContract {
    protected LocalDateTime startDate;
    protected double discountRate;

    public CustomerContract(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
