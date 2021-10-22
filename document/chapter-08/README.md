# 기능 이동

이전까지는 프로그램의 요소를 생성하거나 제거하고 이름을 변경하는 쪽으로 리팩토링을 진행했다. __(코드의 의도를 정확히 알도록 리팩토링을 진행.)__

여기에서는 다른 컨택스트로 옮겨서 적절한 모듈을 만들어주는 방향의 리팩토링을 진행한다.

다른 클래스나 모듈로 옮길떄는 __함수 옮기기 (8.1 절)__

필드를 옮기는 경우라면 __필드 옮기기 (8.2 절)__

문장을 함수 안이나 바깥으로 옮기는 경우라면 __문장을 함수로 옮기기 (8.3 절)__

문장을 호출한 곳으로 옮기는 경우는 경우라면 __문장을 호출한 곳으로 옮기기 (8.4 절)__

한 덩어리의 문장이 기존의 함수와 같은 역할을 하는 경우가 있다면 이를 제거하는 기법인 __인라인 코드를 함수 호출로 바꾸기 (8.5 절)__ 

같은 함수 안에서 옮기는 경우라면 __문장 슬라이스 (8.6 절)__

반복문이 단 하나의 일만 수행하도록 보장하는 변경인 __반복문 쪼개기 (8.7 절)__

반복문을 더 이해하기 쉽게 파이프라인으로 바꾸는 방법인 __반복문을 파이프라인으로 바꾸기 (8.8 절)__

그리고 마지막인 죽은 코드를 제거하는 리팩토링인 __죽은 코드 제거하기 (8.9 절)__ 이 있다. 

***

## 8.1 함수 옮기기

### 배경 

함수 옮기기 기법은 모듈성을 적용하기 위한 리팩토링 기법이다.

어떤 악취로 부터 리팩토링을 적용하는가?

- 뒤엉킨 변경 (목적: 큰 역할을 가진 모듈은 변경 포인트가 너무나도 많다. 그래서 이를 잘게 나눠서 하나의 책임,역할을 가진 모듈로 바꿔줘야 한다.)
- 산탄총 수술 (목적: 모듈화가 안되어있다면 이것도 변경 포인트가 너무나도 많다. 그래서 이를 모아서 하나의 책임, 역할을 가진 모듈로 만들어줘야 한다.)
- 기능 편애 (목적: 같은 모듈에서의 상호작용은 늘리고 다른 모듈과의 상호 작용은 줄이기 위한 것.)
- 임시 필드 (목적: 클래스 안의 임시필드는 오해의 요소이므로 이를 위해 적절한 모듈, 클래스를 만들어줘야 한다.)
- 메시지 체인 (목적: 중재자와 너무 많은 결합이 있다면 모듈화가 잘 되어있지 않다는 것이므로 이를 해결해야 한다.)
- 내부자 거래 (목적: 모듈끼리 불필요한 결합이 있다면 이를 없애도록 하는 것.)
- 서로 다른 인터페이스와 대안들 (목적: 서로 비슷한 클래스인 경우 같은 인터페이스들 바라보도록 하는 것. 이를 통해 대체 가능하도록, 느슨한 결합을 할 수 있도록)
- 데이터 클래스 (목적: 데이터 클래스도 적절한 동작을 가진 모듈로 만들도록.)

좋은 소프트웨어의 핵심은 모듈성 (modularity) 이다.

모듈성이 좋다라는 기준은 뭘까? 

- 서로 연관성이 있는 요소, 함수들이 모여있고 이들의 연관관계를 파악하기 쉽다라는 점. 

이렇게 모듈화가 잘되어있다면 어떤 점이 좋을까?

- 모듈끼리의 불필요한 결합이 줄어든다. 느슨한 결합을 지향할 수 있다. (느슨한 결합은 대체하기가 쉽고 코드의 수정이 쉽다.)
- 캡술화를 통해 불필요한 부분을 드러내지 않아도 된다. 필요한 부분만 보면 되므로 코드의 이해가 쉬워진다.

어떤 기준으로 그러면 함수 옮기기의 기준을 적용할 수 있을까?

- 대상 함수의 현재 컨택스트와 후보 컨택스트를 둘러보면서 비교하면 된다. (어떤 데이터를 가지고 있는지, 어떤 함수를 가지고 있는지 등을 비교하면서)

### 예시

예시로 Account 클래스와 이자를 계산하는 bankCharge() 메소드, 초과 인출 이자를 계산해주는 overdraftCharge() 메소드가 있다.

