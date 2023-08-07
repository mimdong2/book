### 목차
- [비트 필드란](#비트-필드란)
- [비트 필드의 문제점](#비트-필드는-문제점이-가득하다)
- [비트 필드를 대체하는 EnumSet](#비트-필드를-대체하는-enumset)
- [EnumSet을 써야하는 이유](#enumset-을-써야-하는-이유)
- [부록. EnumSet VS Set](#부록-enum을-표현하고-싶다면-그냥-hashset에-넣어도-똑같은거-아닌가)


## 비트 필드란
(당황 스럽다. 비트 필드를 쓰지 말라고 하는데, 비트 필드라는 단어부터 처음 들었다.)
- 비트 필드 열거 상수의 예제
    - 열거 타입을 지원하기 전 열거 패턴을 썼을 때, 상수들을 집합(Set)처럼 관리했을 때가 있었다.
    - 아래 예제는 쉬프트 연산을 사용하여 값을 할당한 열거 패턴을 사용한 것이고, 이렇게 하면 상수 값을 비트로 표현할 수 있었다.
    ```java
    public class Text {
        public static final int BOLD = 1 << 0;          // 1 (이진수 : 0001)
        public static final int ITALIC = 1 << 1;        // 2 (이진수 : 0010)
        public static final int UNDERLINE = 1 << 2;     // 4 (이진수 : 0100)
        public static final int STRIKETHROUGH = 1 << 3; // 8 (이진수 : 1000)

        // 매개변수 styles는 0개 이상의 상수를 비트별 OR한 값이다.
        public void applyStyles(int styles) {
            // ...
        }
    }
    ```
    - 비트값으로 표현된 상수를 비트 논리 연산자 OR(`|`)를 사용하여 여러 상수를 하나의 집합으로 모을 수 있었다.
    - 이렇게 만들어진 집합을 `비트 필드(bit field)`라고 한다.
    ```java
    text.applyStyles(BOLD | ITALIC);    // 3 (이진수 01 | 이진수 10 -> 이진수 11)
    ```

<br>

## 비트 필드는 문제점이 가득하다
```java
text.applyStyles(BOLD | ITALIC);    // `text.applyStyles(3)`
```
- 비트 필드는 해석하기 어렵다.
    - 3이라는 숫자는 BOLD, ITALIC 이라는 정보를 가지고 있지 않으므로 해석하기가 어려워진다.
    - 해석을 위해 비트 필드에 녹아 있는 모든 원소를 순회해야 하는 문제도 있다.
- 비트 필드의 자료형을 미리 예측해야 한다.
    - 정의한 상수 개수가 늘어날 때마다 쉬프트 연산하는 비트 값도 커진다.
    - API를 수정하지 않고서는 비트 수를 늘릴 수는 없다. (보통 타입을 int(32bit) 나 long(64bit) 중 선택)

<br>

## 비트 필드를 대체하는 EnumSet
위 예시를 아래와 같이 바꿔 봤다. (정수 열거 패턴 -> Enum, 비트 필드 -> EnumSet)
- 정수 열거 패턴 -> Enum
    - [아이템34](/effective_java/Chapter%2006.%20%EC%97%B4%EA%B1%B0%20%ED%83%80%EC%9E%85%EA%B3%BC%20%EC%95%A0%EB%84%88%ED%85%8C%EC%9D%B4%EC%85%98/Item%2034.%20int%20%EC%83%81%EC%88%98%20%EB%8C%80%EC%8B%A0%20%EC%97%B4%EA%B1%B0%20%ED%83%80%EC%9E%85%EC%9D%84%20%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC.md) 에서 이야기한 것 처럼 열거 타입을 써야 한다.
    ```java
    public class Text {
        public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }
    }
    ```
- 비트 필드 -> EnumSet
    - EnumSet은 열거 타입 상수 값으로 구성된 집합을 표현해준다.
    ```java
    public class Text {
        
        public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

        // applyStyles 메서드에 EnumSet 인스턴스를 인자로 사용한다.
        public static void applyStyles(Set<Style> styles) {
            //...
        }
    }
    ```

<br>

## EnumSet 을 써야 하는 이유
- 타입이 안전하다.
    - EnumSet은 Set 인터페이스를 완벽히 구현(extend)하고, 타입 안전하다.
- 다른 Set 구현체와도 함께 사용 가능하다.
    - 위 예시 처럼, applyStyles 메서드에 인자를 `EnumSet<Style>` 이 아니라 `Set<Style>` 로 선언하였다.
    - 모든 클라이언트가 EnumSet 을 건네리라 생각되지만, 이왕이면 인터페이스로 선언하는 것이 좋은 습관이다. (아이템64)
    - 좀 특이한 클라이언트가 다른 Set 구현체를 넘기더라도 처리할 수 있다. ([아이템38](/effective_java/Chapter%2006.%20%EC%97%B4%EA%B1%B0%20%ED%83%80%EC%9E%85%EA%B3%BC%20%EC%95%A0%EB%84%88%ED%85%8C%EC%9D%B4%EC%85%98/Item%2038.%20%ED%99%95%EC%9E%A5%ED%95%A0%20%EC%88%98%20%EC%9E%88%EB%8A%94%20%EC%97%B4%EA%B1%B0%20%ED%83%80%EC%9E%85%EC%9D%B4%20%ED%95%84%EC%9A%94%ED%95%98%EB%A9%B4%20%EC%9D%B8%ED%84%B0%ED%8E%98%EC%9D%B4%EC%8A%A4%EB%A5%BC%20%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC.md))
- 정보도 그대로 가지고 있다.
    - `text.applyStyles(EnumSet.of(Style.BOLD, Style.ITALIC, Style.UNDERLINE));`
- 비트 필드에 비견되는 성능을 보여준다.
    - EnumSet의 내부는 비트벡터로 구현되어 있어, 원소가 총 64개 이하라면 EnumSet 전체를 long 변수 하나로 표현할 수 있다.

<br>

## 부록. Enum을 표현하고 싶다면 그냥 HashSet에 넣어도 똑같은거 아닌가?
> EnumSet VS Set   
> // 기능은 같으나 성능적으로 큰 차이가 있다.
- EnumSet의 모든 메서드는 비트 연산을 사용하고 있으며 64개 이하라면 하나의 long 비트만을 사용합니다. 각 계산에 대해 하나의 비트만 검사하는 EnumSet과 해시 코드를 계산해야 하는 HashSet을 비교한다면 당연히 EnumSet이 빠릅니다.
- 즉, 성능 측면에서보자면 EnumSet 사용하는 것이 좋다. (물론, 경우에 따라서는 적절한 타입을 써야 함)