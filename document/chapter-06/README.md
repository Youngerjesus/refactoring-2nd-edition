# 기본적인 리팩토링

***

이제부터 리팩토링의 기법들을 하나씩 소개할건데 가장 기본적이고 많이 사용하는 리팩토링부터 배워보자.

내가 가장 많이 사용하는 리팩토링 기법은 __함수 추출하기 (6.1절)__ 과 __변수 추출하기 (6.3절)__ 이다. 

리팩토릭은 원래 코드를 변경하는 작업인 만큼 이 두 리팩토링을 반대로 변경하는 기법도 있다. __함수 인라인하기 (6.2절)__ 과 __변수 인라인하기 (6.4절)__ 도 자주 사용한다. 

__추출 한다는 건 결국 이름짓기 이다.__

코드 이해도가 높아지다 보면 이름을 바꿔야 할 때가 많다. 

__함수 선언 바꾸기 (6.5절)__ 는 함수의 이름을 변경할 때, 함수의 인수를 추가하거나 제거할 때 많이 쓰인다. 

바꿀 대상이 변수라면 __변수 이름 바꾸기 (6.7절)__ 기법을 사용하고 이는 __변수 캡슐화하기 (6.8절)__ 과 관련이 깊다.

자주 함께 뭉쳐다니는 인수들은 __매개변수 객체 만들기 (6.8절)__ 기법을 적용해서 객체를 하나로 묶으면 편리할 때가 많다.

이렇게 이름을 짓거나 바꾸는 건 가장 기본적인 리팩토링이다. 

이 다음으로는 함수를 만들면 함수들을 모듈로 묶는 __여러 함수를 클래스로 묶기 (6.9절)__ 을 이용할 수도 있다. 

또 다른 함수를 묶는 방법으로는 __여러 함수를 변환 함수로 묶기 (6.10절)__ 도 있는데 이는 읽기 전용 데이터를 다룰 때 특히 좋다. 

그 다음 단계로는 모듈로 만들었으면 명확히 모듈끼리 단계를 구분짓는 __단계 쪼개기 (6.11절)__ 기법을 적용하는 것도 가능하다.

__(정리하자면 가장 기본적인 리팩토링 기법은 추출하고 이름을 지어주거나, 이름을 변경해서 보다 좋은 코드를 만들도록 하고 그 코드들을 하나로 묶어서 모듈로 만드는 것. 그 다음으로 모듈끼리 확실히 처리하도록 하는 것 이런것들이 있다는 것인가?)___

***

## 6.1 함수 추출하기 

#### Before Refactoring
```java
public class Before {
    public void printOwing(Invoice invoice) {
       int outstanding = 0;

        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");

        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            outstanding += o.amount;
        }

        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30); 
        
        // 세부 사항을 출력한다. 
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));

    }
}
```

#### After Refactoring

```java
public class After {
    public void printOwing(Invoice invoice) {
        printBanner();

        int outstanding = calculateOutstanding(invoice);

        recordDueDate(invoice);
        
        printDetails(invoice, outstanding);
    }

    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }

    private int calculateOutstanding(Invoice invoice) {
        int result = 0;
        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            result += o.amount;
        }
        return result;
    }


    private void recordDueDate(Invoice invoice) {
        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);
    }

    private void printDetails(Invoice invoice, int outstanding) {
        // 세부 사항을 출력한다.
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));
    }
}
```

### 배경

함수 추출하기는 내가 가장 많이 사용하는 리팩토링 중 하나다.

함수 뿐 아니라 여기서 가리키는 의미는 객체지향에서의 메소드나 절차형 언어의 프로시저 / 서브루틴 에서도 똑같이 적용이 되는 대상을 말한다.

함수 추출하기는 코드 조각을 찾아 무슨 일을 하는 지 파악한 다음 독립된 함수로 추출하고 목적에 맞는 이름을 붙이는 것이다. __(묶는 기준은 Large Function, 하나의 기능만 담당하는 함수, 변경과 조회 분리, 중복을 제거하기 위해, 목적과 구현을 분리하기 위해 (코드이해를 돕기위한 방법))__

그렇다면 언제 함수 추출하기 기법을 사용할까? 의견은 수없이 많다. 

정리하자면 다음과 같다.

- 길이를 기준으로 (6줄이 넘는 함수에서는 악취가 나기 시작한다.) 예로 절대 한 화면을 넘어가지 않도록 한다. 

- 중복을 제거하기 위해서. 재사용성을 위해서

- 목적과 구현을 분리하는 방식 (함수 하나에 여러가지 일을 하면 구현이 여러개가 담긴다. 그러면 그때부터 이해하기가 어려워지기 시작하므로 이러한 구현들을 추출해서 무엇을 하는지 이름을 지어주자. 목적을 정해주자.)

  - 내가 추천하는 가장 합리작인 방식으로 이렇게 해두면 코드를 다시 읽을 때 무슨 일을 하는지 목적이 뭔지 한 눈에 들어온다. __(마틴이 말하는 목적과 함수가 무슨 일을 하는지 가 동일한 말 같다.)__
  
함수를 짧게 만들면 함수 호출이 많아져서 성능이 느려질까 걱정하는 사람도 많이봤다.

