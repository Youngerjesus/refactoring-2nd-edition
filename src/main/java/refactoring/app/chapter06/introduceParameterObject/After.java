package refactoring.app.chapter06.introduceParameterObject;

import java.util.List;
import java.util.stream.Collectors;

public class After {
    Station station;

    public List<Reading> readingsOutsideRange(Station station, int min, int max, NumberRange numberRange) {
        return station.readings
                .stream()
                .filter(r -> r.temp < numberRange.getMax() || r.temp > numberRange.getMax())
                .collect(Collectors.toList());
    }
}
