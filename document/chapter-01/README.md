# 리팩터링: 첫번째 예시

***

리팩토링을 어떻게 설명하면 좋을까? 

원칙을 나열해서 설명하면 이해하기 어렵기 떄문에 예시를 위주로 설명하곘다. 

예시는 코드가 너무 길면 독자가 따라오기 어렵기 떄문에 간단한 예시를 가지고 오겠다. 

솔직히 이 책에 수록된 예처럼 간단한 프로그램은 굳이 내(마틴 파울러)가 제시하는 리팩토링 전부를 적용할 필요는 없다. 

하지만 그 코드가 대규모 시스템의 일부라면 리팩토링을 적용하고 안하고의 차이는 크다.

그러므로 이러한 간단한 에제들을 대규모 시스템에서 발췌한 코드라고 생각하자.  

## 1.1 자 시작해보자. 

초판에서는 첫 예시로 비디오 대여점에서 영수증을 출력하는 프로그램을 소개헀다. 그런데 요즘 독자는 비디오 대여점이 뭔지 모를 수도 있곘다는 생각이 들어서, 기본 틀은 유지하되 지금은 시대에 맞게 각색하겠다. 

다양한 연극을 외주로 받아서 공연하는 극단이 있다고 생각해보자. 

공연 요청이 들어오면 연극의 장르와 관객 규모를 기초로 비용을 책정한다. 

현재 이 극단은 두 가지 장르, 비극(tragedy) 와 희극(comedy) 만 공연한다. 

그리고 공연료와 별개로 포인트(volume credit) 를 지급해서 다음번 의뢰 시 공연료를 할인받을 수 있다.

극단은 공연할 연극 정보를 다음과 같이 간단한 JSON 파일에 저장한다. 

> plays.json

```json
{
  "hamlet": {
    "name": "hamlet",
    "type": "tragedy"
  },
  "as-like": {
    "name": "As You Like It",
    "type": "comedy"
  },
  "othello": {
    "name": "Othello",
    "type": "tragedy"
  }
}
```

공연료 청구서에 들어갈 데이터도 다음과 같이 JSON 파일로 표현한다. 

> invoice.json

```json
[
  {
    "customer": "BigCo",
    "performances": [
      {
        "playID": "hamlet",
        "audience": 55
      },
      {
        "playID": "as-like",
        "audience": 35
      },
      {
        "playID": "othello",
        "audience": 40
      }
    ]
  }
]
```

공연료 청구서를 출력하는 코드는 다음과 같이 간단히 `Statement` 클래스에서 메소드로 구현했다. 

모든 코드는 이 레파지토리 패키지안에 다 있으니까 참조하면 좋다.

```java
package refactoring.app.chapter01;

public class Statement {
    public String statement(Invoice invoice, Plays plays) throws Exception {
        int totalAmount = 0;
        int volumeCredit = 0;
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
        for (Performance performance : invoice.getPerformances()) {
            Play play = plays.get(performance);
            int thisAmount = 0;

            switch (play.getType()) {
                case TRAGEDY:
                    thisAmount = 40000;
                    if (performance.getAudience() > 30) {
                        thisAmount += 1000 * (performance.getAudience() - 30);
                    }
                    break;
                case COMEDY:
                    thisAmount = 30000;
                    if (performance.getAudience() > 20) {
                        thisAmount += 10000 + 500 * (performance.getAudience() - 20);
                    }
                    thisAmount += 300 * performance.getAudience();
                    break;
                default:
                    throw new Exception("알 수 없는 장르");
            }

            // 포인트를 적립한다.
            volumeCredit += Math.max(performance.getAudience() - 30, 0);

            // 희극 관객 5명마다 추가 포인트를 제공핟나.
            if (play.getType().equals(PlayType.COMEDY)) {
                volumeCredit += Math.floor(performance.getAudience() / 5);
            }

            // 청구 내역을 출력한다.
            result.append(String.format("%s: $%d %d석\n",play.getName(), thisAmount / 100, performance.getAudience()));
            totalAmount += thisAmount;
        }

        result.append(String.format("총액: $%d\n",totalAmount / 100));
        result.append(String.format("적립 포인트: %d점", volumeCredit));
        return result.toString();
    }
}
```

결과는 다음과 같이 출력된다. 

```text
청구내역 (고객명: BigCo)
hamlet: $650 55석
As You Like It: $580 35석
Othello: $500 40석
총액: $1730
적립 포인트: 47점
```

***

## 1.2 예시 프로그램을 본 소감

이 프로그램의 설계를 보고 난 소감은 어떤가? 나는 이 상태로도 그럭저럭 쓸만하다는 생각이 든다. 

프로그램이 너무 짧아서 특별히 애써 이해해야 할 구조도 없다. 

앞에서 설명했듯이 이 책에서는 이처럼 짧은 예만 소개한다. 

하지만 이런 코드가 수백 줄 짜리 프로그램의 일부라면 간단한 인라인 함수 하나라도 이해가기가 쉽지 않다. 

컴파일러는 지저분한 코드이던지, 미적인 코드이던지 신경쓰지 않는다.


요구사항이 들어와서 기능을 수정하기 위해 코드를 수정해야 한다고 생각해보자. 

코드를 수정하려면 사람이 개입되고, 사람은 코드 미적상태에 예민하다. 

사람은 설계가 나쁜 시스템을 수정하기 어렵다. 

원하는 동작을 수행하도록 하기 위해 수정해야 할 부분을 찾고, 기존 코드와 잘 맞물려 작동하게 할 방법을 강구하기 어렵기 때문이다. 

그래서 나는 수백 줄짜리 코드를 수정할 때면 먼저 프로그램의 작동 방식을 더 쉽게 파악할 수 있도록 코드를 여러 함수의 요소로 재구성한다. 

프로그램의 구조가 빈약하다면 대체로 구조부터 바로 잡은 뒤에 기능을 수정하는 편이 훨씬 수월하다.

> 프로그램이 새로운 기능을 추가하기에 편한 구조가 아니라면, 먼저 기능을 추가하기 쉬운 형태로 리팩토링하고 나서 원하는 기능을 추가한다. 

자 이 코드에서 사용자의 입맞에 맞게 수정할 부분을 몇 개 발견했다. 

가장 먼저 청구 내역을 HTML 로도 출력하는 기능이 필요하다. 

이 변경이 어느 부분에 영향을 줄 지 생각해보자.

우선 HTML 태그를 삽입해야 하고 statement() 함수에서 조건문에 따라서 어떤건 HTML 로 어떤건 텍스트로 출력하도록 추가할 것이다.  

