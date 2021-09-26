# 테스트 구축하기 

리팩토링은 분명 필요한 일이지만 리팩토링을 제대로 하기 위해서는 테스트 케이스가 필요하다. (리팩토링을 하더라도 제대로 기능이 동작하고 있다는 테스트를 말한다.)

리팩토링을 하지 않더라도 이런 테스트를 작성하는 일은 개발 효율을 높여준다. 

개발에 시간을 빼앗기는데 개발 효율이 높아지는 것에 의아해 할 수 있다. 그러면 이 이유를 알아보자. 

***

## 4.1 자가 테스트 코드의 가치

프로그래머들이 시간을 쏟는 비중에서 실제로 코드를 짜는 비중은 그렇게 높지 않다.

현재 상황을 파악하는데에 시간을 쓰기도 하고 설계를 하는데에도 시간을 쓰기도 한다. 

그리고 대부분의 시간은 버그를 찾기 위한 디버깅을 하늗네 시간을 쓴다.

대부분의 프로그래머들은 자신마다 디버깅의 무용담을 하나씩 가지고 있다. (며칠이 걸렸던 디버깅 이런 것들을 말한다.)

테스트 코드는 이런 디버깅 시간을 줄여주는데 막대한 영향을 끼친다. 

코드를 짜면서 테스트 코드를 추가하면서 진행을 한다면 테스트 코드가 실패할 때 방금 작성한 코드가 문제가 있을 가능성이 높다.

찾아야 하는 코드의 양을 확연히 줄여주는 측면에서 테스트 코드는 디버깅의 시간을 많이 줄여준다. 

__즉 테스트 코드는 강력한 버그 검출 도구 버그를 찾는 시간을 대폭 줄여준다.__

테스트를 작성하기 좋은 시점은 실제 프로그래잉을 하기 전인 기능을 추가하기 전에 테스트를 작성하는 것이다.

얼핏 순서가 뒤바뀐 듯 들리지만 전혀 그렇지 않다.

테스트를 작성하다 보면 원하는 기능을 추가하기 전에 무엇이 필요한지 고민하게 된다. 

실제 구현에 집착하는게 아니라 인터페이스에 집착하게 된다. 

그리고 테스트를 먼저 작성함을 통해 코딩이 완료되는 시점을 명확하게 정할 수 있다. (테스트가 모두 통과되는 시점이다.)

켄트 백은 이처럼 테스트부터 작성하는 습관을 통해서 테스트 주도 개발 (Test-Driven Development) 를 개발했다.

TDD 에서는 테스트틀 먼저 작성하고 테스트를 통과하기 위해 코드를 작성하고 통과한 다음에 리팩토링을 진행하는 과정으로 이뤄져있다. 

이번 장에서는 테스트 코드를 작성하는 방법을 소개하곘다.

테스트가 주제가 아닌 만큼 깊게 들어가지는 않겠지만 어떠한 효과를 누릴 수 있는지 소개하곘다.

***

## 4.2 테스트 할 샘플 코드 

우선 테스트 할 소프트웨어를 먼저 살펴보자. 

```text
지역: Asia

수요: 30, 가격: 20

생산자 수: 3

Byzantium: 비용: 10, 생산량: 9, 수익: 90

Attalia: 비용: 12, 생산량: 10, 수익: 120

Sinope: 비용: 10, 생산량: 6, 수익: 60

부족분: 5, 총수익: 230
```

생산 계획은 각 지역 (province) 의 수요 (demand) 와 가격 (price) 으로 구성된다. 

지역에 위치한 생산자들은 각기 제품을 특정 가격으로 특정 수량만큼 생산할 수 있다.

이 텍스트는 생산자별로 제품을 모두 판매했을 때 얻을 수 있는 수익 (full revenue) 를 보여준다. 

부족분 (shortfall)은 수요에서 총 생산량을 뺸 값이고 여기서는 총수익 (profit) 도 볼 수 있다. 

사용자는 여기서 수요, 가격, 생산량 (production) 비용 (cost) 를 조정해가며, 그에따른 생산 부족분과 총수익을 확인할 수 있다.

여기서는 이 소프트웨어의 핵심 비즈니스 로직을 살펴보는데 집중하자. 

이 소프트웨어의 전반적인 기능은 다음과 같다. __(TODO 추가해놓자.)__

- 수익과 생산 부족성을 계산할 수 있어야 한다. 

비즈니스 로직 클래스는 크게 두 가지로 구성된다. 지역을 나타내는 Province 와 생산자인 Producer 이다.

