# 조건부 로직 간소화

조건부 로직은 프로그래밍에서 필요한 존재지만 안타깝게도 프로그램을 복잡하게 만드는 주요 요인이다.

그래서 나는 조건부 로직을 이해하기 쉽게 바꾸는 리팩토링을 자주한다.

복잡한 조건문에는 __조건문 분해하기 (10.1 절)__ 을 적용하고

논리적 조합을 명확하게 다듬는 데는 __중복 조건식 통합하기 (10.2 절)__ 을 적용한다.

함수의 핵심 로직에 본격적으로 들어가기 전에 앞서 검사해야 할 때는 __중첩 조건문을 보호 구문으로 바꾸기 (10.3 절)__ 을 

Switch 문이나 똑같은 분기문이 등장한다면 __조건문 로직을 다형성으로 바꾸기 (10.4 절)__ 을 적용한다.

널 (Null) 과 같은 특이 케이스를 처리하는 데도 조건부 로직이 흔히 쓰인다면 __특이 케이스 추가하기 (10.5 절)__ 를 적용해 코드의 중복을 상당히 줄일 수 있다.

한편 내가 조건문을 없애는 걸 상당히 좋아하지만 프로그램의 상태를 확인하고 그 결과에 따라 다르게 동작해야 하는 상황이라면 __어서션 추가하기 (10.6 절)__ 이 도움이 된다.

마지막으로 제어 플래그를 이용해 코드 동작 흐름을 변경하는 코드는 대부분 __제어 플래그를 탈출문으로 바꾸기 (10.7 절)__ 을 적용해 더 간소화 하는게 가능하다.
 
***

## 10.1 조건문 분해하기

### 배경 

복잡한 조건부 로직은 프로그램을 복잡하게 만드는 가장 흔한 원흉이다.

다양한 조건, 그에 따라 동작도 다양한 코드를 작성하면 꽤 긴 함수가 탄생한다.

긴 함수는 그 자체로 읽기가 어렵고 조건문은 이 어려움을 증가시킨다.

긴 함수에 있는 로직들은 어떤 동작이 일어나는지는 설명해주지만 목적이 제대로 드러나지는 않는다.

그러므로 거대한 코드 블록이 주어지면 코드를 부위별로 분해한 다음 각 덩어리에 의도가 들어나도록 함수로 바꿔주자. 

그러면 전체적인 의도가 더 확실히 드러난다.

__조건문이 보이면 나는 조건식과 각 조건절에 이 작업을 해주길 좋아한다.__ __(즉 복잡한 조건식을 의도가 드러나는 코드로 바꿔주는 작업.)__ 

이렇게 하면 해당 조건이 무엇인지 알 수 있다. 

### 절차

1. 조건식과 그 조건식에 딸린 조건절 각각을 __함수로 추출한다. (6.1 절)__

### 예시 

여름철이면 할인율이 달라지는 어떤 서비스의 요금을 계산한다고 해보자.

```java
public void calculatePayment(Plan plan, LocalDateTime dateTime) {
    if (!dateTime.isBefore(plan.summerStart) && dateTime.isAfter(plan.summerEnd)) {
        charge = (int) (quantity * plan.summerRate); 
    } else {
      charge = (int) (quantity * plan.regularRate + plan.regularServiceCharge); 
    }
}
```

여기서 조건절이 복잡하므로 의도가 드러나게 리팩토링 해보자.

````java
 public void calculatePayment(Plan plan, LocalDateTime dateTime) {
    if (isSummer(dateTime, plan)) {
        charge = (int) (quantity * plan.summerRate);
    } else {
      charge = (int) (quantity * plan.regularRate + plan.regularServiceCharge);
    }
}

private boolean isSummer(LocalDateTime dateTime, Plan plan) {
    return !dateTime.isBefore(plan.summerStart) && dateTime.isAfter(plan.summerEnd); 
}
````

이제 조건을 만족했을 때 로직도 의도가 드러나게 바꿔보자.

````java
public void calculatePayment(Plan plan, LocalDateTime dateTime) {
    if (isSummer(dateTime, plan)) {
        charge = summerCharge(plan);
    } else {
      charge = (int) (quantity * plan.regularRate + plan.regularServiceCharge);
    }
}

private boolean isSummer(LocalDateTime dateTime, Plan plan) {
    return !dateTime.isBefore(plan.summerStart) && dateTime.isAfter(plan.summerEnd); 
}

