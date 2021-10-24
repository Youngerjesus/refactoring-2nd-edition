# 데이터 조직화 

데이터 구조는 프로그램에서 중요한 역할을 하니까 이를 위해 따로 챕터를 하나 만들었다. 

하나의 값이 여러 목적으로 사용한다면 혼란과 버그를 야기할 수 있으므로 __변수 쪼개기 (9.1 절)__ 을 활용해 적절하게 분리하자.

다른 프로그램 요소와 마찬가지로 변수 이름을 제대로 짓는것은 중요하므로 __변수 이름 바꾸기 (6.7 절)__ 과는 친해지자.

변수 자체를 완전히 없애는게 더 나은 경우라면 __파생 변수를 질의 함수로 바꾸기 (9.3 절)__ 를 사용하자.

참조 (Reference) 인지 값 (Value) 인지에 따라 헷갈려서 문제가 되는 코드가 있는데 이런 경우에는 둘 사이를 전환할 수 있는 __참조를 값으로 바꾸끼 (9.4 절)__ 과 __값을 참조로 바꾸기 (9.5 절)__ 을 적용하자.

***

## 9.1 변수 쪼개기

### 배경 

변수는 다양한 용도로 사용한다. 

그치만 변수를 사용할 땐 딱 하나의 역할만으로 사용해야 한다. 한 변수를 여러가지 목적으로 사용하면 코드를 이해하는데 혼란을 줄 수 있고 추후에 버그를 낼 가능성이 높다.

이 말은 변수에 한번보다 많은 대입을 하는 경우에는 문제가 있다는 뜻이다.

믈론 여러번의 대입이 필요한 변수도 있다. 반복문을 돌아서 값을 누적시키는 변수의 경우. 

### 절차

1. 변수를 처음 선언한 곳 즉 대입하는 곳에 변수 이름을 바꾼다. (이때 가능하면 불변으로 선언하자. 그래야 컴파일 에러를 만나서 다음 대입 장소를 쉽게 알 수 있다.)

2. 이 변수의 이름을 다음 대입하는 곳까지 쭉 따라서 만들자.

3. 두 번째 대입하는 곳을 만나면 거기에서도 적절한 이름을 지어주자 (여기서도 불변으로 하자.)

4. 아까와 같이 쭉 따라서 두 번째 선언한 변수로 쭉 따라서 교체한다.

5. 테스트한다. 

6. 이런 작업을 반복하면 된다.

### 예시

이번 예에서는 해기스 (Haggis) 라는 음식이 다른 지역으로 전파되는 거리를 구하는 코드를 살펴보자.

해기스는 발상지에서 처음으로 힘을 받아 일정한 가속도로 전파되다가 시간이 흐른 후 두 번째 힘을 받아서 잔파속도가 빨라진다.

이 일반적인 물리 법칙을 적용해 전파 거리를 계산한 공식은 다음과 같다.

```java
public class Haggis {
    public double distanceTravelled(Scenario scenario, int time) {
        double result;
        int acc = scenario.primaryForce / scenario.mass; // 가속도 = 힘 * 질량
        int primaryTime = Math.min(time, scenario.delay);
        result = 0.5 * acc * primaryTime * primaryTime; // 전파된 거리
        int secondaryTime = time - scenario.delay;
        if (secondaryTime > 0) {
            // 두 번째 힘을 반영해 다시 계산
            int primaryVelocity = acc * scenario.delay;
            acc = (scenario.primaryForce + scenario.secondaryForce) / scenario.mass;
            result += primaryVelocity * secondaryTime + 0.5 * acc * secondaryTime * secondaryTime; 
        }
        return result;
    }
}
```

괜찮아 보이는 함수지만 acc 값은 대입이 두 번 일어난다. 

자 리팩토링을 해보자. 

처음 acc 를 선언한 부분에 적절한 이름으로 바꾸고 불변 객체로 선언하자. 그리고 다음 대입까지 쭉 바꿔보자.

쭉 바꾸다가 컴파일 에러가 나는 부분이 있으면 거기가 두 번째 대입을 하는 부분이다. 

