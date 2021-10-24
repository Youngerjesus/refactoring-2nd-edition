package refactoring.app.chapter09.changeValueToReference;

public class Order {
    Customer customer;
    long number;

    public Order(long customerId, long number) {
        this.customer = CustomerRepository.registerCustomer(customerId);
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }
}
