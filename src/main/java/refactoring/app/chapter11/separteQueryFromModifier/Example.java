package refactoring.app.chapter11.separteQueryFromModifier;

import java.util.ArrayList;
import java.util.List;

public class Example {
    List<Person> people = new ArrayList<>();

    public void alertMiscreant() {
        if (findMiscreant().equals("")) return;
        setOffAlarms();
    }

    public String findMiscreant(){
        for (Person p : people) {
            if (p.name.equals("조커")) {
                return "조커";
            }
            if (p.name.equals("사루만")) {
                return "사루만";
            }
        }
        return "";
    }

    private void setOffAlarms() {

    }
}
