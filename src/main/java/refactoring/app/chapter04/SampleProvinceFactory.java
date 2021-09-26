package refactoring.app.chapter04;

import java.util.ArrayList;
import java.util.List;

public class SampleProvinceFactory {

    private SampleProvinceFactory() {

    }

    public static Province getSampleProvince() {
        List<Producer> producers = new ArrayList<>();
        Producer Byzantinum = new Producer("Byzantinum", 10, 9);
        Producer Attalia = new Producer("Attalia", 12, 10);
        Producer Sinope = new Producer("Sinope", 10, 6);

        producers.add(Byzantinum);
        producers.add(Attalia);
        producers.add(Sinope);

        return new Province("Asia", producers, 30, 20);
    }
}
