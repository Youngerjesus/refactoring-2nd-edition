package refactoring.app.chapter08.moveFunction;

public class Account {
    protected int daysOverdrawn;
    private AccountType type;

    public double bankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn > 0) result += type.overdraftCharge(this.daysOverdrawn);
        return result;
    }
}
