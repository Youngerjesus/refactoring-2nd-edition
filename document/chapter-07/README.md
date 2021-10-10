# 캡슐화

***

모듈을 분리하는 가장 중요한 기준은 필요없는 정보는 드러내지 않는 것이다. 

즉 모듈에서 노출되야 할 부분만 정확하게 노출하는 것이다. 

이러한 방법은 __캡슐화를 통해서 가능하다.__ 

대표적인 방법으로는 __레코드 캡슐화 하기 (7.1절)__ 과 __컬렉션 캡슐화하기 (7.2절)__ 로 캡슐화해서 숨기는게 가능하다.

심지어 기본형 데이터 구조도 __기본형을 객체로 바꾸기 (7.3절)__ 로 캡슐화할 수 있다. 

__클래스는 본래 정보를 숨기는 용도로 설계되었다.__

앞 장에선 이 클래스를 만들기 위한 방법들을 소개했는데 __여러 함수를 클래스로 묶기 (6.9절)__ 과 __클래스 추출하기 (7.5절)__ 이 있다.  

그리고 필요없는 클래스를 지우는 방법인 __클래스 인라인하기 (7.6절)__ 도 있다. 

클래스는 내부 정보 뿐 아니라 연결 관계를 숨기는 데도 유용한데 이를 위한 방법으로는 __위임 숨기기 (7.7절)__ 이 있다. 

물롱 위임 숨기기를 통해 필요없는 중개자가 너무 많아지는 경우에는 __중개자 제거하기 (7.8절)__ 도 필요하다. 

가장 큰 캡슐화 단위는 클래스와 모듈이지만 함수도 캡슐화가 가능하다.

때로는 알고리즘을 통째로 바꿔야 하는 경우가 있는데 __함수 추출하기 (6.1절)__ 로 추출한 후 __알고리즘 교체하기 (7.9절)__ 을 적용하면 된다. 

***

## 7.1 레코드 캡슐화 하기

### 배경 

대부분의 프로그래밍 언어에서는 데이터 레코드를 표현하는 구조를 제공한다. __(자바에서는 클래스만 있지 레코드를 지원하지는 않는듯)__

자바스크립트 기준으로 다음과 같은 것이 레코드를 말한다. 

```json
organization = {
  "name" : "애크미 구스베리",
  "country" : "GB"
}
```

레코드를 다룰 때 주의할 점은 계산해서 얻을 수 있는 값과 그렇지 않은 값은 구별해야 한다는 점이다. 

이러한 이유 때문에 가변 데이터에서는 레코드 대신 클래스를 이용하는 걸 선호한다. 

클래스를 이용하면 어떠한 데이터를 제공해주는지 메소드를 보면 바로 알 수 있다. __(계산해서 도출하는 필드와 관계없이)__

데이터가 불변 데이터인 경우에는 값만 제공해주면 되므로 굳이 클래스를 쓰지는 않는다. 그냥 모든 필드를 레코드에서 모두 제공해주도록 한다. 

레코드 구조는 크게 두 가지로 구분하는게 가능하다. 

하나는 필드 이름을 노출시키는 형태가 있고 후자는 주로 해시맵과 같은 라이브러리를 통해서 감싸는 형태다. 

라이브러리를 이용하면 필드를 보기 위해서 처음 그 라이브러리를 처음 생성해서 쓰는 곳을 참조해야 하는데 이게 너무 불편한것 같으면 클래스를 쓰는 것도 추천한다. 그러면 명시적으로 어떠한 데이터를 제공해주는지 알 수 있으니까.

그리고 코드를 작성하다보면 JSON 이나 XML 같은 포맷으로 직렬화 하는 경우가 많은데 이런 구조는 캡슐화 하는게 좋다. 출력하는 형식이 나중에 바뀔 수 있으니까. __(캡술화라는게 결국에 필요한 데이터만 제공해주면 되고 그 과정은 숨기는 것이다.)__

### 절차 

1. 레코드를 담은 변수를 캡슐화(6.6 절) 한다. 


2. 레코드를 감싼 단순한 클래스로 해당 변수의 내용을 교체한다. 그러면서 이 클래스에 원본 레코드를 반환하는 접근자도 정의하도록 하고 변수를 사용하는 함수들은 이 접근자를 이용하도록 한다. 

