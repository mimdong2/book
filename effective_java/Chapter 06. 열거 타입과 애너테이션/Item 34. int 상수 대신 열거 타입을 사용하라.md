### 목차
- [열거 타입 이란](#열거-타입-이란)
- [열거 타입 지원 이전에는](#열거-타입을-지원하기-전으로-돌아가면)
- [이제는 열거 타입을 쓰자](#열거-타입을-쓰자)
- [열거 타입 이 좋은 이유, 열거 패턴과 비교](#-열거-패턴-vs-열거-타입)
- [결론은 열거 타입 이라는 것!](#결론은-열거-타입을-사용하자)

## 열거 타입 이란
- 한정된 값만을 갖는 데이터 타입, 즉 그 이외의 값은 허용하지 않는 타입이다.
- 예를 들어 계절, 태양계 행성, 요일 등이 있다.
- 그리고 열거 타입(enumeration type)은 몇 개의 열거 상수(enumeration constant) 중에서 하나의 상수를 저장하는 데이터 타입이다.

<br>

## 열거 타입을 지원하기 전으로 돌아가면
- 열거 타입을 지원하기 전에는 열거 패턴을 사용했었다.
- 아래 코드 처럼 열거 패턴은 static final int 로 값을 정의하여 한 묶음으로 선언 했었다.
```java
public class Constants {
    public static final int MONDAY = 0;
    public static final int TUESDAY = 1;
    public static final int WEDNESDAY = 2;
    public static final int THURSDAY = 3;
    public static final int FRIDAY = 4;
    public static final int SATURDAY = 5;
    public static final int SUNDAY = 6;

    public static final int RED = 0;
    public static final int ORANGE = 1;
    public static final int GREEN = 2;
}
```
- 구현하기 쉽지만 단점 투성이다.
  - 컴파일러가 이해하는 값은 정의한 값 그 자체이다. 즉 그 값의 의미를 볼 수 없어서 불편하다. (`APPLE_FUJI`를 프린트 못함)
  - 컴파일하면 값 자체가 클라이언트 파일에 그대로 새겨진다. 즉 그 값을 바꾸면 클라이언트는 재 컴파일 해야 한다. (다시 컴파일 안하면 엉뚱하게 동작)
  - 동등 연산자 (== 혹은 equals) 로 비교하는 경우, 오류가 생길 수 있다. (`ORANGE`를 보내야 하는데 `APPLE`를 보낼 수도 있음)

<br>

## 열거 타입을 쓰자.
- 열거 패턴의 단점을 깨트린 것은 물론, 장점도 더 많다.
```java
public enum Day { MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY }
public enum Sign { RED, ORANGE, GREEN }
public enum DATABASE { MYSQL, POSTGRESQL, MARIA, REDIS }
```

<br>

## 열거 패턴 VS 열거 타입
- 타입 안정성???
  - 열거 패턴은 타입 안정성을 보장할 수 없다.
    ```java
    public void function(int day) {
            ... // 요일 관련 상수만 들어가야 하는 메소드
    }
    
    function(MONDAY);   // OK!
    function(RED);      // 사인 관련 상수가 들어가도 컴파일러는 모른다.
    ```
  - 열거 타입은 타입 안정성을 보장한다.
    ```java
    public void function(Day day) {
            ... // 요일만 들어가야 하는 메소드
    }
    function(MONDAY);   // OK!
    function(RED);      // 컴파일 에러!
    ```
- 깨지기 어려운가???
  - 열거 패턴은 깨지기 쉽다.
    - 컴파일하면 값 자체가 클라이언트 파일에 그대로 새겨진다.
    - 즉 그 값을 바꾸면 클라이언트도 다시 컴파일 해야 한다. (다시 컴파일 하지 않으면 엉뚱하게 동작)
  - 열거 타입은 깨지 않는다.
    - 공개되는 것이 오직 필드의 이름뿐이라, 추가하거나 또는 순서를 바꿔도 다시 컴파일하지 않아도 된다.
    - 제거 하더라도 제거한 상수를 참조하지 않는 클라이언트에는 아무 영향이 없다. 만약 참조하고 있다면 컴파일 오류가 발생해서 바로 알 수 있다.
- 표현력이 좋은가???
  - 열거 패턴은 의미가 아닌 그 값으로 보여서 훌륭하지는 않다.
  ```java
  System.out.println(Constants.FRIDAY); // 4 로 출력
  ```
  - 열거 타입은 그 값으로 보이고, `toString()` 는 출력하기에 적합한 문자열을 내어준다.
  ```java
  System.out.println(DAY.FRIDAY);   // FRIDAY 로 출력됨
  ``` 
- 인스턴스를 통제한다???
  - 열거 타입으로 인스턴스 통제된다, 즉 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재한다.
    - 열거 타입 자체가 클래스이고, 상수 하나 당 자신의 인스턴스를 하나씩 만들어 `public static final` 필드로 공개한다.
    - public 생성자를 제공하지 않으므로(사실상 `final`), 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없다.
- 임의의 메서드나 필드를 추가할 수 있다??? 임의의 인터페이스를 구현하게 할 수도 있다???

<br>

## 결론은 열거 타입을 사용하자
- 성능 차이도 없다. 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감될 정도는 아니다.
- 필요한 원소를 컴파일 타임에 다 알 수 있는 상수 집합이면 항상 열거 타입을 사용하자.
- 열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.
- 열거 타입에서 상수마다 다르게 동작해야 할 땐 메서드를 구현해서 사용(switch 말고!)하고, 일부 상수가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자.