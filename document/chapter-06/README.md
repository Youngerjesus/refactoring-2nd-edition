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



 



 
