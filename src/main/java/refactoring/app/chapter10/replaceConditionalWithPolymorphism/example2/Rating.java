package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example2;

import java.util.List;

public class Rating {
    Voyage voyage;
    History history;

    public Rating(Voyage voyage, History history) {
        this.voyage = voyage;
        this.history = history;
    }

    public String value() {
        int vpf = voyageProfitFactor();
        int vr = voyageRisk();
        int chr = captainHistoryRisk();
        if (vpf * 3 > vr + chr * 2) return "A";
        return "B";
    }

    protected int voyageProfitFactor() {
        int result = 2;
        if (voyage.zone.equals("중국")) result += 1;
        if (voyage.zone.equals("동인도")) result += 1;
        result += voyageAndHistoryLengthFactor();
        return result;
    }

    protected int voyageAndHistoryLengthFactor() {
        int result = 0;
        if (history.length() > 8) result += 1;
        if (voyage.length > 14) result -= 1;
        return result;
    }

    protected boolean hasChina() {
        return history.hasChina();
    }

    protected int voyageRisk() {
        int result = 1;
        if (voyage.length > 4) result += 2;
        if (voyage.length > 8) result += voyage.length - 8;
        if (List.of("중국","동인도")
                .stream()
                .anyMatch(v -> voyage.zone.equals(v))) result += 4;
        return Math.max(result, 0);
    }

    protected int captainHistoryRisk() {
        int result = 1;
        if (history.length() < 5) result += 4;
        result += history.noProfitList();
        return Math.max(result, 0);
    }
}
