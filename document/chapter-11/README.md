# API 리팩터링 

모듈과 함수는 소프트웨어를 구성하는 블록이고 API 는 이 블록들을 서로 연결시켜주는 요소다.

그러므로 API 를 이해하기 쉽고 사용하기 쉽게 만드는 일은 중요하다.

좋은 API 는 데이터를 갱신하는 함수와 그저 조회하는 함수를 명확히 구별한다.

두 기능이 섞여 있다면 __질의 함수와 변경 함수 분리하기 (11.1 절)__ 을 적용하자.

값 하나 때문에 여러 개로 나뉜 함수들은 __함수 매개변수화하기 (11.2 절)__ 을 적용해 하나로 합칠 수 있다.

한편 어떤 매개변수는 그저 함수의 동작 모드를 전환하는 용도로 쓰이는데 이 경우에는 __플래그 인수 제거하기 (11.3 절)__ 을 적용하면 좋다.

데이터 구조가 함수 사이를 건너다니면서 분해되는 경우는 __객체 통째로 넘기기 (11.4 절)__ 을 적용해 하나로 유지하면 깔끔하다.

무언가를 매개변수로 건네 피호출 함수가 판단할지 아니면 호출 함수가 직접 정할지에 관해서는 진리가 없으므로 상황에 맞게 __매개변수를 질의 함수로 바꾸기 (11.5 절)__ 이나 __질의 함수를 매개변수로 바꾸기 (11.6 절)__ 을 적용하자.

클래스는 모듈이고 모듈이 불변으로 바뀌길 원한다면 기회가 될때 __세터 제거하기 (11.7 절)__ 을 적용하자.

한편 호출자에 새로운 객체를 만들어 반환하려 할 때 일반적인 생성자의 능력만으로 부족하다면 __생성자를 팩토리 함수로 바꾸기 (11.8 절)__ 을 적용하자.

마지막 두 리팩토링은 수 많은 데이터를 받는 복잡한 함수를 잘게 쪼개는 문제를 다룬다.

__함수를 명령으로 바꾸기 (11.9 절)__ 을 적용하면 이런 함수를 객체로 변환할 수 있는데 그러면 해당 함수 본문에서 함수 추출하기를 적용하기 편해진다.

나중에 이 함수를 단순화 해서 명령 객체가 더 이상 필요 없어진다면 __명령을 함수로 바꾸기 (11.10 절)__ 을 적용해 함수로 되돌리자. 

***

## 11.1 질의 함수와 변경 함수 분리하기

### 배경 

질의 함수와 변경 함수를 분리한다면, 질의 함수가 어떠한 사이드 이펙트를 내지 않고 결과를 내주기만 한다면 문제를 일으킬 걱정을 하지 않아도된다. 

즉 신경쓰지 않아도 된다. 그러므로 질의 함수는 부수 효과가 없어야 한다.

나는 값을 반화하면서 부수효과가 있는 함수를 발견하면 이를 분리시키려고 한다.

물론 나는 이 규칙을 절대적으로는 신뢰하지 않지만 되도록 따르려고 노력은 하고 있고 그 효과를 잘봤다.

__(나는 이 말을 잘못 오해헀는데 질의만 해야한다. 변경만 해야한다. 이런 뜻이 아니라 한 함수에서 질의와 변경이 섞여있다면 이것들을 분리하는게 맞다 라는 뜻이다. 즉 큰 하나의 함수에서는 질의와 변경이 각각의 함수 호출로 있겠지.)__

### 절차 

1. 대상 함수를 복제하고 질의 목적에 충실한 이름을 짓는다. (함수가 어떠한 결과를 반환하는지를 살펴보면 이름을 짓는데 충분한 단초를 줄 수 있다.)

2. 새 질의 함수에서 부수 효과를 모두 지운다.

3. 정적 검사를 수행한다.