그러므로 여기도 적절한 이름으로 바꿔주자.

````java
public class Haggis {

    public double distanceTravelled(Scenario scenario, int time) {
        double result;
        final int primaryAcceleration = scenario.primaryForce / scenario.mass; // 가속도 = 힘 * 질량
        int primaryTime = Math.min(time, scenario.delay);
        result = 0.5 * primaryAcceleration * primaryTime * primaryTime; // 전파된 거리
        int secondaryTime = time - scenario.delay;
        if (secondaryTime > 0) {
            // 두 번째 힘을 반영해 다시 계산
            int primaryVelocity = primaryAcceleration * scenario.delay;
            final int secondaryAcceleration = (scenario.primaryForce + scenario.secondaryForce) / scenario.mass;
            result += primaryVelocity * secondaryTime + 0.5 * secondaryAcceleration * secondaryTime * secondaryTime;
        }
        return result;
    }
}
````
이후 이제 불변을 뜻하는 final 키워드를 삭제하면 된다.

__다음 예시인 입력 매개변수의 값을 수정하는 경우를 보자.__

변수 쪼개기의 한 예로 입력 매개변수를 생각해볼 수 있다. 

```java
public class ExampleInputParameter {
    public int discount(int inputValue, int quantity) {
        if (inputValue > 50) inputValue -= 2;
        if (quantity > 100) inputValue -= 1;
        return inputValue; 
    }
}
```

여기서는 입력 매개변수인 inputValue 가 매개변수의 역할로도 쓰이고 반환하는 용도로도 쓰인다. 

그러므로 이 상황에서는 inputValue 변수를 쪼갠다. Call By Value 로 값이 전달되므로 그냥 복사해서 사용하면 된다.

```java
public class ExampleInputParameter {
    public int discount(int inputValue, int quantity) {
        int result = inputValue;
        if (inputValue > 50) result -= 2;
        if (quantity > 100) result -= 1;
        return result;
    }
}
```

다음과 같이 사용하면 코드가 좀 더 명확해진다. 반환용과 매개변수의 사용이 명확해지므로.

***

## 9.2 필드 이름 바꾸기 

### 배경 

이름이 중요하다는 사실은 다들 안다.

프로그램 곳곳에 쓰이는 레코드의 필드들의 이름은 특히 더 중요하다. 

이런 이름들로 인해 데이터 구조를 이해하기 더 쉬워지고 결국에 프로그램을 이해하기 더 쉬워진다.

데이터 구조가 중요한 만큼 프로젝트를 진행하면서 더 깊은 이해를 바탕으로 더 적합한 이름이 보인다면 즉시 이를 반영하도록 하자.

### 절차

1. 레코드의 유효 범위가 제한적이라면 이름을 바꾸고 테스트해보자. 테스트를 통과한다면 이후 작업은 할 필요가 없다.

2. 레코드가 캡슐화되어 있지 않다면 캡술화부터 한다. __(자바에선 캡슐화가 되어있다.)__

3. 캡슐화된 객체 안 private 필드 명을 바꾸고 그에 맞게 내부 메소드를 수정한다.

***

## 9.3 파생 변수를 질의 함수로 바꾸기

### 배경

파생 변수란 말 자체부터 정확하게 정의하자면 기존의 변수를 조합해서 새로운 변수를 추출하는 걸 말한다. __(즉 기존의 변수들이 업데이트가 되면 이 변수에도 영향이 갈 여지가 있다는 뜻이다.)__ 

가변 데이터는 소프트웨어에 문제를 일으키는 요소 중 하나다. 

주로 일으키는 문제는 한 쪽에서 업데이트 되서 연결된 다른 쪽에서 연쇄효과를 일으켜서 문제를 찾기 어렵게 만든다. __(여기서의 리팩토링 기법은 문제가 났을때 추적하기 위함. 비교적 쉽게 해결하기 위함이다.)___

__그렇다고 가변 데이터를 사용하지 않을 수도 없기 때문에 일단 가변 데이터를 사용하는 경우라면 유효 범위를 제한시켜 놓는게 가장 기본적이다.__