```java
public class Account {
    protected int daysOverdrawn;
    private AccountType type;

    public double bankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn > 0) result += overdraftCharge();
        return result;
    }

    private double overdraftCharge() {
        if (this.type.isPremium) {
            int bankCharge = 10;
            if (this.daysOverdrawn <= 7) {
                return bankCharge;
            }
            else {
                return bankCharge * (this.daysOverdrawn - 7) * 0.85;
            }
        }
        else {
            return this.daysOverdrawn * 1.75;
        }
    }
}
```

여기서 이제 계좌 종류에 따라 이자를 책정하는 알고리즘이 달라지도록 고친다고 생각해보자.

그러면 통장의 유무에 따라 초과 인출 이자를 계산하는 overdraftCharge() 메소드가 AccountType 이라는 클래스에 옮겨지도록 하는게 맞을 수 있다.

그러므로 이를 옮긴다고 가정해보자.

먼저 Account 클래스에서 overdraftCharge() 를 호출하는 곳을 보고 어느 부분까지 AccountType 에 옮길 수 있는지 확인해보자. 

여기서는 daysOverdrawn 이라는 변수까지는 옮기기 힘들어보인다. 왜냐하면 이 변수는 계좌별로 달라지는 변수이기 때문이다. 

```java
public class AccountType {
    protected boolean isPremium;

    public double overdraftCharge(int daysOverdrawn) {
        if (this.isPremium) {
            int bankCharge = 10;
            if (daysOverdrawn <= 7) {
                return bankCharge;
            }
            else {
                return bankCharge * (daysOverdrawn - 7) * 0.85;
            }
        }
        else {
            return daysOverdrawn * 1.75;
        }
    }
}
```

이렇게 옮기고 나서 호출자가 달라졌으므로 약간의 수정을 해주자. 

그 다음 daysOverdrawn 을 매개변수로 받을지 Account 를 매개변수로 받을지 결정해야한다. 

아직은 계좌애서 다른 정보는 필요하지 않으므로 그냥 변수만 넘기면 될 것 같다.

그럼 이제 Account 에서 AccountType.overdraftCharge() 함수를 호출하도록 변경하자.

```java
public class Account {
    protected int daysOverdrawn;
    private AccountType type;

    public double bankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn > 0) result += overdraftCharge();
        return result;
    }

    private double overdraftCharge() {
        return type.overdraftCharge(this.daysOverdrawn);
    }
}
```

문제가 없고 위임 메소드인 Account.overdraftCharge() 를 인라인 할 지 결정하자. 인라인 한다면 다음과 같이 되겠다.

```java
public class Account {
    protected int daysOverdrawn;
    private AccountType type;

    public double bankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn > 0) result += type.overdraftCharge(this.daysOverdrawn);
        return result;
    }
}
```

***

## 8.2 필드 옮기기

### 배경 

프로그램은 동작을 구현하는 코드로 이뤄지지만 그 힘은 데이터 구조로부터 나온다.

잘 짜여진 데이터 구조는 직관적으로 어떠한 동작을 수행하는지 이해하기 쉽고 짜기 쉽다.

처음부터 데이터 구조를 올바르게 짜기가 어렵다.

설계를 열심히 해서 잘 짰다 하더라도 도메인 지식이 점점 쌓이면 더 적합한 데이터 구조가 보일수도 있다. 

그러므로 더 올바른 구조가 보인다면 그떄그때 리팩토링을 적용하는게 중요하다. (부채를 쌓지마라 라는 뜻인듯.)

필드 옮기기는 주로 더 큰 리팩토링을 하기 위한 수단으로 사용된다. 

필드를 하나 옮기면 그 필드를 사용하던 함수들도 같이 옮길 수 있으므로.

어떤 악취로부터 리팩토링을 적용하는가?

- 산탄총 수술
- 내부자 거래 

### 예시

예시로 고객 클래스 (Customer) 와 고객 계약 클래스 (CustomerContract) 가 있다고 보자.

```java
public class Customer {
    protected String name;
    protected double discountRate;
    protected CustomerContract customerContract;

    public Customer(String name, double discountRate) {
        this.name = name;
        this.discountRate = discountRate;
        this.customerContract = new CustomerContract(LocalDateTime.now());
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void becomePreferred() {
        discountRate += 0.3;
        // do something
    }

    public int applyAmount(int amount) {
        return Math.subtractExact(amount, (int) (amount * discountRate));
    }
}
```

여기서 할인율을 뜻하는 discountRate 를 CustomerContract 로 옮기고 싶다고 보자.

