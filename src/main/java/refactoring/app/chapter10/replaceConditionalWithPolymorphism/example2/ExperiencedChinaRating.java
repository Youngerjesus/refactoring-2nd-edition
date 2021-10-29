package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example2;

public class ExperiencedChinaRating extends Rating {

    public ExperiencedChinaRating(Voyage voyage, History history) {
        super(voyage, history);
    }

    @Override
    protected int captainHistoryRisk() {
        return super.captainHistoryRisk() - 2;
    }

    @Override
    protected int voyageAndHistoryLengthFactor() {
        int result = 3;
        if (history.length() > 10) result += 1;
        if (voyage.length > 12) result += 1;
        if (voyage.length > 18) result -= 1;
        return result;
    }
}
