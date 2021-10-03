package refactoring.app.chapter06.extractVariable;

public class Order {
    protected int quantity;
    protected int itemPrice;

    public Order(int quantity, int itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double price() {
        return this.quantity * this.itemPrice -
                Math.max(0, this.quantity - 500) * this.itemPrice * 0.05 +
                Math.min(this.quantity * this.itemPrice * 0.1 , 100);
    }
}
