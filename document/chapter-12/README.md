# 상속 다루기 

마지막 장이다.

이번 장에서는 객체 지향 프로그래밍에서 가장 유명한 상속을 다루겠다.

상속은 유용한 대신에 오용하기 쉽다.

특정 기능을 상속 계층 구조에서 위나 아래로 옮기는 일은 꽤나 흔하다. 

이와 관련된 리팩토링 기법으로는 __메소드 올리기 (12.1 절)__ 과 __필드 올리기 (12.2 절)__, __생성자 본문 올리기 (12.3 절)__, __메소드 내리기 (12.4 절)__, __필드 내리기 (12.5 절)__ 이 있다.

계층 사이에 클래스를 추가하거나 제거하는 리팩토링 기법으로는 __슈퍼 클래스 추출하기 (12.8 절)__, __서브 클래스 제거하기 (12.7 절)__, __계층 합치기 (12.9 절)__ 이 있다.

때론 필드 값에 따라서 동작이 달라지는 클래스가 있는데 이런 필드를 서브 클래스로 대체할 수 있다. 이게 더 나은 것 같다면 적용하는 기법으로는 __타입 코드를 서브 클래스로 바꾸가 (12.6 절)__ 을 이용한다.

상속은 아주 막강한 도구지만 잘못된 곳에서 사용되는 경우에는 문제가 생길 수 있다. 이런 경우에 사용하는 리팩토링 기법으로는 __서브 클래스를 위임으로 바꾸기 (12.10 절)__ 과 __슈퍼 클래스를 위임으로 바꾸기 (12.11 절)__ 를 활용할 수 있다.

이를 통해 상속을 위임으실로 바꿀 수 있다.

***

## 12.1 메소드 올리기

### 배경

중복 코드 제거는 중요하다,

한쪽의 변경이 다른 쪽에는 업데이트 되지 않을 수도 있는, 중복을 놓치는 문제가 생기기 쉽기 때문이다.

메소드 올리기를 적용하는 가장 쉬운 예는 메소드들의 본문 코드가 똑같을 경우다.

이럴 땐 그냥 복사해서 슈퍼 클래스에 붙여 넣기만 하면 끝이다.

리팩터링이 제대로 되었는 지를 검증하려면 테스트가 여전히 잘 작동하는지 확인하면 된다. 그치만 이 방법은 얼마나 테스트를 잘 만들어 놓았는가에 의존한다.

매소드 올리기 리팩토링을 적용하려면 선행 단계를 거쳐야 하는 경우가 많다.

예로 서로 다른 두 클래스의 두 메소드를 매개변수화 하면 궁극적으로 같은 메소드가 된다. __(함수를 매개변수화 하기 (11.2 절))__

또 메소드 올리기를 적용하기에 복잡한 상황은 해당 메소드에서 참조하는 필드들이 서브 클래스에만 있는 경우다.

이런 경우에는 필드를 먼저 슈퍼 클래스로 옮기는 __필드 올리기 (12.2 절)__ 을 먼저 적용하고 메소드에 올려야 한다.

마지막으로 두 메소드의 전체 흐름은 비슷하지만 세부 내용이 다르다면 __템플릿 메소드 만들기 디자인 패턴__ 을 고려하자.

### 절차

1. 똑같이 동작하는 메소드인지 살펴본다. (실질적으로 하는 일은 같지만 코드만 다르면 본문 코드가 똑같아질 때까지 리팩토링 한다.)

2. 옮길 메소드에서 다른 메소드를 호출하거나, 필드를 참조하는데 이게 슈퍼 클래스에서도 충분히 가능한지 확인한다.

3. 메소드 시그니처가 다르다면 __함수 선언 바꾸기 (6.5 절)__ 로 슈퍼 클래스에서 사용하고 싶은 형태로 통일한다.

4. 수퍼 클래스에 메소드를 생성하고, 대상 메소드의 코드를 복사해 붙여넣는다.

5. 정적 검사를 수행한다.

6. 서브 클래스 중 하나의 메소드를 제거한다.

7. 테스트한다.

8. 모든 서브 클래스의 메소드가 없어질 때까지 다른 서브 클래스의 메소드를 하나씩 제거한다.