4. 기존 함수를 호출하는 곳을 모두 찾아내서 새 함수를 호출하도록 변경한다. 그 다음 변경 함수를 새 함수 호출 밑에다가 추가한다.

5. 기존 함수에서 질의 관련 코드를 모두 제거한다.

6. 테스트한다.


## 예시

이름 목록을 보고 악당 (Miscreant) 를 찾는 함수가 있다.

악당을 찾으면 그 사람을 보고 경고를 울린다.

코드는 다음과 같다.

```java
public String alertMiscreant() {
    for (Person p : people) {
        if (p.name.equals("조커")) {
            setOffAlarms();
            return "조커";
        }
        if (p.name.equals("사루만")) {
            setOffAlarms();
            return "사루만";
        }
    }
    return ""; 
}
```

첫 단계는 함수를 복제해서 이름을 짓는 것이다. 이름을 지을 때는 질의 함수를 기준으로 이름을 짓자.

그리고 여기서 변경을 유발하는 코드를 찾아서 제거하자.

```java
public String findMiscreant(){
    for (Person p : people) {
        if (p.name.equals("조커")) {
            return "조커";
        }
        if (p.name.equals("사루만")) {
            return "사루만";
        }
    }
    return "";
}
```

그 다음 클라이언트를 다음과 같이 바꾸자.

```java
String found = findMiscreant();
alertMiscreant();
```

이렇게 하고나서 변경을 유발하는 코드에서 질의 함수와 관련된 부분을 제거하자.

```java
public void alertMiscreant() {
    for (Person p : people) {
        if (p.name.equals("조커")) {
            setOffAlarms();
        }
        if (p.name.equals("사루만")) {
            setOffAlarms();
        }
    }
}
```

여기까지 오고나면 기존 함수와 새 질의 함수가 많이 유사하다.

코드의 중복이 있다. 이런 중복을 없애는 방법을 생각해보자.

여기서의 알고리즘은 악당을 찾고나서 경고를 내뱉는 것이다. 그렇다면 경고를 내뱉는 곳에서 악당을 찾는 함수가 있어도 되지 않을까?

프로그램의 흐름을 바꾸되 목적은 변하지 않는 리팩토링 기법인 __알고리즘 교체하기 (7.9 절)__ 을 적용하면 다음과 같다.

```java
public void alertMiscreant() {
    if (findMiscreant().equals("")) return;
    setOffAlarms();
}
```

***

## 11.2 함수 매개변수화 하기 

### 배경

두 함수의 로직이 유사하고 리터럴 값만 다르다면 그 리터럴 값만 매개변수로 해서 코드의 중복을 제거할 수 있다.

이렇게하면 매개변수의 값만 바꿔서 여러 곳에 적용할 수 있으니 더욱 유용하다.

### 절차

1. 비슷한 함수 중 하나를 선택한다.

2. 함수 선언 바꾸기 (6.5 절) 로 리터럴들을 매개변수로 추가한다.

3. 이 함수를 호출하는 곳 모두에 적절한 리터럴 값을 추가한다.

4. 테스트한다.

5. 매개변수로 받은 값을 사용하도록 함수 본문을 수정한다.

6. 하나 수정할 때마다 테스트한다.

7. 비슷한 다른 함수를 호출하는 곳을 찾아 매개변수화된 함수를 호출하도록 하나씩 수정하고 수정할 때마다 테스트한다.

### 예시

먼저 비슷한 두 함수를 보자.

```java
public void tenPercentRaise(Person person) {
    person.salary = (int) (person.salary * 1.1);
}

public void fivePercentRaise(Person person) {
    person.salary = (int) (person.salary * 1.05);
}
```

앞의 두 함수는 리터럴 값을 매개변수화해서 하나의 함수로 만들 수 있다.

````java
public void raise(Person person, double factor) {
    person.salary = (int) (person.salary * factor);
}
````

이렇게 금방 바꿀 수 있는 경우도 있지만 다음 예제는 한번에 바꾸기 어려워보인다.