private int summerCharge(Plan plan) {
    return (int) (quantity * plan.summerRate);
}
````

마지막으로 else 절도 목적이 드러나게 바꾸자.

```java
public void calculatePayment(Plan plan, LocalDateTime dateTime) {
    if (isSummer(dateTime, plan)) {
        charge = summerCharge(plan);
    } else {
      charge = regularCharge(plan);
    }
}

private boolean isSummer(LocalDateTime dateTime, Plan plan) {
    return !dateTime.isBefore(plan.summerStart) && dateTime.isAfter(plan.summerEnd);
}

private int summerCharge(Plan plan) {
    return (int) (quantity * plan.summerRate);
}

private int regularCharge(Plan plan) {
    return (int) (quantity * plan.regularRate + plan.regularServiceCharge);
}
```

***

## 10.2 조건식 통합하기

### 배경

비교하는 조건은 다르지만 그 결과로 수행하는 동작은 똑같은 코드들이 있을 수 있다.

어짜피 동작할 로직이 같다면 조건식을 하나로 통합하는게 낫다. 

왜냐하면 하나로 통합하는 과정에서 함수 추출을 통해 코드의 의도를 더욱 명확하게 살릴 수 있기 떄문이다.

복잡한 조건식을 함수로 추출하면 코드의 의도가 훨씬 분명하게 드러나는 경우가 많다.

__함수로 추출하기는 무엇을 하는지에 대한 코드를 왜 하는지로 표현할 수 있는 리팩토링 기법이다.__ __(좋은 내용이다.)__

이 기법을 왜 하는지를 알면 응용을 할 수 있다. 

이 기법은 똑같은 동작이라면, 여러 조건식들 사이에서 왜 검사를 하는지에 대한 목표가 같다면 하나로 묶는게 낫다라는 말이다.

그러므로 독립된 로직이라고 판단이 든다면 이 기법을 사용하지 않는게 낫다. 

### 절차

1. 해당 조건식들 모두에 부수효과가 없는지 확인한다. (있다면 질의 함수와 변경 함수 분리하기 (11.1 절) 을 통해 분리하자.) __(부수효과가 있는 예제는 어떤 경우일까?)__

2. 조건문 두 개를 선택해서 두 조건문의 조건식들을 논리 연산자로 결합한다.

3. 테스트한다.

4. 조건이 하나만 남을 때까지 반복한다.

5. 하나로 ㅏㅎㅂ쳐진 조건식을 함수로 추출할지 고민해본다.

***

## 예시 

코드를 보다가 다음 코드를 발견했다고 하자. 

````java
public int disabilityAmount(Employee employee) {
        if (employee.seniority < 2) return 0;
        if (employee.monthDisabled > 12) return 0; 
        if (employee.isPartTime) return 0;
        // 장애 수단 계산
        ... 
}
````

똑같은 결과로 이어지는 조건 검사가 순차적으로 진행하고 있다. 

코드를 보니 목적이 같은 것 같다고 판단되었다. (독립적인 검사가 아니라고 판단.)

여기서 조건식들은 모두 장애가 아닌지에 대한 검사를 수행하고 있다고 판단되었다.

그러므로 조건식을 통합하는게 나아보인다.

이처럼 순차적인 경우에 or 연산자를 이용하면 된다. 

```java
public int disabilityAmount(Employee employee) {
    if (employee.seniority < 2 || employee.monthDisabled > 12) return 0;
    if (employee.isPartTime) return 0;
    // 장애 수단 계산
    ...
}
```

테스트를 한 후 그 다음 조건에도 적용한다.

모든 조건을 통합했다면 최종 조건식을 함수로 추출해볼 수 있다.

```java
public int disabilityAmount(Employee employee) {
    if (isNotEligibleForDisability(employee)) return 0;
    // 장애 수단 계산
    return 0;
}
```

***

## 10.3 중첩 조건문을 보호 구문으로 바꾸기 

### 배경 

조건문이 사용되는 케이스는 흔히 두 개다.

- 참인 경로와 거짓인 경로 모두 정상적인 로직이 필요한 경우.

- 참과 거짓 경로 중 한쪽만 정상적인 로직이 필요한 경우.

정상적인 로직이 명확하게 보이도록 하는게 중요하다. 

그래서 나는 참인 경로와 거짓인 경로 모두 정상적인 로직이라면 if - else 절로 확실하게 두 동작을 보여줄려고 한다.