### 예시 

두 서브 클래스 (Employee, Department) 가 있고 둘 다 Party 클래스를 상속한다. 

그리고 두 클래스에서 같은 일을 수행하는 메소드를 찾았다.

```java
public class Employee extends Party {
    int monthlyCost;

    public int annualCost() {
        return monthlyCost * 12; 
    }

    ...
}

public class Department extends Party{
    int monthlyCost;

    public int totalAnnualCost() {
        return monthlyCost * 12;
    }
    
    ...
}
```

두 메소드의 이름이 일단 다르므로 함수 선언 바꾸기를 통해서 이름을 통일한다.

그 다음 슈퍼 클래스에서 사용할 수 있는지 확인하고 슈퍼 클래스에서 옮길 수 있도록 다른 것들을 옮기자. (여기서는 딱히 옮길만한 것이 없다.)

슈퍼 클래스로 메소드를 올린다면 다음과 같다. 

```java
public class Party {
    int monthlyCost;

    public int annualCost() {
        return monthlyCost * 12;
    }
    
    ...
}
```

***

## 12.2 필드 올리기

### 배경 

서브 클래스들이 독립적으로 시간을 두고 개발되었거나 뒤늦게 하나의 계층 구조로 통합되어 있는 경우라면 필드가 중복되는 경우가 생길 수 있다.

항상 그런 것은 아니기 때문에 필드가 어떻게 사용되고 있는지 분석해야 한다.

분석 결과 필드들이 비슷한 방식으로 쓰인다고 판단되면 슈퍼 클래스로 올리자.

이렇게 하면 얻을 수 있는 것으로 데이터 선언 중복을 제거하는게 가능하고 그 선언을 바탕으로 하는 동작을 슈퍼 클래스로 올겨서 중복을 제거할 수 있다.

### 절차

1. 후보 필드들을 사용하는 곳 모두가 그 필드를 똑같은 방식으로 사용하는지 분석한다.

2. 분석이 끝나고 그렇다고 판단되면 리팩토링을 시작할 수 있다. 필드의 이름이 똑같은지 보자. 다르다면 같도록 바꾸자. __(필드 이름 바꾸기 (9.2 절))__

3. 슈퍼 클래스에 새로운 필드를 생성한다.

4. 서브 클래스들의 필드를 제거한다.

5. 테스트한다.

***

## 12.3 생성자 본문 올리기

### 배경

생성자는 일반 메소드와 많이 달라서 나는 생성자가 하는 일에 제약을 둔다. __(Validation, 초기화 로직 등인가?)__

생성자는 할 수 있는 일과 호출 순서에 제약이 있기 때문이다. __(생성자 대신 팩토리 함수로 바꾸는게 더 나은지 생각해보는 것도 좋다. , 슈퍼 클래스의 생성자를 먼저 호출하는 걸 말하나?)__

### 절차

1. 슈퍼 클래스에 생성자가 없다면 하나 정의한다. 서브 클래스의 생성자들에서 슈퍼 클래스의 생성자를 호출하는지 확인한다. 

2. __문장 슬라이드 하기 (8.6 절)__ 을 이용해 공통 문장 모두를 super() 호출 직후로 옮긴다. 

3. 공통 코드를 슈퍼 클래스에 추가하고 서브 클래스들에게서 제거한다. 생성자 매개변수 중 공통 코드로 참조하는 값은 super 로 건넨다.

4. 테스트한다.

5. 생성자 시작 부분으로 옮길 수 없는 공통 코드에는 __함수 추출하기 (6.1 절)__ 과 __메소드 올리기 (12.1 절)__ 을 차례로 적용한다.

### 예시

다음 코드에서 시작해보자. 

````java
public class Party {}

public class Employee extends Party {
    public Employee(Long id, String name, int monthlyCost) {
        super();
        this.id = id;
        this.name = name;
        this.monthlyCost = monthlyCost;
    }
    
    ...
}

public class Department extends Party{
    public Department(String name, Staff staff) {
        super();
        this.name = name;
        this.staff = staff;
    }
    
    ...
}
````

여기서 공통 코드는 this.name = name 부분이다. 

Employee 클래스에서 이 부분을 문장 슬라이드를 적용해서 super() 코드 바로 앞으로 올리자.

