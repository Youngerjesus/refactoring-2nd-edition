package refactoring.app.chapter10.consolidateConditionalExpression;

public class Example {
    public int disabilityAmount(Employee employee) {
        if (isNotEligibleForDisability(employee)) return 0;
        // 장애 수단 계산
        return 0;
    }

    private boolean isNotEligibleForDisability(Employee employee) {
        return employee.seniority < 2 || employee.monthDisabled > 12 || employee.isPartTime;
    }
}