3. 테스트 한다.

4. 원본 레코드 대신 새로 정의한 클래스 타입의 객체를 반환하는 함수들을 새로 만든다. 

5. 레코드를 반환하는 예전 함수를 새로 만든 함수로 바꾼다. 

6. 한 부분을 바꿀 때마다 테스트한다. 

### 예시 

여기서 예시는 자바스크립트의 레코드를 기준이므로 자바를 이용하는 경우랑은 달라서 생략한다.

***

## 7.2 컬렉션 캡슐화하기 

### 배경 

__나는 가변 데이터는 모두 캡슐화를 하는 편이다.__

이렇게 할 경우 데이터들이 언제 어떻게 수정되는지 추적하기 좋기 때문이다. __(데이터 변경 자체는 추적하기 굉장히 힘들다.)__

컬렉션 구조를 사용할 때 주의할 점이 있는데 컬렉션안의 요소를 변경하는 작업이 필요한건 클래스 메소드로 따로 만들어 두는 것이다. 예로 add() remove() 같은 메소드들 말이다. 

__이 말은 컬렉션 자체를 반환하는 것을 막는다는 건 아니다.__ 

컬렉션 자체 반환을 막도록 하면 컬렉션에서 사용가능한 다채로운 인터페이스를 사용하는데 제한이 걸리기 때문이다. 

그래서 컬렉션 변경과 같은 작업은 클래스 메소드를 통해서 이뤄지도록 하고 

컬렉션을 반환하는 getter 함수는 컬렉션 자체를 반환하는 것이 아니라 복사본을 반환하도록 하자. 그리고 컬렉션을 통째로 변경할 수 있는 세터는 없애도록 하자. 없앨 수 없으면 인수로 전달받은 컬렉션을 복제본으로 가져와서 원본에 영향이 안가도록 하자.

물론 원본에 영향이 안가는게 이상하다고 느낄 순 있지만 대부분의 프로그래머는 이 패턴을 이용하니까 이상하게 느껴지지는 않을 것이다. 물론 성능적으로 크게 문제가 된다면 이를 적용하면 안되겠지만 그럴 일은 거의 없다. 

이 방법외에도 컬렉션을 읽기 전용으로 해놓고 사용하는 방법도 있다. 그러면 문제가 되는 상황은 거의 없을 것이니. 

여기서 중요한 점은 코드베이스에서 일관성을 주는 것이다. 한 패턴을 적용하기로 했다면 통일하자. 

### 절차 

1. 아직 컬렉션을 캡슐화하지 않았다면 변수 캡슐화하기 (6.6절) 부터 한다.

2. 컬렉션에 원소를 추가/제거 하는 함수를 만든다. __(컬렉션을 통째로 변경하는 세터를 모두 제거하고 인수로 받은 컬렉션은 복제본을 사용하도록 하자.)__

3. 정적 검사를 수행한다.

4. 컬렉션을 참조하는 부분을 찾고 하나씩 클래스로 바꾸자. 하나씩 수정할 때마다 테스트한다.

5. 컬렉션 게터를 수정해서 원본 내용을 수정할 수 없는 읽기 전용 프록시나 복제본을 반환하도록 한다. 

6. 테스트한다. 

### 에시 

수업 (course) 목록을 필드로 지니고 있는 Person 클래스를 예로 살펴보자.

```java
public class Person {
    protected String name; 
    protected List<Course> courses = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}
```

````java
public class Course {
    protected String name; 
    protected boolean isAdvanced;

    public String getName() {
        return name;
    }

    public boolean isAdvanced() {
        return isAdvanced;
    }
}
````

이렇게 개발자가 캡술화를 하면 되었다고 생각할 수 있겠지만 문제가 있다.

여기에는 일단 컬렉션에 setter 가 있다는 점과 컬렉션을 제어하는 메소드가 없다. 그러므로 이를 반영해야한다. 

````java
public class Person {
    protected String name;
    protected List<Course> courses = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    public void removeCourse(int index) {
        try {
            this.courses.remove(index);   
        } catch (IndexOutOfBoundsException e) {
            throw e; 
        }
    }
}
````

