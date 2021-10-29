package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example1;

public class EuropeanSwallow extends Bird {
    @Override
    public String plumage() {
        return "보통이다.";
    }

    @Override
    public int airSpeedVelocity() {
        return 35;
    }
}