그치만 한 쪽 경로만 정상적인 로직이라고 한다면 빨리 리턴하게 해서 정상적인 로직이 두드러지게 보이도록 한다.

예를 들면 비정상 조건을 if 절에서 검사한 후 조건이 참이면 (비정상이면) 함수에서 빠져나오도록 한다. 

이렇게 if 절로 검사하는 형태를 흔히 보호 구문 (guard clause) 라고 한다.

여거 조건문이 쌓인 중첩 조건문을 보호 구문을 바꾸면 코드가 명확해진다. 

중첩 조건문은 눈에 잘 들어오지 않기 때문이다.

중첩 조건문을 사용하는 프로그래머는 흔히 함수의 진입점과 반환점은 하나여야 한다 라는 규칙을 따르는 경우가 많다.

__코드의 작성은 명확해야 한다. 이 규칙을 따름으로써 코드가 명확해진다면 따르자. 그렇지 않다면 따를 필요는 없겠다.__

### 절차

1. 교체해야 할 조건 중 가장 바깥 것을 선택하여 보호 구문으로 바꾼다.

2. 테스트한다.

3. 이 과정을 반복한다.

4. 모든 보호 구문이 같은 결과를 반환한다면 보호 구문의 조건식을 통합한다. (10.2 절)

### 예시 

직원 급여를 계산하는 코드를 예로 가져왔다. 

현직 직원만 급여을 받아야 하므로 이 함수는 두 가지 조건을 검사하고 있다.

```java
public int payAmount(Employee employee) {
    int result;
    if (employee.isSeparated) {
        result = 0;
    }
    else {
        if (employee.isRetired) {
            result = 0;
        }
        else {
            result = calculateSalary();
        }
    }
    return result; 
}
```

이 코드는 실제 정상적인 동작들이 중첩된 조건문에 의해서 잘 보이지 않는다.

이 코드가 진짜 의도한 일들이 모든 조건이 거짓일때 실행되기 때문이다.

이런 상황에서는 보호 구문으로 중요하지 않은 코드들을 위에서 쳐내서 중요한 코드를 보도록 하는게 코드의 명확성을 살리기에 좋아보인다.

리팩토링을 진행할 땐 나는 항상 작은 단계부터 시작해서 컴파일 오류가 나지 않으면서 테스트가 실패하지 않도록 (사실 실패해도 상관없다.) 하는 걸 좋아한다.

이번에도 한 조건식부터 시작해보자.

````java
public int payAmount(Employee employee) {
    int result;
    if (employee.isSeparated) return 0; 
    else {
        if (employee.isRetired) {
            result = 0;
        }
        else {
            result = calculateSalary();
        }
    }
    return result;
}
```` 

계속해서 더 해보자

```java
public int payAmount(Employee employee) {
    int result;
    if (employee.isSeparated) return 0;
    if (employee.isRetired) return 0; 
    else {
        result = calculateSalary();
    }
    return result;
}
```

정리하면 이렇게 될 것이다.

````java
public int payAmount(Employee employee) {
    if (employee.isSeparated) return 0;
    if (employee.isRetired) return 0;
    return calculateSalary();
}
````

### 예시: 조건 반대로 만들기

이 기법의 또 다른 응용으로 여러 조건식을 통해서 정상적인 동작을 수행하는 코드라도 중첩된 조건문으로 인해서 한 눈에 보이지 않는 경우가 많다. 

이런 경우에는 조건식을 역으로 만들어서 즉 보호 구문으로 처내도록 만들면 중첩 조건식을 사용하지 않을 수 있다.

```java
public int adjustedCapital(Instrument instrument) {
    int result = 0; 
    if (instrument.interRate > 0) {
        if (instrument.interRate > 0 && instrument.duration > 0) {
            result = (int) ((instrument.income / instrument.duration) * instrument.adjustmentFactor);
        }
    }
    return result; 
}
```

여기서도 역시 한 번에 하나씩만 수정한다.

````java
public int adjustedCapital(Instrument instrument) {
    int result = 0;
    if (instrument.capital <= 0) return 0;
    if (instrument.capital > 0) {
        if (instrument.interRate > 0 && instrument.duration > 0) {
            result = (int) ((instrument.income / instrument.duration) * instrument.adjustmentFactor);
        }
    }
    return result;
}
````

계속해서 작업해보자.