여기서는 컬렉션안의 요소를 제거할 때 에러가 발생하는 경우를 잡을 수 있도록 해놨다. 상황에 맞게 호출자가 대응할 여지를 남겨두기 위해서다.

이렇게 바꾼 후 컬렉션을 직접 다루던 코드가 있다면 방금 추가한 클래스와 메소드를 이용하도록 바꾸면 된다. 

그 다음 getter 에서 원본을 그대로 노출시키는게 아니라 복사본을 주도록 바꾸자. 

```java
public class Person {
    protected String name;
    protected List<Course> courses = new ArrayList<>();

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<Course> getCourses() {
        return new ArrayList<>(courses);
    }

    public void addCourse(Course course) {
        this.courses.add(course);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
    }

    public void removeCourse(int index) {
        try {
            this.courses.remove(index);
        } catch (IndexOutOfBoundsException e) {
            throw e;
        }
    }
}
```

***

## 7.3 기본형을 객체로 바꾸기 

### 배경

개발 초기에는 단순한 정보나 문자를 표현했던 데이터들이 프로그램 규모가 커질수록 간단해지지 않아진다.

예컨대 전화번호 같은 문자열 데이터가 나중에는 포매팅이나 지역 코드 추출과 같은 특별한 동작이 필요해질 수 있다.

나는 데이터가 단순히 출력 이상의 기능이 필요해진다면 클래스로 바꾼다.

이렇게 바꿈으로써 나중에 특별한 동작이 필요해지면 이 클래스에 추가하면 되므로 유용하다.

### 절차

1. 아직 변수를 캡슐화하지 않았다면 캡슐화 (6.6절) 부터 한다.

2. 단순한 값 클래스 (Value Class) 를 만든다. 생성자는 기존 값을 인수로 받아서 자장하고 이 값을 반환하는 Getter 를 추가한다.

3. 정적 검사를 수행한다.

4. 값 클래스의 인스턴스의 세터과 게터의 수정이 필요하면 변경한다.

5. 테스트한다.

### 예시

단순한 주문 (Order) 클래스가 있다고 생각해보자.

이 클래스는 우선순위 (Priority) 라는 항목이 있고 이것은 지금 문자열로 받는 구조다.

```java
public class Order {
    protected String priority;

    public Order(String priority) {
        this.priority = priority;
    }
}
```

여기서 먼저 데이터를 객체로 다루기 전에 캡슐화를 먼저 해주자. 

```java
public class Order {
    protected String priority;

    public Order(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
```

캡슐화를 했다면 이제 우선순위 값을 표현하는 클래스를 만들자.

````java
public class Priority {
    protected String value;

    public Priority(String value) {
        this.value = value;
    }
    
    public String toString() {
        return value; 
    }
}
````

여기서는 getter 대신에 toString() 이라는 메소드로 표현했는데 클라이언트 입장에서는 이게 더 자연스러울 수 있다. __(결국 문자열로 표현 요청을 한 것이기 때문에.)__

그 다음 Priority 클래스를 사용하도록 Order 객체를 바꾸자.

````java
public class Order {
    protected Priority priority;

    public Order(String priority) {
        this.priority = new Priority(priority);
    }

    public String getPriorityString() {
        return priority.toString();
    }

    public void setPriority(String priority) {
        this.priority = new Priority(priority);
    }
}
````

여기서는 getPriority() 메소드도 String 이 붙는게 더 자연스러우므로 바꿔준다. 

이후에 우선순위를 비교하는 작업 같은 것들이 요구사항으로 들어올 수 있다. 그런 경우에는 Priority 클래스에 동작으로 추가해주면 된다. 

***

## 7.4 임시 변수를 질의 함수로 바꾸기

### 배경 

함수 안에서 어떤 코드의 결과값을 뒤에서 다시 참조할 목적으로 임시 변수를 사용한다.

임시 변수는 계산된 결과를 반복적으로 계산하지 않기 위해서 사용하는데 이는 함수로 만들어두는게 유용한 경우가 있다. (그래서 나는 여러 곳에서 똑같은 방식으로 계산되는 변수를 보면 이를 함수로 추출해놓는다.)

