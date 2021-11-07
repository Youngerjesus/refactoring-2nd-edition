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

## 12.6 타입 코드를 서브 클래스로 바꾸기

### 배경

소프트웨어 시스템에서는 비슷한 대상을 특정 특성에 따라 구분해야 할 때가 있다.

예로 직원을 담당 업무로 구분하거나 (엔지니어, 관리자, 영업자 등), 주무능ㄹ 시급성으로 구분 하거나 (급함, 보통) 등

이런 일을 다루는 수단으로 타입 코드를 프로그래밍에서 자주 사용한다.

타입 코드만으로는 불편한 상황은 없지만 그 이상으로 무언가 필요할 때가 있다.

특성에 따라서 다르게 동작하도록 하거나, 특정 타입에 따라서 다른 동작이나 데이터가 필요하거나 할 때 

이런 방식은 서브 클래스를 사용하면 해결해줄 수 있다. 

물론 타입 코드르 사용할 때도 코드를 넣어줄 수 있다. 서브 클래스 방식이 관계를 더욱 명확히 드러내주기는 하지만.

이번 리팩토링은 서브 클래스를 이용할 지, 타입 코드를 이용할 지 고민하는 문제다.

서브 클래스는 하위 타입인 클래스를 만드는 방식이고 타입 코드는 속성을 클래스로 만들고 속성을 서브 클래스로 정의하는 방식이다. 

### 절차

1. 타입 코드 필드를 캡슐화한다. __(타입 코드를 사용하는 코드가 있을 수도 있기 때문에. 캡슐화해둔다. 서브 클래스에서도 재정의해서 컴파일 에러가 없도록 하기 위해서.)__

2. 타입 코드 값 하나를 선택해서 그 값에 해당하는 서브 클래스를 만든다. 

3. 매개변수로 받은 타입 코드와 방금 만든 서브 클래스를 매핑하는 선택 로직을 만든다. __(선택 로직이라는 말은 어떤 객체를 생성할 지 선택하는 로직을 말한다. 직접 상속인 경우에는 생성자를 팩토리 함수로 바꿔서 호출하도록 하고 선택 로직을 팩토리에 넣는다. 간접 상속인 경우에는 선택 로직을 생성자에 두도록 한다.)__

4. 테스트한다.

5. 타입 코드 값 각각에 대해 서브 클래스 생성과 선택 로직 추가를 반복한다. 클래스 하나가 완성될 때마다 테스트한다.

6. 타입 코드 필드를 제거한다.

7. 테스트한다.

8. 타입 코드 접근자를 이용하는 메소드 모두에 __메소드 내리기 (12.4 절)__ 과 __조건부 로직을 다형성으로 바꾸기 (10.4 절)__ 을 이용한다.

### 예시: 직접 상속할 때

이번에도 직원 코드를 예로 보자.

````java
public class Employee {
    String name;
    String type;

    public Employee(String name, String type) throws Exception {
        validateType(type);
        this.name = name;
        this.type = type;
    }

