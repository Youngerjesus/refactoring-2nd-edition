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

 


