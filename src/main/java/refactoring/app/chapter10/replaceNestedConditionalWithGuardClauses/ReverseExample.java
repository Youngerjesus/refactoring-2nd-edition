package refactoring.app.chapter10.replaceNestedConditionalWithGuardClauses;

public class ReverseExample {
    public int adjustedCapital(Instrument instrument) {
        if (    instrument.capital < 0 ||
                instrument.interRate <= 0 ||
                instrument.duration <= 0) return 0;

        return (int) ((instrument.income / instrument.duration) * instrument.adjustmentFactor);
    }
}
