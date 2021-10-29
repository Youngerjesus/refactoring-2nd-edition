package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example2;

import java.util.List;

public class Example2 {
    // 투자 등급을 계산해주는 함수
    public String rating(Voyage voyage, History history) {
        int vpf = voyageProfitFactor(voyage, history);
        int vr = voyageRisk(voyage);
        int chr = captainHistoryRisk(voyage, history);
        if (vpf * 3 > vr + chr * 2) return "A";
        return "B";
    }

    private int voyageProfitFactor(Voyage voyage, History history) {
        int result = 2;
        if (voyage.zone.equals("중국")) result += 1;
        if (voyage.zone.equals("동인도")) result += 1;
        if (voyage.zone.equals("wndrnr") && hasChina(history)) {
            result += 3;
            if (history.length() > 10) result += 1;
            if (voyage.length > 12) result += 1;
            if (voyage.length > 18) result -= 1;
        }
        else {
            if (history.length() > 8) result += 1;
            if (voyage.length > 14) result -= 1;
        }
        return result;
    }

    private boolean hasChina(History history) {
        return history.hasChina();
    }

    private int voyageRisk(Voyage voyage) {
        int result = 1;
        if (voyage.length > 4) result += 2;
        if (voyage.length > 8) result += voyage.length - 8;
        if (List.of("중국","동인도")
                .stream()
                .anyMatch(v -> voyage.zone.equals(v))) result += 4;
        return Math.max(result, 0);
    }

    private int captainHistoryRisk(Voyage voyage, History history) {
        int result = 1;
        if (history.length() < 5) result += 4;
        result += history.noProfitList();
        if (voyage.zone.equals("중국") && hasChina(history)) result -= 2;
        return Math.max(result, 0);
    }

    public Rating createRating(Voyage voyage, History history) {
        if (voyage.zone.equals("중국") && history.hasChina()) {
            return new ExperiencedChinaRating(voyage, history);
        }
        return new Rating(voyage, history);
    }
}
