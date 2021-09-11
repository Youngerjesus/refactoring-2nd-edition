package refactoring.app.chapter01.performanceCalculator;

import refactoring.app.chapter01.Performance;
import refactoring.app.chapter01.Play;

public class PerformanceCalculatorFactory {
    public PerformanceCalculator createPerformanceCalculator(Performance performance, Play play) throws Exception {
        switch (play.getType()) {
            case TRAGEDY:
                return new TragedyCalculator(performance, play);
            case COMEDY:
                return new ComedyCalculator(performance, play);
            default:
                throw new Exception("알 수 없는 타입입니다.");
        }
    }
}