__가장 먼저 할 일은 옮길 필드를 캡슐화를 해놓는 것이다. (직접적인 접근부터 하나씩 없애기 위해서.)__

```java
public class Customer {
    protected String name;
    protected double discountRate;
    protected CustomerContract customerContract;

    public Customer(String name, double discountRate) {
        this.name = name;
        setDiscountRate(discountRate);
        this.customerContract = new CustomerContract(LocalDateTime.now());
    }

    private void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void becomePreferred() {
        setDiscountRate(getDiscountRate() + 0.3);
        // do something
    }

    public int applyAmount(int amount) {
        return Math.subtractExact(amount, (int) (amount * getDiscountRate()));
    }
}
```

```java
public class CustomerContract {
    protected LocalDateTime startDate;
    protected double discountRate;

    public CustomerContract(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
```

그 다음 Customer 클래스에 있는 discountRate 를 CustomerContract discountRate 를 사용하도록 옮기자.

````java
public class Customer {
    protected String name;
    protected double discountRate;
    protected CustomerContract customerContract;

    public Customer(String name, double discountRate) {
        this.name = name;
        setDiscountRate(discountRate);
        this.customerContract = new CustomerContract(LocalDateTime.now());
    }

    private void setDiscountRate(double discountRate) {
        this.customerContract.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return customerContract.discountRate;
    }

    public void becomePreferred() {
        setDiscountRate(getDiscountRate() + 0.3);
        // do something
    }

    public int applyAmount(int amount) {
        return Math.subtractExact(amount, (int) (amount * getDiscountRate()));
    }
}
````

이제 Customer 클래스에 있는 discountRate 를 지우자.

```java
public class Customer {
    protected String name;
    protected CustomerContract customerContract;

    public Customer(String name, double discountRate) {
        this.name = name;
        setDiscountRate(discountRate);
        this.customerContract = new CustomerContract(LocalDateTime.now());
    }

    private void setDiscountRate(double discountRate) {
        this.customerContract.discountRate = discountRate;
    }

    public double getDiscountRate() {
        return customerContract.discountRate;
    }

    public void becomePreferred() {
        setDiscountRate(getDiscountRate() + 0.3);
        // do something
    }

    public int applyAmount(int amount) {
        return Math.subtractExact(amount, (int) (amount * getDiscountRate()));
    }
}
```

***

## 8.3 문장을 함수로 옮기기

## 배경 
이 방법은 중복 코드를 제거하기 위해 하나의 함수로 합치는 리팩토링 기법이다.   

어떤 악취로 부터 리팩토링을 적용하는가?

- 그냥 중복 코드 제거 (3장과는 딱히 연관성 없음.)

중복 제거는 코드를 건강하게 만드는 가장 효과적인 방법 중 하나다.

만얃 어떤 함수를 호출한 이후에 앞 뒤로 같은 함수를 호출하는 일이 반복된다면 이를 합치는게 좋다.

이렇게 함치기 위해 문장을 옮길려면 합쳐지는 함수 즉 피호출 함수와 옮겨지는 문장이 한 몸이라는 확신이 있어야 한다.

한 몸 정도까지는 아니고 그냥 단순히 합쳐지는 경우가 꽤 많다면 그냥 새로운 함수를 추출하는게 낫다.

### 예시 

사진 관련 데이터를 HTML 로 내보내는 코드가 있다고 가정해보자. 

````java
public class Camera {
    public String renderPeron(OutputStream outputStream, Person person) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> %s </p>", person.name));
        result.append(renderPhoto(person.photo));
        result.append(String.format("<p> 제목: %s </p>", person.photo.title));
        result.append(emitPhotoData(person.photo));
        return result.toString();
    }

    public String renderPhoto(Photo photo) {
        return null;
    }

    public String photoDiv(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append("<div>");
        result.append(String.format("<p> %s </p>", photo.title));
        result.append(emitPhotoData(photo));
        result.append("</div>");
        return result.toString();
    }

    private String emitPhotoData(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }
}
````

이 코드에서는 emitPhotoData() 를 두군데서 호출하는데 모두 제목 코드 앞에다 온다는 점이다.

호출자가 단순히 하나였다면 그냥 emitPhotoData 의 코드를 붙여서 하나의 함수로 만들면 되지만 호출자가 두 개 이므로 새로운 함수를 추출하는 방법으로 가보자.

