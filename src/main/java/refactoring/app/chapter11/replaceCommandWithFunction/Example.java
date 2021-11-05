package refactoring.app.chapter11.replaceCommandWithFunction;

public class Example {

    public void client(Customer customer, int usage, Provider provider) {
        charge(customer, usage, provider);
    }

    public void charge(Customer customer, int usage, Provider provider) {
        double monthCharge = customer.baseRate * usage + provider.connectionCharge;
    }
}