````java
public int adjustedCapital(Instrument instrument) {
    int result = 0;
    if (instrument.capital <= 0) return 0;
    if (instrument.interRate <= 0 || instrument.duration <= 0) return 0;
    if (instrument.interRate > 0 && instrument.duration > 0) {
        result = (int) ((instrument.income / instrument.duration) * instrument.adjustmentFactor);
    }
    return result;
}
````

정리하면 이렇게 될 것이다.

```java
public int adjustedCapital(Instrument instrument) {
    if (    instrument.capital < 0 || 
                    instrument.interRate <= 0 || 
                    instrument.duration <= 0) return 0;
    
    return (int) ((instrument.income / instrument.duration) * instrument.adjustmentFactor);
}
```

***

## 10.4 조건부 로직을 다형성으로 바꾸기

### 배경

복잡한 조건부 로직은 프로그래밍에서 해석하기 가장 난해하다. 

그래서 나는 조건부 로직을 직관적으로 구조화할 방법을 항상 고민한다.

종종 더 높은 수준의 개념을 도입해 이 조건들을 분리해낼 수 있다.

이는 클래스와 다횽성을 이용하면 확실하게 분리하는게 가능하다.

흔한 예로 타입을 여러 개 만들고 각 타입이 조건부 로직을 자신만의 방식으로 처리하도록 구성하는 방법이 있다. 

타입이 다르니까 각 타입을 기준으로 분기하는 Switch 문이 여러 개 보인다면 case 별로 클래스를 만들어서 Switch 문의 중복을 없앨 수 있다.

또 다른 예로 기본 동작을 위한 case 문과 그 변형 동작으로 구성된 로직을 떠올릴 수 있다.

기본 동작은 가장 일반적이거나 가장 직관적인 동작이다.

그 다음 변형 동작은 각각의 서브 클래스에 넣는다.

기본 동작과 변형 동작이 섞여 있다면 코드가 지저분해질 수 밖에 없다.

그러므로 가장 직관적이고 일반적인 코드는 슈퍼 클래스에 넣고, 기본 동작과 차이를 나타내는 변형 동작들은 서브 클래스에 넣도록 하는 방식이 있다.

### 절차

1. 다형성 동작을 표현하는 클래스들이 없다면 만들어준다. 이왕이면 적합한 인스턴스를 알아서 만들엊누느 팩토리 함수도 함께 만든다.

2. 호출하는 코드에서 팩토리 함수를 사용하게 한다.

3. 조건부 로직 함수를 슈퍼 클래스로 옮긴다.

4. 서브 클래스 중 하나를 선택한다. 서브 클래스에서 슈퍼 클래스에 조건부 로직 메소드를 오버라이딩 한다. 조건부 문장 중 선택된 서브클래스에 해당하는 조건절을 서브클래스 메소드로 복사한 다음 적절히 수정한다.

5. 같은 방식으로 각 조건절을 해당 서브클래스에서 메소드로 구현한다.

6. 슈퍼클래스 메소드에는 기본 동작 부분만 남긴다.

### 예시

새의 종에 따른 비행 속도와 깃털 상태를 알려주는 프로그램이 있다고 보자.

```java
public String plumage(Bird bird) {
    switch (bird.type) {
        case "유럽 제비":
            return "보통이다.";
        case "아프리카 제비":
            return bird.numberOfCoconuts > 2 ? "지쳤다." : "보통이다."; 
        case "노르웨이 파랑 앵무":
            return bird.voltage > 100 ? "그을렸다." : "예쁘다"; 
        default:
            return "알 수 없다."; 
    }
}

public int airSpeedVelocity(Bird bird) {
    switch (bird.type) {
        case "유럽 제비":
            return 35;
        case "아프리카 제비":
            return 40 - 2 * bird.numberOfCoconuts; 
        case "노르웨이 파랑 앵무":
            return bird.isNailed ? 0 : 10 + bird.voltage / 10; 
        default:
            return 0; 
    }
}
```

새 종류에 따라 다르게 동작하는 함수가 몇 개 보이니 종류별 클래스를 만들어서 각각에 맞는 동작으로 표현하는게 복잡한 로직을 줄일 수 있을 것 같다.

가장 먼저 airSpeedVelocity() 함수와 plumage() 함수를 Bird 클래스로 묶어보자.

```java
public String plumage(Bird bird) {
    return bird.plumage();
}

public int airSpeedVelocity(Bird bird) {
   return bird.airSpeedVelocity(); 
}
```

````java
public class Bird {
    String type;
    int numberOfCoconuts;
    int voltage;
    boolean isNailed;