````java
public class Camera {
    public String renderPeron(OutputStream outputStream, Person person) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> %s </p>", person.name));
        result.append(renderPhoto(person.photo));
        result.append(zznew(person.photo));
        return result.toString();
    }

    public String renderPhoto(Photo photo) {
        return null;
    }

    public String photoDiv(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append("<div>");
        result.append(zznew(photo));
        result.append("</div>");
        return result.toString();
    }

    private String emitPhotoData(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }

    public String zznew(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 제목: %s </p>", photo.title));
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }
}
````

이렇게 새로운 함수를 추출하고 이 함수를 호출하도록 변경했다면 기존의 함수는 인라인하자.

```java
public class Camera {
    public String renderPeron(OutputStream outputStream, Person person) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> %s </p>", person.name));
        result.append(renderPhoto(person.photo));
        result.append(zznew(person.photo));
        return result.toString();
    }

    public String renderPhoto(Photo photo) {
        return null;
    }

    public String photoDiv(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append("<div>");
        result.append(zznew(photo));
        result.append("</div>");
        return result.toString();
    }
    
    public String zznew(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 제목: %s </p>", photo.title));
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }
}
```

그리고 함수 이름을 목적에 맞게 바꿔준다.

```java
public class Camera {
    public String renderPeron(OutputStream outputStream, Person person) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> %s </p>", person.name));
        result.append(renderPhoto(person.photo));
        result.append(emitPhotoData(person.photo));
        return result.toString();
    }

    public String renderPhoto(Photo photo) {
        return null;
    }

    public String photoDiv(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append("<div>");
        result.append(emitPhotoData(photo));
        result.append("</div>");
        return result.toString();
    }

    public String emitPhotoData(Photo photo) {
        StringBuilder result = new StringBuilder();
        result.append(String.format("<p> 제목: %s </p>", photo.title));
        result.append(String.format("<p> 위치: %s </p>", photo.location));
        result.append(String.format("<p> 날짜: %s </p>", photo.date.toString()));
        return result.toString();
    }
}
```

***

## 8.4 문장을 호출한 곳으로 옮기기

### 배경

함수는 프로그래머가 쌓아 올리는 추상화의 기본 빌딩 블록이다.

그런데 추상화라는 것이 항상 경계가 완벽하게 되는 것은 아니다.

코드베이스의 기능 범위가 달라진다먄 기존에 하나의 일만 수행하던 함수가 어느새 두 개 이상의 일을 수행하는 경우도 많다. __(오해의 여지가 있을 수 있는데 여기서는 기존의 코드에서 더 추가한 경우를 말하는게 아니다. 기존에 한 가지 일만 수행하도록 코드를 짰는데 어느 시점에 코드를 보니 역할이 커진 경우를 말하는 것.)__

즉 여러 곳에서 사용하던 함수가 일부 호출자에게는 다른 기능을 적용하도록 해야한다면 이 코드의 일부를 호출자에게 전달하는게 좋다. 

작은 변경이라면 문장을 호출한 곳으로 옮기는 것으로 충분하지만 호출자와 호출 대상의 경계가 명확하지 않는 경우라면 함수를 먼저 인라인 하고 (6.2 절) 문장 슬라이스 (8.6 절) 와 함수 추출하기 (6.1 절) 로 더 적합한 경계를 설정하면 된다.

### 절차

1. 호출자가 한 개 뿐이고 피호출자도 간단한 거라면 피호출자의 함수를 추출해서 호출자에게 넣고 테스트를 돌려본다. 성공한다면 끝이다.

2. 더 복잡한 상황에서는 이동하지 '않길' 원하는 코드를 함수로 추출한다. (6.1 절) 그 다음 검색하기 쉬운 이름으로 지어준다. __(이 방법은 더 안전한 방법으로 리팩토링을 하기 위한 것이다.)__

3. 원래 함수를 인라인 한다.

4. 추출한 함수의 이름을 기존 이름이나 더 나은 이름으로 변경한다. 


### 예시

호출자가 둘 뿐인 다음과 같은 상황을 생각해보자.

```java
public class Camera {
    public void renderPeron(OutputStream outputStream, Person person) throws IOException {
        outputStream.write(String.format("<p> %s </p>", person.name).getBytes());
        renderPhoto(outputStream, person.photo);
        emitPhotoData(outputStream, person.photo);
    }

    public void listRecentPhotos(OutputStream outputStream, List<Photo> photos) {
        photos.stream()
                .filter(p -> p.date.isAfter(recentDateCutOff()))
                .forEach(p -> {
                    try {
                        outputStream.write("<div> \n".getBytes());
                        emitPhotoData(outputStream, p);
                        outputStream.write("</div>\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void emitPhotoData(OutputStream outputStream, Photo photo) throws IOException {
        outputStream.write(String.format("<p> 제목: %s </p>", photo.title).getBytes());
        outputStream.write(String.format("<p> 위치: %s </p>", photo.location).getBytes());
        outputStream.write(String.format("<p> 날짜: %s </p>", photo.date.toString()).getBytes());
    }
}
```

