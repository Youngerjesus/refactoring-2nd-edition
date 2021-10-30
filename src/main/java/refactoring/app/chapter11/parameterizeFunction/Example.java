package refactoring.app.chapter11.parameterizeFunction;

public class Example {
    public void tenPercentRaise(Person person) {
        person.salary = (int) (person.salary * 1.1);
    }

    public void fivePercentRaise(Person person) {
        person.salary = (int) (person.salary * 1.05);
    }

    public void raise(Person person, double factor) {
        person.salary = (int) (person.salary * factor);
    }
}
