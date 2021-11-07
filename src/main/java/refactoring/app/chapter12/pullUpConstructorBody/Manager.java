package refactoring.app.chapter12.pullUpConstructorBody;

public class Manager extends Employee {
    Grade grade;

    public Manager(String name, Grade grade, boolean isPrivileged) {
        super(name);
        this.grade = grade;
        finishConstruction();
    }

    @Override
    public boolean isPrivileged() {
        return grade.val > 4;
    }
}
