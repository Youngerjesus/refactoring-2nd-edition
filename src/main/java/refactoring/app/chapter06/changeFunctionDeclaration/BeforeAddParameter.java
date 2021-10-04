package refactoring.app.chapter06.changeFunctionDeclaration;

import java.util.ArrayDeque;
import java.util.Queue;

public class BeforeAddParameter {
    protected Queue<Customer> reservations = new ArrayDeque<>();

    public void addReservation(Customer customer) {
        this.reservations.add(customer);
    }
}
