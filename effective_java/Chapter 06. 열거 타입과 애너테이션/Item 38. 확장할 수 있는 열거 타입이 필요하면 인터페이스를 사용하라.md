### 목차
- [열거 타입을 확장하는 것은 좋지 않다](#enum-을-확장하는-것은-좋지-않다)
- [다만 아주 가끔 열거 타입을 확장해야 하는 경우도 있다](#아주-가끔은-enum-을-확장해야-할-수도-있다)
- [인터페이스를 통해 enum을 확장할 수 있다](#상속이-되지-않으니-인터페이스를-통해-enum을-확장할-수-있다)
- [타입 수준으로 다형성을 적용할 수 있다](#타입-수준으로-다형성을-적용할-수-있다)
- [인터페이스를 이용해 확장한 열거 타입에서의 사소한 문제점](#인터페이스를-이용해-확장한-열거-타입에서의-사소한-문제점)

## enum 을 확장하는 것은 좋지 않다
- enum 은 `extends` 를 쓸 수 없다.
    - enum 의 바이트코드를 보면 자동으로 `java.lang.Enum` 을 상속하고 있다.
    - 자바는 다중 상속이 지원되지 않기 때문에 enum 클래스에 다른 클래스를 상속받을 수 없다.
- 대부분 상황에서 `enum` 을 확장하는 것은 안좋다.
    - enum 은 상수 집합인데, 하위 상수는 상위 타입의 요소로 인정하지만 상위 타입의 상수는 하위 타입으로 인정못하는 것은 이상하다.
    - 상위, 하위타입의 모든 상수를 순회할 방법도 마땅치 않다.
    - 설계와 구현이 복잡해진다.

<br>

## 아주 가끔은 enum 을 확장해야 할 수도 있다
- 연산코드(기계가 수행하는 연산)의 경우 확장할 수 있는 enum 을 사용하면 쓰임새가 있다.
- 기계마다 다른 연산을 제공하거나, 기본 연산 외에 확장 연산을 추가할 수 있어야 한다.

<br>

## 상속이 되지 않으니, 인터페이스를 통해 enum을 확장할 수 있다
#### 연산 인터페이스
```java
public interface Operation {
    double apply (double x, double y);
}
```
#### 연산 인터페이스를 구현한 서울 연산 enum
```java
public enum SeoulOperation implements Operation {
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return x + y;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return x - y;
        }
    },
    TIMES("*") {
        @Override
        public double apply(double x, double y) {
            return x * y;
        }
    },
    DIVIDE("/") {
        @Override
        public double apply(double x, double y) {
            return x / y;
        }
    }
    ;

    private final String symbol;

    SeoulOperation(String symbol) {
        this.symbol = symbol;
    }
}
```
#### 연산 인터페이스를 구현한 수원 연산 enum
```java
public enum SuwonOperation implements Operation {
    // SeoulOperation 과 다르게 SuwonOperation 은 더하기,빼기만 있고 결과에 *2를 한다.
    PLUS("+") {
        @Override
        public double apply(double x, double y) {
            return (x + y) * 2;
        }
    },
    MINUS("-") {
        @Override
        public double apply(double x, double y) {
            return (x - y) * 2;
        }
    }
    ;

    private final String symbol;
    
    SuwonOperation(String symbol) {
        this.symbol = symbol;
    }
}
```
- Operation 인터페이스를 구현한 enum 이라면, 서울 연산 enum 에서 수원 연산 enum 으로 바꿔서 사용할 수 있다.
- 추상 메서드가 인터페이스에 선선되어 있으니 enum 에서 따로 추상 메서드를 선언하지 않아도 된다.

<br>

## 타입 수준으로 다형성을 적용할 수 있다.
#### 한정적 타입 매개변수를 이용한 방법

```java
private static <T extends Enum<T> & Operation> void test (Class<T> opEnumType, double x, double y) {
    for (Operation op : opEnumType.getEnumConstants()) {
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
    }
}

@Test
public void 한정적타입매개변수() {
    double x = 1.4;
    double y = 2.0;

    System.out.println("----SEOUL----");
    test(SeoulOperation.class, x, y);
    System.out.println("----SUWON----");
    test(SuwonOperation.class, x, y);
}
```
- 다음과 같이 출력 된다.
  ```
  ----SEOUL----
  1.400000 PLUS 2.000000 = 3.400000
  1.400000 MINUS 2.000000 = -0.600000
  1.400000 TIMES 2.000000 = 2.800000
  1.400000 DIVIDE 2.000000 = 0.700000
  ----SUWON----
  1.400000 PLUS 2.000000 = 6.800000
  1.400000 MINUS 2.000000 = -1.200000
  ```
- test 메서드의 opEnumType 매개변수의 선언(`<T extends Enum<T> & Operation>`)은 Class 객체가 열거 타입인 동시에 Operation 하위 타입이여야 한다.
- 열거 타입이어야 원소를 순회할 수 있고, Operation 이어야 뜻하는 연산을 수행할 수 있기 때문이다.
- 참고
    ```java
    // 열거 클래스의 요소를 반환하거나 null을 반환
    /**
     * Returns the elements of this enum class or null if this
     * Class object does not represent an enum class.
     *
     * @return an array containing the values comprising the enum class
     *     represented by this {@code Class} object in the order they're
     *     declared, or null if this {@code Class} object does not
     *     represent an enum class
     * @since 1.5
     * @jls 8.9.1 Enum Constants
     */
    public T[] getEnumConstants() {
        T[] values = getEnumConstantsShared();
        return (values != null) ? values.clone() : null;
    }
    ```

#### 한정적 와일드 카드를 이용한 방법
```java
private static void test (Collection<? extends Operation> opSet, double x, double y) {
    for (Operation op : opSet) {
        System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
    }
}

@Test
public void 한정적와일드카드() {
    double x = 1.4;
    double y = 2.0;

    System.out.println("----SEOUL----");
    test(EnumSet.allOf(SeoulOperation.class), x, y);
    System.out.println("----SUWON----");
    test(EnumSet.allOf(SuwonOperation.class), x, y);
    
    System.out.println("----SEOUL, SUWON 의 PLUS----");
    // EnumSet 을 쓰지 못함.
    test(Set.of(SuwonOperation.PLUS, SeoulOperation.PLUS), x, y);
}
```
- 다음과 같이 출력 된다.
  ```
  ----SEOUL----
  1.400000 PLUS 2.000000 = 3.400000
  1.400000 MINUS 2.000000 = -0.600000
  1.400000 TIMES 2.000000 = 2.800000
  1.400000 DIVIDE 2.000000 = 0.700000
  ----SUWON----
  1.400000 PLUS 2.000000 = 6.800000
  1.400000 MINUS 2.000000 = -1.200000
  ----SEOUL, SUWON 의 PLUS----
  1.400000 PLUS 2.000000 = 3.400000
  1.400000 PLUS 2.000000 = 6.800000
  ```
- Class 객체 대신 한정적 와일드카드 타입 ([아이템31](/effective_java/Chapter%2005.%20%EC%A0%9C%EB%84%A4%EB%A6%AD/Item%2031.%20%ED%95%9C%EC%A0%95%EC%A0%81%20%EC%99%80%EC%9D%BC%EB%93%9C%EC%B9%B4%EB%93%9C%EB%A5%BC%20%EC%82%AC%EC%9A%A9%ED%95%B4%20API%20%EC%9C%A0%EC%97%B0%EC%84%B1%EC%9D%84%20%EB%86%92%EC%9D%B4%EB%9D%BC.md)) 을 사용 했다.
- `Collection<? extends Operation>` 을 사용하여 여러 구현 타입의 연산을 조합하여 호출할 수 있지만, 특정 연산에서는 EnumSet, EnumMap을 사용하지 못한다.
    ```java
    @Test
    public void EnumSet과EnumMap제한() {
        // 가능
        EnumSet<SeoulOperation> s = EnumSet.of(SeoulOperation.PLUS, SeoulOperation.MINUS);

        // 불가능!!!!!
        EnumSet<Operation> ss = EnumSet.of(SeoulOperation.PLUS, SeoulOperation.MINUS);
        EnumSet.of(SeoulOperation.PLUS, SuwonOperation.PLUS);
    }
    ```

<br>

## 인터페이스를 이용해 확장한 열거 타입에서의 사소한 문제점
- 열거 타입끼리 구현을 상속할 수 없다.
- 디폴트 구현을 이용해 인터페이스에 추가하는 방법이 있으나, 예제 Operaion 의 경우, 연산 기호를 저장하고 찾는 로직이 SeoulOperation, SuwonOperaion 에 모두 들어가야 한다.
- 위 예제는 중복량이 적으니 문제가 되지는 않으나, 공유하는 기능이 많다면 그 부분을 별도의 도우미 클래스나 정적 도우미 메서드로 분리하는 방식으로 코드 중복을 없앨 수 있을 것이다.