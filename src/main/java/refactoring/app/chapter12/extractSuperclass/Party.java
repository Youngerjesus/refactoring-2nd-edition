package refactoring.app.chapter12.extractSuperclass;

public class Party {
    String name;

    public Party(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int monthlyCost() {
        return 0;
    }

    public int annualCost() {
        return monthlyCost() * 12;
    }
}
