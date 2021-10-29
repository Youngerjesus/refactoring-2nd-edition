package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example1;

public class Bird {
    String type;
    int numberOfCoconuts;
    int voltage;
    boolean isNailed;

    public String plumage() {
        return "알 수 없다.";
    }

    public int airSpeedVelocity() {
        return 0;
    }
}
