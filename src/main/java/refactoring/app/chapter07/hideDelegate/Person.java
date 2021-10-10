package refactoring.app.chapter07.hideDelegate;

public class Person {
    protected String name;
    protected Department department;

    public Person(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Person manager() {
        return department.getManager();
    }
}
