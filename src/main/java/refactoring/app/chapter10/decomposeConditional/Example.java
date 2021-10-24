package refactoring.app.chapter10.decomposeConditional;

import java.time.LocalDateTime;

public class Example {
    int quantity;
    int charge;

    public void calculatePayment(Plan plan, LocalDateTime dateTime) {
        if (isSummer(dateTime, plan)) {
            charge = summerCharge(plan);
        } else {
          charge = regularCharge(plan);
        }
    }

    private boolean isSummer(LocalDateTime dateTime, Plan plan) {
        return !dateTime.isBefore(plan.summerStart) && dateTime.isAfter(plan.summerEnd);
    }

    private int summerCharge(Plan plan) {
        return (int) (quantity * plan.summerRate);
    }

    private int regularCharge(Plan plan) {
        return (int) (quantity * plan.regularRate + plan.regularServiceCharge);
    }
}
