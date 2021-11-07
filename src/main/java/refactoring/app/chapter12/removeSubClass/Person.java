package refactoring.app.chapter12.removeSubClass;

public class Person {
    String name;
    String gender;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String gender) {
        this.name = name;
        this.gender = gender.isEmpty() ? "X" : gender;
    }

    public String getName() {
        return name;
    }

    public String getGenderCode() {
        return gender;
    }
}
