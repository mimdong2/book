### 목차
- [가변인수 메서드란](#가변인수-메서드란)
- [가변인수 메서드를 잘못 구현한 예](#가변인수-메서드를-잘못-구현한-예)
- [성능에 민감한 사항에서는 가변인수가 걸림돌이 된다](#성능에-민감한-사항에서는-가변인수가-걸림돌이-된다)

## 가변인수 메서드란
- 가변인수 메서드는 명시한 타입의 인수를 0개 이상 받을 수 있다.
- 가변인수 메서드를 호출하면, 가장 먼저 인수의 개수와 길이가 같은 배열을 만들고, 인수들을 이 배열에 저장하여 가변인수 메서드에 건내준다.
- 예시. 간단한 가변인수 활용
```java
static int sum(int... numbers) {
    int sum = 0;
    for (int number : numbers) {
        sum += number;
    }
        return sum;
}

@Test
void 가변인수메서드사용() {
    System.out.println(sum(1, 2, 3));    // 6
    System.out.println(sum());           // 0
    System.out.println(sum(1));          // 1 
}
```

<br>

## 가변인수 메서드를 잘못 구현한 예
- 아래 예시에서, 인수가 1개 이상이여야 하기 때문에 유효성 검사, `validation` 로직을 작성했다.
  - **가장 심각한 문제!! 인수를 0개만 넣어 호출하면 컴파일 타임이 아닌 런타임에 실패한다.**
```java
static int min(int... numbers) {
    if (numbers.length == 0) {
        throw new IllegalArgumentException("인수가 1개 이상 필요!!!!");
    }
    int min = numbers[0];
    for (int i = 1; i < numbers.length; i++) {
        if (numbers[i] < min) {
            min = numbers[i];
        }
    }
    return min;
}
```
- 위의 문제는 다음과 같이 해결할 수 있다.
  - 매개변수를 2개로 받도록 하면 된다. 첫번째 인자는 평범한 매개변수를 받고, 두번째 인자로 가변인수를 받으면 된다.
```java
    static int min(int firstNumber, int... remainNumbers) {
        int min = firstNumber;
        for (int remainNumber : remainNumbers) {
            if (remainNumber < min) {
                min = remainNumber;
            }
        }
        return min;
    }
```

<br>

## 성능에 민감한 사항에서는 가변인수가 걸림돌이 된다.
- 가변인수 메서드는 호출될 때마다 배열을 새로 하나 할당하고 초기화하여 성능에 민감하다!
- 성능에 대한 비용을 최적화 하기 위한 해법이 아래와 같다.
  - 아래 메서드 호출의 95% 가 인수를 3개 이하로 사용한다고 했을 때, 단 5%만이 배열을 생성하므로, 오버로딩 전략을 사용하면 된다.
  ```java
  static int sum(int number1)
  static int sum(int number1, int number2)
  static int sum(int number1, int number2, int number3)
  static int sum(int number1, int number2, int number3, int... numbers)
  ```
  - EnumSet 도 위와 같은 기법으로 열거 타입 집합 생성 비용을 최소화한다. (EnumSet API 문서 참조)
  ```java
  public static <E extends Enum<E>> EnumSet<E> of(E e) {
      EnumSet<E> result = noneOf(e.getDeclaringClass());
      result.add(e);
      return result;
  }
  public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2) {
      EnumSet<E> result = noneOf(e1.getDeclaringClass());
      result.add(e1);
      result.add(e2);
      return result;
  }
  public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3) {
      EnumSet<E> result = noneOf(e1.getDeclaringClass());
      result.add(e1);
      result.add(e2);
      result.add(e3);
      return result;
  }
  public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4) {
      EnumSet<E> result = noneOf(e1.getDeclaringClass());
      result.add(e1);
      result.add(e2);
      result.add(e3);
      result.add(e4);
      return result;
  }
  public static <E extends Enum<E>> EnumSet<E> of(E e1, E e2, E e3, E e4, E e5) {
      EnumSet<E> result = noneOf(e1.getDeclaringClass());
      result.add(e1);
      result.add(e2);
      result.add(e3);
      result.add(e4);
      result.add(e5);
      return result;
  }
  @SafeVarargs
  public static <E extends Enum<E>> EnumSet<E> of(E first, E... rest) {
      EnumSet<E> result = noneOf(first.getDeclaringClass());
      result.add(first);
      for (E e : rest)
      result.add(e);
      return result;
  }
  ```