요즘은 그럴 일이 없다 컴파일러가 캐싱을 잘해주기 떄문에. 

그리고 성능 최적화를 생각하는 사람은 항상 일반 지침을 따르도록 하자.

- "최적화를 할 때는 다음 두 규칙을 따르기 바란다. 첫 번째, 하지 마라. 두 번째(전문간 한정) 아직 하지 마라." __(당장 할 필요가 없다라는 뜻인거 같은데 이유는 뭘까? 개발이 끝난 후에 하라는 말인가?)__

__함수 추출하기는 늘 이름 짓기가 동반되므로 이름을 잘 지어야만 이 리팩토링의 효과가 발휘된다.__ __(이름을 잘 짓는법이라 이 방법에 대해서 찾아봐야겠네.)__

이름을 잘 짓기까지는 어느 정도 훈련이 필요하지만 일단 요령을 터득한 후에는 별도 문서 없이 코드 자체만으로 설명되게 만들 수 있다. 

### 절차

절차는 되게 간단하다. 요즘 IDE 에는 함수 추출하기 기능이 기본적으로 탑재되어 있기 때문에.

여기서는 주의할 점만 보고 하자.

이름을 지을 땐 __함수가 무엇을 하는지가 드러나야 한다.___ 

함수 추출할 땐 연관된 문장들을 합쳐서 한번에 넘겨주는 문장 슬라이스 (8.6절) 도 필요할 때가 있다. 

함수를 추출할 때 주의할 점은 필요한 변수를 넘겨줘야 하는데 필요한 변수들을 다 넘겨주는지 보자. 

- 주의할 점은 추출한 코드 안에서 새로 생기거나 변하는 변수 값이다. 이와 관련해서 조심해서 보자. 

  - 함수 안에서 수정되는 부분이 함수 밖에서도 유지가 되는지를 보자. __(원시 변수를 넘기는 부분 즉 Call By Value 로 인해서 수정해도 변경이 안되는 부분)__

- 더 나아가면 파라미터들을 객체로 합치거나 애초에 객체를 통째로 넘기는 기법이 있다.

값을 반환할 변수가 여러개 일 수도 있는데 이 경우에 처리하는 방법은 다음과 같다. 

- 반환하는 함수 값 자체를 여러개로 만들려고 한다면 이를 합치면 된다.

- 다른 방법으로는 임시 변수를 질의 함수로 바꾸거나 (7.4절) 변수를 쪼개는 방식 (9.1절) 을 사용할 수도 있다. 


### 예시 

위의 예제를 가지고 실제 리팩토링을 진행해보자.

```java
public class Before {
    public void printOwing(Invoice invoice) {
       int outstanding = 0;

        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");

        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            outstanding += o.amount;
        }

        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30); 
        
        // 세부 사항을 출력한다. 
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));

    }
}
```

여기서 `고객 채무` 를 출력하는 코드는 아주 간단히 출력할 수 있다. __(전달해주는 변수가 없기 때문에.)__

그러므로 다음과 같이 바꾸면 된다. 

```java
public class After {
    public void printOwing(Invoice invoice) {
        int outstanding = 0;

        printBanner();

        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            outstanding += o.amount;
        }

        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);

        // 세부 사항을 출력한다. 
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));

    }

    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }
}
```

다음 리팩토링은 `미해결 채무` 를 계산하는 부분이다. 

이 부분도 지역 변수를 사용하지만 다른 값을 대입해서 다른 목적으로 사용하지는 않기 때문에 쉽게 추출하는게 가능하다. 

```java
public class After {
    public void printOwing(Invoice invoice) {
        printBanner();

        int outstanding = calculateOutstanding(invoice);

        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);

        // 세부 사항을 출력한다.
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));

    }

    private int calculateOutstanding(Invoice invoice) {
        int result = 0;
        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            result += o.amount;
        }
        return result;
    }
    

    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }
}
```

다음으로 마감일을 기록하는 부분을 추출해보자. 

````java
public class After {
    public void printOwing(Invoice invoice) {
        printBanner();

        int outstanding = calculateOutstanding(invoice);

        recodDueDate(invoice);

        // 세부 사항을 출력한다.
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));

    }
    
    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }

    private int calculateOutstanding(Invoice invoice) {
        int result = 0;
        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            result += o.amount;
        }
        return result;
    }


    private void recodDueDate(Invoice invoice) {
        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);
    }
}
````

마지막으로 세부 내용을 추출하는 걸 보면 다음과 같다. 

```java
public class After {
    public void printOwing(Invoice invoice) {
        printBanner();

        int outstanding = calculateOutstanding(invoice);

        recordDueDate(invoice);
        
        printDetails(invoice, outstanding);
    }

    private void printBanner() {
        System.out.println("*****************");
        System.out.println("**** 고객 채무 ****");
        System.out.println("*****************");
    }

    private int calculateOutstanding(Invoice invoice) {
        int result = 0;
        // 미해결 채무 (outstanding) 을 계산한다.
        for (Order o : invoice.getOrders()) {
            result += o.amount;
        }
        return result;
    }


    private void recordDueDate(Invoice invoice) {
        // 마감일(dueDate) 을 기록한다.
        LocalDateTime today = Clock.today();
        invoice.dueDate = today.plusDays(30);
    }

    private void printDetails(Invoice invoice, int outstanding) {
        // 세부 사항을 출력한다.
        System.out.println(String.format("고객명: %s", invoice.customer));
        System.out.println(String.format("채무액: %d", outstanding));
        System.out.println(String.format("마감일: %s", invoice.dueDate));
    }
}
```

