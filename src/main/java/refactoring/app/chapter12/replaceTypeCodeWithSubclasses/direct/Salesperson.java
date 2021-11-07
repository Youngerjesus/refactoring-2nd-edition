package refactoring.app.chapter12.replaceTypeCodeWithSubclasses.direct;

public class Salesperson extends Employee {
    public Salesperson(String name) {
        super(name);
    }

    public String getType() {
        return "salesperson";
    }
}
