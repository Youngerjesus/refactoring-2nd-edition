package refactoring.app.chapter07.replacePrimitiveWithObject;

public class Order {
    protected Priority priority;

    public Order(String priority) {
        this.priority = new Priority(priority);
    }

    public String getPriorityString() {
        return priority.toString();
    }

    public void setPriority(String priority) {
        this.priority = new Priority(priority);
    }
}
