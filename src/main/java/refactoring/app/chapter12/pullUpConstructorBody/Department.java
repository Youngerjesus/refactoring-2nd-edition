package refactoring.app.chapter12.pullUpConstructorBody;

public class Department extends Party{
    Staff staff;

    public Department(String name, Staff staff) {
        super(name);
        this.staff = staff;
    }
}