그러면 statement() 의 함수의 복잡도가 크게 증가한다. 

이런 문제 때문에 기존에 존재하는 statement() 함수의 복사본을 만들어서 htmlStatement() 로 만들어서 사용하기도 할 것이다.

이렇게 되면 코드의 중복이 발생하고 변경 포인트가 두 개가 된다. 즉 DRY 원칙에 위반된다. 

두 번째 변경 사항으로 배우들이 사극, 전원극, 전원 희극, 역사 전원극, 역사 비극, 회비 역사 전원극, 장면 변화가 없는 고전극 등 더 많은 장르를 연기하고자 한다. 

언제 어떤 연극을 할지는 아직 결정하지 못했지만 이 변경은 공연료와 적립 포인트 계산법에 영향을 줄 것이다. 
 
이처럼 연극 장르와 공연료 정책이 달라질 때마다 statement() 함수를 수정해야 한다. 

리팩토링이 필요한 이유는 바로 이러한 변경 때문이다. 작 작동하고 나중에 변경할 일이 없다면 코드를 현재 상태로 나눠도 아무런 문제가 없다. 

더 다듬어두면 물론 좋겠지만 누군가 코드를 읽지 않는 한 아무런 피해가 없다. 

하지만 그러다 다른 사람이 읽고 이해해야 할 일이 생겼는데 로직을 파악하기 어렵다면 뭔가 대책을 마련해야 한다.

***

## 1.3 리팩토링의 첫 단계 

리팩토링의 첫 단계는 항상 똑같다. 

리팩토링할 코드가 잘 작동하는지 검사해줄 테스트 코드를 만드느 것이다. 

리팩토링에서 테스트의 역할은 굉장히 중요하다. 

리팩토링 기법들로 버그의 발생 여지를 최소화 한다고는 하지만 사람이 수행하는 일은 언제든 실수할 수 있다. 

statement() 함수의 테스트는 어떻게 구현하면 될까? 

이 함수가 문자열을 반환하므로 다양한 장르와 공연들로 구성된 형태를 몇 개 작성해서 문자열 형태로 준비해둔다. 즉 시나리오를 준비해둔다. 

테스트 결과는 눈으로 보지말고 시스템이 판단하도록 한다. (테스트 결과를 성공하면 초록불이 뜨도록 실패하면 빨간불이 드는 JUnit 과 같이) 

정리하자면 다음과 같다. 

__리팩토링하기 전에 제대로 된 테스트를 마련한다. 테스트는 반드시 자가진단하도록 한다.__

리팩토링에서 테스트의 역할이 굉장히 중요하기 떄문에 4장 전체를 테스트에 할애했다. 

***

## 1.4 statement() 함수 쪼개기 

statement() 처럼 긴 함수를 리팩토링 할 때는 먼저 전체 동작을 각각의 부분으로 나눌 수 있는 지점을 찾는다. 

