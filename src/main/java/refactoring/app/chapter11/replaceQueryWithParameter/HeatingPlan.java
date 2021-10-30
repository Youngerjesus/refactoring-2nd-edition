package refactoring.app.chapter11.replaceQueryWithParameter;

public class HeatingPlan {
    int max;
    int min;

    public int targetTemperature(int selectedTemperature) {
        if (selectedTemperature > this.max) {
            return this.max;
        }
        if (selectedTemperature < this.min) {
            return this.min;
        }
        return selectedTemperature;
    }
}
