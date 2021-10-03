package refactoring.app.chapter06.extractMethod;

public class Order {
    protected int amount;

    public Order(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }
}
