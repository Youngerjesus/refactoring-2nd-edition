package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example1;

public class AfricanSwallow extends Bird {
    @Override
    public String plumage() {
        return this.numberOfCoconuts > 2 ? "지쳤다." : "보통이다.";
    }

    @Override
    public int airSpeedVelocity() {
        return 40 - 2 * this.numberOfCoconuts;
    }
}
