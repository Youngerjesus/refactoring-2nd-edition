package refactoring.app.chapter12.replaceTypeCodeWithSubclasses.direct;

import java.util.List;

public class Employee {
    String name;

    public static Employee createEmployee(String name, String type) throws Exception {
        switch (type) {
            case "engineer": return new Engineer(name);
            case "manager": return new Manager(name);
            case "salesperson": return new Salesperson(name);
            default: throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다."));
        }
    }

    public Employee(String name) {
        this.name = name;
    }
}