***

## 6.2 함수 인라인 하기 


### 배경

함수 자체가 짧은걸 권장하지만 때로는 함수 본문이 함수 이름만큼 명확한 경우가 있다면 함수를 제거하는 게 좋다.

쓸데없는 간접 호출은 거슬릴 뿐이다.

또 적용할 시점은 리팩토링 과정에서 잘못 추출한 함수들이 있다면 이를 제거하기 위해 인라인 할 수도 있다.

그리고 간접 호출을 너무 과하게 쓰는 경우가 있다면 즉 가령 다른 함수들로 위임만 하는 구조가 있다면 이를 인라인 하기도 한다. 

정리하자면 이 기법은 필요없는 함수 호출을 제거하기 위해서 있다. 

### 절차  

이 기법을 적용하는 순서는 다음과 같다. __(배경 - 절차 - 예시 이런식으로 구성되어 있는데 절차는 이 기법을 적용하는 방법 그 자체를 아는 것도 있지만 적용할 때 주의할 점을 생각해보는 것.)__ 

1. 다형 메소드 인지 확인한다. (왜냐하면 서브 클래스에서 오버라이) 

2. 인라인 할 함수를 호출하는 곳을 모두 찾아서 교체한다. 

3. 인라인 하기 까다로운 부분이 있다면 이를 남겨놓고 가볍게 바꿀 수 있는 부분부터 바꾼다. 

4. 함수 선언부를 제거한다. 

### 예시 

예시는 간단하다. 다음 함수를 살펴보자. 

```java
public class Before {
    
    public int rating(Driver driver) {
        return moreThanFiveLateDeliveries(driver) ? 2 : 1;
    }

    private boolean moreThanFiveLateDeliveries(Driver driver) {
        return driver.numberOfLateDeliveries > 5;
    }
}
```

호출하는 함수 반환부를 그대로 가져와서 붙혀놓고 기존의 함수는 제거하면 된다. 

```java
public class After {

    public int rating(Driver driver) {
        return driver.numberOfLateDeliveries > 5 ? 2 : 1;
    }
}
```

__지금은 되게 간단하지만 귀찮은 경우도 많다.__ 

함수를 인라인 하지만 변수의 이름이 달라서 여러번 이름을 바꿔야 하는 경우가 그렇다. __(주의할 점 중에 또 하나.)__ 

이런 경우는 컴파일 에러가 안나는 선에서 한 문장씩 옮기는게 좋다. 그러면서 테스트를 진행하는거지. 즉 짧은 스텝으로 옮기는게 중요하다.

***

## 6.3 변수 추출하기 

__(신기하네 이 부분은 chapter 3 에서 사용하는 부분이 없네)__

### 배경 

하나의 표현식이 너무 복잡하다면 표현식의 일부를 지역 변수로 추출해서 관리하면 이해하기가 더 쉽다. __(코드의 이해와 관련된 부분이네.)__ __(한번 나중에 Chapter 3 에서 리팩토링 악취 냄새 종류에 대해서 정리해보는 것도 좋을듯.)__

복잡한 단계를 하나의 호흡으로 가져가기 보다 매 단계별 이름을 붙혀서 보다 코드의 목적을 드러내기 쉽기 때문이다.

__변수를 추출하기로 결정했다면 그것이 적용할 문맥이 어디까지 필요한지 살펴봐야한다.__ __(이거 이 기법에서 신경써야 하는 점이네)__ 

이 함수 안에서만 필요한 지 아니면 다른 함수에서도 이 표현식의 단계가 필요한 지 생각해봐야 한다.

이 함수 안에서만 필요하면 지역 변수로 쓰면 되지만 다른 함수에서도 필요하다면 이 표현식을 함수로 추출하는게 더 나을 수 있다. 

이렇게 맥락을 고려해서 짜면 중복 작성이 줄어들 수 있다는 장점이 있다. 

### 절차 

이 기법의 순서는 다음과 같다. __(특별한건 딱히 없다.)__

1. 추출하려는 표현식에 부작용은 없는지 확인한다. 

2. 불변 변수를 선언하고 이름을 붙일 표현식의 복제본을 대입한다.

3. 원본 표현식을 새로 만든 변수로 교체한다.

4. 테스트한다.

5. 표현식을 여러 곳에서 사용한다면 각각 교체한다. __(새로운 함수나 변수를 만들어서 사용하는 경우 이를 전체에 적용시킨다는 뜻.)__ 


### 예시 

다음과 같은 간단한 계산식을 보자. 

```java
public class Before {
    public double price(Order order) {
        return order.quantity * order.itemPrice - 
                Math.max(0, order.quantity - 500) * order.itemPrice * 0.05 + 
                Math.min(order.quantity * order.itemPrice * 0.1 , 100);
    }
}
```

