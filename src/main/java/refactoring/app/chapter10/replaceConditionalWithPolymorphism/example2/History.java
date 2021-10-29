package refactoring.app.chapter10.replaceConditionalWithPolymorphism.example2;

import java.util.ArrayList;
import java.util.List;

public class History {
    List<Voyage> voyages = new ArrayList<>();

    public boolean hasChina() {
        return voyages.stream()
                .anyMatch(v -> v.zone.equals("중국"));
    }

    public int length() {
        return voyages.size();
    }

    public int noProfitList() {
        return (int) voyages.stream()
                .filter(v -> v.profit < 0)
                .count();
    }
}
