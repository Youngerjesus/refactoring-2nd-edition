package refactoring.app.chapter11.parameterizeFunction;

public class Example2 {
    public int baseCharge(int usage) {
        if (usage < 0) return 0;

        int amount =
                (int) (withinBand(usage, 0, 100) * 0.03
                       + withinBand(usage, 100, 200) * 0.05
                       + withinBand(usage, 200, Integer.MAX_VALUE) * 0.07);

        return usd(amount);
    }

    private int usd(int amount) {
        return amount;
    }

    private double topBand(int usage) {
        return usage > 200 ? usage - 200 : 0;
    }

    private double middleBand(int usage) {
        return usage > 100 ? Math.min(usage, 200) - 100 : 0;
    }

    private int bottomBand(int usage) {
        return Math.min(usage, 0);
    }

    private double withinBand(int usage, int bottom, int top) {
        return usage > bottom ? Math.min(usage, top) - bottom : 0;
    }
}