또 효과가 좋은 방법으로 값을 계산해낼 수 있는 변수들은 모두 제거하고 함수로 만들어 두는게 더 나을 수 있다. __(매번 값을 계산해서 가지고 있다라는 것 보다 계산하는게 더 문제를 해결하기 쉽다. 함수 자체가 또 목적이 드러나므로 이해하기 더 쉬울수 있다. 함수를 찾는것 vs 변수에 값을 대입하는 부분을 찾기 이므로.)__

물론 여기에는 합당한 예가 있다. 피연산자 데이터가 불변인 경우. 계산 결과도 일정할 것이므로 문제를 해결하기 쉽다. __(불변으로 만들어져있다는 것 자체가 문제의 원인을 찾기가 쉽다.)__

### 절차 

1. 변수 값이 갱신되는 지점을 모두 찾는다. 필요하다면 __변수 쪼개기 (9.1 절)__ 을 통해서 원인을 찾기 쉽게 구별시켜 놓자. __(주로 가변 데이터의 경우 변하는 값과 변하지 않는 값 이 두 요소로 나누는 것 같다.)__
   
2. 해당 변수의 값을 계산해주는 함수를 만든다. 

3. 해당 변수가 사용되는 곳에 Assertion 을 추가해서 함수의 결과와 변수의 값이 같은지 확인한다.

4. 테스트한다.

5. 변수를 읽는 코드를 모두 함수 호출로 대체한다.

6. 테스트한다.

7. 변수를 선언하고 갱신하는 코드를 죽은 코드 제거하기 (8.9 절)로 없앤다.

### 예시

디음과 같은 예시가 있다고 가정해보자. 

```java
public class ProductionPlan {
    int production;
    List<Adjustment> adjustments = new ArrayList<>();

    public int getProduction() {
        return production;
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
        production += adjustment.amount; 
    }
}
```

여기서는 중복이 있다. 데이터 중복.

adjustment 를 관리하는 과정에서 누적하는 값 production 이라는 변수를 만들어서 또 관리한다.

그런데 이 누적 값은 매번 값을 갱신하지 않고도 필요할 때 계산하면 된다. 

여기서 production 이라는 변수가 계산해낼 수 있다는 걸로 아직은 추측한 것일 뿐이므로 Assertion 을 통해 테스트를 해봐야한다.

````java
public class ProductionPlan {
    int production;
    List<Adjustment> adjustments = new ArrayList<>();

    public int getProduction() {
        assert production == calculatedProduction();
        return production;
    }

    private int calculatedProduction() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
        production += adjustment.amount;
    }
}
````

Assert 가 실패하지 않으면 이 코드와 production 변수를 지우고 기존의 calculatedProduction 함수를 인라인 하자.

```java
public class ProductionPlan {
    List<Adjustment> adjustments = new ArrayList<>();

    public int getProduction() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }
    
    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
    }
}
```

__예시 2: 변경되는 소스가 둘 이상일 때__

앞의 예는 production 변수를 변경하는 요소가 하나 뿐이라서 쉬웠다. 

다음과 같은 예를 보자. 

````java
public class Example2ProductionPlan {
    int production;
    List<Adjustment> adjustments;

    public Example2ProductionPlan(int production) {
        this.production = production;
        this.adjustments = new ArrayList<>();
    }

    public int getProduction() {
        return production;
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
        production += adjustment.amount;
    }
}
````

초기 생성자에서 받는 production 값이 있다.

이 요소와 함께 파생 변수로서의 값이 누적되는 production 을 구별시켜줘야한다.

이를 위해서 변수 쪼개기를 하자. 처음에 생성자로 전달받는 변수를 구별시켜놓으면 파생 변수로서 값이 누적되는 부분만 때서 생각하기 쉬워진다.

```java
public class Example2ProductionPlan {
    int initialProduction;
    int productionAccumulate;
    List<Adjustment> adjustments;

    public Example2ProductionPlan(int production) {
        this.initialProduction = production;
        this.productionAccumulate = 0;
        this.adjustments = new ArrayList<>();
    }

    public int getProduction() {
        return initialProduction + productionAccumulate;
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
        productionAccumulate += adjustment.amount;
    }
}
```

