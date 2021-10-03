package refactoring.app.chapter06.extractMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    protected List<Order> orders = new ArrayList<>();
    protected LocalDateTime dueDate;
    protected String customer;

    public List<Order> getOrders() {
        return orders;
    }
}
