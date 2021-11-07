package refactoring.app.chapter12.replaceSubclassWithDelegate.multiSubClass;

public class Client {
    public Bird createBird(Data data) {
        switch (data.type) {
            case "유럽 제비":
                return new EuropeanSwallow(data);
            case "아프리카 제비":
                return new AfricanSwallow(data);
            case "노르웨이 파랑 앵무":
                return new NorwegianBlueParrot(data);
            default:
                return new Bird(data);
        }
    }
}