그러면 중간 즈음의 switch 문이 가장 눈에 띌 것이다. 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        Play play = plays.get(performance);
        int thisAmount = 0;
    
        // 여기에 주목 
        switch (play.getType()) {
            case TRAGEDY:
                thisAmount = 40000;
                if (performance.getAudience() > 30) {
                    thisAmount += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                thisAmount = 30000;
                if (performance.getAudience() > 20) {
                    thisAmount += 10000 + 500 * (performance.getAudience() - 20);
                }
                thisAmount += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }

        // 포인트를 적립한다.
        volumeCredit += Math.max(performance.getAudience() - 30, 0);

        // 희극 관객 5명마다 추가 포인트를 제공핟나.
        if (play.getType().equals(PlayType.COMEDY)) {
            volumeCredit += Math.floor(performance.getAudience() / 5);
        }

        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",play.getName(), thisAmount / 100, performance.getAudience()));
        totalAmount += thisAmount;
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

이 switch 문을 살펴보면 한 번의 공연에 대한 요금을 계산하고 있다. 

이러한 사실은 코드 분석을 하면서 얻은 정보다. 

워드 커닝햄(Ward Cunningham) 이 말하길, 이런 식으로 파악한 정보는 휘발성이 높기로 악명 높은 저장 장치인 내 머릿속에 기록되므로, 잊지 않으려면 재빨리 코드에 반영해야 한다. 

그러면 다음번에 코드를 볼 때, 다시 분석하지 않아도 코드 스스로가 자신이 하는 일이 무엇인지 이야기해줄 것이다. 

여기서는 코드 조각을 별도 함수로 추출하는 방식으로 앞서 파악한 정보를 코드에 반영할 것이다. 

추출한 함수에는 그 코드가 하는 일을 설명하는 이름을 지워준다. 

이름은 amountFor(performance) 정도면 적당해 보인다. 

나는 이렇게 코드 조각을 함수로 추출할 때 실수를 최소화해주는 절차를 마련해뒀다. 

이 절차를 따로 기록해두고, 나중에 참조하기 쉽도록 '함수 추출하기' 란 이름을 붙였다. 

먼저 별도 함수로 빼냈을 때 유효범위를 벗어나는 변수, 즉 새 함수에서 필요한 변수들을 뽑는다. 

여기서는 performance, play, thisAmount 가 있다. 

뽑은 변수에서 performance 와 play 같은 경우는 값을 참조만 하지 변경하지 않으니까 새 함수의 파라미터로 전달하면 된다. 

그치만 thisAmount 같은 경우는 새 함수에서 변경을 하는데 이는 주의해서 다뤄야한다. 

여기서는 새 함수에서 변경하는 함수가 thisAmount 밖에 없으니까 이것을 새 함수에서 선언하고 리턴해주는 방식으로 사용하면 된다. 

이렇게 리팩토링한 결과는 다음과 같다. 

> amountFor() 메소드 
```java
private int amountFor(Performance performance, Play play) throws Exception {
    int thisAmount;
    switch (play.getType()) {
        case TRAGEDY:
            thisAmount = 40000;
            if (performance.getAudience() > 30) {
                thisAmount += 1000 * (performance.getAudience() - 30);
            }
            break;
        case COMEDY:
            thisAmount = 30000;
            if (performance.getAudience() > 20) {
                thisAmount += 10000 + 500 * (performance.getAudience() - 20);
            }
            thisAmount += 300 * performance.getAudience();
            break;
        default:
            throw new Exception("알 수 없는 장르");
    }
    return thisAmount;
}
```

> 바뀐 statement() 메소드 
```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        Play play = plays.get(performance);

        int thisAmount = amountFor(performance, play);

        // 포인트를 적립한다.
        volumeCredit += Math.max(performance.getAudience() - 30, 0);

        // 희극 관객 5명마다 추가 포인트를 제공핟나.
        if (play.getType().equals(PlayType.COMEDY)) {
            volumeCredit += Math.floor(performance.getAudience() / 5);
        }

        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",play.getName(), thisAmount / 100, performance.getAudience()));
        totalAmount += thisAmount;
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

이렇게 리팩토링을 할 때마다 곧바로 테스트를 돌려서 제대로 작동하는지 확인한다. 

아무리 간단한 리팩토링이라고 하더라도 리팩토링 후에는 항상 테스트 습관을 들이는 것이 바람직하다. 

사람은 실수하기 마련이다. 이를 명심하자. 

__한 가지를 수정할 때마다 테스트하면 오류가 생기더라도 변경 폭이 작기 때문에 문제를 찾고 해결하기가 쉽다.__

한 번에 너무 많이 수정하려다 실수하면 디버깅하기 어려워져서 작업 시간이 늘어난다. 

__리팩토링은 프로그램 수정을 작은 단계로 나눠 진행한다. 그래서 중간에 실수하더라도 버그를 쉽게 찾을 수 있다.__ 

깃을 쓴다고 한다면 이렇게 의미있는 작업을 한단계식 진행할때마다 커밋을 하면 된다. 

(그 다음 Pull Request 를 보낸다고 한다면 여러 개의 커밋으로 이뤄진 하나의 큰 단위의 작업인 브런치를 새로 파서 하나의 커밋으로 만들어서 요청 보내면 되겠다. Pull Request 의 원칙은 3 커밋을 넘지 않도록 하는게 좋으므로.)

함수 추출하기는 흔히 IDE 에서 자동으로 수행해준다. 

__그러므로 이런 기능을 하는 단축키를 알아두는게 좋다.__ 

__함수를 추출하고 나면 추출된 함수 코드를 자세히 들여다보면서 지금보다 명확하게 표현할 수 있는 간단한 방법은 없는지 검토한다.__ 

가장 먼저 변수의 이름을 더 명확하게 바꿔보자. 

thisAmount 의 이름은 result 로 변경하는게 가능하다. 

나는 함수의 반환 값에는 항상 result 라는 이름을 쓴다. 

이번에도 마찬가지로 테스트를 돌려보자. 

다음으로는 인수의 이름을 변경해보자. performance 를 aPerformance 로 변경해보자. 

(이는 자바스크립트 코딩이라서 타입을 명확하게 들어내도록 이름을 이렇게 변경한 것이다. 위 코드는 자바로 작성되어있으므로 그렇게 하지 않아도 된다. 마틴 파울러는 매개변수의 역할이 뚜렷하지 않을 때는 부정 관사를 붙인다고 한다.)   

이렇게 이름을 바꿀 필요는 있을까?

물론이다. 좋은 코드는 하는 일이 명확히 보여야하고 이름은 이에 크게 기여한다. 

다음으로 play 매개변수의 이름을 바꿀 차례다. 그런데 이 변수는 좀 다르게 처리해야한다. 

### Play 변수 제거하기 

amountFor() 함수를 다시 천천히 살펴보자. performance 파라미터는 statement() 함수에서 반복문을 돌때마다 새로운 값으로 된다. 

반면에 play 는 performance 의 값을 기준으로 plays 에서 계산되는 변수다. 즉 performance 로 부터 계산되는 변수기 때문에 애초에 변수로 만들 필요는 없다. 

그냥 계산해주는 함수를 만들면 된다. 

나는 긴 함수를 잘게 쪼갤 때마다 play 같은 변수는 최대한 제거한다. 이런 임시 변수들 때문에 로컬 범위에 존재하는 이름이 늘어나서 추출 작업이 복잡해지기 때문이다. (즉 변수를 많이 만들면 함수 추출이 어렵기 때문에 계산된 결과를 반영해주는 변수는 함수로 바꾼다는 뜻인 ) 

이 방법은 __임시 변수를 질의 함수로 바꾸기__ 기법을 사용해서 해결한다. 

> playFor 메소드 

```java
private Play playFor(Plays plays, Performance performance) {
    return plays.get(performance);
}   
```

> statement 메소드 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        int thisAmount = amountFor(performance, plays);

        // 포인트를 적립한다.
        volumeCredit += Math.max(performance.getAudience() - 30, 0);

        // 희극 관객 5명마다 추가 포인트를 제공핟나.
        if (playFor(plays, performance).getType().equals(PlayType.COMEDY)) {
            volumeCredit += Math.floor(performance.getAudience() / 5);
        }

        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), thisAmount / 100, performance.getAudience()));
        totalAmount += thisAmount;
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

> amountFor 메소드 

```java
private int amountFor(Performance performance, Plays plays) throws Exception {
    int result;
    switch (playFor(plays, performance).getType()) {
        case TRAGEDY:
            result = 40000;
            if (performance.getAudience() > 30) {
                result += 1000 * (performance.getAudience() - 30);
            }
            break;
        case COMEDY:
            result = 30000;
            if (performance.getAudience() > 20) {
                result += 10000 + 500 * (performance.getAudience() - 20);
            }
            result += 300 * performance.getAudience();
            break;
        default:
            throw new Exception("알 수 없는 장르");
    }
    return result;
}
```

이렇게 지역 변수를 제거해서 얻는 가장 큰 장점은 함수 추출하기 작업이 훨씬 쉬워진다는 것이다. 

유효 범위를 신경써야 할 대상이 줄어들기 때문이다. 

이제 statement() 함수로 돌아가서 보자. amountFor 함수로 thisAmount 변수를 대체할 수 있고 thisAmount 값이 이제 변하지 않으니 인라인 함수로 바꿀 수 있다. 

그러므로 따라서 __변수 인라인하기__ 를 적용한다. 

````java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        // 포인트를 적립한다.
        volumeCredit += Math.max(performance.getAudience() - 30, 0);

        // 희극 관객 5명마다 추가 포인트를 제공핟나.
        if (playFor(plays, performance).getType().equals(PlayType.COMEDY)) {
            volumeCredit += Math.floor(performance.getAudience() / 5);
        }

        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
````

이제 변수가 많이 줄었다. 그러므로 적립 포인트 계산 부분을 추출하기가 더 쉬워졌다.  
 
이제 처리해야할 변수가 두 개 더 남아있다. performance 를 간단히 전달만 하면 된다. 

> volumeCreditFor 메소드 

```java
private int volumeCreditFor(Plays plays, Performance performance) {
    int volumeCredit = 0; 
    
    // 포인트를 적립한다.
    volumeCredit += Math.max(performance.getAudience() - 30, 0);

    // 희극 관객 5명마다 추가 포인트를 제공핟나.
    if (playFor(plays, performance).getType().equals(PlayType.COMEDY)) {
        volumeCredit += Math.floor(performance.getAudience() / 5);
    }
    
    return volumeCredit;
}
```

> statement 메소드

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        volumeCredit += volumeCreditFor(plays, performance);

        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

그 다음 아까와 마찬가지로 새로 추출한 함수에서 변수 이름을 바꾼다. 

> volumeCreditsFor 메소드 

```java
private int volumeCreditFor(Plays plays, Performance performance) {
    int result = 0;

    result += Math.max(performance.getAudience() - 30, 0);

    if (playFor(plays, performance).getType().equals(PlayType.COMEDY)) {
        result += Math.floor(performance.getAudience() / 5);
    }

    return result;
}
```

### volumeCredits 변수 제거하기 

다음으로 살펴볼 변수는 statement 메소드에 있는 volumeCredits 인데 이 변수는 반복문을 돌 때마다 값을 누적시킨다. 

이 변수를 메소드로 같이 빼내고 싶지만 다른 함수들과 변수들도 같이 있기 때문에 빼내기 쉽지않다.

이때 사용하는 방법은 __반복문 쪼개기__ 라는 방법을 사용한다. (이 방법의 트레이드 오프는 한 반복문에서 모든 처리를 하는게 아니라 반복을 여러번 돌리는 것이므로 성능상으로는 단점이 있겠다. 대신에 얻는 장점으로는 코드의 가독성이 있겠지.)

> statement 메소드 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    int volumeCredit = 0;

    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }

    for (Performance performance : invoice.getPerformances()) {
        volumeCredit += volumeCreditFor(plays, performance);
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

이어서 __문장 슬라이스__ 라는 방법을 이용해서 변수의 위치를 옮긴다. 

> statement 메소드 
>
```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;

    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }

    int volumeCredit = 0;
    for (Performance performance : invoice.getPerformances()) {
        volumeCredit += volumeCreditFor(plays, performance);
    }

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

