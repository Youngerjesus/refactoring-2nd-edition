package refactoring.app.chapter11.removeSettingMethod;

public class Person {
    long id;
    String name;

    public Person(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
