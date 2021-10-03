package refactoring.app.chapter06.inlineVariable;

public class Before {

    public boolean method(Order order) {
        int basePrice = order.basePrice;
        return basePrice > 1000;
    }
}
