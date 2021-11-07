package refactoring.app.chapter12.extractSuperclass;

public class Employee extends Party {
    Long id;
    int monthlyCost;

    public Employee(Long id, String name, int monthlyCost) {
        super(name);
        this.id = id;
        this.monthlyCost = monthlyCost;
    }

    public Long getId() {
        return id;
    }

    public int monthlyCost() {
        return monthlyCost;
    }
}