```java
public class Employee extends Party {
    public Employee(Long id, String name, int monthlyCost) {
        super();
        this.name = name;
        this.id = id;
        this.monthlyCost = monthlyCost;
    }
    
    ...
}
```

그 다음 공통 부분을 슈퍼 클래스로 올리자.

````java
public class Party {
    String name;

    public Party(String name) {
        this.name = name;
    }
    
    ...
}

public class Employee extends Party {

    public Employee(Long id, String name, int monthlyCost) {
        super(name);
        this.id = id;
        this.monthlyCost = monthlyCost;
    }
    
    ...
}   

public class Department extends Party{

    public Department(String name, Staff staff) {
        super(name);
        this.staff = staff;
    }

    ...
}
````

### 예시: 공통 코드가 나중에 올 때 

생성자는 대부분 super() 를 호출해서 공통 코드를 먼저 처리하고 각 서브 클래스에 필요한 추가 작업을 처리하는 식으로 동작한다.

그런데 이따금 공통 작업이 뒤에 오는 경우도 있다. __(이 경우도 공통 로직이 서브 클래스 각각에 있는 것보다 슈퍼 클래스에 몰아서 중복을 없애도록 하는 리팩토링 기법.)__

다음 예시를 보자.

```java
public class Employee extends Party {
    Long id;
    int monthlyCost;

    public Employee(String name) {
        super(name);
    }
    
    public boolean isPrivileged() {
        ... 
    }

    protected void assignCar() {
        ...
    }
}
```

```java
public class Manager extends Employee {
    Grade grade; 
    
    public Manager(String name, Grade grade, boolean isPrivileged) {
        super(name);
        this.grade = grade; 
        if (isPrivileged()) assignCar();
    }

    @Override
    public boolean isPrivileged() {
        return grade.val > 4; 
    }
}
```

슈퍼 클래스인 Employee 에 있는 assignCar() 메소드는 Grade 값이 할당된 다음에 호출할 수 있다. 

이런 공통 코드가 뒤에 오는 부분은 공통 코드를 추출해서 메소드 올리기를 적용시키자.

````java
public class Employee extends Party {
    
    ...

    protected void finishConstruction() {
        if (isPrivileged()) assignCar();
    }
}
````

````java
public class Manager extends Employee {
    Grade grade;

    public Manager(String name, Grade grade, boolean isPrivileged) {
        super(name);
        this.grade = grade;
        finishConstruction();
    }
    
    @Override
    public boolean isPrivileged() {
        return grade.val > 4;
    }

    ...
}
````

- 서브 클래스에서 슈퍼 클래스의 메소드를 호출 할 때 일부를 서브 클래스에서 재정의 (Override) 했다면 서브 클래스의 메소드가 호출 된다는 점. 

***

## 12.4 메소드 내리기

### 배경

특정 서브 클래스 하나 (혹은 소수) 와만 관련된 메소드는 슈퍼 클래스에서 제거하고 서브 클래스에 두는 것이 더 깔끔하다. __(LSP 원칙.)__

다만 이 리팩토링은 슈퍼 클래스에서 제공하는 기능이 어떤 서브 클래스에서 사용하는지 명확할 때 사용해야 한다.

만약에 각 서브 클래스마다 다르게 동작해야 하는 부분이 있다면 이 기능의 많은 로직보다 다형성으로 바꾸는게 나을 경우가 있기 때문에.

### 절차

1. 대상 메소드를 모든 서크 클래스에 복사한다.

2. 슈퍼 클래스에서 그 메소드를 제거한다.

3. 테스트한다.

4. 메소드를 사용하지 않는 서브클래스에선 제거한다.

5. 테스트한다.

***

## 12.5 필드 내리기

### 배경

서브 클래스 하나 (혹은 소수) 에서만 사용하는 필드는 해당 서브 클래스로 옮긴다.

### 절차

1. 대상 필드를 모든 서브 클래스에서 정의한다.

2. 슈퍼 클래스에서 그 필드를 제거한다.

3. 테스트한다.

4. 이 필드를 사용하지 않는 서브 클래스에선 필드를 제거한다.

5. 테스트한다.


***


