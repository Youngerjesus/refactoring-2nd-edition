package refactoring.app.chapter10.replaceControlFlagWithBreak;

import java.util.List;

public class Example {
    public void checkForMiscreants(List<Person> people) {
        for (Person p : people) {
            if (p.name.equals("조거")) {
                sendAlert();
                return;
            }
            if (p.name.equals("사루만")) {
                sendAlert();
                return;
            }
        }
    }

    private void sendAlert() {
    }
}
