package refactoring.app.chapter09.changeValueToReference;

public class Customer {
    long id;

    public Customer(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