volumeCredits 값 갱신과 관련된 문장들을 한데다 모우면 __임시 변수를 질의 함수로 바꾸기__ 가 가능해진다. 

그러면 다음과 같이 된다. 

> totalVolumeCredits() 메소드 

````java
private int totalVolumeCredits(Invoice invoice, Plays plays) {
    int volumeCredit = 0;
    for (Performance performance : invoice.getPerformances()) {
        volumeCredit += volumeCreditFor(plays, performance);
    }
    return volumeCredit;
}
````

> statement() 메소드

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;

    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }

    int volumeCredit = totalVolumeCredits(invoice, plays);

    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", volumeCredit));
    return result.toString();
}
```

이렇게 임시 변수를 질의 함수로 바꿨다면 변수를 함수로 바꾸는 __인라인 기법__ 을 사용할 수 있다. 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;

    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        // 청구 내역을 출력한다.
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        totalAmount += amountFor(performance, plays);
    }
    
    result.append(String.format("총액: $%d\n",totalAmount / 100));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
    return result.toString();
}
```

여기서 잠시 멈추고 방금 한 일에 대해 생각해보자. 

반복문을 쪼개서 성능이 느려지지 않을까? 라는 걱정을 할 수 있다. 

이처럼 반복문이 중복되는 것을 꺼려하는 이들이 많지만 이 정도 중복은 성능에 미치는 영향이 미비할때가 많다. 

실제로 리팩터링 전과 후의 실행 시간을 측정해보면 차이를 거의 느끼지 못할 것이다. 

그리고 똑똑한 컴파일러들은 최신 캐싱 기법을 무장하고 있어서 차이가 없도록 만들어 줄 것이다. 

하지만 이처럼 리팩토링이 성능에 상당히 영향을 줄 수도 있다. 

__그런 경우라도 나는 개의치 않고 리팩토링 한다. 잘 다듬어진 코드여야 성능의 개선 작업도 훨씬 수월하기 때문이다.__

리팩터링 과정에서 성능이 크게 떨어졌다면 리팩토링 후에 시간을 내서 성능을 개선하면 된다. 

리팩토링의 효과로 인해 더 깔끔하면서 더 빠른 코드를 얻을 확률이 높다. 

정리하자면 리팩토링으로 인한 성능 문제에 대한 나의 조언은 __특별한 경우가 아니라면 일단 무시하라.__ 라는 것이다. 

리팩토링 때문에 성능이 떨어졌다면 하던 리팩토링을 마무리하고 성능을 올리면 된다. 

그리고 리팩토링 중간에 테스트가 실패하고 원인을 바로 찾지 못한다면 가장 최근의 커밋으로 돌아가서 테스트에 실패한 리팩토링의 단계르 더 작게 나눠서 다시 시도하면 된다. (이처럼 자주 커밋을 하는게 좋겠다.)

다음으로 totalAmount 도 앞에서와 똑같은 절차로 제거한다. 

먼저 반복문을 쪼개고, 변수 초기화 문장을 옮긴 다음에 함수를 추출한다. 

그 다음 인라인 함수로 만들면 된다. 

> totalAmount() 메소드  

```java
private int totalAmount(Invoice invoice, Plays plays) throws Exception {
    int totalAmount = 0;
    for (Performance performance : invoice.getPerformances()) {
        totalAmount += amountFor(performance, plays);
    }
    return totalAmount / 100;
}
```

> statement 메소드 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(invoice, plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
    return result.toString();
}
```

***

## 1.5 중간 점검: 난무하는 중첩 함수 

여기서 잠시 멈춰 서서 지금까지 리팩토링한 결과를 살펴보자. 

> 전체 메소드 구조 

```java
public class Statement {
    public String statement(Invoice invoice, Plays plays) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
        for (Performance performance : invoice.getPerformances()) {
            result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
        }

