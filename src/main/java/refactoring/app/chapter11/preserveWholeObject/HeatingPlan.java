package refactoring.app.chapter11.preserveWholeObject;

public class HeatingPlan {
    Range temperatureRange;

    public boolean withinRange(Range range) {
        return (range.low >= temperatureRange.low) && (range.high <= temperatureRange.high);
    }
}
