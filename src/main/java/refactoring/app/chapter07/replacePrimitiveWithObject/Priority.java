package refactoring.app.chapter07.replacePrimitiveWithObject;

public class Priority {
    protected String value;

    public Priority(String value) {
        this.value = value;
    }

    public String toString() {
        return value;
    }
}