        result.append(String.format("총액: $%d\n",totalAmount(invoice, plays)));
        result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
        return result.toString();
    }

    private int totalAmount(Invoice invoice, Plays plays) throws Exception {
        int totalAmount = 0;
        for (Performance performance : invoice.getPerformances()) {
            totalAmount += amountFor(performance, plays);
        }
        return totalAmount / 100;
    }

    private int totalVolumeCredits(Invoice invoice, Plays plays) {
        int volumeCredit = 0;
        for (Performance performance : invoice.getPerformances()) {
            volumeCredit += volumeCreditFor(plays, performance);
        }
        return volumeCredit;
    }

    private int volumeCreditFor(Plays plays, Performance performance) {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);

        if (playFor(plays, performance).getType().equals(PlayType.COMEDY)) {
            result += Math.floor(performance.getAudience() / 5);
        }

        return result;
    }

    private Play playFor(Plays plays, Performance performance) {
        return plays.get(performance);
    }

    private int amountFor(Performance performance, Plays plays) throws Exception {
        int result;
        switch (playFor(plays, performance).getType()) {
            case TRAGEDY:
                result = 40000;
                if (performance.getAudience() > 30) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                result = 30000;
                if (performance.getAudience() > 20) {
                    result += 10000 + 500 * (performance.getAudience() - 20);
                }
                result += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }
        return result;
    }
}

```

statement() 메소드의 경우 전체 줄이 7 줄 밖에 없다. 

계산 로직은 모두 여러 개의 보조 함수로 빼냈다. 

결과적으로 각 계산 과정은 물론 전체 흐름을 이해하기가 훨씬 쉬워졌다. 

***

## 1.6 계산 단계와 포맷팅 단계 분리하기 

지금까지는 프로그램의 논리적인 요소를 파악하기 쉽도록 코드의 구조를 나누고 보강하는데 주력으로 리팩토링 했다. 

이는 리팩토링 초기에 주로 수행하는 일이다.

복잡하게 얽힌 덩어리를 잘게 쪼개는 작업은 이름 짓기 만큼이나 중요하다. 

골격은 충분히 개선댔으니 이제 원하던 기능 변경, 즉 statement() 의 HTML 버전을 만드는 작업을 살펴보자. 

여러 각도에서 볼 때 확실히 처음 코드보다 작업하기 편해졌다. 

계산 코드가 모두 분리됐기 때문에 일곱 줄짜리 최상단 코드에 대응하는 HTML 버전만 작성하면 된다. 

현재 statement() 메소드 안에는 텍스트 버전만 들어있다. 물론 이 코드를 그대로 복사해서 이용하면 htmlStatement() 를 쉽게 만들 수 있다.

이 방법은 코드의 중복을 야기하므로 좋진 않다. 

나는 텍스트 버전과 HTML 버전 함수 모두가 똑같은 계산 함수들을 사용하게 만들고 싶다. 

다양한 해결책 중 내가 가장 선호하는 방식은 __단계 쪼개기__ 다.(겹치는 부분과 다른 부분을 나누는 방식같다. 다른 부분이 아마 확장성과 관련된 부분이곘지.)

첫 단계에서는 statement() 에 필요한 데이터를 처리하는 부분으로 하고 

두 번째 단계에서는 앞서 처리한 결과를 텍스트나 HTML 로 표현하도록 한다. 

다시 말해서 첫 번째 단계에서는 두 번째 단계로 전달할 중간 데이터 구조를 생성하는 것이다. 

단계를 쪼개려면 먼저 두 번째 단계가 될 코드들을 __함수 추출하기__ 로 뽑아내야 한다. 

이예에서는 두 번째 단계가 청구 내역을 출력하는 코드인데 현재까지 작성한 statement 에서는 본문 전체가 해당한다. 

> renderPlainText 메소드 

```java
private String renderPlainText(Invoice invoice, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(invoice, plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
    return result.toString();
}
```

> statement() 메소드 

```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    return renderPlainText(invoice, plays);
}
```

항상 하듯이 수정한 코드를 컴파일-테스트-커밋 한다. 

다음으로 중간 데이터 구조 역할을 할 객체를 만들어서 renderPlainText() 에 인수로 전달한다. 

> statement 메소드와 renderPlainText 메소드

````java
public String statement(Invoice invoice, Plays plays) throws Exception {
    StatementData statementData = new StatementData(invoice, plays); 
    return renderPlainText(statementData, invoice, plays);
}

private String renderPlainText(StatementData statementData, Invoice invoice, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", invoice.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(invoice, plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
    return result.toString();
}
````

이제 renderPlainText 에 전달되는 다른 두 인수 Invoice 와 Plays 의 데이터를 StatementData 로 하나씩 옮겨보면 된다. 

그러면 출력에 필요한 데이터는 모두 StatementData 로 옮겨지게 되고 출력은 출력에만 집중하면 된다. 

가장 먼저 고객정보로부터 중간 데이터 구조로 옮겨보자 이 작업도 컴파일-테스트-커밋 하면 된다. 

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String getCustomer() {
        return invoice.getCustomer(); 
    }
}
```

> renderPlainText() 메소드 

```java
private String renderPlainText(StatementData statementData, Invoice invoice, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", statementData.getCustomer()));
    for (Performance performance : invoice.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(invoice, plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(invoice, plays)));
    return result.toString();
}
```

같은 방식으로 공연 정보까지 중간 데이터 구조로 옮긴다면 Invoice 객체는 이제 renderPlainText() 에서 사라져도 된다. 

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String getCustomer() {
        return invoice.getCustomer();
    }
    
    public Invoice getInvoice() {
        return invoice; 
    }
    
    public List<Performance> getPerformances() {
        return invoice.getPerformances(); 
    }
}
```

> statement() 메소드와 renderPlainText() 메소드 
```java
public String statement(Invoice invoice, Plays plays) throws Exception {
    StatementData statementData = new StatementData(invoice, plays);
    return renderPlainText(statementData, plays);
}