클래스들은 다음과 같이 있다. 

#### Province Class

````java
public class Province {
    private String name;
    private List<Producer> producers = new ArrayList<>();
    private int totalProduction;
    private int demand;
    private int price;

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
````

#### Producer Class

````java
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
````

#### SampleProvinceFactory Class 

```java
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
```

***

## 4.3 첫 번째 테스트

이 코드를 테스트하기 위해서는 테스트 프레임워크를 마련해야 한다. 

여기서는 자바로 진행을 할 예정이니 JUnit5 를 사용하겠다. 

다음은 생산 부족분을 테스트하는 코드다.

```java
class ProvinceTest {

    @Test
    void shortfallTest(){
        //given
        Province sampleProvince = SampleProvinceFactory.getSampleProvince();
        int answer = 5;
        //when
        int result = sampleProvince.shortFall();
        //then
        assertEquals(answer, result);
    }

}
```

- 여기서 `given` 으로 주어진 부분에서 테스트에 필요한 객체와 데이터를 설정한다. 

- `when` 에서 실제 코드를 수행을 해보고 `then` 에서 검증을 해보는 과정으로 이뤄져있다. 

이렇게 테스트를 짤 수 있다. 

나는 일할 때 테스트를 굉장히 자주 수행하는데 방금 추가한 코드가 문제가 있는지 없는지 체크하기 위함이다. 

***

## 4.4 테스트 추가하기 

계속해서 테스트를 추가해보자. 

테스트 추가는 public 메소드로 되어있는 모든 메소드를 테스트 하는게 아니다. 

__위험요인을 기준으로 테스트를 작성해야 한다.__

즉 적은 수의 테스트로도 충분한 효과를 누릴 수 있어야 한다. 

__테스트의 목적은 향후 버그를 찾기 위함이다.__ 이를 알고가자.

그러므로 get() 과 set() 과 같은 단순 메소드들은 테스트할 필요가 없다. 

다음으로 수익을 계산하는 부분을 테스트로 추가해보자.

````java
@Test
void profitTest(){
    //given
    Province sampleProvince = SampleProvinceFactory.getSampleProvince();
    int answer = 230;
    //when
    int result = sampleProvince.profit();
    //then
    assertEquals(answer, result);
}
```` 

여기서 보면 이전에 작성한 생산 부족분을 계산하는 코드와 유사한 부분이 있다. 

바로 `Province sampleProvince = SampleProvinceFactory.getSampleProvince();` 이 부분인데 테스트 할 대상을 결정하는 부분이다. 

이런 중복은 제거하는게 좋다.

근데 중복을 제거할 떈 다음과 같이 하는건 추천하지 않는다. 

```java
class ProvinceTest {

    Province sampleProvince = SampleProvinceFactory.getSampleProvince();
    
    @Test
    void shortfallTest(){
        //given
        int answer = 5;
        //when
        int result = sampleProvince.shortFall();
        //then
        assertEquals(answer, result);
    }

    @Test
    void profitTest(){
        //given
        int answer = 230;
        //when
        int result = sampleProvince.profit();
        //then
        assertEquals(answer, result);
    }
}
```

왜냐하면 테스트가 객체를 공유하도록 설정하면 한 테스트 할 객체를 고립시키지 못하므로 한 테스트가 변경하면 다른 테스트에서도 영향이 갈 수 있기 떄문이다.

그러므로 다음과 같이 변경해야 한다.

````java
class ProvinceTest {

    Province sampleProvince;

    @BeforeEach
    void setUp() {
        sampleProvince = SampleProvinceFactory.getSampleProvince();
    }

    @Test
    void shortfallTest(){
        //given
        int answer = 5;
        //when
        int result = sampleProvince.shortFall();
        //then
        assertEquals(answer, result);
    }

