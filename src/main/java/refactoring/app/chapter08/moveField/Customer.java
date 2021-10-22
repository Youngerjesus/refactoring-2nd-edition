package refactoring.app.chapter08.moveField;

import java.time.LocalDateTime;

public class Customer {
    protected String name;
    protected CustomerContract customerContract;

    public Customer(String name, double discountRate) {
        this.name = name;
        setDiscountRate(discountRate);
        this.customerContract = new CustomerContract(LocalDateTime.now());
    }

    private void setDiscountRate(double discountRate) {
        this.customerContract.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return customerContract.discountRate;
    }

    public void becomePreferred() {
        setDiscountRate(getDiscountRate() + 0.3);
        // do something
    }

    public int applyAmount(int amount) {
        return Math.subtractExact(amount, (int) (amount * getDiscountRate()));
    }
}
