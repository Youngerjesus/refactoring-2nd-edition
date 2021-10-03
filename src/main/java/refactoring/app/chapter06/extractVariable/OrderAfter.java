package refactoring.app.chapter06.extractVariable;

public class OrderAfter {
    protected int quantity;
    protected int itemPrice;

    public OrderAfter(int quantity, int itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double price() {
        return basePrice() - quantityDiscount() + shipping();
    }

    private double shipping() {
        return Math.min(this.quantity * this.itemPrice * 0.1, 100);
    }

    private double quantityDiscount() {
        return Math.max(0, this.quantity - 500) * this.itemPrice * 0.05;
    }

    private int basePrice() {
        return this.quantity * this.itemPrice;
    }
}
