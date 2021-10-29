package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example1;

public class Example1 {
    public String plumage(Bird bird) {
        return bird.plumage();
    }

    public int airSpeedVelocity(Bird bird) {
       return bird.airSpeedVelocity();
    }

    public Bird createBird(String type) {
        switch (type) {
            case "유럽 제비":
                return new EuropeanSwallow();
            case "아프리카 제비":
                return new AfricanSwallow();
            case "노르웨이 파랑 앵무":
                return new NorwegianBlueParrot();
            default:
                return new Bird();
        }
    }
}
