package refactoring.app.chapter08.replaceLoopWIthPipeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public List<Office> acquireData(String input) {
        String[] lines = input.split("\n");
        List<Office> result = new ArrayList<>();

        String[] loop = lines;
        Arrays.stream(lines)
                .skip(1)
                .filter(line -> !line.trim().equals(""))
                .map(line -> line.split(","))
                .filter(record -> record[1].trim().equals("India"))
                .forEach(record -> result.add(new Office(record[0], record[2])));

        return result;
    }
}
