package refactoring.app.chapter11.preserveWholeObject;

public class Example {
    Room room;
    HeatingPlan heatingPlan;
    public void client() throws Exception {
        if (!heatingPlan.withinRange(room.daysTempRange)) {
            throw new Exception("방 온도가 지정 범위를 벗어났습니다.");
        }
    }
}