여기서 소프트웨어를 수정하는데 한쪽인 listRecentPhotos 에서 위치를 렌더링하는 부분이 다르게 적용된다고 가정해보자. 

이 경우에는 emitPhotoData 에서 마지막 줄을 각각의 호출자에게 추가한 후 제거하는 방법을 쓰면 된다. __(여기서 그런 생각도 할 수 있다. renderPerson 은 기존의 emitPhotoData 함수를 그대로 쓰고 위치 렌더링 부분을 제외한 새로운 함수를 만들어서 listRecentPhotos 함수에 넣으면 되지 않느냐고 생각할 수 있는데 비슷한 함수가 많으면 혼돈을 줄 수 있기 때문에, 코드의 의도를 알기 어려울 수 있기 때문에 마냥 새로운 걸 추가하는건 좋은 방법은 아닌 거 같다.)__

간단한 방법이지만 여기서는 좀 더 복잡한 예제에서 사용할 수 있는 방법을 소개하곘다.

먼저 이동하지 않을 부분을 새로운 함수로 추출하고 검색하기 쉬운 이름으로 만들자.

```java
public void emitPhotoData(OutputStream outputStream, Photo photo) throws IOException {
        zzNew(outputStream, photo);
        outputStream.write(String.format("<p> 날짜: %s </p>", photo.date.toString()).getBytes());
    }

private void zzNew(OutputStream outputStream, Photo photo) throws IOException {
    outputStream.write(String.format("<p> 제목: %s </p>", photo.title).getBytes());
    outputStream.write(String.format("<p> 위치: %s </p>", photo.location).getBytes());
}
```

그 다음 이 함수를 이용해서 호출하도록 하고 호출자에 위치를 렌더링 하는 함수를 추가하자. __(안전하게 격리하기 위해서 이 방법을 쓴다.)__

```java
public void renderPeron(OutputStream outputStream, Person person) throws IOException {
        outputStream.write(String.format("<p> %s </p>", person.name).getBytes());
        renderPhoto(outputStream, person.photo);
        zzNew(outputStream, person.photo);
        outputStream.write(String.format("<p> 날짜: %s </p>", person.photo.date.toString()).getBytes());
    }

public void listRecentPhotos(OutputStream outputStream, List<Photo> photos) {
    photos.stream()
            .filter(p -> p.date.isAfter(recentDateCutOff()))
            .forEach(p -> {
                try {
                    outputStream.write("<div> \n".getBytes());
                    zzNew(outputStream, p);
                    outputStream.write(String.format("<p> 날짜: %s </p>", p.date.toString()).getBytes());
                    outputStream.write("</div>\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
}
```

그 다음 기존의 함수를 인라인하고 새 함수의 이름을 바꾸자.

```java
public class Camera {
    public void renderPeron(OutputStream outputStream, Person person) throws IOException {
        outputStream.write(String.format("<p> %s </p>", person.name).getBytes());
        renderPhoto(outputStream, person.photo);
        emitPhotoData(outputStream, person.photo);
        outputStream.write(String.format("<p> 날짜: %s </p>", person.photo.date.toString()).getBytes());
    }

    public void listRecentPhotos(OutputStream outputStream, List<Photo> photos) {
        photos.stream()
                .filter(p -> p.date.isAfter(recentDateCutOff()))
                .forEach(p -> {
                    try {
                        outputStream.write("<div> \n".getBytes());
                        emitPhotoData(outputStream, p);
                        outputStream.write(String.format("<p> 날짜: %s </p>", p.date.toString()).getBytes());
                        outputStream.write("</div>\n".getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
    
    private void emitPhotoData(OutputStream outputStream, Photo photo) throws IOException {
        outputStream.write(String.format("<p> 제목: %s </p>", photo.title).getBytes());
        outputStream.write(String.format("<p> 위치: %s </p>", photo.location).getBytes());
    }

    public String renderPhoto(OutputStream outputStream, Photo photo) {
        return null;
    }

    private LocalDateTime recentDateCutOff() {
        return null;
    }
}
```

