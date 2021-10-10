package refactoring.app.chapter07.removeMiddleMan;


public class Department {
    protected int chargeCode;
    protected Person manager;

    public int getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(int chargeCode) {
        this.chargeCode = chargeCode;
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }
}