private String renderPlainText(StatementData statementData, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", statementData.getCustomer()));
    for (Performance performance : statementData.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",playFor(plays, performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(statementData.getInvoice(), plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(statementData.getInvoice(), plays)));
    return result.toString();
}
```

그렁 다음 renderPlainText() 안에서 playFor() 를 호출하던 부분을 중간 데이터를 사용하도록 바꾼다. 

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String getCustomer() {
        return invoice.getCustomer();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public List<Performance> getPerformances() {
        return invoice.getPerformances();
    }

    public Play playFor(Performance performance) {
        return plays.get(performance);
    }
}
```

> renderPlainText() 메소드 

```java
 private String renderPlainText(StatementData statementData, Plays plays) throws Exception {
    StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", statementData.getCustomer()));
    for (Performance performance : statementData.getPerformances()) {
        result.append(String.format("%s: $%d %d석\n",statementData.playFor(performance).getName(), amountFor(performance, plays) / 100, performance.getAudience()));
    }

    result.append(String.format("총액: $%d\n",totalAmount(statementData.getInvoice(), plays)));
    result.append(String.format("적립 포인트: %d점", totalVolumeCredits(statementData.getInvoice(), plays)));
    return result.toString();
}
```

이어서 amountFor 메소드도 비슷하게 옮긴다. 

계속해서 적립 포인트 계산하는 부분과 총합을 구하는 부분도 중간 데이터(statementData) 를 이용하도록 바꾼다. 

정리하면 다음과 같다. 

> Statement 클래스 

```java
public class Statement {
    public String statement(Invoice invoice, Plays plays) throws Exception {
        StatementData statementData = new StatementData(invoice, plays);
        return renderPlainText(statementData);
    }

    private String renderPlainText(StatementData statementData) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", statementData.getCustomer()));
        for (Performance performance : statementData.getPerformances()) {
            result.append(String.format("%s: $%d %d석\n",statementData.playFor(performance).getName(), statementData.amountFor(performance) / 100, performance.getAudience()));
        }

        result.append(String.format("총액: $%d\n", statementData.totalAmount()));
        result.append(String.format("적립 포인트: %d점", statementData.totalVolumeCredits()));
        return result.toString();
    }
}
```

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String getCustomer() {
        return invoice.getCustomer();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public List<Performance> getPerformances() {
        return invoice.getPerformances();
    }

    public Play playFor(Performance performance) {
        return plays.get(performance);
    }

    public int amountFor(Performance performance) throws Exception {
        int result;
        switch (playFor(performance).getType()) {
            case TRAGEDY:
                result = 40000;
                if (performance.getAudience() > 30) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                result = 30000;
                if (performance.getAudience() > 20) {
                    result += 10000 + 500 * (performance.getAudience() - 20);
                }
                result += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }
        return result;
    }

    public int totalAmount() throws Exception {
        int totalAmount = 0;
        for (Performance performance : invoice.getPerformances()) {
            totalAmount += amountFor(performance);
        }
        return totalAmount / 100;
    }

    public int totalVolumeCredits() {
        int volumeCredit = 0;
        for (Performance performance : invoice.getPerformances()) {
            volumeCredit += volumeCreditFor(performance);
        }
        return volumeCredit;
    }

    private int volumeCreditFor(Performance performance) {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);

        if (playFor(performance).getType().equals(PlayType.COMEDY)) {
            result += Math.floor(performance.getAudience() / 5);
        }

        return result;
    }
}
```

마지막으로 컴파일-테스트-커밋 후에 HTML 버전을 작성하면 된다. 

> renderHtml() 메소드 

```java
private String renderHtml(StatementData statementData) throws Exception {
    StringBuilder result = new StringBuilder(String.format("<h1> 청구내역 (고객명: %s)\n </h1>", statementData.getCustomer()));
    result.append("<table> \n");
    result.append("<tr><th> 연극 </th> <th>좌석 수</th> <th>금액</th>"); 
    for (Performance performance : statementData.getPerformances()) {
        result.append(String.format("<tr><td> %s: </td> <td> $%d </td> <td> %d석 </td></tr>\n",statementData.playFor(performance).getName(), statementData.amountFor(performance) / 100, performance.getAudience()));
    }
    result.append("</table>\n");
            
    result.append(String.format("총액: $%d\n", statementData.totalAmount()));
    result.append(String.format("적립 포인트: %d점", statementData.totalVolumeCredits()));
    return result.toString();
}
```

***

## 1.7 중간 점검: 두 파일(과 두 단계)로 분리됨

잠시 쉬면서 코드의 상태를 점검해보자. 현재 코드는 두 개의 파일로 구성된다. 

> Statement 클래스 

```java
public class Statement {
    public String statement(Invoice invoice, Plays plays) throws Exception {
        StatementData statementData = new StatementData(invoice, plays);
        return renderPlainText(statementData);
    }

    private String renderPlainText(StatementData statementData) throws Exception {
        StringBuilder result = new StringBuilder(String.format("청구내역 (고객명: %s)\n", statementData.getCustomer()));
        for (Performance performance : statementData.getPerformances()) {
            result.append(String.format("%s: $%d %d석\n",statementData.playFor(performance).getName(), statementData.amountFor(performance) / 100, performance.getAudience()));
        }

        result.append(String.format("총액: $%d\n", statementData.totalAmount()));
        result.append(String.format("적립 포인트: %d점", statementData.totalVolumeCredits()));
        return result.toString();
    }

    private String renderHtml(StatementData statementData) throws Exception {
        StringBuilder result = new StringBuilder(String.format("<h1> 청구내역 (고객명: %s)\n </h1>", statementData.getCustomer()));
        result.append("<table> \n");
        result.append("<tr><th> 연극 </th> <th>좌석 수</th> <th>금액</th>");
        for (Performance performance : statementData.getPerformances()) {
            result.append(String.format("<tr><td> %s: </td> <td> $%d </td> <td> %d석 </td></tr>\n",statementData.playFor(performance).getName(), statementData.amountFor(performance) / 100, performance.getAudience()));
        }
        result.append("</table>\n");

        result.append(String.format("총액: $%d\n", statementData.totalAmount()));
        result.append(String.format("적립 포인트: %d점", statementData.totalVolumeCredits()));
        return result.toString();
    }
}
```

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    public String getCustomer() {
        return invoice.getCustomer();
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public List<Performance> getPerformances() {
        return invoice.getPerformances();
    }

    public Play playFor(Performance performance) {
        return plays.get(performance);
    }

    public int amountFor(Performance performance) throws Exception {
        int result;
        switch (playFor(performance).getType()) {
            case TRAGEDY:
                result = 40000;
                if (performance.getAudience() > 30) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                result = 30000;
                if (performance.getAudience() > 20) {
                    result += 10000 + 500 * (performance.getAudience() - 20);
                }
                result += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }
        return result;
    }

    public int totalAmount() throws Exception {
        int totalAmount = 0;

        for (Performance performance : invoice.getPerformances()) {
            totalAmount += amountFor(performance);
        }
        return totalAmount / 100;
    }

    public int totalVolumeCredits() {
        int volumeCredit = 0;
        for (Performance performance : invoice.getPerformances()) {
            volumeCredit += volumeCreditFor(performance);
        }
        return volumeCredit;
    }

    private int volumeCreditFor(Performance performance) {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);

        if (playFor(performance).getType().equals(PlayType.COMEDY)) {
            result += Math.floor(performance.getAudience() / 5);
        }

        return result;
    }
}
```

