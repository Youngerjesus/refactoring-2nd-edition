package refactoring.app.chapter01.performanceCalculator;

import refactoring.app.chapter01.Performance;
import refactoring.app.chapter01.Play;
import refactoring.app.chapter01.PlayType;

public class PerformanceCalculator {
    protected Performance performance;
    protected Play play;

    public PerformanceCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    public int amountFor() throws Exception {
        throw new Exception("서브 클래스에서 이르 모두 구현했습니다.");
    }

    public int volumeCreditFor() throws Exception {
        throw new Exception("서브 클래스에서 이르 모두 구현했습니다.");
    }
}
