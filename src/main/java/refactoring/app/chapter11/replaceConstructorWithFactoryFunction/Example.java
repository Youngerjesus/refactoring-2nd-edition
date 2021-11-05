package refactoring.app.chapter11.replaceConstructorWithFactoryFunction;

public class Example {
    Document document;


    public void client() {
        Employee candidate = createEmployee(document.name, document.empType);
    }

    public void client2() {
        Employee leadEngineer = createEngineer(document.leadEngineer);
    }

    public Employee createEmployee(String name, String empType) {
        return new Employee(name, empType);
    }

    public Employee createEngineer(String name) {
        return new Employee(name, "E");
    }
}