처음보다 코드량이 늘어났지만 __모듈화__ 를 통해 전체 로직이 보다 명료해졌다. 

이렇게 모듈화하면 각 부분이 하는 일과 그 부분들이 맞물려서 돌아가는 과정을 이해하기가 쉬워진다. 

간결함이 지혜의 정수일지 몰라도 프로그래밍에서만큼은 명료함이 진화할 수 있는 소프트웨어의 정수다. 

모듈화한 덕분에 계산 코드를 중복하지 않고도 HTMl 버전을 만들 수 있었다. 

다음과 같은 말이 있다. 

```text
캠필자들에게는 도착했을 대보다 깔끔하게 정돈하고 떠난다 라는 규칙이 있다. 프로그래밍도 마찬가지다. 항시 코드베이스를 작업 시작 전보다 건강하게 만들어 놓고 떠나야한다. 
```

출력 로직을 더 간결하게 만들 수도 있지만 일단 이 정도에서 멈추겠다. 

나는 항상 리팩토링과 기능 추가 사이의 균형을 맞출려고 한다. 

현재 코드에서는 리팩토링이 그다지 절실하게 느껴지지 않을 수 있지만 어느정도 균형점을 찾을 수 있다. 

이럴 때 나는 __항시 코드베이스를 작업 시작 전보다 더 건강하게 고친다__ 라는 캠픽 규칙의 변형 버전을 적용시킨다. 

***

## 1.8 다형성을 활용해 계산 코드 재구성하기 

이번에는 연극 장르를 추가하고 장르마다 공연료와 적립 포인트 계산법을 다르게 저장하도록 기능을 수정해보자. 

현재 상태에서 코드를 변경하려면 이 계산을 수행하는 함수에서 조건문을 수정해야한다. 

amountFor 메소드를 보면 연극 장르에 따라 계산 방법이 달라진다는 사실을 알 수 있는데,

이런 형태의 조건부 로직은 코드 수정 횟수가 늘어날수록 골칫거리고 전략하기 쉽다. 

이를 방지하려면 프로그래밍 언어가 제공하는 구조적인 요소로 보완해야 한다. 

조건부 로직을 명확한 구조로 보완하는 방법은 다양하다. 

여기서는 객체지향 핵심 특성인 다형성을 활용하도록 하겠다. 

이번 작업의 목표는 상속 계층을 구성해서 희극 서브 클래스와 비극 서브 클래스가 각자의 구체적인 계산 로직을 정의하도록 해서 해결하곘다. 

호출하는 쪽에서는 다형성 버전의 공연료 계산 함수를 호출하기만 하면 되고, 희극이나 비극이냐에 따라 정확한 계산 로직을 연결하는 작업은 언어차원에서 지원받도록 한다. 

적립 포인트도 비슷한 구조로 만들 것이다. 이 과정에서 몇 가지 리팩토링 기법을 사용하는데, 그 중 핵심은 __조건부 로직을 다형성으로 바꾸느 것이다.__ 

이 리팩토링 기법을 사용할려면 상속 계층부터 정의해야 한다. 즉 공연료와 적립 포인트 계산 함수를 담을 클래스가 필요하다. 

### 공연료 계산기 만들기

여기서는 공연료를 계산하는 amountFor() 함수를 공연마다 자기가 공연료를 계산하는 PerformanceCalculator 를 만들어서 거기에 옮기는 작업부터 하곘다. (타입마다 조건마다 구현이 약간씩 다른 경우에 클래스와 상속을 이용한 코드로 리팩토링을 할 수 있구나.)

여기서는 함수를 클래스로 옮기는 __함수 옮기기__ 기법을 사용한다. 

> PerformanceCalculator 클래스 

```java
public class PerformanceCalculator {
    private Performance performance;
    private Play play;

    public PerformanceCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    public int amountFor() throws Exception {
        int result;
        switch (play.getType()) {
            case TRAGEDY:
                result = 40000;
                if (performance.getAudience() > 30) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                result = 30000;
                if (performance.getAudience() > 20) {
                    result += 10000 + 500 * (performance.getAudience() - 20);
                }
                result += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }
        return result;
    }
}
```

> StatementData 클래스 의 amountFor() 메소드 

````java
public int amountFor(Performance performance) throws Exception {
    return new PerformanceCalculator(performance, playFor(performance)).amountFor();
}
````

이렇게 수정한 다음에 컴파일-테스트-커밋 단계를 실행한다. 

그 다음 적립 포인트를 계산하는 volumeCreditFor 메소드도 PerformanceCalculator 클래스로 옮긴다. 

> PerformanceCalculator 클래스 

```java
public class PerformanceCalculator {
    private Performance performance;
    private Play play;

    public PerformanceCalculator(Performance performance, Play play) {
        this.performance = performance;
        this.play = play;
    }

    public int amountFor() throws Exception {
        int result;
        switch (play.getType()) {
            case TRAGEDY:
                result = 40000;
                if (performance.getAudience() > 30) {
                    result += 1000 * (performance.getAudience() - 30);
                }
                break;
            case COMEDY:
                result = 30000;
                if (performance.getAudience() > 20) {
                    result += 10000 + 500 * (performance.getAudience() - 20);
                }
                result += 300 * performance.getAudience();
                break;
            default:
                throw new Exception("알 수 없는 장르");
        }
        return result;
    }

    public int volumeCreditFor() {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);

        if (play.getType().equals(PlayType.COMEDY)) {
            result += Math.floor(performance.getAudience() / 5);
        }

        return result;
    }
}
```

> StatementData 클래스의 volumeCreditFor 메소드 

```java
private int volumeCreditFor(Performance performance) {
    return new PerformanceCalculator(performance, playFor(performance)).volumeCreditFor();
}
```

### 공연료 계산기를 다형성 버전으로 만들기 

클래스에 이제 로직을 담았으니 이제 다형성을 지원하도록 해보자. 

먼저 타입 코드 대신 서브클래스를 사용하도록 변경해야 한다. 이는 __타입 코드를 서브 클래스로 바꾸기__ 기법이 사용한다. 

이렇게 하려면 PerformanceCalculator 의 서브 클래스들을 준비하고 그 중에서 적합한 서브 클래스를 사용하게 만들어야 한다. 

이를 위해 PlayType 에 맞게 적절한 Calculator 를 생성해주는 PerformanceCalculatorFactory 를 만들고 이를 사요하도록 했따. 

