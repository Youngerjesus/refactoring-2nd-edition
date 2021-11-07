package refactoring.app.chapter12.replaceSubclassWithDelegate.multiSubClass;

public class Bird {
    String name;
    String plumage;
    SpeciesDelegate speciesDelegate;

    public Bird(Data data) {
        this.name = data.name;
        this.plumage = data.plumage;
        speciesDelegate = selectSpeciesDelegate(data);
    }

    private SpeciesDelegate selectSpeciesDelegate(Data data) {
        switch (data.type) {
            case "유럽 제비":
                return new EuropeanSwallowDelegate(data);
            case "아프리카 제비":
                return new AfricanSwallowDelegate(data);
            case "노르웨이 파랑 앵무":
                return new NorwegianBlueParrotDelegate(data);
            default:
                return new SpeciesDelegate(data);
        }
    }

    public String getName() {
        return name;
    }

    public String getPlumage() {
        return speciesDelegate.getPlumage();
    }

    public int airSpeedVelocity() {
        return speciesDelegate.airSpeedVelocity();
    }
}
