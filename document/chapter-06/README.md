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

  - 함수 안에서 수정되는 부분이 함수 밖에서도 유지가 되는지를 보자. (원시 변수를 넘기는 부분)

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


