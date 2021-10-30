package refactoring.app.chapter11.replaceQueryWithParameter;

public class Example {
    HeatingPlan heatingPlan;
    public void client() {
        if (heatingPlan.targetTemperature(Thermostat.selectedTemperature) > Thermostat.currentTemperature) setToHeat();
        else if (heatingPlan.targetTemperature(Thermostat.selectedTemperature) < Thermostat.currentTemperature) setToCool();
        else setOff();
    }

    private void setOff() {
    }

    private void setToHeat() {
    }

    private void setToCool() {
    }
}
