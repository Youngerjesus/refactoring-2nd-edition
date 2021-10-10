package refactoring.app.chapter07.replaceTempWithQuery;

public class Order {
    protected int quantity;
    protected Item item;

    public Order(int quantity, Item item) {
        this.quantity = quantity;
        this.item = item;
    }

    public double getPrice() {
        return getBasePrice() * getDiscountFactor();
    }

    private int getBasePrice() {
        return quantity * item.price;
    }

    private double getDiscountFactor() {
        double discountFactor = 0.98;
        if (getBasePrice() > 1000) discountFactor -= 0.03;
        return discountFactor;
    }
}
