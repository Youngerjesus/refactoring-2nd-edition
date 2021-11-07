package refactoring.app.chapter12.pullUpMethod;

public class Party {
    int monthlyCost;

    public int annualCost() {
        return monthlyCost * 12;
    }
}
