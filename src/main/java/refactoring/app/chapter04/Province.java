package refactoring.app.chapter04;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Province {
    protected String name;
    protected List<Producer> producers = new ArrayList<>();
    protected int totalProduction;
    protected int demand;
    protected int price;

    public Province(String name, int demand, int price) {
        this.name = name;
        this.totalProduction = 0;
        this.demand = demand;
        this.price = price;
    }

    public Province(String name, List<Producer> producers, int demand, int price) {
        this.name = name;
        this.producers = producers;
        this.totalProduction = 0;
        this.demand = demand;
        this.price = price;
        producers.forEach(p -> {
            p.province = this;
            totalProduction += p.production;
        });
    }

    public void addProducer(Producer producer) {
        producers.add(producer);
        totalProduction += producer.production;
    }

    // 생산 부족분을 계산하는 메소드
    public int shortFall() {
        return this.demand - this.totalProduction;
    }

    // 수익을 계산하는 메소드
    public int profit() {
        return demandValue() - demandCost();
    }


    private int demandValue() {
        return satisfiedDemand() * this.price;
    }

    private int satisfiedDemand() {
        return Math.min(this.demand, this.totalProduction);
    }

    private int demandCost() {
        int remainDemand = this.demand;
        int result = 0;

        this.producers.sort(Comparator.comparingInt(p -> p.cost));

        for (Producer p : this.producers) {
            int contribution = Math.min(remainDemand, p.production);
            remainDemand -= contribution;
            result += contribution * p.cost;
        }

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Producer> getProducers() {
        return producers;
    }

    public void setProducers(List<Producer> producers) {
        this.producers = producers;
    }

    public int getTotalProduction() {
        return totalProduction;
    }

    public void setTotalProduction(int totalProduction) {
        this.totalProduction = totalProduction;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
