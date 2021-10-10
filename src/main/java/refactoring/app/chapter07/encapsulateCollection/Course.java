package refactoring.app.chapter07.encapsulateCollection;

public class Course {
    protected String name;
    protected boolean isAdvanced;

    public String getName() {
        return name;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }
}