```java
public int baseCharge(int usage) {
    if (usage < 0) return 0;

    int amount =
            (int) (bottomBand(usage) * 0.03 
                   + middleBand(usage) * 0.05 
                   + topBand(usage) * 0.07);
    
    return usd(amount);
}

private double topBand(int usage) {
    return usage > 200 ? usage - 200 : 0; 
}

private double middleBand(int usage) {
    return usage > 100 ? Math.min(usage, 200) - 100 : 0;
}

private int bottomBand(int usage) {
    return Math.min(usage, 0);
}
```

bottomBand, middleBand, topBand 함수 모두 비슷해보인다. usage 값의 범위를 기반으로 리턴하는 함수들이다.'

그러므로 코드의 중복이라는 걸 알 수 있다. __(논리가 비슷하므로.)__

이런 경우에는 대표적인 함수를 기반으로 모든 함수들을 아우를 수 있는 함수를 만들어야 한다.

middleBand() 를 기반으로 하한과 상한을 파라미터로 받아서 다른 함수들을 커버할 수 있을 것 같다.

한번 새로운 함수를 만들어보자.

```java
private double withinBand(int usage, int bottom, int top) {
    return usage > bottom ? Math.min(usage, top) - bottom : 0;
}
```

이렇게 만든 함수를 호출하도록 변경해보자.

```java
public int baseCharge(int usage) {
    if (usage < 0) return 0;

    int amount =
            (int) (withinBand(usage, 0, 100) * 0.03
                   + withinBand(usage, 100, 200) * 0.05
                   + withinBand(usage, 200, Integer.MAX_VALUE) * 0.07);

    return usd(amount);
}
```

***

## 11.3 플래그 인수 제거하기

### 배경

플래그 인수란 (Flag Argument) 호출하는 함수가 호출되는 함수의 로직을 결정하기 위해 전달하는 매개변수다.

다음과 같은 함수를 보자.

```java
public void bookConcert(Customer customer, boolean isPremium) {
    if (isPremium) {
        // 프리미엄 예약용 로직
        ...    
    }    
    else {
        // 일반 예약용 로직
    }
}
```

여기에 전달되는 boolean 변수가 플래그 인수다.

플래그 인수는 이렇게 boolean 타입일수도, enum 일수도, String 일수도 있다. 다양하다.

주요 포인트는 함수의 실행을 결정해주는 매개변수라는 점이다.

나는 플래그 인수를 사용하는 함수를 극도로 싫어하는데 함수 호출자의 입장에서 이 매개변수에 어떤 값을 전달해야하는지 이해하기 어렵기 때문이다.

즉 함수를 이해하기 어렵다.

### 절차 

1. 플래그 인수로 실행될 수 있는 흐름들에 대한 함수를 명시적으로 만든다.

  - 플래그 인수를 기반으로 깔끔하게 분기되어있다면 조건문 분해하기 (10.1 절) 을 이용해서 명시적인 함수들을 생성하면 좋다.
  - 그렇지 않다면 래핑 함수 (Wrapping Function) 형태로 만들면 된다.
2. 원래 함수를 호출하는 코드들을 찾아서 명시적인 함수 호출로 바꾸자.

### 예시: 매개변수를 까다로운 방식으로 사용할 때

바로 예시를 보자. 

```java
public LocalDateTime deliveryDate(Order order, boolean isRush) {
    int deliveryTime;

    if (Stream.of("MA", "CT")
            .anyMatch(state -> order.deliveryState.equals(state))) {
        deliveryTime = isRush ? 1 : 2;
    }
    else if (Stream.of("NY", "NH")
            .anyMatch(state -> order.deliveryState.equals(state))) {
        deliveryTime = 2;
        if (order.deliveryState.equals("NH") && !isRush) {
            deliveryTime = 3;
        }
    }
    else if (isRush) {
        deliveryTime = 3;
    }
    else if (order.deliveryState.equals("ME")) {
        deliveryTime = 3;
    }
    else {
        deliveryTime = 4;
    }
    LocalDateTime result = order.placeOn.plusDays(2 + deliveryTime);
    if (isRush) result = result.minusDays(1);
    return result;
}
```

