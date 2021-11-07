package refactoring.app.chapter12.replaceSubclassWithDelegate.multiSubClass;

public class AfricanSwallow extends Bird {
    int numberOfCounts;

    public AfricanSwallow(Data data) {
        super(data);
        this.numberOfCounts = data.numberOfCounts;
    }

    @Override
    public int airSpeedVelocity() {
        return 40 - 2 * numberOfCounts;
    }
}
