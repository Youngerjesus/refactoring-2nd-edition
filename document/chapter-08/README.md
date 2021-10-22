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

## 문장을 호출한 곳으로 옮기기

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