이제 이전의 방법과 똑같아진다. 파생 변수로서의 역할이 내가 예측한 것과 맞게 Assert 를 걸어주고 맞는지 확인한 후 그 부분을 대체해주면 된다.

```java
public class Example2ProductionPlan {
    int initialProduction;
    int productionAccumulate;
    List<Adjustment> adjustments;

    public Example2ProductionPlan(int production) {
        this.initialProduction = production;
        this.productionAccumulate = 0;
        this.adjustments = new ArrayList<>();
    }

    public int getProduction() {
        assert productionAccumulate == calculatedProductionAccumulate();
        return initialProduction + productionAccumulate;
    }

    private int calculatedProductionAccumulate() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
        productionAccumulate += adjustment.amount;
    }
}
```

Assert 가 맞다면 이제 함수를 인라인 해주고 productionAccumulate 변수를 지워주면 된다.

다만 여기서는 calculateProductionAccumulate() 함수를 인라인 할 필요는 없을 것 같다.

```java
public class Example2ProductionPlan {
    int initialProduction;
    List<Adjustment> adjustments;

    public Example2ProductionPlan(int production) {
        this.initialProduction = production;
        this.adjustments = new ArrayList<>();
    }

    public int getProduction() {
        return initialProduction + calculatedProductionAccumulate();
    }

    private int calculatedProductionAccumulate() {
        return adjustments.stream()
                .map(a -> a.amount)
                .reduce(0, Integer::sum);
    }

    public void applyAdjustment(Adjustment adjustment) {
        adjustments.add(adjustment);
    }
}
```

***

## 9.4 참조를 값으로 바꾸기 

### 배경 

객체를 다른 객체에서 사용한다고 하면 참조 또는 값으로 취급한다. 

참조냐 값이냐에 따른 가장 극명한 차이는 변경인데 참조의 경우에는 내부의 객체의 값이 변경되면 전파되지만 값인 경우에는 새로운 객체가 전달되므로 기존 객체에는 영향이 없다.

클래스 필드르 값으로 다룬다면 내부 객체의 클래스를 수정해서 값 객체 (Value Object) 로 만들 수 있다.

값 객체로 다루면 불변성을 줄 수 있으므로 대체로 활용하기 쉽고 프로그램 외부로 던져줘도 나중에 그 값이 나 몰래 바뀌어서 내부에 영향을 줄까 걱정하지 않아도 된다. __(여기서의 리팩토링 기법은 문제가 났을 때 해결하기 쉽도록 하는 기법이다.)__

그러므로 값을 복제해서 이곳저곳 사용하더라도 걱정하지 않아도 된다.

그렇다면 참조 객체를 사용하는 경우는 언제일까? 

예컨대 특정 객체를 여러 객체에서 공유하고자 한다면, 한 객체의 변경이 다른 객체도 알아야 한다면 이런 경우에는 객체를 참조로 다뤄야한다.

### 절차

1. 후보 클래스가 불변인지 혹은 불변이 될 수 있는지 확인한다. __(값 객체는 변경해서 사용하는게 아니라 새로 생성해서 사용하는 것이므로.)__

2. 각각의 세터를 하나씩 제거한다. (11.7 절) __(세터를 제거하는 이유가 다른 곳에서 변경하지 못하도록, 변경하는 곳이 있다면 컴파일 오류를 내도록 하기 위해서. 컴파일 오류가 있는 곳에는 새로운 객체를 사용하도록 하기 위해서.)__

3. 이 값 객체의 필드들을 사용하는 동치성 (equality) 비교 메소드르 만든다.
 
### 예시

사람 (Person) 객체가 있고, 이 객체는 다음 코드처럼 생성자에서 전하번호가 올바로 설정되지 못하게 짜여있다고 해보자. 

````java
public class Person {
    TelephoneNumber telephoneNumber; 
    