이건 간단한 코드지만 더 쉽게 만들 수 있다. 

맥락은 다음과 같다. 

`가격(price) = 기본 가격 - 수량 할인 + 배송비`

이 맥락대로 이름을 하나씩 지어주면 된다. 

```java
public class After {
    public double price(Order order) {
        double basePrice = order.quantity * order.itemPrice;
        return basePrice -
                Math.max(0, order.quantity - 500) * order.itemPrice * 0.05 +
                Math.min(order.quantity * order.itemPrice * 0.1 , 100);
    }
}
```

이렇게 하나씪 추출하고 이런 표현식이 쓰이는 곳이 있다면 모두 변하자. 

````java
public class After {
    public double price(Order order) {
        double basePrice = order.quantity * order.itemPrice;
        double quantityDiscount = Math.max(0, order.quantity - 500) * order.itemPrice * 0.05;
        double shipping = Math.min(order.quantity * order.itemPrice * 0.1 , 100); 
        return basePrice - quantityDiscount + shipping;
    }
}
````

클래스에서 적용할 땐 이들을 함수 내에있는 변수로 적용하지 말고 메소드로 빼내도록 하자. 

즉 이런 클래스가 있다면

```java
public class Order {
    protected int quantity;
    protected int itemPrice;

    public Order(int quantity, int itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double price() {
        return this.quantity * this.itemPrice -
                Math.max(0, this.quantity - 500) * this.itemPrice * 0.05 +
                Math.min(this.quantity * this.itemPrice * 0.1 , 100);
    }
}
```

변수 추출하면 이렇게 바꾸자. 

````java
public class OrderAfter {
    protected int quantity;
    protected int itemPrice;