***

## 8.5 인라인 코드를 함수 호출로 바꾸기

### 배경 

함수는 여러 동작을 하나로 묶어준다. 

그리고 함수의 이름이 코드의 동작보다 목적을 말해주기 때문에 코드를 함수를 활용하면 이해하기가 더 쉬워진다는 장점이 있다.

여기서의 리팩토링 기법은 이런 함수를 중복을 없애는데 사용한다. 

이렇게 해두면 비슷한 코드를 일일이 찾아 수정하는 대신 함수 하나만 수정하면 된다는 장점이 있다. (물론 모든 호출자가 수정된 코드를 사용하는 게 맞는지 확인해야하지만 이렇게 함수로 만드는편이 더 비용이 싸다.)

이미 존재하는 함수와 똑같은 일을 하는게 있다면 이 코드를 인라인으로 바꾸도록 하자.

인라인 코드 자체가 짧기 때문에 명확히 목적이 드러나기도 하지만 함수의 이름을 잘지어도 목적이 잘 드러난다.

여기서는 예시랄것도 없기 때문에 방법을 생략한다. 그냥 같은 일을 하는 인라인 코드가 보이면 함수로 바꾸면 된다. 

***

## 8.6 문장 슬라이스하기 

### 배경 

관련된 코드들이 서로 가까이 모여 있다면 이해하기가 더 쉽다.

실제로 나는 문장 슬라이드하기 리팩토링으로 이런 코드들을 한 데 모아둔다. 

가장 흔한 사례는 변수를 선언하고 사용할 때인데 모든 변수 선언을 함수 첫머리에 모아두는 사람도 있지만 나는 변수를 처음 사용할 때 선언하는 스타일을 선호한다.

이런 문장 슬라이스를 통해 관련 코드끼리 모우는 작업은 주로 다른 리팩토링의 준비 단계로 자주 수행한다. (이렇게 관련 있는 코드를 모아서 함수로 추출하는 등)

### 절차

1. 코드 문장들을 보면서 이동할 위치를 찾는다. 코드의 원래 위치와 목표 위치를 보면서 이동하면 동작이 달라지는 코드가 있는지 본다. 문제가 있다면 이 리팩토링을 포기한다.

2. 코드 조각을 원래 위치에서 잘라내어 목표 위치에 붙여 넣는다.

3. 테스트 한다.

### 예시

코드 조각을 슬라이드 할 땐 두 가지를 확인해야 한다.

__무엇을 슬라이드할지와 슬라이드 할 수 있는지의 여부다.__

무엇을 슬라이드할지는 맥락과 관련이 깊은데 나는 선언한 변수를 사용하는 곳 근처에 두는것을ㅈ ㅗㅎ아한다.

코드를 슬라이드 하기로 했다면 다음 단계로는 그 일이 실제로 가능한지를 점검한다.

그러려면 슬라이드할 코드 자체와 그 코드가 건너뛰어야 할 코드를 모두 살핀다.

이 코드들의 순서가 바뀌면 프로그램의 동작이 달라지는지 확인한다.

다음 코드의 예를 보자. 

```java
1. PricingPlan pricingPlan = retrievePricingPlan();
2. Order order = retrieveOrder();
3. int baseCharge = pricingPlan.base;
4. int charge;
5. int chargePerUnit = pricingPlan.unit;
6. int units = order.units;
7. int discount;
8. charge = baseCharge + units * chargePerUnit;
9. int discountableUnits = Math.max(units - pricingPlan.discountThreshold, 0);
10. discount = (int) (discountableUnits * pricingPlan.discountFactor);
11. if (order.isRepeat) discount += 20; 
12. charge = charge - discount; 
13. chargeOrder(charge);
```
처음 7줄은 선언이므로 이동하기가 쉽다. 이동해도 큰 영향이 없을 수 있다.

여기서 예로 할인 관련 코드를 모우고 싶다면 7번 줄을 10번줄 바로 위까지 올리면 된다.

이와 비슷하게 2번 줄도 6번 줄 위로 옮려도 상관없다.

이렇게 이동할 수 있는 이유는 __코드의 부수효과가 없기 때문이다.__

__사실 부수효과가 없는 코드끼리는 마음 가는 대로 재배치할 수 있다.__ 

__즉 명령-질의 분리 (Command-Query Separation) 원칙을 지키면 코드의 이해가 더 쉬워지고 리팩토링이 더쉽다.__

