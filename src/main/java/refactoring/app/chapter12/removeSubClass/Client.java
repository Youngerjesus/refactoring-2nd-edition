package refactoring.app.chapter12.removeSubClass;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Client {
    public List<Person> loadFromInput(List<Data> dataList) {
        return dataList.stream()
                .map(this::createPerson)
                .collect(Collectors.toList());
    }

    private Person createPerson(Data d) {
        Person p;
        switch (d.gender) {
            case "M": p = new Person(d.name, "M"); break;
            case "F": p = new Person(d.name, "F"); break;
            default: p = new Person(d.name);
        }
        return p;
    }
}
