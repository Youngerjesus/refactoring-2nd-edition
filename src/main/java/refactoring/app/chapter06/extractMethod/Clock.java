package refactoring.app.chapter06.extractMethod;

import java.time.LocalDateTime;

public class Clock {
    public static LocalDateTime today() {
        return LocalDateTime.now();
    }
}