    public OrderAfter(int quantity, int itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double price() {
        return basePrice() - quantityDiscount() + shipping();
    }

    private double shipping() {
        return Math.min(this.quantity * this.itemPrice * 0.1, 100);
    }

    private double quantityDiscount() {
        return Math.max(0, this.quantity - 500) * this.itemPrice * 0.05;
    }

    private int basePrice() {
        return this.quantity * this.itemPrice;
    }
}
````

***

## 6.4 변수 인라인 하기 

### 배경

변수는 함수 안에서 특정한 의미를 가져서 코드의 이해를 도와준다. 

하지만 변수가 많으면 코드를 리팩토링 하는데 방해를 주기도 하고 변수가 언래 표현식과 다를 바 없을 때도 있다. __(원래 표현식만으로도 충분히 설명이 되는걸 말하는듯)__

### 절차

이 기법대로 절차를 따르면 된다. 

1. 대입문의 우변(표현식) 에서 부작용이 생기지  않는지 확인하자.

2. 변수가 불변으로 선언되지 않았다면 불변으로 만든 후 테스트 한다. (이렇게 하면 변수가 값이 단 한번만 대입하는지 확인할 수 있다.) __(불변으로 만들어서 테스트 해야하는 상황은 어떤 경우지? 변수를 인라인 한다는 말은 그 변수를 지우고 표현식으로 대체하겠다는 뜻이다. 이 표현식은 인풋이 같다면 계속해서 같은 값을 리턴하는 구조일텐데 지우려는 변수가 업데이트되는 구조라면 이 표현식으로 대체했을 때 문제갓 생긴다.)__

3. 이 변수를 가장 처음 사용하는 코드를 찾아서 대입문 우변의 코드로 바꾼다.

4. 태스트한다.

5. 변수를 사용하는 모든 부을 모두 교체하고 테스트한다.

6. 변수 선언문과 대입문을 지운다. __(언제든지 돌아갈 수 있도록 고려하는듯)__

7. 테스트한다. 

### 예시 

즉 다음과 같은 메소드가 있면 

```java
public boolean method(Order order) {
    int basePrice = order.basePrice;
    return basePrice > 1000; 
}
```

이렇게 바꾸면 된다. 

```java
public boolean method(Order order) {
    return order.basePrice > 1000; 
}
```

***

## 6.5 함수 선언 바꾸기 

### 배경 

함수의 이름이 좋으면 이름만 보고도 무슨 일을 하는지 파악하는게 가능하지만 나쁜 이름은 혼란을 일으킨다. 

이름이 잘못된 함수가 있다면 무조건 바꾸자. __(잘못된 이름이 가장 크리티컬한 이유라고 생각하는 듯.)__

물론 좋은 이름을 한번에 잘 지을 순 없다. 

그치만 좋은 이름을 짓기 위한 팁이 있는데 함수의 목적을 주석으로 설명해보는 것이다. 그러다 보면 주석이 멋진 이름으로 바뀌어 올 때가 있다. __(이거의 또 디테일한 설명이 나는 실제 구현사항에 집중해서 함수의 이름을 짓기 보다는 해줘야하는 역할에 좀 더 집중하면 잘되더라.)__

함수의 이름 뿐 아니라 매개변수도 마찬가지다.

매개변수는 함수와 어울려서 함수의 문맥을 정해준다. __(함수이름 + 매개변수로 함수를 이해한다 라는 뜻)__

예컨대 전화번호 포매팅 함수가 매개변수로 사람을 받는다고 하면 회사 전화번호는 사용할 수 없게된다. 

그러므로 사람 보다는 전화번호 자체를 전달받도록 하는게 더 좋다. __(이게 바로 어떠한 기능을 만드는게 아니라 어떠한 일을 해줘야 하는지. 역할에 맞는 사용법 아닐까?)__

이렇게 하면 활용 범위가 넓어질 뿐 아니라, 다른 모듈과의 결합(coupling) 도 줄어들 수 있다. __(매개변수로 전달 받는 다는 건 결국 그 대상과 결합을 한다라는 뜻일텐데 누구와 결합을 하는게 나을까? 가 중요한 문제인가 결합에서는)__

매개변수를 올바르게 선택하기는 단순히 규칙 몇 개로 표현할 수는 없다. 

예를들어서 대여한 지 30일이 지났는지를 기준으로 지불 기한이 넘었는지 판단하는 함수가 있다고 생각해보자.

매개변수로 지불 객체가 적절할까? 마감일을 넘기는게 적합할까? 

이런 문제는 정답이 없다.

마감일을 넘기면 날짜와만 결합하면 되므로 다른 모듈과 결합하지 않아도 된다. 즉 신경 쓸 요소가 적어진다.

지불 객체를 전달하면 지불이 제공하는 여러 속성을 전달받을 수 있다. 이로 인해 캡슐화 수준을 높일 수 있다. 

그러므로 각각에 장단점이 있기 때문에 우리가 취해야 하는 건 고칠 수 있는 능력을 갖추는 것이다. 

이 리팩토링 기법을 잘 알아서 더 적합한 쪽으로 바꿀 수 있는 능력을 갖추면 된다. 

### 절차 

이 책에서 리팩토링을 적용하는 절차는 대게 한 가지만 소개한다. 

왜냐하면 그 방법이 대체로 대부분의 상황에서 효과적이기 때문인데 함수 선언 바꾸기는 좀 사정이 다르다.

__간단한 절차__ 만으로 충분할 때가 많지만, 더 세분화된 __마이그레이션 절차__ 가 훨씬 적합한 경우가 많다. 

따라서 이 리팩토링을 할 때는 먼저 변경 사항을 살펴보고 함수 선언문과 호출문을 한번에 고칠 수 있는지 가늠해본다.

가능해보인다면 간단한 절차를 따르자.

호출하는 곳이 많거나, 호출 과정이 복잡하거나, 호출 대상이 다형 메소드이거나, 선언이 복잡한 경우에는 마이그레이션 절차를 따라서 점진적으로 수정을 해나가야한다. 



#### 간단한 절차

1. 매개변수를 제거하기전에 먼저 함수 본문에서 매개변수를 참조하는 곳이 없는지 확인한다.

2. 메소드 선언을 원하는 형태로 바꾼다.

3. 기존 메소드 선언을 참조하는 부분을 모두 찾아서 바꾼다.

4. 테스ㅌ 한다. 

#### 마이그레이션 절차

1. 이어지는 추출 단계를 수월하게 만들어야 한다면 함수 본문을 적절히 리팩토링한다. 

2. 함수 본문을 새로운 함수로 추출한다. (새로운 함수의 이름은 일단 임시로 해놔도 된다.)

3. 추출한 함수에 매개변수를 추가해야 한다면 추가한다.

4. 테스트한다.

5. 기존 함수를 이제 인라인 한다. 

6. 함수 이름을 적절하게 바꾼다. 

7. 테스트한다. 


다형성을 구현한 클래스 나 상속 구조에서는 메소드 변경이 어렵기 때문에 새롭게 원하는 함수를 만들고 원래 함수를 호출하는 메소드로 사용해서 변경하면 편하다.

### 예시: 함수 이름 바꾸기 (간단한 절차)

함수 이름을 너무 축약한 예가 있고 이것을 바꾸는 과정을 해보자.

```java
public double circum(double radius) {
    return 2 * Math.PI * radius;
}
```

이 함수의 이름을 바꾸는게 목표다 함수의 이름을 circumference 로 바꾸고 circum 을 사용하는 모든 곳을 찾아서 바꾸면 된다. 

```java
public double circumference(double radius) {
    return 2 * Math.PI * radius;
}
```

### 예시: 함수 이름 바꾸기 (마이그레이션 절차)

이번엔 마이그레이션 절차를 한번 적용해보자. 

```java
public double circum(double radius) {
    return 2 * Math.PI * radius;
}
```

다음과 같은 코드를 새로운 함수로 추출해서 이를 호출하는 구조로 바꾸자. 

```java
public double circum(double radius) {
    return circumference(radius);
}

private double circumference(double radius) {
    return 2 * Math.PI * radius;
}
```

이렇게 바꾸고 테스트 한 후 정상적으로 돌아가면 기존의 함수를 인라인하고 새로운 함수로 바꾸면 된다. 그 다음 테스트를 하면 된다.

### 예시: 매개변수 추가하기 

도석 관리 프로그램에서 책에 대한 예약 기능이 구현되어 있다고 가정해보자. 

여기서 새로운 요구사항으로 우선순위 큐를 지원하라는 기능이 들어왔다. 

이 기능을 지원하기 위해서 매개변수로 일반 큐를 사용할 지 우선순위 큐를 사용할지 여부를 추가하려고 한다. 

여기서는 한번에 변경하기 힘드므로 마이그레이션 절차로 진행한다고 가정해보자.

일단 다음과 같은 예약자를 추가하는 기능이 있다.  

```java
public void addReservation(Customer customer) {
    this.reservations.add(customer); 
}
```

마이그레이션 기법에 따라 다음과 같이 변경했다. 

```java
public void addReservation(Customer customer) {
    priorityAddReservation(customer);
}

private void priorityAddReservation(Customer customer) {
    this.reservations.add(customer);
}
```

그 다음 우선순위 파라미터를 넣자. 

```java
public void addReservation(Customer customer) {
        priorityAddReservation(customer, false);
    }

private void priorityAddReservation(Customer customer, boolean isPriority) {
    this.reservations.add(customer);
}
```

이런식으로 만든 다음에 완전히 완료가 되면 기존 함수를 인라인하면 된다. 

***

## 6.6 변수 캡슐화하기 

### 배경

함수는 데이터보다 다루기 수월하다.

함수는 대체로 호출 하는식으로 동작되며 함수를 바꿀 때는 함수가 다른 함수를 호출하도록 변경만해주면 쉽게 바꾸는게 가능하다.

하지만 데이터는 데이터를 사용하는 모든 부분을 바꿔줘야한다. 

짧은 함수 안의 임수 변수처럼 유효범위가 아주 좁은 데이터는 문제가 되지 않지만 이러한 이유로 전역 데이터는 골칫거리가 될 수 있다.

그래서 접근할 수 있는 넓은 유혀범위를 가진 데이터는 먼저 그 데이터의 접근을 독점하는 함수를 만드는게 가장 좋다. __(그냥 데이터를 바로 접근하는 것보다 함수를 통해서 접근하는게 통제성이 더 좋다라는 뜻.)__

데이터 재구성 보다 함수 재구성이 더 간단하기 때문이다. 

이렇게 데이터 캡슐화를 하면 이점이 있는데 데이터 변경 전이나 변경 후 추가 로직을 쉽게 넣는게 가능하다.

__나는 유혀범위가 함수 하나보다 넓은 가변 데이터는 모두 이런식으로 캡슐화를 한다.__

레거시 코드를 다룰 때는 이런 변수를 참조하는 코드를 추가하거나 변경할 때마다 최대한 캡슐화를 한다. 

그래야 자주 사용하는 데이터에 대한 결합도가 높아지는 일을 막을 수 있다. __(데이터 그 자체로 버로 접근해서 사용하는 경우는 문제가 많았다.)__

객체 지향에서 객체의 데이터를 항상 private 으로 유지해야 한다고 그토록 강조하는 이유가 여기에 있다. __(데이터 자체로 접근을 한다면 변경의 어려움이 있어서)__

나는 public 필드를 발견할 때마다 private 으로 변경하고 캡슐화를 한다. 

어떠한 사람은 나보다 더 나아가서 self-encapsulation 을 주장하는 사람도 있는데 이건 좀 과하지 않나 라는 생각을 한다. __(self-encapsulation 은 자기 클래스 안에서 필드 접근할 때 get() 메소드 쓰는 것)__

self-encapsulation 을 해야 할 정도라면 클래스를 쪼개는게 맞다. 

여기서 불변 데이터의 경우에는 가변 데이터보다 캡슐화할 이유가 적다. 

데이터가 변경될 일이 없어서 갠신 검증이나 추가 로직이 있을 필요가 없기 때문이다. __(데이터를 변형시킬 일이 없으니까 딱히 걱정하지 않아도 된다 라는 뜻. 원래 변수 캡슐화 쓰기 할 때 변경의 유무 때문에 추적하기가 어렵다 라는 문제가 크리티컬해서 사용하는 것도 있었으니까. 데이터 이름이 바뀌는건 그렇게 크리티컬 하지는 않은 듯)__

### 절차

1. 변수로의 접근과 갱신을 전담하는 캡슐화 함수를 만든다.

2. 정적 검사를 수행한다. __(무슨 뜻?)__

3. 변수를 직접 참조하던 부분을 모두 적절한 캡슐화 함수 호출로 바꾼다. 하나씩 바꿀 때마다 테스트한다.

4. 변수의 접근 범위를 제한한다.

5. 테스트한다.

6. 변수 값이 레코드라면 레코드 캡슐화하기 (7.1절) 을 적용할 지 고려해본다. 


### 예시 

예시는 간단하다. 

전역변수에 데이터가 담겨있고 이를 참조하는 코드가 있다고 했을 때 이를 getX() 메소드, setX() 메소드를 만들면 된다. 

만들어주고 참조하는 부분을 Getter 메소드로 변경해주자. 

그리고 하나씩 변경할 때마다 테스트를 해주고 모두 테스트가 완료되면 기존 변수에 접근하지 못하도록 접근 제어자를 바꾸자. 

__여기서 추가로 Getter 로 가져간 데이터의 변경이 원본에 영향을 주지 않도록 할려면 clone() 메소드를 통해 복제본을 던지도록 하면 된다. 아니면 클래스를 통해 매번 새로운 객체를 만들어주도록 하는 방법도 있다.__

***

## 6.7 변수 이름 바꾸기 

### 배경 

이름 짓기와 관련된 리팩토링 기법

개발을 진행하다 보니 문제를 좀 더 잘 이해해서 그에 맞도록 이름을 변경하거나 사용자의 요구사항이 변경되서 그에 맞게 이름을 변경해야 하는 경우에 사용한다.

### 절차

1. 이름을 변경할 때 사용 범위도 고려해보자. 폭 넓게 사용되는 변수라면 변수 캡슐화 하기 (6.8절) 을 고려하자.

2. 이름을 바꿀 변수를 참조하는 곳을 모두 찾아서 하나씩 변경하자. __(변수 값이 변하지 않는다면 복제본을 이용해서 하나씩 점진적으로 변경해나가자.)__

3. 테스트 한다.

### 예시: 변수 캡슐화 하기

변수 이름 바꾸기는 간단하다. 

그저 변수를 참조하는 곳이 있다면 하나씩 바꾸면 된다.

여기서는 변수가 수정되는 부분도 있어서 변수 캡슐화하기와 변수 이름 바꾸기를 모두 적용하는 경우를 보겠다. 

다음과 같은 변수가 있다고 보자. 

```java
String tpHd = "untitled"; 
``` 

어떤 참조는 이 변수를 읽기만 한다. 

```java
String result = String.format("<h1> %s </h1> ", tphd); 
```

또 어떤 곳에는 이 변수의 값을 수정하는 부분도 있다.

```java
tphd = obj.getArticleTitle(); 
``` 

나는 이럴 때 주로 변수 캡슐화 하기를 이용해서 해결한다. (변수 이름은 title 로 바꿨다.)

```java
String result = String.format("<h1> %s </h1> ", getTitle());  
```

또 수정될 수 있으므로 setX() 메소드도 만들어서 사용한다.

```java
setTitle(obj.getArticleTitle()); 
```

### 예시: 상수 이름 바꾸기 

변경되지 않는 상수 값은 캡슐화 하지 않고 복제 발식으로도 쉽게 변수 이름을 바꿀 수 있다. 

다음과 같은 변수 이름이 있다고 가정해보자. 

```java
String cpNm = "애크미 구수베리"; 
```

여기서 복제본을 만들어서 사용한다. 

```java
String companyName = "애크미 구수베리"; 
String cpNm = companyName; 
```

이렇게 복제본을 대입한 후 하나씩 참조하는 부분을 `companyName` 으로 변경해서 점진적으로 바꿀 수 있다. 

***

## 6.8 매개변수 객체 만들기 

### 배경 

데이터 항목 여러 개가 이 함수로 저 함수로 같이 몰려다니는 경우를 자주 볼 수 있다. 

나는 이런 데이터 무리를 발견하면 하나의 데이터 구조로 모아주곤 한다. __(데이터 사이의 관계가 명확해져서 하나의 책임을 가질 수 있도록 하기 위해서, 또 파라미터 개수를 줄여줄 수 있으니까 함수의 이해가 쉬워진다.)__

이 리팩토링의 진정한 힘은 코드를 더 근본적으로 바꿔 준다는 데 있다. __(새로운 책임을 가진 객체가 만들어진 다는 것)__

__이런 데이터 구조를 새로 발견하면 이 데이터 구조를 활용하는 형태로 프로그램 동작을 재구성한다.__

이 과정에서 새로 만든 데이터 구조가 문제를 보다 간결하게 표현할 수 있다. 추상화를 통해서. 

그러면 놀라울 정도로 갈력한 효과를 낸다.
 
### 절차

1. 적당한 데이터 구조가 없다면 새로 만든다. (클래스로 만드는 걸 추천한다.) 

2. 테스트한다. (해당 데이터구조가 제대로 동작하는지 테스트 하는 걸 말하는 듯) 

3. 함수 선언 바꾸기 (6.8절) 로 새 데이터 구조를 매개변수로 활용한다.

4. 테스트 한다. 

5. 함수 호출 시 새로운 데이터 구조 인스턴스를 넘기도록 수정한다. 그러고 나서 하나씩 테스트 해본다. 

6. 기존 매개변수를 사용하던 코드를 새 데이터 구조의 원소를 사용하도록 바꾼다.

7. 다 바꿨다면 기존 매개변수를 사용하는 함수는 제거하고 테스트 한다. 

### 예시

온도 측정값 (reading) 에서 정상 작동 범위를 벗어나는 코드가 있는지 검사하는 코드가 있다고 살펴보자. 

```java
public List<Reading> readingsOutsideRange(Station station, int min, int max) {
    return station.readings
            .stream()
            .filter(r -> r.temp < min || r.temp > max)
            .collect(Collectors.toList()); 
}
```

즉 다음과 같은 코드가 있다. 

이 함수는 다음과 같은 호출하는 코드가 있다. 

```java
List<Reading> alerts = readingsOutsideRange(station, 
    operationPlan.temperatureFloor, // 최저 온도 
    operationPlan.temperatureCeiling) //최고 온도
```

호출 코드에는 데이터 항목 두 개를 쌍으로 가져와서 `readingOutsideRange()` 함수에 전달한다. 

__범위__ 라는 개념은 객체 하나로 충분히 묶을 수 있다. 

그러므로 다음과 같은 새 데이터 구조를 만들 수 있다. 

````java
public class NumberRange {
    protected int min;
    protected int max;

