package refactoring.app.chapter11.removeSettingMethod;

public class Example {
    public void client() {
        Person person = new Person(1234);
        person.name = "마틴";
    }
}