이 예시 같은 경우은 매개변수인 isRush 가 조건절에 가장 바깥에 있지는 않다.

그래서 조건문 분해하기로 빼내기는 힘들다.

이와 같은 코드는 리팩토링 할려면 코드 자체를 분석하고 isRush 를 밖으로 빼내야하는데 이는 사이드 이펙트가 있을 여지가 크므로 래핑 함수를 통해서 처리하자.

````java
public LocalDateTime rushDeliveryDate(Order order) {
    return deliveryDate(order, true); 
}

public LocalDateTime regularDeliveryDate(Order order) {
    return deliveryDate(order, false);    
}
````
***

## 11.4 객체 통째로 넘기기

### 배경

하나의 레코드에서 값 두어개를 가져와서 인수로 넘기는 코드를 보면 그냥 객체를 통째로 넘기는게 낫지 않은가 라는 고민을 한다.

객체 자체를 통째로 넘기면 대응하기 쉽다.

객체에서 값을 뽑아서 다른 곳을 던진다는 것 자체가 의미없는 코드를 양산하는 것과 같다. (명확성을 떨어뜨린다.)

그리고 매개변수 목록도 짧아지므로 함수를 이해하기가 더 쉽기도하다.

하지만 함수가 객체 자체에 의존하면 안되는 경우라면, 서로 다른 모듈에 있는 관계라면 이 리팩토링의 기법을 사용하지는 않는다.

이 리팩토링은 객체로부터 값 몇개를 꺼내서 함수에 전달하고 함수가 그 값들을 이용해서 무언가를 하는 행위를 한다면 이는 객체로 넘기는게 좋다.

추상화면에서도 코드의 의도를 나타내는 면에서도 그런 편이 더 좋기 때문이다.

한편 객체의 일부를 기반으로 같은 동작을 반복하는 코드가 있다면 그 일부가 클래스가 되어야 한다는 뜻이기도 하다. 그래서 따로 묶어서 클래스 추출하기 (7.5 절) 을 적용하자.

마지막으로 많은 사람이 놓치는 예제가 있는데 객체가 다른 객체의 메소드를 호출하는 과정에서 자신이 가지고 있는 메소드 여러개를 전달하는 경우라면 그 객체 자체를 넘기는 경우, 즉 this 를 통해 넘기는 경우가 더 나을 수 있다. 이런 기법도 있다.

### 절차

1. 매개변수를 원하는 형태로 빈 함수를 만든다. (이후 이름을 변경할 것이다.)

2. 새 함수의 본문에서는 원래 함수를 호출하도록 만든다. 매핑하도록 하면 된다.

3. 정적 검사를 수행한다.

4. 모든 호출자가 새 함수를 호출하도록 만든다.

5. 호출자를 모두 수정했다면 원래 함수를 인라인 (6.2 절) 한다.

6. 새 함수의 이름을 적절히 수정하고 모든 호출자에 반영한다.

### 예시

실내온도 모니터링 시스템을 생각해보자.

이 시스템은 일일 최저, 최고 기온이 난방 계획 (Heating Plan) 에서 정한 범위를 벗어나는지 확인한다.

```java
// Client
int low = room.daysTempRange.low;
int high = room.daysTempRange.high;
if (!heatingPlan.withinRange(low, high)) {
    throw new Exception("방 온도가 지정 범위를 벗어났습니다.");
}
```

````java
// HeatingPlan 클래스
public boolean withinRange(int bottom, int top) {
    return (bottom >= temperatureRange.low) && (top <= temperatureRange.high);
}
````

클라이언트 코드를 보면 객체에서 값을 꺼내서 다른 객체의 매개변수로 던져준다.