    private void validateType(String type) throws Exception {
        if (List.of("manager", "engineer", "salesperson")
            .stream()
            .noneMatch(t -> t.equals(type))
        ) {
            throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다.", type)); 
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
````

첫 번째로 타입 코드 변수를 __자가 캡슐화 (6.6 절)__ 한다. 

`````java
// Employee 클래스 
@Override
public String toString() {
    return "Employee{" +
            "name='" + name + '\'' +
            ", type='" + getType() + '\'' +
            '}';
}

public String getType() {
    return type;
}
`````

타입 코드 중 하나, 여기서 엔지니어 (Engineer) 클래스를 생성한다고 해보자.

직원 (Employee) 클래스 자체를 서브 클래싱하면 된다.

각각의 인스턴스를 생성하는 로직을 생성자에 넣으려 하면 로직이 꼬일 것이므로 __생성자를 팩터리 함수로 바꾸기 (11.8 절)__ 을 적용하자. 

새로 만든 서브 클래스를 사용하기 위하 선택 로직을 팩토리 함수 추가하면 다음과 같다. 

```java
public class Employee {
    public static Employee createEmployee(String name, String type) throws Exception {
        switch (type) {
            case "engineer": return new Engineer(name, type); 
        }
        return new Employee(name, type); 
    }
    
    ...
}
```

이제 다른 클래스들도 하나씩 추가하자. 

```java
// Employee 클래스 
public static Employee createEmployee(String name, String type) throws Exception {
    switch (type) {
        case "engineer": return new Engineer(name, type);
        case "manager": return new Manager(name, type); 
        case "salesperson": return new Salesperson(name, type); 
    }
    return new Employee(name, type);
}
```

```java
public class Engineer extends Employee {
    public Engineer(String name, String type) throws Exception {
        super(name, type);
    }

    @Override
    public String getType() {
        return "engineer";
    }
}

public class Salesperson extends Employee {
    public Salesperson(String name, String type) throws Exception {
        super(name, type);
    }

    @Override
    public String getType() {
        return "salesperson";
    }
}

public class Manager extends Employee {

    public Manager(String name, String type) throws Exception {
        super(name, type);
    }

    @Override
    public String getType() {
        return "manager";
    }
}
```

이제 타입 필드를 제거하자. 

정리하면 다음과 같다.

```java
public class Employee {
    String name;

    public static Employee createEmployee(String name, String type) throws Exception {
        switch (type) {
            case "engineer": return new Engineer(name);
            case "manager": return new Manager(name);
            case "salesperson": return new Salesperson(name);
            default: throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다."));
        }
    }

    public Employee(String name) {
        this.name = name;
    }
}

public class Engineer extends Employee {
    public Engineer(String name) {
        super(name);
    }

    public String getType() {
        return "engineer";
    }
}

public class Salesperson extends Employee {
    public Salesperson(String name) {
        super(name);
    }

    public String getType() {
        return "salesperson";
    }
}

public class Manager extends Employee {

    public Manager(String name) {
        super(name);
    }

    public String getType() {
        return "manager";
    }
}
```

### 예시: 간접 상속일 경우

처음 상황으로 돌아가보자. 직원 클래스 (Employee) 가 이미 서브 클래스가 있었더라면 어떻게 해야할까? (아르바이트인지, 정직원인지)

이미 서브 클래스가 있기 떄문에 Engineer, Salesperson, Manager 와 같은 서브 클래스를 만드는 건 힘들다.

이 경우에는 타입 코드를 객체로 만들고 타입 객체에 서브 클래스를 두는 방식으로 해결할 수 있다.

다음 Employee 클래스를 보자.

````java
public class Employee {
    String name;
    String type;

    public Employee(String name, String type) throws Exception {
        validateType(type);
        this.name = name;
        this.type = type;
    }

    private void validateType(String type) throws Exception {
        if (List.of("manager", "engineer", "salesperson")
                .stream()
                .noneMatch(t -> t.equals(type))
        ) {
            throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다.", type));
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public String getType() {
        return type;
    }
}
````

첫 번째로 할 일은 타입 코드를 객체로 바꾸는 일이다. __기본형을 객체로 바꾸기 (7.3 절)__

````java
public class EmployeeType {
   String value;

    public EmployeeType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value; 
    }
}
````

````java
public class Employee {
    String name;
    EmployeeType type;

    public Employee(String name, EmployeeType type) throws Exception {
        validateType(type);
        this.name = name;
        this.type = type;
    }

    private void validateType(EmployeeType type) throws Exception {
        if (List.of("manager", "engineer", "salesperson")
                .stream()
                .noneMatch(t -> t.equals(type.toString()))
        ) {
            throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다.", type));
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public String getType() {
        return type.toString();
    }
}
````

이제 각 타입을 기준으로 서브 클래스를 만들자. 

그 다음 Employee 생성자에서 각각의 인스턴스를 만들도록 하자.

```java
public class Employee {
    String name;
    EmployeeType type;

    public Employee(String name, String type) throws Exception {
        this.name = name;
        this.type = Employee.createEmployeeType(type);
    }

    private static EmployeeType createEmployeeType(String type) throws Exception {
        switch (type) {
            case "engineer": return new Engineer();
            case "manager": return new Manager();
            case "salesperson": return new Salesperson();
            default: throw new Exception(String.format("%s 에 해당하는 직원 유형은 없습니다."));
        }
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }

    public String getType() {
        return type.toString();
    }
}
```

***

## 12.7 서브 클래스 제거하기

### 배경

서브 클래스는 원래 데이터 구조와는 다른 변종을 만들어서 동작을 달라지게 하는 유용한 수단이다.

하지만 소프트웨어가 커지면서 변종이 다른 모듈로 이동하거나 사라지기도 하면서 한 번도 활용되지 않기도 한다.

더 이상 쓰이지 않는 서브 클래스는 그냥 슈퍼 클래스가 대체하는 게 최선이다.

### 절차

1. 서브 클래스의 생성자를 팩토리 함수로 만든다.

2. 서브 클래스의 타입을 검사하는 코드가 있다면 그 검사 코드를 함수 추출과 함수 옮기기를 통해서 슈퍼 클래스로 옮긴다. 

3. 서브 클래스의 타입을 나타내는 필드를 슈퍼 클래스에 만든다.

4. 서브 클래스를 참조하는 메소드가 방금 만든 타입 필드를 이용하도록 수정한다.

5. 서브 클래스를 지운다.

6. 테스트한다. 

### 예시

다음 서브 클래스들을 살펴보자.

````java
public class Person {
    String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getGenderCode() {
        return "X";
    }
}

public class Male extends Person {
    public Male(String name) {
        super(name);
    }

    @Override
    public String getGenderCode() {
        return "M";
    }
}

public class Female extends Person {

    public Female(String name) {
        super(name);
    }

    @Override
    public String getGenderCode() {
        return "F";
    }
}
````

서브 클래스가 하는 일이 겨우 gender 를 표시하는 일이 다라면 굳이 존재할 필요가 없다. 슈퍼 클래스에서 필드로 젠더를 갖는게 더 나아보이나.

그렇다고해서 바로 서브 클래스를 지우지말자. 

먼저 서브 클래스를 사용하는 클라리언트를 확인하고 서브 클래스를 지웠을 때의 파급 효과를 최소화로 하기 위해 캡슐화를 하도록하자.

서브 클래스를 지울 때 파급력 중 하나는 객체의 생성과 관련된 부분이다. 

클라이언트에 다음과 같은 부분이 있다고 가정해보자. 

````java
public List<Person> loadFromInput(List<Data> dataList) {
    List<Person> result = new ArrayList<>(); 
    
    dataList.stream()
            .forEach(d -> {
                Person p; 
                switch (d.gender) {
                    case "M": p = new Male(d.name); break;
                    case "F": p = new Female(d.name); break;
                    default: p = new Person(d.name);
                }
                result.add(p);
            });
    
    return result; 
}
````

여기서 생성할 클래스를 선택하는 로직을 함수로 추출하고 그 함수를 팩토리 함수로 캡슐화하자.

````java
public List<Person> loadFromInput(List<Data> dataList) {
    return dataList.stream()
            .map(this::createPerson)
            .collect(Collectors.toList()); 
}

private Person createPerson(Data d) {
    Person p;
    switch (d.gender) {
        case "M": p = new Male(d.name); break;
        case "F": p = new Female(d.name); break;
        default: p = new Person(d.name);
    }
    return p;
}
````

또 서브 클래스를 사용하는 부분이 있다면 부모 클래스로 옮기자.

이렇게 서브 클래스 정보를 슈퍼 클래스나 팩토리 함수로 옮기고 나서 서브 클래스를 지울 준비를하자. 

먼저 슈퍼 클래스에 서브 클래스를 대신할 정보로 필드를 추가하자. 그 다음 필드를 슈퍼 클래스에서 받도록 하자.

````java
public class Person {
    String name;
    String gender;

    public Person(String name) {
        this.name = name;
    }

    public Person(String name, String gender) {
        this.name = name;
        this.gender = gender.isEmpty() ? "X" : gender;
    }

    public String getName() {
        return name;
    }

    public String getGenderCode() {
        return gender;
    }
}
````

***

## 12.8 슈퍼 클래스 추출하기

### 배경

비슷한 일을 수핼하는 두 클래스가 있다면 공통 부분을 슈퍼 클래스에 넣을 수 있다.

데이터라면 필드를, 동작이라면 메소드를 올리면 된다.

객체 지향을 설명할 때 상속 구조는 현실 세계에서 활용하는 분류 체계에 기초해서 이용을 해야 한다고 말한다. 

하지만 내 경험에 비추어보면 상속은 프로그램이 성장하게 되면서 깨우치게 되고 슈퍼 클래스로 끌어올리고 싶은 공통 요소를 찾았을 때 수행하게 되는 경우가 많다.

슈퍼 클래스 추출하기의 대안으로 __클래스 추출하기 (7.5 절)__ 이 있다. 

어느 것을 선택하느냐는 상속으로 해결할 지, 위임으로 해결할 지에 달렸다.

슈퍼 클래스 추출이 더 쉬우므로 이것을 하는걸 먼저 권장한다. 나중에라도 위임이 더 나은 구조라고 판단되면 __슈퍼 클래스를 위임으로 바꾸기 (12.11 절)__ 을 적용해보자.

### 절차

1. 빈 슈퍼 클래스를 만든다. 원래의 클래스들이 새 클래스를 상속받도록 한다. 

2. 테스트한다.

3. __생성자 본문 올리기 (12.3 절), 메소드 올리기 (12.1 절), 필드 올리기 (12.2 절)__ 을 적용해서 공통 요소를 슈퍼 클래슬 ㅗ옮기자.

4. 서브 클래스에 남은 메소드들을 검토하자. 공통되는 부분이 있다면 함수로 추출해서 메소드에 올리기 (12.1 절) 을 적용하자.

5. 원래 클래스들을 사용하는 코드를 검토하여 슈퍼 클래스의 인터페이스를 사용하게 할지 고민해보자.

### 예시 

다음 두 클래스를 사용하고 있는데 공통된 기능이 있다. 

연간 비용과 월간 비용이라는 개념이 공통되었다. 

예시를 보자. 

```java
public class Employee {
    Long id;
    String name;
    int monthlyCost;

    public Employee(Long id, String name, int monthlyCost) {
        this.id = id;
        this.name = name;
        this.monthlyCost = monthlyCost;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getMonthlyCost() {
        return monthlyCost;
    }

    public int annualCost() {
        return this.monthlyCost * 12;
    }
}
```

```java
public class Department {
    String name;
    Staff staff;

    public Department(String name, Staff staff) {
        this.name = name;
        this.staff = staff;
    }

    public String getName() {
        return name;
    }

    public Staff getStaff() {
        return staff;
    }

    public int length() {
        return staff.length;
    }

    public int totalMonthlyCost() {
        return staff.employees
                .stream()
                .map(e -> e.monthlyCost)
                .reduce(0, Integer::sum);
    }

    public int totalAnnualCost() {
        return totalMonthlyCost() * 12; 
    }
}
```

두 쿨래스로부터 슈퍼 클래스를 추출하면 이 공통된 동작이 더욱 명확할 것 같다. 

우선 빈 슈퍼 클래스를 만들고 두 클래스가 이를 확장하도록 하자. 

그 다음 슈퍼 클래스에 공통의 속성을 넣자. Getter 까지 추가해주자. 


```java
public class Party {
    String name;

    public Party(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

public class Employee extends Party {
    Long id;
    int monthlyCost;

    public Employee(Long id, String name, int monthlyCost) {
        super(name);
        this.id = id;
        this.monthlyCost = monthlyCost;
    }

    public Long getId() {
        return id;
    }

    public int monthlyCost() {
        return monthlyCost;
    }

    public int annualCost() {
        return this.monthlyCost * 12;
    }
}

public class Department extends Party {
    Staff staff;

    public Department(String name, Staff staff) {
        super(name);
        this.staff = staff;
    }

    public Staff getStaff() {
        return staff;
    }

    public int length() {
        return staff.length;
    }

    public int totalMonthlyCost() {
        return staff.employees
                .stream()
                .map(e -> e.monthlyCost)
                .reduce(0, Integer::sum);
    }

    public int totalAnnualCost() {
        return totalMonthlyCost() * 12;
    }
}
```

그 다음 Department 의 월간 비용을 계산하는 부분과 Employee 에서 월간 비용을 계산하는 부분을 보자. 

두 부분은 같은 일을 하는데 기능이 다르다. 

그러므로 이름을 맞추자. 

이는 함수 선언 바꾸기 리팩토링을 적용하면 된다.

그 다음 연간 계산을 하는 방법인데 여기서는 월간 계산 방법만 다르지 로직은 같다.

그러므로 이 부분을 슈퍼 클래스에 메소드 올리기를 통해서 올리자. 

정리하면 다음과 같다. 

```java
public class Party {
    String name;

    public Party(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int monthlyCost() {
        return 0;
    }

    public int annualCost() {
        return monthlyCost() * 12;
    }
}

public class Employee extends Party {
    Long id;
    int monthlyCost;

    public Employee(Long id, String name, int monthlyCost) {
        super(name);
        this.id = id;
        this.monthlyCost = monthlyCost;
    }

    public Long getId() {
        return id;
    }

    public int monthlyCost() {
        return monthlyCost;
    }
}

public class Department extends Party {
    Staff staff;

    public Department(String name, Staff staff) {
        super(name);
        this.staff = staff;
    }

    public Staff getStaff() {
        return staff;
    }

    public int length() {
        return staff.length;
    }

    public int monthlyCost() {
        return staff.employees
                .stream()
                .map(e -> e.monthlyCost)
                .reduce(0, Integer::sum);
    }
}
```

***

## 12.9 계층 합치기
 
### 배경 

클래스 구조를 리팩토링 ㅏㅎ다 보면 기능들을 위로 올리거나 아래로 내리는 일은 다반사다.

예컨대 계층구조도 진화하면서 어떤 클래스는 부모와 너무 비슷해져서 독립적으로 존재해야 할 이유가 사라지기도한다.

그때 바로 그들을 합쳐야 할 시점이다.

### 절차

1. 두 클래스 중 제거할 클래스를 고른다. 

2. 필드 올리기, 내리기 메소드 올리기, 내리기 를 적용해서 하나의 클래스로 만든다.

3. 제거할 클래스를 참조하던 모든 코드가 남겨질 클래스를 참조하도록 바꾼다.

4. 빈 클래스를 제거한다.

5. 테스트한다.

***

## 12.10 서브 클래스를 위임으로 바꾸기 

### 배경

특성에 따라 동작이 달라지는 객체들은 상속으로 표현하는게 자연스럽다.

공통 데이터와 동작은 모두 슈퍼 클래스에 두고 서브 클래스는 자신에 맞게 기능을 추가하거나 오버라이드 하면 된다.

객체 지향에서 이러한 매키니즘은 자연스럽다.

하지만 상속은 단점이 있다.

가장 명확한 단점은 한 번만 쓸 수 있는 카드라는 것이다.

무언가가 달라져야 하는 이유가 여러 개여도 상속은 한 이뮤만 잡을 수 밖에 없다.

예로 사람 객체의 동작을 나이대와 소득 수준에 따라 달라지게 하고 싶다면 서브 클래스는 젊은이와 어르신이 되거나 혹은 부자와 서민이 되어야 한다.

둘 다는 안된다.

또 다른 문제로 상속은 클래스들의 관계를 아주 긴밀하게 결합한다.

부모를 바꾸면 자식들의 기능을 해치기가 쉽기 때문에 주의해야 한다.

그래서 자식들이 슈퍼 클래스를 어떻게 상속해 스는지를 이해해야 한다.

부모와 자식이 서로 다른 모듈에 속하거나 다른 팀에서 구현한다면 문제는 더욱 커진다. 

위임 (Delegate) 는 위의 두 문제를 모두 해결해준다. 

다양한 클래스에 서로 다른 이유로 위임할 수 있다.

위임은 상속보다 결합도가 낮다. __(다시 정리하면 상속의 결합도는 높다. 자식 클래스는 부모 클래스를 이해하고 설계하기 떄문에 부모 클래스의 변경은 자식 클래스에게 많은 변화를 줄 수 있다.)__

유명한 원칙이 있다.

__"상속 보다는 컴포지션을 사용하라!"__

여기서 컴포지션이 위임을 말하는 것이다.

이 말은 상속은 위험하다고 상속을 사용하지 말라고도 하는데 나는 상속을 자주 사용한다.

이렇게 하는 배경에는 __나중에라도 필요하면 언제든 서브 클래스를 위임으로 바꿀 수 있기 때문이다.__

그래서 처음에는 상속으로 접근한 다음, 문제가 생기면 위임으로 갈아탄다.

실제로 이 원칙을 주장한 디자인 패턴 책은 상속과 컴포지션을 함께 사용하는 방법을 설명해준다. __(여기서는 상속의 과용을 설명해준 것.)__

디자인 패턴에 익숙한 사람이라면 이 패턴을 State Pattern 이나 Strategy Pattern 이라고 생각해도 좋다.

구조적으로 보면 두 패턴은 위임 방식으로 계층 구조를 분리해준다.

### 절차

1. 생성자를 호출하는 곳이 많다면 __생성자를 팩토리 함수로 바꾼다. (11.8 절)__ __(한번에 바꿨을 때 부작용 여파를 없애기 위해서.)__

2. 위임으로 활용할 빈 클래스를 만든다. 이 클래스의 생성자는 서브 클래스에 특화된 데이터를 전부 받아야 하며 보통은 슈퍼 클래스를 가리키는 역참조 (Back Reference) 도 필요하다.

3. 위임을 저장할 필드를 슈퍼 클래스에 추가한다.

4. 서브 클래스 생성 코드를 수정하여 위임 인스턴스를 생성하고 위임 필드에 대입해 초기화한다. (이 작업은 팩토리 함수에서 주로 하는데 위임 클래스 생성을 생성자에서 가능하다면 그렇게한다.)

5. 서브 클래스 메소드 중 위임 클래스로 이동할 것을 고른다.

6. __함수 옮기기 (8.1 절)__ 을 적용해 위임 클래스로 옮긴다. 원래 메소드에서 위임하는 코드는 지우지 않는다.

7. 서브 클래스 외부에도 원래 메소드를 호출하는 코드가 있다면 서브 클래스의 위임 코드를 슈퍼 클래스로 옮긴다. 이때 위임이 존재하는지를 검사하는 보호 코드로 감싸야한다.
호출하는 외부 코드가 없다면 원래 메소드는 죽은 코드가 되므로 제거한다. 

8. 테스트한다.

9. 서브 클래스의 모든 메소드가 옮겨질 때까지 과정을 반복한다.

10. 서브 클래스의 생성자를 호출하는 코드를 찾아서 슈퍼 클래스의 생성자를 사용하도록 수정한다.

11. 테스트한다.

12. 서브 클래스를 삭제한다.

### 예시: 서브 클래스가 하나일 때

공연 예약 (Booking) 클래스를 준비했다. 

```java
public class Booking {
    Show show;
    LocalDateTime date;

    public Booking(Show show, LocalDateTime date) {
        this.show = show;
        this.date = date;
    }
    ...
}
```

그리고 ㅜ가 비용을 다양하게 설정할 수 있는 프리미엄 예약 서브 클래스가 있다. (PremiumBooking)

```java
public class PremiumBooking extends Booking {
    Extras extras;

    public PremiumBooking(Show show, LocalDateTime date, Extras extras) {
        super(show, date);
        this.extras = extras;
    }
    ...
}
```

프리미엄 예약 클래스는 슈퍼 클래스를 상속해 제법 많은 걸 변경한다.

다름에 기반한 프로그래밍 방식으로 서브 클래스에서 슈퍼 클래스의 메소드 일부를 오버라이드 하거나 서브 클래스에만 특화된 메소드 몇개가 추가되었다.

여기에서는 몇 개만 짚어보겠다.

첫째, 간단한 오버라이드 메소드가 있다. 

다음 코드처럼 일반 예약은 공연 후 관객과의 대화 시간을 성수기가 아닐때만 제공한다.

```java
// Booking 클래스 
public boolean hasTalkback() {
    return show.talkback && isPeakDay();
}
```

프리미엄 예약용 클래스는 이를 오버라이드 해서 항시 관객과의 대화 시간이 있다.

```java
@Override
public boolean hasTalkback() {
    return true; 
}
```

비슷하게, 가격 결정도 슈퍼 클래스의 메소드를 호출해 추가 요금을 더하는 식으로 오버라이드 한다.

```java
// Booking 클래스
public int basePrice() {
    int result = show.price;
    if (isPeakDay()) result += Math.round(result * 0.15);
    return result;
}
```

```java
// PremiumBooking 클래스
@Override
public int basePrice() {
    return Math.round(super.basePrice() + extras.premiumFee);
}
```

마지막은 슈퍼 클래스에도 없는 PremiumBooking 클래스에서만 제공하는 기능이다.

```java
// PremiumBooking 클래스
public boolean hasDinner() {
    return extras.dinner && !isPeakDay(); 
}
```

이 에는 상속에 잘 들어맞는다.

서브클래스에 대한 지식 없이도 슈퍼 클래스를 이해하는게 가능하고 서브 클래스와 슈퍼 클래스의 차이가 무엇인지 정의되어있다.

현실은 위의 예처럼 완벽하지 않다.

슈퍼 클래스에서는 서브 클래스 없이는 완성되지 않는 불완정한 구조가 될 수 있다. __(슈퍼 클래스에서 기본 뼈대만 잡아주고, 서브 클래스의 오버라이딩이 필요한 경우.)__

그리고 슈퍼 클래스를 수정할 때 굳이 서브 클래스까지 고려할 필요가 없는 게 보통이지만 이 무지로 서브 클래스를 망가뜨리는 상황이 닥칠 수 있다.

__이 문제를 해결하기 위해서는 서브 클래스가 망가지는지 확인해두는 테스트를 만들어주면 상속은 충분한 값어치를 할 수 있다.__

그렇다면 나는 왜 서브 클래스를 위임으로 바꾸려 할까?

상속은 한 번만 사용할 수 있는 도구인데 상속해야할 다른 이유가 생기고 그게 PremiumBooking 클래스의 가치보다 크다면 다른 방식으로 프리미엄 예약을 표현해야 할 것이다.

또한 기본 예약에서 프리미엄 예약으로 동적으로 전환할 수 있도록 할 것이다.`booking.bePremium()` 같은 메소드를 추가하는 식으로 말이다.

이렇게 다양한 이유로 서브 클래스를 위임으로 바꾸는 경우가 생길 숭 ㅣㅆ다.

다음과 같이 두 예약 클래스의 생성자를 호출하는 클라이언트들이 있다고 해보자.

```java
public void example(Show show, LocalDateTime date) {
    Booking booking = new Booking(show, date);
}

public void example2(Show show, LocalDateTime date, Extras extras) {
    PremiumBooking premiumBooking = new PremiumBooking(show, date, extras);
}
``` 

서브 클래스를 제거하려면 수정할 게 많으니 먼저 생성자를 팩토리 함수로 바꿔서 생성자 부분을 캡슐화하자. 

```java
private Booking createBooking(Show show, LocalDateTime date) {
    return new Booking(show, date);
}

private PremiumBooking createPremiumBooking(Show show, LocalDateTime date, Extras extras) {
    return new PremiumBooking(show, date, extras);
}
```

이제 위임 클래스를 새로 만들자. 

위임 클래스의 생성자는 서브 클래스가 사용하던 매개변수와 예약 객체로의 역참조를 매개변수로 받는다. 

역참조가 필요한 이유는 서브 클래스의 메소드 중 슈퍼 클래스에 저장된 데이터를 사용하는 경우가 있어서 이다. 

상속에서는 이를 쉽게 처리하는게 가능하지만 위임에서는 역참조가 있어야 한다. 

```java
public class PremiumBookingDelegate {
    Extras extras;
    Booking booking;

    public PremiumBookingDelegate(Extras extras, Booking booking) {
        this.extras = extras;
        this.booking = booking;
    }
}
```

이제 새로운 위임을 예약 객체와 연결할 때다. 

프리미엄 예약을 생성하는 팩토리 함수를 수정하자.

그 다음 예약 객체를 위임 객체와 연결시키자.

```java

```

구조가 갖춰졌으니 다음은 기능을 옮길 차례다.

PremiumBooking 의 메소드들을 하나씩 PremiumBookingDelegate 로 옮기자.

거기서 Booking 의 기능이 필요하다면 역참조를 이용해서 해결하자. 

모든 기능이 잘 작동하는지 테스트한 후 서브 클래스의 메소드를 삭제한다. 

모두 옮겼다면 서브 클래스를 삭제하자.

위임 클래스는 다음과 같다. 

```java
public class PremiumBookingDelegate {
    Extras extras;
    Booking booking;

    public PremiumBookingDelegate(Extras extras, Booking booking) {
        this.extras = extras;
        this.booking = booking;
    }

    public boolean hasTalkback() {
        return true;
    }

    public int extendPrice() {
        return Math.round(booking.basePrice() + extras.premiumFee);
    }

    public boolean hasDinner() {
        return extras.dinner && !booking.isPeakDay();
    }
}
```

### 예시: 서브 클래스가 여러 개일때

서브 클래스가 여러 개일때도 이 리팩토링을 적용할 수 있다. 

````java
public class Client {
    public Bird createBird(Data data) {
        switch (data.type) {
            case "유럽 제비": 
                return new EuropeanSwallow(data);
            case "아프리카 제비":
                return new AfricanSwallow(data);
            case "노르웨이 파랑 앵무":
                return new NorwegianBlueParrot(data);
            default:
                return new Bird(data);
        }
    }
}
````

````java

public class Bird {
    String name;
    String plumage;
    public Bird(Data data) {
        this.name = data.name;
        this.plumage = data.plumage;
    }

    public String getName() {
        return name;
    }

    public String getPlumage() {
        return plumage;
    }
    
    public int airSpeedVelocity() {
        return 0; 
    }
}
````

````java
public class EuropeanSwallow extends Bird {
    public EuropeanSwallow(Data data) {
        super(data);
    }

    @Override
    public int airSpeedVelocity() {
        return 35;
    }
}
````

````java
public class AfricanSwallow extends Bird {
    int numberOfCounts; 
    
    public AfricanSwallow(Data data) {
        super(data);
        this.numberOfCounts = data.numberOfCounts;
    }

    @Override
    public int airSpeedVelocity() {
        return 40 - 2 * numberOfCounts; 
    }
}
````

````java
public class NorwegianBlueParrot extends Bird {
    int voltage;
    boolean isNailed;

    public NorwegianBlueParrot(Data data) {
        super(data);
        this.voltage = data.voltage;
        this.isNailed = data.isNailed;
    }

    @Override
    public String getPlumage() {
        if (voltage > 100) return "그을렸다.";
        return "예쁘다."; 
    }

    @Override
    public int airSpeedVelocity() {
        return isNailed ? 0 : 10 + voltage / 10;
    }
}
````

이 코드는 야생 (Wild) 와 사육 (Captivity) 조류를 구분짓기 위해 크게 수정할 예정이다.

이 차이를 WildBird 와 CaptiveBird 두 서브 클래스로 모델링 하는 방법도 있다.

하지만 상속은 한번만 사용이 가능하니 야생과 사육 기준으로 나누려면 종에 따른 분류룰 포기해야 한다.

이처럼 서브 클래스가 여러개 관련된 경우라면 한 번에 하나씩, 간단한 것부터 시작한다.

이번 예에서는 유럽 제비가 좋겠다. 

우선 빈 위임 클래스를 만들어보자. 그 다음 여러 각 클래스를 위임 클래스로 뺄 것이므로 그것들의 공통인 특성을 부모 클래스로 만들어두자.

````java
public class EuropeanSwallowDelegate extends SpeciesDelegate {
}
````

그 다음 개별적인 속성은 각각에 이동시키면 되고 공통의 속성은 SpeciesDelegate 로 옮기자.

***

## 12.11 슈퍼 클래스를 위임으로 바꾸기

### 배경

객체 지향 프로그래밍에서 상속은 기존 기능을 재활용하는 강력한 수단이다.

기존 클래스를 상속해 입맛에 맞게 오버라이드 하거나 새 기능을 추가하면 된다.

하지만 상속이 혼란과 복잡도를 키우는 방식으로 이뤄지기도 한다.

자바의 스택 클래스가 그 예다.

자바의 스택은 리스트를 상속하고 있는데 데이터를 저장하고 조회하는 리스트의 기능을 재활용하겠다는 생각이 초래한 결과다.

재활용 관점에서는 좋았지만 이 상속에는 문제가 있다.

리스트의 연산 중 스택에는 적용되지 않는게 많은데도 그 모든 연산이 스택 인터페이스에 그대로 노출되어 있다.

__이 보다는 스택에 리스트 객체를 필드에 두고 필요한 기능만 위임하는 식으로 했다면 더 나을 것이다.__

자바의 스택이 슈퍼 클래스를 위임으로 바꾸는 이번 리팩터링을 적용할 좋은 예다.

__슈퍼 클래스의 기능들이 서브 클래스에 어울리지 않는다면 그 기능들을 상속을 통해 이용하면 안된다는 신호다.__

제대로 된 상속이라면 서브 클래스가 슈퍼 클래스의 모든 기능을 사용해야 하고, 서브 클래스의 인스턴스를 슈퍼 클래스의 인스턴스로도 취급할 수 있어야한다.

이외에 서브 클래스 방식 모델링이 합리적일 때도 슈퍼 클래스를 위임으로 바꾸기도 한다.

슈퍼 / 서브 클래스가 아주 강하게 결합되어 있어서 슈퍼 클래스를 수정하면 서브 클래스가 망가지기 쉬울 경우를 말한다.

### 절차 

1. 슈퍼 클래스 객체를 참조하는 필드를 서브 클래스에 만든다. (이번 리팩토링이 끝나면 슈퍼 클래스가 위임 객체가 될 것이므로 위임 참조라고 부르겠다.) 위임 참조를 새로운 슈퍼 클래스 인스턴스로 초기화한다.

2. 슈퍼 클래스의 동작 각각에 대응하는 전달 함수를 서브 클래스에 만든다. (물론 위임 참조로 전달한다.) 서로 관련된 함수끼리 그룹을 묶어서, 그룹을 하나씩 만들 때마다 테스트한다.

3. 슈퍼 클래스의 동작 모두가 전달 함수로 오버라이드 되었다면 상속 관계를 끊는다.

### 예시 

고대 스크롤 (Scroll) 을 보관하고 있는 오래된 도서관 예제이다.

스크롤의 상세정보는 이미 카탈로그 (Catalog) 로 분류되어 있고 스크롤에는 여러가지 태그가 붙어 있었다.

```java
public class CatalogItem {
    Long id;
    String title;
    List<String> tags;

    public CatalogItem(Long id, String title, List<String> tags) {
        this.id = id;
        this.title = title;
        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean hashTag(String tag) {
        return tags.contains(tag);
    }
}
```

스크롤에는 정기 세척 이력이 필요했고 그래서 카탈로그 아이템을 확장해서 세척 관련 데이터를 추가 사용했다.

```java
public class Scroll extends CatalogItem {
    LocalDateTime lastCleaned;

    public Scroll(Long id, String title, List<String> tags, LocalDateTime dateLastCleaned) {
        super(id, title, tags);
        lastCleaned = dateLastCleaned;
    }

    public boolean needsCleaning(LocalDateTime targetDate) {
        int threshold = hashTag("revered") ? 700 : 1500;
        return daysSinceLastCleaning(targetDate) > threshold;
    }

    private long daysSinceLastCleaning(LocalDateTime targetDate) {
        return lastCleaned.until(targetDate, ChronoUnit.DAYS);
    }
}
```

여기서는 모델링이 잘못되었다.

물리적인 스크롤과 논리적인 카랕로그 아이템은 차이가 있다.

스크롤은 사본이 여러개일 수 있는데 카탈로그 아이템은 하나일 수 있기 때문이다.

그러므로 모델의 관계가 잘못되어있으므로 이 사이를 끊자.

먼저 Scroll 에서 CatalogItem 을 참조하도록 슈퍼 클래스의 인스턴스를 Scroll 에서 만들자.

```java

public class Scroll extends CatalogItem {
    CatalogItem catalogItem; 
    LocalDateTime lastCleaned;

    public Scroll(Long id, String title, List<String> tags, LocalDateTime dateLastCleaned) {
        super(id, title, tags);
        catalogItem = new CatalogItem(id, title, tags); 
        lastCleaned = dateLastCleaned;
    }

    ...
}
```

그 다음 카탈로그아이템의 기능들을 모두 가져오자. 여기서 위임 참조를 이용해서 사용하도록 하자.

```java
public class Scroll extends CatalogItem {
    ...
    public Long getId() {
        return catalogItem.getId();
    }
    
    public String getTitle() {
        return catalogItem.getTitle();
    }
    
    public boolean hashTag(String tag) {
        return catalogItem.hashTag(tag);
    }
}
```

이제 상속관계를 끊자.

```java
public class Scroll {
    CatalogItem catalogItem;
    LocalDateTime lastCleaned;

    public Scroll(Long id, String title, List<String> tags, LocalDateTime dateLastCleaned) {
        catalogItem = new CatalogItem(id, title, tags);
        lastCleaned = dateLastCleaned;
    }

    public Long getId() {
        return catalogItem.getId();
    }

    public String getTitle() {
        return catalogItem.getTitle();
    }

    public boolean hashTag(String tag) {
        return catalogItem.hashTag(tag);
    }

    public boolean needsCleaning(LocalDateTime targetDate) {
        int threshold = hashTag("revered") ? 700 : 1500;
        return daysSinceLastCleaning(targetDate) > threshold;
    }

    private long daysSinceLastCleaning(LocalDateTime targetDate) {
        return lastCleaned.until(targetDate, ChronoUnit.DAYS);
    }
}
```

여기서 리팩터링을 조금만 더 해보자.

여기까지 리팩토링을 하면 카탈로그 아이템의 역할이 스크롤의 속성으로 옮겨졌다.

각각의 스크롤들은 하나의 카탈로그 아이템을 가지게 되었다.

하지만 도서관에 보관된 사본 스크롤 여섯 개 모두 단 하나의 카탈로그 아이템만을 가진다.

여기서의 모델링은 카탈로그 아이템들이 모두 값으로서 여러개가 존재되는 것이다.

그러므로 __값을 참조로 바꾸기 (9.5 절)__ 을 적용하자.

원래의 상속 구조에서는 스코롤이 자신의 ID 를 카탈로그 아이템 ID 필드에 저장했다. 

여기서는 카탈로그 아이템의 ID 를 빌려쓰지말고 자신만의 ID 를 새로 만들어야 한다.

일단 임시로 이렇게 해주자. 

```java
public class Scroll {
    Long id; 
    CatalogItem catalogItem;
    LocalDateTime lastCleaned;

    public Scroll(Long id, String title, List<String> tags, LocalDateTime dateLastCleaned) {
        this.id = id; 
        catalogItem = new CatalogItem(null, title, tags);
        lastCleaned = dateLastCleaned;
    }

    ...

}
```

값을 참조로 바꾸기 위해서는 저장소를 통해서 참조 값을 찾아야 한다.

저장소를 찾으면 저장소에서 값을 찾기 위한 색인 값을 또 전달해줘야한다.

그리고 안쓰는 필드를 지우면 다음과 같다. 

````java
public class Scroll {
    Long id;
    CatalogItem catalogItem;
    LocalDateTime lastCleaned;

    public Scroll(Long id, LocalDateTime dateLastCleaned, CatalogRepository catalogRepository, Long catalogId) {
        this.id = id;
        catalogItem = catalogRepository.get(catalogId); 
        lastCleaned = dateLastCleaned;
    }
    
    ... 
}
```` 






 





















