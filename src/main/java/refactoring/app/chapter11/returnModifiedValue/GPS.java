package refactoring.app.chapter11.returnModifiedValue;

import java.util.ArrayList;
import java.util.List;

public class GPS {
    List<Point> points = new ArrayList<>();
    int totalAscent;
    int totalTime;
    int totalDistance;

    public void calculate() {
        totalAscent = calculateAscent();
        totalTime = calculateTime();
        totalDistance = calculateDistance();

        int pace = totalTime / 60 / totalDistance;
    }

    private int calculateAscent() {
        int result = 0;
        for (int i = 0; i < points.size(); i++) {
            int verticalChange = points.get(i).elevation - points.get(i - 1).elevation;
            result += Math.max(verticalChange, 0);
        }
        return result;
    }

    private int calculateTime() {
        return 0;
    }

    private int calculateDistance() {
        return 0;
    }

}
