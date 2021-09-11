package refactoring.app.chapter01.performanceCalculator;

import refactoring.app.chapter01.Performance;
import refactoring.app.chapter01.Play;

public class ComedyCalculator extends PerformanceCalculator {
    public ComedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    @Override
    public int amountFor() {
        int result = 30000;
        if (performance.getAudience() > 20) {
            result += 10000 + 500 * (performance.getAudience() - 20);
        }
        result += 300 * performance.getAudience();
        return result;
    }

    @Override
    public int volumeCreditFor() {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);
        result += Math.floor(performance.getAudience() / 5);

        return result;
    }
}