주로 함수를 추출할 때 임시 변수가 문제가 되는데 (파라미터로 전달해야 하니까) 함수의 파라미터 수를 줄이는데 기여를 한다. 

이 기법을 사용할 때 주의할 점은 변수를 스냅샷 처럼 사용하는 경우 즉 여러번의 대입을 하는 경우에는 사용하면 안된다는 점이다.

### 절차

1. 변수가 사용되기 전에 값이 확실히 결정되는지, 즉 매번 다른 결과를 내지 않는지 확인하자.

2. 읽기전용으로 만들 수 있는 변수는 읽기전용으로 만든다.

3. 테스트한다.

4. 변수 대입문을 함수로 추출한다.

5. 테스트한다.

6. 변수 인라인하기 (6.4절) 로 임시 변수를 제거한다.

### 예시

간단한 주문 (Order) 클래스가 있다.

````java
public class Order {
    protected int quantity;
    protected Item item;

    public Order(int quantity, Item item) {
        this.quantity = quantity;
        this.item = item;
    }
    
    public double getPrice() {
        int basePrice = quantity * item.price;
        double discountFactor = 0.98; 
        
        if (basePrice > 1000) discountFactor -= 0.03; 
        return basePrice * discountFactor;
    }
}
````

여기서 임시 변수인 basePrice 와 discountFactor 를 메소드로 바꾸는 리팩토링을 해보자.

읽기전용 변수가 아닌 경우에는 메소드로 빼기 어렵기 떄문에 임시변수에 final 키워드를 통해 읽기전용인지 확인해보자.

먼저 basePrice 변수인 경우에는 읽기전용으로 사용할 수 있기 때문에 금방 빼낼 수 있다. 

````java
public class Order {
    protected int quantity;
    protected Item item;

    public Order(int quantity, Item item) {
        this.quantity = quantity;
        this.item = item;
    }

    public double getPrice() {
        double discountFactor = 0.98;

        if (getBasePrice() > 1000) discountFactor -= 0.03;
        return getBasePrice() * discountFactor;
    }

    private int getBasePrice() {
        return quantity * item.price;
    }
}
```` 

다음으로 빼낼 변수는 discountFactor 인데 이는 대입하는 경우가 있으므로 이 부분까지 고려해서 함수를 추출해야한다. 

```java
public class Order {
    protected int quantity;
    protected Item item;

    public Order(int quantity, Item item) {
        this.quantity = quantity;
        this.item = item;
    }

    public double getPrice() {
        return getBasePrice() * getDiscountFactor();
    }

    private int getBasePrice() {
        return quantity * item.price;
    }

    private double getDiscountFactor() {
        double discountFactor = 0.98;
        if (getBasePrice() > 1000) discountFactor -= 0.03;
        return discountFactor;
    }
}
```

***

## 7.5 클래스 추출하기 

### 배경 

클래스는 반드시 명확하게 추상화하고 주어진 소수의 역할만 수행해야한다.

하지만 실무에서는 주어진 클래스에 데이터와 동작이 계속해서 추가되면서 커지는 경우가 많다. 

역할이 많아지고 데이터와 메소드가 많은 클래스는 이해하기 어렵다. 

그러므로 메소드와 데이터를 따로 묶을 수 있다면 클래스를 분리하라는 신호다. 

또 함께 변경되는 일이 많거나 서로 의존하는 데이터가 많다면 이도 분리할 수 있다는 신호다. 

### 절차

1. 클래스의 역할을 분리하는 방법을 정한다.

2. 분리될 역할을 담당할 클래스를 새로 만든다.

3. 원래 클래스의 생성자에서 새로운 클래스의 인스턴스를 생성하여 필드에 저장해둔다.

4. 분리될 역할에 필요할 필드들을 새 클래스로 옮긴다. 하나씩 옮길 때마다 테스트한다.

5. 메소드들도 새 클래스로 옮긴다. 하나씩 옮길 때마다 테스트한다.

6. 양쪽 클래스의 인터페이스를 살펴보면서 메소드를 제거하고 이름도 새로운 환경에 맞게 바꾼다.

