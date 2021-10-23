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