    public String plumage() {
        switch (this.type) {
            case "유럽 제비":
                return "보통이다.";
            case "아프리카 제비":
                return this.numberOfCoconuts > 2 ? "지쳤다." : "보통이다.";
            case "노르웨이 파랑 앵무":
                return this.voltage > 100 ? "그을렸다." : "예쁘다";
            default:
                return "알 수 없다.";
        }
    }

    public int airSpeedVelocity() {
        switch (this.type) {
            case "유럽 제비":
                return 35;
            case "아프리카 제비":
                return 40 - 2 * this.numberOfCoconuts;
            case "노르웨이 파랑 앵무":
                return this.isNailed ? 0 : 10 + this.voltage / 10;
            default:
                return 0;
        }
    }
}
````

이제 종별로 서브클래스를 만들자.

적합한 서브클래스의 인스턴스를 만들어줄 팩토리 함수도 잊지 말자.

그러고 나서 객체를 얻을 때 팩토리 함수를 사용하도록 수정한다.

```java
public Bird createBird(String type) {
    switch (type) {
        case "유럽 제비":
            return new EuropeanSwallow(); 
        case "아프리카 제비":
            return new AfricanSwallow();
        case "노르웨이 파랑 앵무":
            return new NorwegianBlueParrot();
        default:
            return new Bird(); 
    }
}
```

이제 서브 클래스에서 각각 차이에 해당하는 메소드를 구현해보자.

```java
public class EuropeanSwallow extends Bird {
    @Override
    public String plumage() {
        return "보통이다.";
    }

    @Override
    public int airSpeedVelocity() {
        return 35;
    }
}

public class AfricanSwallow extends Bird {
    @Override
    public String plumage() {
        return this.numberOfCoconuts > 2 ? "지쳤다." : "보통이다.";
    }

    @Override
    public int airSpeedVelocity() {
        return 40 - 2 * this.numberOfCoconuts;
    }
}

public class NorwegianBlueParrot extends Bird {
    @Override
    public String plumage() {
        return this.voltage > 100 ? "그을렸다." : "예쁘다";
    }

    @Override
    public int airSpeedVelocity() {
        return this.isNailed ? 0 : 10 + this.voltage / 10;
    }
}
```

### 예시: 변형 동작을 다형성으로 표현하기

조건문을 다형성으로 바꾸는 예제는 전형적인 방식이다.

이런 예 말고도 상속은 다양하게 이용하는게 가능하다.

지금은 거의 똑같지만 다른 케이스의 경우 다형성으로 어떻게 푸는지 살펴보겠다.

이러한 예로 신용 평가 기관에서 선박의 향해 투자 등급을 계산하는 코드를 생각해보자.

평가기관은 위험요소와 잠재 수익에 영향을 주는 다양한 요인을 기초로 향해 등급을 'A' 와 'B' 로 나눈다.

위험요소로는 향해 경로의 자연조건과 선장의 향해 이력을 고렿나다.

````java
public String rating(Voyage voyage, History history) {
    int vpf = voyageProfitFactor(voyage, history);
    int vr = voyageRisk(voyage);
    int chr = captainHistoryRisk(voyage, history);
    if (vpf * 3 > vr + chr * 2) return "A";
    return "B";
}

private int voyageProfitFactor(Voyage voyage, History history) {
    int result = 2;
    if (voyage.zone.equals("중국")) result += 1;
    if (voyage.zone.equals("동인도")) result += 1;
    if (voyage.zone.equals("wndrnr") && hasChina(history)) {
        result += 3;
        if (history.length() > 10) result += 1;
        if (voyage.length > 12) result += 1;
        if (voyage.length > 18) result -= 1;
    }
    else {
        if (history.length() > 8) result += 1;
        if (voyage.length > 14) result -= 1;
    }
    return result;
}

private boolean hasChina(History history) {
    return history.hasChina();
}

private int voyageRisk(Voyage voyage) {
    int result = 1;
    if (voyage.length > 4) result += 2;
    if (voyage.length > 8) result += voyage.length - 8;
    if (List.of("중국","동인도")
            .stream()
            .anyMatch(v -> voyage.zone.equals(v))) result += 4;
    return Math.max(result, 0);
}

private int captainHistoryRisk(Voyage voyage, History history) {
    int result = 1;
    if (history.length() < 5) result += 4; 
    result += history.noProfitList(); 
    if (voyage.zone.equals("중국") && hasChina(history)) result -= 2; 
    return Math.max(result, 0);
}
````