    public Person() {
        telephoneNumber = new TelephoneNumber(); 
    }
    
    public String getOfficeAreaCode() { return telephoneNumber.areaCode; }
    public void setOfficeAreaCode(String areaCode) { telephoneNumber.setAreaCode(areaCode); }
    
    public String getOfficeNumber() { return telephoneNumber.number; }
    public void setOfficeNumber(String number ) { telephoneNumber.setNumber(number);}
}
````

````java
public class TelephoneNumber {
    String areaCode;
    String number;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
```` 

이 경우의 문제는 TelephoneNumber 에서 있어야 할 메소드들이 Person 에 있다는 것이다. 

주로 클래스 추출 (7.5 절) 을 하다보면 이런 상황이 발생하는데 여기서 리팩토링을 해보자.

TelephoneNumber 를 가리키는 참조는 Person 하나 뿐이므로 참조를 값으로 바꾸는게 가능하다. __(여러 곳에서 참조한다면 마음대로 참조를 값으로 바꾸기 어려울 것.)__

가장 먼저 할 일은 TelephoneNumber 를 불변으로 만들자. __(불변으로 만드는걸 통해서 이 클래스는 아무데서돋 변경될 수 없고 새로운 클래스를 만들어야 한다.)__

```java
public class Person {
    TelephoneNumber telephoneNumber;

    public Person(String areaCode, String number) {
        telephoneNumber = new TelephoneNumber(areaCode, number);
    }

    public String getOfficeAreaCode() { return telephoneNumber.areaCode; }
    public void setOfficeAreaCode(String areaCode) { telephoneNumber = new TelephoneNumber(areaCode, telephoneNumber.number); }

    public String getOfficeNumber() { return telephoneNumber.number; }
    public void setOfficeNumber(String number ) { telephoneNumber = new TelephoneNumber(telephoneNumber.areaCode, number); }
}
```

````java
public class TelephoneNumber {
    String areaCode;
    String number;

    public TelephoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public String getNumber() {
        return number;
    }
}
````

***

## 9.5 값을 참조로 바꾸기 

### 배경

하나의 데이터 구조 안에 논리적으로 똑같은 데이터 구조를 참조하는 레코드가 여러 개 있을 때가 있다.

예로보면 주문 목록을 읽다 보면 같은 고객의 요청 주문이 여러 개 섞여 있을 수 있다.

이때 고객을 값으로도, 참조로도 다룰 수 있는데 __둘의 차이는 변경의 유무만 신경쓰면 된다. 물론 복사를 하면 더 많은 메모리를 사용한다는 점이 있지만 크게 문제가 되진 않는다.__

__논리적으로 같은 데이터를 물리적으로 복제할 때 가장 큰 문제점은 데이터를 갱신할 때 일관성의 문제다.__

데이터를 갱신하면 모든 복제본의 데이터가 빠짐없이 갱신되어야 하고 하나라도 빠지면 안된다라면 연결된 참조로 바꿔주는게 좋다.

값을 참조로 바꾸면 엔터티 하나당 객체가 단 하나만 존재하게 되는데 그러면 보통 이런 객체들을 한데 모아놓고 클라이언트들의 접근을 관리해주는 일종의 저장소가 필요해진다.

각 엔터티를 표현하는 객체를 한 번만 만들고, 객체가 필요한 곳에서는 모두 이 저장소로부터 얻어쓰는 형태가 된다.


### 절차

1. 같은 부류애 속하는 객체들을 보관할 저장소를 만든다.

2. 생성자에서 이 부류의 객체들 중 특정 객체를 정확히 찾아내는 방법이 있는지 확인한다.

3. 호스트 객체의 생성자들을 수정하여 필요한 객체를 이 저장소에서 찾도록 한다. 하나씩 수정할 때마다 테스트한다.

### 예시 

예시로 주문 클래스를 준비했다. 

이 과
````java
public class Order {
    Customer customer; 
    long number;

    public Order(long customerId, long number) {
        this.customer = new Customer(customerId);
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }
}
````

````java
public class Customer {
    long id;

