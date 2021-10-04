package refactoring.app.chapter06.changeFunctionDeclaration;

import java.util.ArrayDeque;
import java.util.Queue;

public class AfterAddParameter {
    protected Queue<Customer> reservations = new ArrayDeque<>();

    public void addReservation(Customer customer) {
        priorityAddReservation(customer, false);
    }

    private void priorityAddReservation(Customer customer, boolean isPriority) {
        this.reservations.add(customer);
    }
}