이 특수한 상황을 다루는 로직을 기본 동작에서 분리하기 위해서 상속과 다형성을 이용해보겠다.

중국으로의 향해 시 추가될 로직이 많았더라면 이번 리팩토링의 효과가 더욱 컸겠지만, 지금 상황에서도 이 특수한 상황을 검사하는 로직이 반복되서 기본 동작을 이해하는데 방해가 되고 있다.

함수는 많은데 세부 계산을 수행하는 함수들을 먼저 처리해보자.

다형성을 처리하기 위해선 클래스가 필요하니 클래스부터 만들자.

````java
public class Rating {
    Voyage voyage;
    History history;

    public Rating(Voyage voyage, History history) {
        this.voyage = voyage;
        this.history = history;
    }

    public String value() {
        int vpf = voyageProfitFactor();
        int vr = voyageRisk();
        int chr = captainHistoryRisk();
        if (vpf * 3 > vr + chr * 2) return "A";
        return "B";
    }

    protected int voyageProfitFactor() {
        int result = 2;
        if (voyage.zone.equals("중국")) result += 1;
        if (voyage.zone.equals("동인도")) result += 1;
        if (voyage.zone.equals("wndrnr") && hasChina()) {
            result += 3;
            if (history.length() > 10) result += 1;
            if (voyage.length > 12) result += 1;
            if (voyage.length > 18) result -= 1;
        }
        else {
            if (history.length() > 8) result += 1;
            if (voyage.length > 14) result -= 1;
        }
        return result;
    }

    protected boolean hasChina() {
        return history.hasChina();
    }

    protected int voyageRisk() {
        int result = 1;
        if (voyage.length > 4) result += 2;
        if (voyage.length > 8) result += voyage.length - 8;
        if (List.of("중국","동인도")
                .stream()
                .anyMatch(v -> voyage.zone.equals(v))) result += 4;
        return Math.max(result, 0);
    }

    protected int captainHistoryRisk() {
        int result = 1;
        if (history.length() < 5) result += 4;
        result += history.noProfitList();
        if (voyage.zone.equals("중국") && hasChina()) result -= 2;
        return Math.max(result, 0);
    }
}

````

기본 동작을 담당할 클래스가 만들어졌으니 다음 차례는 변형 동작을 담은 서브 클래스를 만들자.

```java
public class ExperiencedChinaRating extends Rating {
    
}
```

그 다음 적절한 Rating 클래스를 반환해 줄 팩토리 함수도 만들자.

```java
public Rating createRating(Voyage voyage, History history) {
    if (voyage.zone.equals("중국") && history.hasChina()) {
        return new ExperiencedChinaRating(voyage, history); 
    }
    return new Rating(voyage, history);
}
```

이제 기본 동작과 변형 동작을 구별해보자.

먼저 captainHistoryRisk() 함수부터 구별해보겠다.

```java
class Rating {
    ...
    protected int captainHistoryRisk() {
        int result = 1;
        if (history.length() < 5) result += 4;
        result += history.noProfitList();
        return Math.max(result, 0);
    }
}

class ExperiencedChinaRating extends Rating {
    ...
    @Override
    protected int captainHistoryRisk() {
        return super.captainHistoryRisk() - 2;
    }
}
```

계속해서 작업해보자.

voyageProfitFactor 를 작업하는 건 좀 더 어려워보인다. 왜냐하면 중국을 검사하는 부분과 그렇지 않은 부분 둘로 나눠져있기 떄문이다.

해결하는 방법은 다양할 수 있겠다.

그 중 하나의 방법으로 슈퍼 클래스의 메소드를 그대로 복사해서 서브 클래스로 가져와서 약간만 바꾸는 방법이 있겠지만 이 방법은 코드의 중복을 발생하므로 그렇게 하고 싶지 않다.

그러므로 차이가 나는 부분만 따로 메소드를 만들어서 그 부분만 다르게 하는 방법을 하도록 하겠다.

```java
class Rating {
    ...
    protected int voyageProfitFactor() {
        int result = 2;
        if (voyage.zone.equals("중국")) result += 1;
        if (voyage.zone.equals("동인도")) result += 1;
        result += voyageAndHistoryLengthFactor();
        return result;
    }

    protected int voyageAndHistoryLengthFactor() {
        int result = 0;
        if (history.length() > 8) result += 1;
        if (voyage.length > 14) result -= 1;
        return result;
    }
}

class ExperiencedChinaRating extends Rating {
    ...
    @Override
    protected int voyageAndHistoryLengthFactor() {
        int result = 3;
        if (history.length() > 10) result += 1;
        if (voyage.length > 12) result += 1;
        if (voyage.length > 18) result -= 1;
        return result;
    }
}
```

