package refactoring.app.chapter06.changeFunctionDeclaration;

public class AfterMigrationProcess {
    public double circum(double radius) {
        return circumference(radius);
    }

    private double circumference(double radius) {
        return 2 * Math.PI * radius;
    }
}
