package refactoring.app.chapter09.replaceDerivedVariableWithQuery;

import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class ProductionPlan {
    List<Adjustment> adjustments = new ArrayList<>();

    public int getProduction() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
    }
}