이러는 것보다 그냥 객체 자체를 던져주는게 더 코드를 간소화 시킬 수 있을 것 같다. 

먼저 HeatingPlan 클래스에서 객체를 전달받아서 실행시키는 함수를 만들자.

그리고 기존의 함수를 래핑시켜놓자.

```java
public boolean nexWithinRange(Range range) {
    return withinRange(range.low, range.high);    
}
```

그 다음 새로운 함수를 호출하도록 클라이언트를 변경하자. 

````java
if (!heatingPlan.nexWithinRange(room.daysTempRange)) {
    throw new Exception("방 온도가 지정 범위를 벗어났습니다.");
}
````

이렇게 하나씩 바꿔놓고 기존에 래핑된 함수의 본문을 바꾸고 이름을 개선하자.

````java
public boolean withinRange(Range range) {
    return (range.low >= temperatureRange.low) && (range.high <= temperatureRange.high);
}
````

***

## 11.5 매개변수를 질의 함수로 바꾸기

### 배경 

매개변수 목록은 함수의 동작에 영향을 줄 수 있는 요소들이다.

매개변수의 목록은 중복이 없는게 좋고 짧을수록 좋다.

피호출 함수가 스스로 매개변수의 값을 알고있고 구할 수 있는 경우라면 매개변수는 없는게 더 코드를 이해하기가 쉽다.

이 경우에 해당 매개변수는 의미없는 코드일 뿐이기 때문이다.

매개변수가 있다면 호출자가 이런 의존성을 연결시켜주는 작업을 해야하고 매개변수가 없다면 피호출자가 주체가 되서 의존성 문제를 해결해야한다.

나는 습관적으로 피호출자가 주체가 되도록 하는데 그러면 함수의 사용이 훨씬 쉬워지기 때문이다.

피호출자가 주체가 될 때 고려해야하는 사항은 피호출자 함수에 의도치 않은 의존성이 생기는지의 여부다. 

피호출자가 쉽게, 또는 다른 매개변수를 통해서 알 수 있는 경우라면 의존성이 생기지 않겠지만 다른 모듈에 있는 객체를 매개변수로 받지않고 스스로 알아낼려고 하면 불필요한 의존성이 생긴다.

즉 해당 함수가 알면 안되는 변수는 매개변수를 삭제하면 안된다.

그리고 주의사항으로 매개변수를 없애는 대신 전역변수 같은 걸 이용할려고는 하지말자. 함수는 참조 투명 (referential transparency) 해야한다.

함수에 똑같은 매개변수의 전달은 같은 결과를 반영해야 한다. 이를 기억하자.

### 절차

1. 필요하다면 대상 매개변수를 계산하는 코드를 별도의 함수로 추출하자.

2. 함수 본문에서 대상 매개변수로의 참조를 새로 추출한 함수 호출로 바꾸자.

3. 함수 선언 바꾸기 (6.5 절) 로 대상 매개변수를 없애자.

### 예시

다음 예시를 보자.

```java
// Order 클래스
public int finalPrice() {
    int basePrice = quantity * itemPrice;
    int discountLevel = 1;
    if (quantity > 100) discountLevel = 2;

    return discountPrice(basePrice, discountLevel);
}

private int discountPrice(int basePrice, int discountLevel) {
    switch (discountLevel) {
        case 1: return (int) (basePrice * 0.95);
        case 2: return (int) (basePrice * 0.90);
    }
    return 0;
}
```

함수를 보면 discountLevel 같은 경우는 직접 계산하는게 쉬워보인다. Order 클래스안에 있는 quantity 를 기준으로 계산해내는 변수이므로.

````java
private int discountLevel() {
    return quantity > 100 ? 2 : 1;
}
````

이제 discountLevel 을 사용하는 함수로 가서 질의 함수를 사용하도록 하자.