여기서 좀 더 리팩토링을 한다하면 voyageAndHistoryLengthFactor 의 메소드의 경우 이름이 And 가 있다.

이 말은 여러가지의 일을 한다는 뜻으로 이를 줄여주도록 하는 방법이 있을 것 같다.

***

## 10.5 특이 케이스 추가하기

### 배경

데이터 구조의 특정 값을 확인한 후 똑같은 동작을 반복하는 코드가 곳곳에 등장한다면 이를 해결해야 한다.

이 같은 경우는 반복되는 코드들을 한 곳으로 모아야 효율적이다.

특수한 경우의 공통 동작을 하나에 모아서 사용하는 경우가 특이 케이스 패턴 (Special Case Pattern) 이라고 한다.

특이 케이스는 여러 형태로 표현할 수 있다. 값 객체로 표현하는 것도 가능하고 어떠한 동작을 수행할 수도 있다.

이 패턴이 주로 사용되는 경우가 null 인 경우라서 널 객체 패턴 (Null Object Pattern) 이라고도 하는데 널 외에도 특이 케이스 패턴을 적용할 수 있다.

### 절차

이번 리팩토링에서는 데이터 구조 (클래스) 가 있다고 가정하고 이를 컨테이너라고 부르겠다.

컨테이너를 사용하는 코드에선 해당 속성이 특이한 값인지를 검사하고 우리는 이 대상이 가질 수 있는 값 중 특별하게 다뤄야 할 값을 특이 케이스 클래스로 대체하겠다.

1. 컨테이너에 특이 케이스인지 검사하는 속성을 추가하고 false 를 반환한다.

2. 특이 케이스 객체를 만든다. 이 객체는 특이 케이스인지를 검사하는 속성만 포함하고 이 속성은 true 를 반환한다.

3. 클라이언트에서 특이 케이스인지를 검사하는 코드를 함수로 추출한다. 모든 클라이언트가 값을 직접 비교하는 대시 방금 추출한 함수를 사용하도록 고친다.

4. 코드에 새로운 특이 케이스 대상을 추가한다. 함수의 반환 값으로 받거나 변환 함수를 적용하면 된다.

5. 특이 케이스를 검사하는 함수 본문을 수정하여 특이 케이스 객체의 속서을 사용하도록 한다.

6. 테스트 한다.

7. 여러 함수를 클래스로 묶기 (6.9 절) 이나 여러 함수를 변환 함수로 묶기 (6.10 절) 을 적용하여 특이 케이스를 처리하는 공통 동작을 새로운 요소로 옮긴다. 

8. 아직도 특이 케이스 검사 함수를 이용하는 곳이 남아 있다면 검사 함수를 인라인 (6.2 절) 한다. 


### 예시 

Javascript 코딩으로만 가능한 예제라서 생략한다. 

***

## 10.6 어서션 추가하기 

### 배경 

특정한 조건 내에서만 동작하는 코드가 있을 수 있다.

예로 제곱근 계산은 입력이 양수 일때만 동작할 수 있다.

객체로 접근을 하면 필드 값을 기반으로 동작을 할 수도 있다.

이런 조건이 코드에 명시적으로 드러나면 좋겠지만 그렇지 않은 경우도 있다. 알고리즘을 보고 연역적으로 알아내야 하는 경우도 있다. 

주석으로라도 표현이 되어있으면 좋고 가장 좋은 방법은 assertion 을 이용해서 코드 자체에 삽입해놓는 것이 가장 좋다.

어서션 (Assertion) 은 항상 참이라고 가정하는 조건부로 이게 참이 아닌 값이 넘어온다면 프로그래머가 실수했다는 뜻이다.

어서션의 실패는 시스템의 다른 부분에서 절대 검사하지 않아야하고 어서션이 있고 없고가 프로그램 기능 동작에 아무런 영향을 주지 않도록 작성해야 한다. __(단지 검사로만 이용을 해야한다 라는 뜻.)__

그래서 어서션을 컴파일러에 켜고 끌 수 있는 스위치를 제공하는 프로그래밍 언어도 있다. 

어서션을 오류 찾기에 활용하라는 사람도 있다.

