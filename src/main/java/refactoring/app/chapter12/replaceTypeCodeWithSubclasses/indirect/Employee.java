package refactoring.app.chapter12.replaceTypeCodeWithSubclasses.indirect;

public class Employee {
    String name;
    EmployeeType type;

    public Employee(String name, String type) throws Exception {
        this.name = name;
        this.type = Employee.createEmployeeType(type);
    }

    private static EmployeeType createEmployeeType(String type) throws Exception {
        switch (type) {
            case "engineer": return new Engineer();
            case "manager": return new Manager();
            case "salesperson": return new Salesperson();
            default: throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다."));
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public String getType() {
        return type.toString();
    }
}