7. 새 클래스를 외부로 노출할 지 정한다.

### 예시 

단순한 Person 클래스를 예로 준비했다. 

````java
public class Person {
    protected String name;
    protected String officeAreaCode;
    protected String officeNumber;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficeAreaCode() {
        return officeAreaCode;
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.officeAreaCode = officeAreaCode;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
}
````

여기서 전화번호를 별도의 클래스로 뽑는 걸 생각해볼 수 있다.

그러므로 TelephoneNumber 클래스를 정의한다.

```java
public class TelephoneNumber {
}
```

이후에 TelephoneNumber 로 필드를 하나씩 옮겨본다. 

Person 클래스에 TelephoneNumber 필드를 추가하고 여기에다가 officeAreaCode 필드를 옮겨보자. 

```java
public class Person {
    protected String name;
    protected String officeNumber;
    protected TelephoneNumber telephoneNumber;

    public Person() {
        telephoneNumber = new TelephoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOfficeAreaCode() {
        return telephoneNumber.officeAreaCode;
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.telephoneNumber.officeAreaCode = officeAreaCode;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
}
```

```java
public class TelephoneNumber {
    protected String officeAreaCode;
}
```

이렇게 필드를 모두 옮겨보자. 

````java
public class Person {
    protected String name;
    protected TelephoneNumber telephoneNumber;

    public Person() {
        telephoneNumber = new TelephoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOfficeAreaCode() {
        return telephoneNumber.officeAreaCode;
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.telephoneNumber.officeAreaCode = officeAreaCode;
    }

    public String getOfficeNumber() {
        return this.telephoneNumber.officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.telephoneNumber.officeNumber = officeNumber;
    }
}
````

```java
public class TelephoneNumber {
    protected String officeAreaCode;
    protected String officeNumber;
}
```

이후에는 메소드도 옮겨보자. 

```java
public class Person {
    protected String name;
    protected TelephoneNumber telephoneNumber;

    public Person() {
        telephoneNumber = new TelephoneNumber();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOfficeAreaCode() {
        return telephoneNumber.getOfficeAreaCode();
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.telephoneNumber.setOfficeAreaCode(officeAreaCode);
    }

    public String getOfficeNumber() {
        return this.telephoneNumber.getOfficeNumber();
    }

    public void setOfficeNumber(String officeNumber) {
        this.telephoneNumber.setOfficeNumber(officeNumber);
    }
}
```

```java
public class TelephoneNumber {
    protected String officeAreaCode;
    protected String officeNumber;

    public String getOfficeAreaCode() {
        return officeAreaCode;
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.officeAreaCode = officeAreaCode;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
}
```

이후에는 메소드의 이름을 적절하게 바꾸자. 

여기서 TelephoneNumber 이라는 클래스를 맥락으로 주고 있으므로 굳이 office 라는 단어를 사용할 필요는 없다.

```java
public class TelephoneNumber {
    protected String areaCode;
    protected String number;

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
```

이후에 전화번호와 관련된 동작이 필요하다면 여기에다가 추가하면된다. 

***

## 7.6 클래스 인라인 하기

### 배경

클래스 인라인 하기는 클래스 추출하기의 반대되는 리팩토링 기법이다.

나는 더 이상 제 역할을 하지 못하는 클래스가 있다면 인라인 해버린다. 

주로 역할을 옮기는 리팩토링 이후 남은 역할이 거의 없을 때 이 클래스를 가장 많이 사용하는 클래스로 옮긴다. 

두 클래스의 기능을 다시 배분하고 싶을 때 인라인 하는 기법을 사용하기도 한다.

애매한 역할을 하는 두 클래스가 있다면 그것들을 합쳐서 새로운 클래스를 추출 (7.5절) 하는게 더 나을 수 있기 때문이다.

### 절차

1. 소스 클래스 (인라인 하려는 클래스) 의 public 메소드를을 타겟 클래스에 생성한다. 

2. 소스 클래스의 메소드를 사용하는 코드를 모두 타겟 클래스의 위임 메소드를 사용하도록 바꾼다. 하나씩 바꿀 때마다 테스트를 한다.

3. 소스 클래스의 메소드와 필드를 모두 타겟 클래스로 옮긴다. 하나씩 옮길 때마다 테스트한다.

4. 소스 클래스를 삭제한다.

### 예시 

배송 추적 정보를 표현하는 TrackingInformation 클래스가 있다고 하자.

````java
public class TrackingInformation {
    protected String shippingCompany;
    protected String trackingNumber;

