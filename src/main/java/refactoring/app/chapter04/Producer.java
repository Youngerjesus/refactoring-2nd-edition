package refactoring.app.chapter04;

public class Producer {
    protected String name;
    protected int cost;
    protected int production;
    protected Province province;

    public Producer(String name, int cost, int production) {
        this.name = name;
        this.cost = cost;
        this.production = production;
    }

    public Producer(String name, int cost, int production, Province province) {
        this.name = name;
        this.cost = cost;
        this.production = production;
        this.province = province;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getProduction() {
        return production;
    }

    public void setProduction(int production) {
        this.province.totalProduction += production - this.production;
        this.production = production;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }
}