    @Test
    void profitTest(){
        //given
        int answer = 230;
        //when
        int result = sampleProvince.profit();
        //then
        assertEquals(answer, result);
    }
}
````

즉 공유 픽처스를 사용하면 안되고 불변의 객체를 가지고 테스트를 진행해야 한다. 

***

## 4.5 픽스처 수정하기 

지금까지는 고정된 픽스처를 사용해서 테스트를 진행했다. 

그치만 실전에서는 이런 픽스처가 Setter 메소드로 인해 변경될 가능성이 있다. 

__이런 변경 가능성을 염두해두고 테스트를 짜야한다.__

Producer 클래스의 경우에는 setProduction() 같은 메소드는 특히 좀 특이하다. 

그러므로 이 부분을 테스트 해봐야한다.

````java
@Test
void changeProductionTest(){
    //given
    int shortFall = -6;
    int profit = 292;
    //when
    sampleProvince.producers.get(0).setProduction(20);
    int actualShortFall = sampleProvince.shortFall();
    int actualProfit = sampleProvince.profit();
    //then
    assertEquals(shortFall, actualShortFall);
    assertEquals(profit, actualProfit)  ;
}
```` 

여기서는 검증을 하는 부분이 두 assertEquals() 로 나뉘는데 일반적으로 한 테스트에서는 한 개의 assertEquals() 만 있는게 좋다.  

왜냐하면 한 개의 assertEquals() 가 실패하면 뒤에있는건 검사를 하지도 않기 때문이다. 

그러므로 유용한 정보를 놓칠 수도 있기 떄문에 웬만하면 한 개의 assertEquals() 로 두는게 좋다. 

***

## 4.6 경계 조건 검사하기 

지금까지 작성한 모든 경우는 정상적인 데이터를 가지고 검사를 진행했다. 일명 꽃길 (Happy Path) 상황에 집중한 것이다. 

그런데 이 __범위를 벗어나는 경계 지점에서 문제가 생기면 어떤 일이 일어나는지 확인하는게 좋다.__ 

그 중 하나로 나는 컬렉션을 쓰는 데이터가 있다면 이 값이 비어있을 때 무슨 일이 일어날까? 

다음과 같이 테스트를 만들어보면 된다.

```java
@Test
void noProducersTest(){
    //given
    Province province = new Province("No Producers", new ArrayList<>(), 30, 20);
    int shortFall = 30;
    int profit = 0;
    //when
    int actualShortFall = province.shortFall();
    int actualProfit = province.profit();
    //then
    assertEquals(shortFall, actualShortFall);
    assertEquals(profit, actualProfit);
}
```

__일반적인 데이터의 경우에는 맞지않는 특이한 데이터를 넣어보면 된다.__

예로 수요는 음수가 될 수 없다. 

수요의 최솟값은 0 이어야 한다. 

즉 이와 같은 __문제가 생길 수 있는 경계 조건을 생각해보고 그 부분을 집중적으로 테스트를 해보자.__

이러한 유효성 검사는 너무 많으면 중복 체크를 하게 될 가능성이 많으므로 문제가 될 수 있다.

하지만 외부 시스템으로부터 받는 JSON 과 같은 데이터는 유효성 체크를 항상 하는게 좋다.

이제 그렇다면 테스트는 어느 수준까지 작성해야 할까? 

많이들 이러한 고민을 할 것이다. 

테스트가 개발 속도를 높여준다는 말은 있지만 테스트에 너무 집착하다 보면 기능을 추가하는 의욕이 떨어질 수 있다. 

__테스트는 위험한 부분을 위주로 작성하는게 좋다.__

__또 코드가 복잡한 부분을 찾아보자 이는 틀릴 여지가 많기 떄문에 테스트를 추가해서 검증하는게 좋다.__

***

## 4.7 끝나지 않은 여정 

이 장에서 설명할 내용은 여기까지다. 

결국 이 책의 핵심은 리팩토링이기 떄문에 테스트에 대해서 많은 내용을 설명하지는 않겠다. 

이 장에서 보여준 테스트는 단위 테스트 (Unit Test) 이다. 

단위 테스트는 코드의 작은 영역만 집중적으로 테스트하되 컴포넌트의 상호작용까지 테스트를 하지는 않는 테스트틀 말한다. 

모든 자가 검증의 테스트는 단위 테스트로부터 나온다. 

이외에도 물론 테스트는 다양하게 많다. 

컴포넌트의 상호작용을 하는 통합 테스트, 성능 테스트 등등

__테스트를 할 때 명심해야 하는 생각이 항상 처음부터 완벽한 테스트 케이스를 모두 갖출 순 없다.__

__그러므로 버그 리포트를 만날 때 마다 해당 테스트 케이스부터 작성해두자.__

많은 사람들이 테스트를 할 때 어느 정도까지 해야하는 지를 궁금해한다. 

누군가는 이에대한 답변으로 테스트 커버리지를 말하기도 하는데 테스트 커버리지는 코드에서 테스트 하지 않은 부분을 찾는 것이지 코드 자체를 모두 테스트 한 것은 아니다. 

그러므로 커버리지가 높다라는 것이 모든 경우를 테스트 한 것은 아니다.


 
 