    public NumberRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
````

그런 다음 새 객체를 readingsOutsideRange() 의 매개변수로 추가하도록 하자. 

````java
public List<Reading> readingsOutsideRange(Station station, int min, int max, NumberRange numberRange) {
    return station.readings
            .stream()
            .filter(r -> r.temp < min || r.temp > max)
            .collect(Collectors.toList());
}
````

이렇게 하면 이 함수를 호출하는 곳에서는 매개변수로 null 만 넣으면 컴파일 오류는 생기지 않는다. __(컴파일 오류가 생기지 않는 선에서 점진적으로 문제를 개선할 수 있도록 하는게 중요한 것.)__

이제 하나씩 바꿔보면 된다. 

max 대신 numberRange.getMax() 를 사용하도록 변경해보자. 

```java
public List<Reading> readingsOutsideRange(Station station, int min, int max, NumberRange numberRange) {
    return station.readings
            .stream()
            .filter(r -> r.temp < min || r.temp > numberRange.getMax())
            .collect(Collectors.toList());
}
```

문제가 없는지 확인해보자. 물론 호출문도 그에 맞게 NumberRange 를 사용하도록 전달해줘야 한다. 

테스트가 통과하면 min 대신 numberRange.getMin() 을 사용하도록 변경해보자. 

```java
public List<Reading> readingsOutsideRange(Station station, int min, int max, NumberRange numberRange) {
    return station.readings
            .stream()
            .filter(r -> r.temp < numberRange.getMax() || r.temp > numberRange.getMax())
            .collect(Collectors.toList());
}
```

테스트 통과되면 매개변수를 제거하고 테스트 해보자. 

***

## 6.9 여러 함수를 클래스로 묶기 

### 배경 

나는 함수 호출 시 공통 인수로 전달되는 공통 데이터를 사용하는 함수가 여럿 있다면 이들을 하나의 클래스로 묶고 싶다. __(하나의 새로운 책임을 가진 클래스, 함수의 파라미터를 줄여서 이해를 높일 수 있다는 측면)___

클래스로 묶으면 이 함수들이 공유하는 공통 환경을 더 명확하게 표현하는게 가능해진다. 

그리고 각 함수에 전달되는 인수를 줄여서 함수 호출이 더 간결하게 만들 수 있다. 

이 리팩토링은 기존의 함수들을 재구성할 때와 새로 만든 클래스와 관련해 놓친 연산을 찾아서 새 클래스의 메소드로 뽑아내는 것도 좋다. 

함수를 한데 묶는 또 다른 방법으로는 여러 함수를 변환 함수로 묶기 (6.10 절) 기법도 있어서 이건 맥락에 따라 잘 결정해야한다. 

### 절차 

1. 함수들이 공유하는 공통 데이터 레코드를 캡술화 (7.1 절) 한다. 

2. 공통 레코드를 사용하는 함수 각각을 새 클래스로 옮긴다. (함수 옮기기 (8.1절))

3. 데이터를 조작하는 로직들은 함수로 추출 (6.1절)해서 새 클래스로 옮긴다.  

### 예시 

나는 차 (tea) 를 좋아하므로 차를 가지고 예제를 만들어보았다. 

정부에서 차를 수돗물처럼 제공하고 사람들은 매달 차 계량기를 익어서 측정값 (reading) 을 다음과 같이 기록한다고 생각해보자. 

```json
reading = {
  "customer": "ivan", 
  "quantity": 10,
  "month": 5,
  "year": 2017
}
```

이 레코드를 처리하는 코드를 보니 비슷한 연산을 수행하는 부분이 많았다. 

#### 클라이언트 1

```java
Reading reading = acquireReading(); 
double baseCharge = baseRate(reading.month, reading.year) * reading.quantity; 
```

#### 클라이언트 2

```java
Reading reading = acquireReading(); 
double base = (baseRate(reading.month, reading.year) * reading.quantity); 
double taxableCharge = Math.max(0, base - taxThreshold(reading.year)); 
```

여기서도 기본요금 계산 공식이 똑같이 등장하는 것을 발견했다.

원래는 이런 중복을 보면 함수 추출하기로 변경을 하곘지만 새로운 클라이언트에서는 이미 만들어놨었다. 

```java
Reading reading = acquireReading(); 
double base = calculateBaseCharge(reading)

public double calculateBaseCharge(Reading reading) {
    return (baseRate(reading.month, reading.year) * reading.quantity); 
}
```

이렇게 중복을 위해 메소드를 빼놔도 데이터와의 거리는 가깝지 않으니까 못보고 사용한게 아닌가 싶다.

데이터와 그것의 동작은 가까운 거리에 있는게 좋으므로 클래스로 만들어서 처리할 수 있다. 

`calculateBaseCharge()` 메소드도 이제 클래스로 옮기면 된다. 

그 다음 기존에 계산하던 코드들을 모두 클래스를 이용하도록 변경하자. 

***


 







 



 



 
