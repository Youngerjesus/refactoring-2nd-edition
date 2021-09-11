package refactoring.app.chapter01;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Plays {
    private Map<String, Play> playMap = new HashMap<>();

    public Plays(Map<String, Play> playMap) {
        this.playMap = playMap;
    }

    public Play get(Performance performance) {
        return playMap.get(performance.getPlayId());
    }
}
