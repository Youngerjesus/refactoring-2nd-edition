package refactoring.app.chapter11.replaceConstructorWithFactoryFunction;

public class Employee {
    String name;
    String type;

    public Employee(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