    public String display() {
        return String.format("%s: %s", shippingCompany, trackingNumber);     
    }
    
    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
````

이 클래스는 배송 (shipment) 클래스의 일부처럼 사용된다. 

```java
public class Shipment {
    protected TrackingInformation trackingInformation; 
    
    public String trackingInfo() {
        return trackingInformation.display(); 
    }

    public TrackingInformation getTrackingInformation() {
        return trackingInformation;
    }

    public void setTrackingInformation(TrackingInformation trackingInformation) {
        this.trackingInformation = trackingInformation;
    }
}
```

여기서 TrackingInformation 클래스가 제 역할을 하지 못한다고 판단해 인라인하려고 한다.

여기서 먼저 기존의 TrackingInformation 에서 사용하는 메소드들을 모두 Shipment 클래스에 옮긴다.

그 다음 TrackingInformation 의 모든 요소를 옮긴다.

```java
public class Shipment {
    protected TrackingInformation trackingInformation;
    protected String shippingCompany;
    protected String trackingNumber;

    public String display() {
        return String.format("%s: %s", shippingCompany, trackingNumber);
    }
    
    public String trackingInfo() {
        return trackingInformation.display();
    }

    public TrackingInformation getTrackingInformation() {
        return trackingInformation;
    }

    public void setTrackingInformation(TrackingInformation trackingInformation) {
        this.trackingInformation = trackingInformation;
    }

    public String getShippingCompany() {
        return shippingCompany; 
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany; 
    }

    public String getTrackingNumber() {
        return trackingNumber; 
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber; 
    }
}
```

그 다음 메소드를 하나씩 인라인 한다. 

다 옮겼다면 TrackingInformation 클래스를 지운다.

***

## 7.7 위임 숨기기

### 배경

모듈화 설계를 제대로 하는 핵심은 캡슐화다.

캡슐화는 모듈이 노출하는 요소롤 제한해서 꼭 필요한 부분을 위주로 협력하도록 해준다.

캡슐화가 잘 되어 있다면 무언가를 변경할 때 함께 고려해야 할 모듈 수가 적어져서 코드를 변경하기 쉬워진다.

예로 객체가 다른 객체의 메소드를 호출할려면 그 객체를 알아야 한다. 근데 호출 당하는 객체의 인터페이스가 변경되면 그 객체를 알고있는 모든 객체가 변경해야한다.

이런 경우가 발생할 수 있다면 그 객체를 노출시키지 않으면 된다. 숨기면 된다. 그러면 아무런 영향을 받지 않는다.

이렇게 객체가 다른 객체를 알면 안되는 경우 즉 객체와 다른 객체가 결합하면 안되는 경우에 이 기법을 쓰면 좋다.

### 절차

1. 위임 객체의 각 메소드에 해당하는 위임 메소드를  서버 객체에 생선하다. (서버 객체가 대신 호출해주는 구조다.)

2. 클라이언트가 위임 객체 대신 서버를 호출하도록 변경한다. 하나씩 변경할 때마다 테스트를 한다.

3. 모두 수정했다면 서버로부터 위임 객체를 얻는 접근자를 제거한다.

4. 테스트한다.

### 예시

사람 (person) 과 사람이 속한 부서 (department) 가 있다고 하자.

```java
public class Person {
    protected String name;
    protected Department department; 
    
    public Person(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
```

```java
public class Department {
    protected int chargeCode;
    protected Person manager;

    public int getChargeCode() {
        return chargeCode;
    }

    public void setChargeCode(int chargeCode) {
        this.chargeCode = chargeCode;
    }

    public Person getManager() {
        return manager;
    }

    public void setManager(Person manager) {
        this.manager = manager;
    }
}
```

클라이언트에서 어떤 사람이 속한 부서의 관리자를 알고 싶다고 하자.

그러려면 부서 객체를 얻어와야한다.

즉 다음과 같이 타고가야한다.

```java
person.department.manager
```

항상 부서 클래스를 통해서 매니저를 조회하는데 이런 의존성을 줄이고 싶다면 사람 클래스에 간단히 위임 메소드를 만들면 된다.

```java
// Person 클래스 
public Person manager() {
    return department.getManager(); 
}
```

그리고 Person 객체에서 부서를 조회하는 메소드를 지우자.

***

## 7.8 중개자 제거하기

### 배경 

위임 숨기기의 반대되는 리팩토링이다.

위임 숨기기는 접근하려는 객체를 제한하는 캡슐화를 제공하는 이점으로 불필요한 결합이나 의존성을 제거해주는 이점이 있다.

근데 만약 클래스에 위임이 너무 많다면 그냥 접근을 허용하도록 하는게 더 나을 수도 있다. 

즉 결합을 해야하는 구조라면 결합을 하는게 나을 수 있다. 

객체가 단순히 중개자 (middle man) 역할만 해준다면 이 리팩토링 기법을 고려해보자. 

### 절차

1. 위임 객체를 얻는 게터를 만든다.

2. 위임 메소드를 호출하는 클라이언트가 이 게터를 거치도록 수정한다.

3. 하나씩 바꿀 때마다 테스트를 진행한다.

4. 모두 수정했다면 위임 메소드를 삭제한다.

### 예시

이전 예시와 마찬가지로 자신이 속한 부서 (Department) 가 있고 이 객체를 통해 관리자 (Manager) 를 찾는 사람 (Person) 클래수가 있다고 살펴보자.

```java
public class Person {
    protected String name;
    protected Department department;

    public Person(String name) {
        this.name = name;
    }
    
    public void setDepartment(Department department) {
        this.department = department;
    }

    public Person manager() {
        return department.getManager();
    }
}
```

즉 Person 객체에서 department 를 거치지 않고 Manager 를 조회하는 위임 메소드가 있다. 

여기서 중개자를 제거하도록 해보자. 

그러려먼 먼저 Person 객체에서 department 를 조회하는 getter 를 만들어야 한다.

```java
public class Person {
    protected String name;
    protected Department department;

    public Person(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Person manager() {
        return department.getManager();
    }
}
```

이제 클라이언트에서는 이 Department 객체를 통해서 Manager 에 접근할 수 있다.

이제 manager() 메소드는 필요없으므로 지우자. 

```java
public class Person {
    protected String name;
    protected Department department;

    public Person(String name) {
        this.name = name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}
```

***

## 7.9 알고리즘 교체하기

### 배경

어떤 목적을 달성하는 방법은 여러가지가 있다.

그 중에선 분명 더 나은 방법이 있을 것이다.

나는 이렇게 더 나은 방법을 찾아내면 복잡한 기존의 방법을 걷어내고 코드를 간명한 방식으로 고친다.

리팩토링하면 복잡한 대상을 단순한 단위로 나누는게 가능하지만 이렇게 때로는 알고리즘 전체를 걷어내고 훨씬 간결한 알고리즘으로 바꿔야 할 때가 있다.

알고리즘을 살짝 다르게 동작하도록 바꾸고 실을 때도 통쨰로 바꾼후에 처리하면 더 간단하게 할 수 있다.

이 방법을 하기전에는 만드시 메소드를 가능한 잘게 나눴는지 확인하자. 거대하고 복잡한 알고리즘은 교체하기 어려우므로.

### 절차

1. 교체할 코드를 함수 하나에 모운다.

2. 이 함수만을 이용해 동작을 검증하는 테스트를 마련한다.

3. 대체할 알고리즘을 준비한다.

4. 정적 검사를 수행한다.

5. 기존 알고리즘과 새 알고리즘의 결과를 비교하는 테스트를 수행한다. 두 결과가 같다면 리팩토링이 끝난다. 그렇지 않다면 기존 알고리즘을 참고해서 새 알고리즘을 테스트하고 디버깅한다. __(즉 잠시동안 두 가지의 알고리즘이 있는것이다.)__






 











 
   