명령-질의 분리 원칙을 지키지 않는 코드베이스가 있을 수 있으므로 이를 확인하자. 그리고 나라도 나의 코드베이스에서는 명령-질의 분리 원칙을 지키자.

그러면 부수효과가 있는 코드를 슬라이드 해본다고 가정해보자. 

11번 코드를 슬라이드 하려고 해보자. 12번 코드에서 막힌다. 

12번 코드에서 discount 는 11번 코드에서 변경된 discount 에 의존하기 때문이다.

__즉 건너뛸 코드 조각이나 슬라이드할 코드 조각이 서로 값을 바꿀 가능성이 있다면 사이드 이펙트가 일어날 수 있으므로 주의하자.__  __(변경된 값에 의존하는 경우가 있을 수 있으니까 주의.)__

여기서는 복잡한 경우가 아니라서 부수효과가 일어나는 경우를 바로 알 수 있는데 복잡한 경우라면 훨씬 어려울 수 있다.

그러므로 이 경우에는 테스트를 보강해두자. __(실제로 현재 제대로 돌아가는 예시를 가져와서 테스트 코드로 만들어 두면 되지 않을까.)__

***

## 8.7 반복문 쪼개기

### 배경 

종종 반복문 하나에서 두 가지 일을 수행하는 경우가 있다.

한번에 모두 처리하는게 성능상에 좋지 않을까? 라는 생각에 기안해서 말이다. 

근데 이렇게 하면 코드를 고치기 어렵다. 반복문 안에서 어떠한 일을 수행하고 있는지 제대로 파악해야만 고칠 수 있기 때문이다. __(그리고 몇번의 반복을 더한다고 그렇게 느려지지 않을 것이다.)__

반복문을 분리하면 사용하기도 쉬워진다. 

한 가지 값만 계산하는 반복문이라면 그 값만 곧바로 반환하는게 가능하다.

반면 여러 일을 수행하는 반복문이라면 구조체를 반환하거나 지역 변수를 활용해야 한다.

참고로 반복문 쪼개기는 서로 다른 일들이 한 함수에서 이뤄지고 있는 신호일 수 있다. 그래서 반복문 쪼개기와 함수 추출하기는 연이어서 수행하는 일이 잦다.

### 절차

1. 반복문을 복제해 두 개로 만든다.

2. 반복문이 중복되어 생기는 부수효과를 파악해서 제거한다.

3. 테스트한다.

4. 완료됐으면 각 반복문을 함수로 추출할지 고민해본다.

### 예시

전체 급여와 가장 어린 나이를 계산하는 코드에서 시작해보자.

````java
int youngest = peopleList.isEmpty() ? Integer.MAX_VALUE : peopleList.get(0).age;
int totalSalary = 0; ````
for (People p : peopleList) {
    if (p.age < youngest) youngest = p.age; 
    totalSalary += p.salary;
}
````

아주 간단한 반복문이지만 관련 없는 두 가지 계산을 수행한다.

반복문 쪼개기를 적용해보자. 먼저 반복문을 복제히고 중복을 제거하자.

```java
int youngest = peopleList.isEmpty() ? Integer.MAX_VALUE : peopleList.get(0).age;
int totalSalary = 0; ```
for (People p : peopleList) {
    if (p.age < youngest) youngest = p.age;         
}

for (People p : peopleList) { 
    totalSalary += p.salary;
}
```                      

__반복문을 이렇게 쪼갰으면 이제 부수효과가 없는지 한번 검사해보자. 지금은 없어서 넘어간다.__

***

## 8.8 반복문을 파이프라인으로 바꾸기

### 배경 

프로그래머 대부분이 그렇듯 나도 객체 컬렉션을 순회할 때 반복문을 사용하라고 배웠다.

하지만 언어는 계속해서 발전하고 더 나은 구조를 제공해준다.

여기서 나오는 파이프라인 (Pipeline) 을 이용하면 처리 과정을 일련의 연산으로 표현할 수 있다.

이때 각 연산은 컬렉션을 입력받아서 다른 컬렉션을 내뱉는다. 

이렇게 파이프라인은 다음 단계를 위해서 컬렉션을 뱉으므로 논리를 파이프라인으로 표현하면 이해하기가 더 쉽다.

### 절차

1. 반복문에서 사용하는 컬렉션을 가리키는 변수를 만든다.

2. 반복문의 첫 줄부터 시작해서 각각의 단위 행위를 적절한 컬렉션 파이프라인 연산으로 대체한다. 이때 컬렉션 파이프라인 연산은 이전에 만든 변수부터 시작해서 연쇄적으로 수행한다. 하나싞 대체할 때마다 테스트한다.

3. 반복문의 모든 동작을 대체했다면 반복문 자체를 지운다.

### 예시

다음은 예시를 위한 데이터로, 내 회사의 지점 사무실 정보를 CSV 형태로 정리한 것이다.

```
office, country, telephone
Chicago, USA, +1 312 373 1000
Beijing, China, +86 4008 900 505
Bangalor, India, +91 80 4064 9570

