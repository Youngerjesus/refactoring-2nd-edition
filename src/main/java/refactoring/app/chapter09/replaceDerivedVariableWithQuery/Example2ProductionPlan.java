package refactoring.app.chapter09.replaceDerivedVariableWithQuery;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class Example2ProductionPlan {
    int initialProduction;
    List<Adjustment> adjustments;

    public Example2ProductionPlan(int production) {
        this.initialProduction = production;
        this.adjustments = new ArrayList<>();
    }

    public int getProduction() {
        return initialProduction + calculatedProductionAccumulate();
    }

    private int calculatedProductionAccumulate() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
    }
}
