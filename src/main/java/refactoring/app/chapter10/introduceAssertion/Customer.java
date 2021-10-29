package refactoring.app.chapter10.introduceAssertion;

import org.springframework.util.Assert;

public class Customer {
    double discountRate;

    public int applyDiscount(int number) {
        if (discountRate > 0) return number;
        return number - ((int) discountRate * number);
    }

    public void setDiscountRate(double discountRate) {
        Assert.isTrue(discountRate > 0);
        this.discountRate = discountRate;
    }
}