... (더 많은 데이터)
```

다음 함수는 인도 (India) 에 자리한 사무실을 찾아서 도서명과 전화번호를 반환한다.

```java
public List<Office> acquireData(String input) {
    String[] lines = input.split("\n");
    boolean firstLine = true;
    List<Office> result = new ArrayList<>(); 
    for (String line : lines) {
        if (firstLine) {
            firstLine = false; 
            continue;
        }
        if (line.trim().equals("")) continue;

        String[] record = line.split(",");
        if (record[1].trim().equals("India")){
            result.add(new Office(record[0], record[2]));
        }
    }
    return result;
}
```

이 코드를 파이프라인으로 바꿔보자.

첫 번째로 할 일은 반복문에서 사용하는 컬렉션을 가리키는 별도의 변수를 따로 만드느 것이다.

이 변수를 루프 변수 (loop variable) 이라고 하겠다.

````java
public List<Office> acquireData(String input) {
    String[] lines = input.split("\n");
    boolean firstLine = true;
    List<Office> result = new ArrayList<>();
    String[] loop = lines;
    for (String line : lines) {
        if (firstLine) {
            firstLine = false;
            continue;
        }
        if (line.trim().equals("")) continue;

        String[] record = line.split(",");
        if (record[1].trim().equals("India")){
            result.add(new Office(record[0], record[2]));
        }
    }
    return result;
}
```` 

이 코드를 분석해보면 firstLine 은 첫 반복문을 뛰어넘는 역할을 한다. 이는 파이프라인 연산에서 skip 와 같으므로 대체한다.

대체하고 반복문에서 firstLine 과 관련된 부분을 지우자.

````java
public List<Office> acquireData(String input) {
    String[] lines = input.split("\n");
    List<Office> result = new ArrayList<>();
    
    String[] loop = lines;
    Arrays.stream(lines)
            .skip(1); 
            
    for (String line : lines) {
        if (line.trim().equals("")) continue;

        String[] record = line.split(",");
        if (record[1].trim().equals("India")){
            result.add(new Office(record[0], record[2]));
        }
    }
    return result;
}
````

다음 작업은 빈 줄 지우기 (trim) 이다. 이 작업은 filter 연산으로 대체한다.

````java
public List<Office> acquireData(String input) {
    String[] lines = input.split("\n");
    List<Office> result = new ArrayList<>();

    String[] loop = lines;
    Arrays.stream(lines)
            .skip(1)
            .filter(line -> !line.trim().equals("")); 

    for (String line : lines) {
        String[] record = line.split(",");
        if (record[1].trim().equals("India")){
            result.add(new Office(record[0], record[2]));
        }
    }
    return result;
}
````

다음으로 map 연산을 통해 여러 줄 짜리의 CSV 데이터를 문자열 배열로 매핑한다.

그 다음 India 를 기준으로 filter 를 하고 결과를 넣어주면 된다.

```java
public List<Office> acquireData(String input) {
    String[] lines = input.split("\n");
    List<Office> result = new ArrayList<>();

    String[] loop = lines;
    Arrays.stream(lines)
            .skip(1)
            .filter(line -> !line.trim().equals(""))
            .map(line -> line.split(","))
            .filter(record -> record[1].trim().equals("India"))
            .forEach(record -> result.add(new Office(record[0], record[2])));
    
    return result;
}
```

***

## 8.9 죽은 코드 제거하기 

### 배경

소프트웨어에서 사용되지 않은 코드가 있다면 그 소프트웨어의 동작을 이해하는 데 커다란 어려움을 줄 수 있다.

이 코드들은 절대 호출되지 않으니 무시해도 된다! 라는 신호를 주지 않기 때문이다. __(호출이 되지 않더라도 다른 개발자가 의도적으로 남겨놓았을 수도 있기 떄문에 사용되지 않는다면 삭제하자.)__

코드가 더 이상 사용되지 않게 됐다면 지웡야한다. 혹시라도 다시 필요해질 날이 오지 않을까 싶다면 버전 관리 시스템을 이용하도록 하자.