```java
public int finalPrice() {
    int basePrice = quantity * itemPrice;
    return discountPrice(basePrice);
}

private int discountLevel() {
    return quantity > 100 ? 2 : 1;
}

private int discountPrice(int basePrice) {
    switch (discountLevel()) {
        case 1: return (int) (basePrice * 0.95);
        case 2: return (int) (basePrice * 0.90);
    }
    return 0;
}
```

***

## 11.6 질의 함수 매개변수로 바꾸기

### 배경

코드를 읽다 보면 함수 안에 있기에는 적합하지 않은 참조들이 있다.

이 경우에는 해당 참조를 매개변수로 바꿈으로써 해결할 수 있다.

이로인해 함수를 호출하는 호출자에게 책임을 옮겨진다. 

이런 상황 대부분은 코드안에서 의존관계를 바꾸려고 할 때 일어난다.

함수가 특정 대상에 대한 의존을 가지지 않기 위해 매개변수로 바꾸고 호출하는 쪽이 해당 의존성을 가지게 된다.

즉 어느 쪽에 의존성을 둘 것인지에 대한 문제로 여기에는 정답은 없다.

따라서 프로그램을 더 잘 이해하게 되서 적합한 쪽으로 의존성을 옮기면 된다. 

이렇게 언제든지 리팩토링이 일어날 수 있게 기존의 코드를 바꾸기 쉽게 설계해두는 것이 중요하다.

그리고 함수를 설계할 땐 참조 투명성이 중요하다. 왜냐하면 참조 투명성이 있는 함수는 똑같은 매개변수에선 똑같은 동작을 하기 때문에 예측하기 쉽기 때문이다.

함수가 어떤 대상의 값에 의존하고 있고 이 값이 바뀔 여지가 많다면 참조 투명성을 가지는 함수로, 매개변수로 전달받도록 하면 함수를 다루기 쉽다. 

이 리팩토링을 수행하면 함수를 호출하는 쪽에 책임을 전가하게 된다. 그래서 호출하는 쪽의 코드가 더 어려워지고 결합도가 높아진다는 특징이 있다.

그치만 그로인해 얻을 수 있는 혜택은 피호출자 함수가 담긴 클래스는 의존성을 없앨 수 있다. 필요하지 않는 의존성을 없앨 수 있어서 때로는 불변의 특성을 갖기도 한다.

### 절차

1. 변수 추출하기 (6.3 절) 로 질의 코드를 함수 본문의 코드와 분리한다.

2. 함수 본문 중 해당 질의를 호출하지 않는 코드들을 별도의 함수로 추출 한다.

3. 방금 만든 변수를 인라인해서 제거한다.

4. 원래 함수도 인라인 한다.

5. 새 함수 이름을 원래 함수의 이름으로 변경한다.

### 예시

예시로 실내온도 제어 시스템을 준비했다.

사용자는 온도조절기 (thermostat) 으로 온도를 설정할 수 있지만 난방 계획 (HeatingPlan) 내에서만 온도를 정할 수 있다.

```java
// HeatingPlan 클래스 
public int targetTemperature() {
    if (Thermostat.selectedTemperature > this.max) {
        return this.max;
    }
    if (Thermostat.selectedTemperature < this.min) {
        return this.min;
    }
    return Thermostat.selectedTemperature;
}
```

```java
// 클라이언트 
public void client() {
    if (heatingPlan.targetTemperature() > Thermostat.currentTemperature) setToHeat();
    else if (heatingPlan.targetTemperature() < Thermostat.currentTemperature) setToCool();
    else setOff();
}
```

targetTemperature() 함수의 문제점은 전역변수를 통해서 함수를 실행한다는 점이다.

참조 투명하지 않다. 

그러므로 전역변수를 사용하는 것에서 매개변수를 받도록 하자. 매개변수를 받아도 충분히 함수 설명을 해줄 수 있고 함수를 쉽게 쓸 수 있을 것이다.

