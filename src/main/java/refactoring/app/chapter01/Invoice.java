package refactoring.app.chapter01;

import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String customer;
    List<Performance> performances = new ArrayList<>();

    public Invoice(String customer, List<Performance> performances) {
        this.customer = customer;
        this.performances = performances;
    }

    public String getCustomer() {
        return customer;
    }

    public List<Performance> getPerformances() {
        return performances;
    }
}