> PerformanceCalculatorFactory 클래스  

```java
public PerformanceCalculator createPerformanceCalculator(Performance performance, Play play) throws Exception {
    switch (play.getType()) {
        case TRAGEDY:
            return new TragedyCalculator(performance, play);
        case COMEDY:
            return new ComedyCalculator(performance, play);
        default:
            throw new Exception("알 수 없는 타입입니다.");
    }
}
```

> TragedyCalculator and ComedyCalculator 클래스 

````java
public class ComedyCalculator extends PerformanceCalculator {
    public ComedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }
}

public class TragedyCalculator extends PerformanceCalculator {

    public TragedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }
}
````

> StatementData 클래스 

```java
public class StatementData {
    private Invoice invoice;
    private Plays plays;
    private PerformanceCalculatorFactory performanceCalculatorFactory;

    public StatementData(Invoice invoice, Plays plays) {
        this.invoice = invoice;
        this.plays = plays;
        this.performanceCalculatorFactory = new PerformanceCalculatorFactory();
    }

    public String getCustomer() {
        return invoice.getCustomer();
    }

    public List<Performance> getPerformances() {
        return invoice.getPerformances();
    }

    public Play playFor(Performance performance) {
        return plays.get(performance);
    }

    public int amountFor(Performance performance) throws Exception {
        return performanceCalculatorFactory.createPerformanceCalculator(performance, playFor(performance)).amountFor();
    }


    public int totalAmount() throws Exception {
        int totalAmount = 0;

        for (Performance performance : invoice.getPerformances()) {
            totalAmount += amountFor(performance);
        }
        return totalAmount / 100;
    }

    public int totalVolumeCredits() throws Exception {
        int volumeCredit = 0;
        for (Performance performance : invoice.getPerformances()) {
            volumeCredit += volumeCreditFor(performance);
        }
        return volumeCredit;
    }

    private int volumeCreditFor(Performance performance) throws Exception {
        return performanceCalculatorFactory.createPerformanceCalculator(performance, playFor(performance)).volumeCreditFor();
    }
}
```

그 다음 공연에 맞게 계산하는 로직을 TragedyCalculator 와 ComedyCalculator 로 옮기면 된다. 

정의하면 다음과 같다. 

> TragedyCalculator 클래스 

```java
public class TragedyCalculator extends PerformanceCalculator {

    public TragedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    @Override
    public int amountFor() throws Exception {
        int result = 40000;

        if (performance.getAudience() > 30) {
            result += 1000 * (performance.getAudience() - 30);
        }

        return result;
    }

    @Override
    public int volumeCreditFor() {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);

        return result;
    }
}
```

> ComedyCalculator 클래스 

```java
public class ComedyCalculator extends PerformanceCalculator {
    public ComedyCalculator(Performance performance, Play play) {
        super(performance, play);
    }

    @Override
    public int amountFor() {
        int result = 30000;
        if (performance.getAudience() > 20) {
            result += 10000 + 500 * (performance.getAudience() - 20);
        }
        result += 300 * performance.getAudience();
        return result;
    }

    @Override
    public int volumeCreditFor() {
        int result = 0;

        result += Math.max(performance.getAudience() - 30, 0);
        result += Math.floor(performance.getAudience() / 5);

        return result;
    }
}
```

이렇게 나눔으로써 확장성 측면에서 좀 더 명확해졌다. 

이번 예를 보면 서브 클래스를 언제 사용하면 좋을지 감이 잡힐 것이다. 

여기서는 두 개의 함수 amountFor() 함수와 volumeCreditsFor() 의 조건부 로직을 각각의 서브 클래스로 옮겼다. 

같은 타입의 다형성을 기반으로 실행하는 함수가 많을수록 이렇게 구성하는 쪽이 유리하다. 

***

## 1.10 마치며

간단한 예였지만 리팩토링이 무엇인지 감을 잡았길 바란다. 

__함수 추출하기__

__변수 인라인하기__

__함수 옮기기__

__조건부 로직을 다형성으로 바꾸기__ 

를 비롯한 다양한 리팩토링 기법을 선보였다. 

이번 장에서는 리팩토링을 크게 세 단계로 진행했다. 

먼저 원본 함수를 중첩 함수 여러개로 나눴다. (코드 구조를 분리한 단계로 가장 기본적인 단계의 리팩토링을 말한다.)

다음으로 __단계 쪼개기__ 를 사용해서 각 기능별로 모듈화를 해서 중복을 제거했다. 

마지막으로 계산 로직을 다형성으로 표시했다. 각 단계에서 코드 구조를 보강했고 코드가 무슨 일을 하는지 보다 더 명확해졌다.

__좋은 코드를 가늠하는 확실한 방법은 '얼마나 수정하기 쉬운가' 이다.__

이 책은 코드를 개선하는 방법을 다룬다. 

그런데 프로그래머 사이에서 어떤 코드가 좋은 코드인가에 대한 의견은 분분하다. 

내가 선호하는 __적절한 이름의 작은 함수들__ 로 만드는 방식에 대해 반대하는 사람도 분명 있을 것이다. 

미적인 관점으로 접근하면 좋고 나쁨을 넘어서 명확하지 않다. 어떠한 지침도 세울 수 없다. 

하지만 '수정하기 쉬운 코드' 는 분명히 좋은 코드의 관점을 제공해준다. 

코드는 명확해야 한다. 코드를 수정해야 할 상황이 오면 고쳐야 할 곳을 쉽게 찾을 수 있고 오류 없이 빠르게 수정할 수 있어야 한다. 

건강한 코드 베이스는 생산성을 극대화 하고, 고객에게 필요한 기능을 더 빠르게 저렴한 비용으로 제공하도록 해준다.

이번 예시를 통해 배울 수 있는 가장 중요한 것은 바로 리팩토링의 리듬이다. 

사람들에게 내가 리팩토링을 하는 과정을 보여줄 때마다 각 단계를 굉장히 잘게 나누고 매번 컴파일하고 테스트하여 작동하는 상태로 유지한다는 사실에 놀란다. 

나도 캔트 벡이 이렇게 작업하는 것을 처음 봤을 때 비슷한 심정이다. 

리팩토링을 효과적으로 하는 핵심은, 단계를 잘게 나눠야 더 빠르게 처리할 수 있고, 코드는 절대 깨지지 않으며ㅑ 이러한 작은 단계들이 모여서 상당히 큰 변화를 이룰 수 있다는 사실을 꺠닫는 것이다. 
 