package refactoring.app.chapter12.replaceSubclassWithDelegate.multiSubClass;

public class NorwegianBlueParrot extends Bird {
    int voltage;
    boolean isNailed;

    public NorwegianBlueParrot(Data data) {
        super(data);
        this.voltage = data.voltage;
        this.isNailed = data.isNailed;
    }

    @Override
    public String getPlumage() {
        if (voltage > 100) return "그을렸다.";
        return "예쁘다.";
    }

    @Override
    public int airSpeedVelocity() {
        return isNailed ? 0 : 10 + voltage / 10;
    }
}