    public Customer(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
}
````

여기서 하나의 고객이 주문 다섯개를 생성한다면 독립된 고객 객체가 다섯 개 만들어진다.

이 중 하나를 수정한다 하더라도 네 개는 반영되지 않는다.

그리고 고객의 정보를 조금이라도 수정할려면 고객 객체 모두 찾아서 수정해야한다.

여기서 이제 리팩토링을 해보자.

물리적으로 똑같은 고객 객체를 사용하고 싶다면 먼저 이 유일한 객체를 저장해둘 곳이 있어야 한다.

객체를 저장하는 곳은 어플리케이션 마다 다르겠지만 간단한 상황이라면 나는 repository object 를 사용하는 편이다.

````java

public class CustomerRepository {
    Map<Long, Customer> repository = new HashMap<>(); 
    
    public Customer registerCustomer(long id) {
        if (!repository.containsKey(id)) repository.put(id, new Customer(id));
        return findCustomer(id);
    }
    
    public Customer findCustomer(long id) {
        return repository.get(id);
    }
}
````

이렇게 저장소를 만들면 ID 하나당 오직 하나의 고객 객체만 생성됨을 보장하는게 가능해진다.

이제 저장소를 만들었으니 Order 에서 사용하도록 하자.

````java

public class Order {
    Customer customer;
    long number;

    public Order(long customerId, long number) {
        this.customer = CustomerRepository.registerCustomer(customerId); 
        this.number = number;
    }

    public Customer getCustomer() {
        return customer;
    }
}
````

이 예시에서는 전역 변수를 사용하므로 주의하자. 전역 객체는 독한 악취를 낼 수 있다. __(어디서든 접근이 가능하고 누가 변경했는지 추적하기가 어려우므로.)__

그러므로 저장소를 사용할 땐 유효범위를 적절하게 사용하도록 하자. 

***

## 9.6 매직 리터럴 바꾸기

### 배경

매직 리터럴 (Magic Literal) 이란 소스 코드에 등장하는 일반적인 리터럴 값을 말한다. 

예컨대 움직임을 계산하는 코드에서라면 9.806655 라는 숫자가 산재해 있다. __(중력 가속도를 말하는 듯)__

이런 숫자는 특별한 의미가 있어서 이런 의미를 알지 못한다면 코드를 읽는 사람은 이해하기 어렵다. 이런 코드를 매직 리터럴 이라고 한다.

이런 매직 리터럴 코드를 보면 코드 자체가 어떤 의므를 나타내는지 분명하게 드러내 주는게 좋다.

리팩토링 기법은 쉽다. 숫자대신 상수를 정의하고 상수를 사용하도록 바꾸면 된다.

매직 리터럴은 대체로 숫자가 많지만 다른 타입의 리터럴도 특별한 의미를 지닐 수 있다.

예컨대 "M" 은 남성을 "서울" 은 본사를 뜻할 수도 있다.

일반적으로 해당 값이 쓰이는 모든 곳을 적절한 이름의 상수로 바꿔주는 방법이 가장 좋다.

상수로 바꾸는 것과 함께 다른 방법이 있는데 비교 로직이 있다면 고려해볼 수 있는 방법이다. 

예를 들어 `aValue == "M"` 을 `aValue == MALE_GENDER` 로 바꾸기보다. `isMale(aValue)` 라는 함수 호출로 만드는 걸 선호한다.

상수를 사용할 땐 상수를 과용하는 것도 의미없다. `int ONE = 1` 과 같은 의미없는 상수는 사용하지 않아도 된다. 

그리고 리터럴이 함수 하나에서만 쓰이고 함수가 충분한 맥락을 제공해주고 있다면 상수를 사용하지 않아도 된다.

### 절차 

1. 상수를 선언하고 매직 리터럴을 대입한다.

2. 해당 리터럴이 사용되는 곳을 모두 찾는다.

3. 찾은 곳 각각에서 리터럴이 세 상수와 똑같은 의미로 쓰였는지 확인하며, 같은 의미라면 상수로 대체한 후 테스트한다.




