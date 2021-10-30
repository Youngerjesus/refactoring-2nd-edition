package refactoring.app.chapter11.replaceParameterWithQuery;

public class Order {
    int quantity;
    int itemPrice;

    public int finalPrice() {
        int basePrice = quantity * itemPrice;
        return discountPrice(basePrice);
    }

    private int discountLevel() {
        return quantity > 100 ? 2 : 1;
    }

    private int discountPrice(int basePrice) {
        switch (discountLevel()) {
            case 1: return (int) (basePrice * 0.95);
            case 2: return (int) (basePrice * 0.90);
        }
        return 0;
    }
}