먼저 변수 쪼개기를 통해 질의 코드를 함수 본문과 분리하자.

````java
public int targetTemperature() {
    int selectedTemperature = Thermostat.selectedTemperature;
    if (selectedTemperature > this.max) {
        return this.max;
    }
    if (selectedTemperature < this.min) {
        return this.min;
    }
    return selectedTemperature;
}
````

그 다음 매개변수를 사용하는 부분을 함수로 추출해서 새 함수로 사용할 준비를 하자.

그리고 매개변수를 가져오는 부분을 인라인하자.

````java
public int targetTemperature() {
    return newTargetTemperature(Thermostat.selectedTemperature);
}

public int newTargetTemperature(int selectedTemperature) {
    if (selectedTemperature > this.max) {
        return this.max;
    }
    if (selectedTemperature < this.min) {
        return this.min;
    }
    return selectedTemperature;
}
````

그 다음 클라이언트 코드에서 새로운 함수를 사용하도록 바꾸자.

```java
public void client() {
    if (heatingPlan.newTargetTemperature(Thermostat.selectedTemperature) > Thermostat.currentTemperature) setToHeat();
    else if (heatingPlan.newTargetTemperature(Thermostat.selectedTemperature) < Thermostat.currentTemperature) setToCool();
    else setOff();
}
```

이제 새롭게 만들었던 함수의 이름을 변경하자.

그리고 기존의 함수를 지우자.

```java
public int targetTemperature(int selectedTemperature) {
   if (selectedTemperature > this.max) {
       return this.max;
   }
   if (selectedTemperature < this.min) {
       return this.min;
   }
   return selectedTemperature;
}   
```

***

## 11.7 세터 제거하기

### 배경

세터 메소드가 있는 것은 객체가 변경될 여지가 있다는 의미를 나타낸다.

객체 생성 후에 변경되지 않을 것이라고 설계한 불변 객체라면 세터 메소드를 없애도록 하는게 맞다.

즉 수정하지 않겠다 라는 의도를 드러내는 것이다.

세터 제거하기 위한 상황은 크게 두 가지인데 첫 번째는 생성자에서만 세터를 호출하는 경우다.

이런 세터는 필요하지 않으므로 그냥 세터를 없애고 생성자에서 필드 값을 설정하도록 하고 불변의 의도를 드러내는게 조핟.

두 번째는 객체 생성 코드에서 세터를 통해 객체를 완성하는 경우다.

이런 경우에도 세터를 제거하고 생성자를 이용하는게 더 좋다.

### 절차

1. 설정해야 할 값을 생성자에서 받지 않는다면 그 값을 생성자에서 매개변수로 받도록 한다.

2. 생성자 밖에서 세터를 호출하는 코드를 모두 찾아서 제거하고 새로운 생성자를 쓰도록 한다.

3. 세터 메소드를 인라인 한다.

4. 테스트한다.

### 예시

예시로 간단히 사람 (Person) 클래스를 준비했다.

```java
public class Person {
    long id;
    String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

클라이언트 코드는 다음과 같다.

````java
public void client() {
    Person person = new Person();
    person.id = 1234;
    person.name = "마틴"; 
}
````

객체에서 이름은 바뀔 수 있겠지만 객체의 고유한 값을 나타내는 ID 값은 불변의 특성을 띄어야한다.

그러므로 생성자에서 ID 를 받도록 하자.

````java
public class Person {
    long id;
    String name;

    public Person() {}
    
    public Person(long id) {
        this.id = id;
    }
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
````

그 다음 id 값을 세터나 직접 접근해서 할당하는 곳을 찾아서 모두 생성자를 이용하도록 바꾸자.

```java
public void client() {
    Person person = new Person(1234);
    person.name = "마틴";
}
```

모두 옮겼으면 세터를 제거하고 기본 생성자도 제거하자.

```java
public class Person {
    long id;
    String name;

    public Person(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

***
