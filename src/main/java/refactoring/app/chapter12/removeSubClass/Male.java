package refactoring.app.chapter12.removeSubClass;

public class Male extends Person {
    public Male(String name) {
        super(name);
    }

    @Override
    public String getGenderCode() {
        return "M";
    }
}