__물론 좋은 일이지만 어서션의 본질적인 목적은 프로그램이 어떠한 상태여야 하는지 다른 개발자들에게 설명해주는 도구라는 점이다.__

디버깅하기도 편하고 소통 수단으로서의 가치도 있어서 나는 추적하던 버그를 잡은 뒤에도 어서션을 코드에 남겨 두기도 한다.

테스트 코드가 있다면 어서션의 디버깅 용도는 줄어든다. __(테스트가 설명해주는 부분이 있기 때문에)__

단위 테스트를 꾸준히 축해서 시각을 좁혀 놓으면 어서션보다 나을 때가 많다. 하지만 소통 측면에서 어서션은 매력적이다.

### 절차

1. 참이라는 가정이 보이면 그 조건을 명시하는 어서션을 추가한다.  

### 예시 

할인과 관련한 간단한 예를 준비했다.

다음과 같이 고객은 상품 구입 시 할인율을 적용받는다.

````java
public int applyDiscount(int number) {
    Assert.isTrue(discountRate > 0);
    if (discountRate > 0) return number; 
    return number - ((int) discountRate * number); 
}
````

여기서는 어서션이 applyDiscount() 메소드에 있다.

근데 좀만 더 생각해보면 discountRate 가 처음에 할당되는 곳에 어서션이 있는게 맞다. 

그 값을 넣는 곳에 검증을 하는게 연관성이 있기 때문이다.

그래서 세터에 넣자.

````java
public void setDiscountRate(double discountRate) {
    Assert.isTrue(discountRate > 0);
    this.discountRate = discountRate;
}
````

***

## 10.7 제어 플래그를 탈출문으로 바꾸기

### 배경

제어 플래그의 정의를 먼저 보자.

제어 플래그는 코드의 동작을 바꾸는 곳에서 나온다.

조건절에서는 제어 플래그를 통해 검사하고 어떤 문에서는 계산을 통해 제어 플래그의 값을 바꾸는 구조로 되어있다.

제어 플래그를 사용하면 코드를 이해하기 어려워지므로 이를 리팩토링해서 쉽게 바꾸자. 

제어 플래그의 주 서식지는 반복문이다.

break 문이나 continue 문에 익숙하지 않은 사람이거나, 함수의 return 을 적절하게 이용하지 못하면 나온다.

### 절차

1. 제어 플래그를 사용하는 코드를 함수로 추출할지 고려한다.

2. 제어 플래그를 갱신하는 코드 각각을 적절한 제어문으로 바꾼다. 하나씩 바꿀 때마다 테스트한다.

3. 모두 수정했다면 제어 플래그를 제거한다.

### 예시 

다음 예시는 사람 목록을 훑으면서 악당 (miscreant) 을 찾는 코드다. 

악당 이름은 하드코딩 되어있다.

````java
boolean found = false; 

for (Person p : people) {
    if (!found) {
        if (p.name.equals("조거")) {
            sendAlert();
            found = true; 
        }
        if (p.name.equals("사루만")) {
            sendAlert();
            found = true; 
        }
    }
}
````

여기서 found 플래그는 제어 플래그다.

리팩토링을 하기 위해서 제어 플래그를 사용하는 부분을 따로 뽑아보자.

````java
public void checkForMiscreants(List<Person> people) {
    boolean found = false;

    for (Person p : people) {
        if (!found) {
            if (p.name.equals("조거")) {
                sendAlert();
                found = true;
            }
            if (p.name.equals("사루만")) {
                sendAlert();
                found = true;
            }
        }
    }
}
````

코드를 보니 제어 플래그가 참이면 sendAlert() 메소드를 호출하고 딱히 하는 일이 없다. 그러므로 리턴을 해주면 될 것 같다.

`````java
public void checkForMiscreants(List<Person> people) {
    boolean found = false;

    for (Person p : people) {
        if (!found) {
            if (p.name.equals("조거")) {
                sendAlert();
                return;
            }
            if (p.name.equals("사루만")) {
                sendAlert();
                return;
            }
        }
    }
}
`````

모두 이렇게 리턴하도록 하자.

그런다음 제어 플래그를 제거하자.

```java
public void checkForMiscreants(List<Person> people) {
    for (Person p : people) {
        if (p.name.equals("조거")) {
            sendAlert();
            return;
        }
        if (p.name.equals("사루만")) {
            sendAlert();
            return;
        }
    }
}
```



  

















