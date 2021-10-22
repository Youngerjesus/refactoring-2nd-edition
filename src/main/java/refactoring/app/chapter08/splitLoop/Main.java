package refactoring.app.chapter08.splitLoop;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<People> peopleList = getPeopleList();
        int youngest = peopleList.isEmpty() ? Integer.MAX_VALUE : peopleList.get(0).age;
        int totalSalary = 0;
        for (People p : peopleList) {
            if (p.age < youngest) youngest = p.age;
        }

        for (People p : peopleList) {
            totalSalary += p.salary;
        }
    }

    private static List<People> getPeopleList() {
        return null;
    }
}
