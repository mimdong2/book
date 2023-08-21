### 목차
- [매개변수 검사를 해야 하는 이유](#매개변수-검사를-해야-하는-이유)
- [매개변수 검사가 제대로 이뤄지지 못하면?](#매개변수-검사가-제대로-이뤄지지-못하면)
- [문서화 하자](#문서화-해라)
- [유용한 유효성 검사 API](#유용한-유효성-검사-api)
- [assert 문 사용](#assert-문-사용)
- [추가 부가적으로 설명하자면](#추가-부가적으로-설명하자면)

## 매개변수 검사를 해야 하는 이유
메서드와 생성자 대부분 입력 매개변수의 값이 특정 조건을 만족하기를 바란다.
```java
private List <String> names;

public String getNameOf (int index) {
    return names.get(index);
}
```
위와 같이 이름을 관리하는 클래스가 있고, 이름의 인덱스 값을 통해 조회하는 메소드가 있다고 가정했을 때, 이때 index 가 음수이거나 names 의 크기보다 크면 에러가 발생한다.
이러한 제약은 반드시 문서화해야 하며 메서드 몸체가 실행되기 전에 검사해야 한다.

> 오류는 가능한 빨리 (발생한 곳에서) 잡아야 한다. 오류를 발생한 지점에서 바로 잡지 못하면 해당 오류를 감지하기 어려워지고, 감지하더라도 오류의 발생 지점을 찾기 어려워진다.

즉, 메서드 몸체가 실행되기 전에 매개변수를 확인한다면 잘못된 값이 넘어왔을 때 즉각적이고 깔끔한 방식으로 예외를 던질 수 있다.

<br>

## 매개변수 검사가 제대로 이뤄지지 못하면?
- 메서드가 수행되는 중간에 모호한 예외를 던지며 실패할 수 있다.
- 더 나쁜 상황은 메서드가 잘 수행되지만 잘못된 결과를 반환할 때다.
- 한층 더 나쁜 상황은 메서드는 문제없이 수행됐지만, 어떤 객체를 이상한 상태로 만들어놓아서 미래의 알 수 없는 시점에 이 메서드와는 관련없는 오류를 낼 때다. 즉, 매개변수 검사에 실패하면 실패 원자성(아이템 76)을 어기는 결과를 낳을 수 있다.

<br>

## 문서화 해라.
- `public` 과 `protected` 는 외부 접근도가 높은 제어자이므로, 이들 메서드에는 @throws 자바독 태그를 통해 문서화해야한다.
- 참고로, 클래스 수준의 주석은 그 클래스의 모든 `public` 메소드에 적용되므로, 각 메서드에 일일이 기술하는 것보다 훨씬 깔끔한 방법이다.

<br>

## 유용한 유효성 검사 API
#### 자바 7에 추가된, java.util.Objects.requireNonNull 메서드
- 아래 코드를 보면 간단하다고 느낄 수 있는데, `null` 검사 이후 `null`이면 `NullPointerException` 를 던지고, `null`이 아니면 obj 를 리턴한다.
```java
// 단순 null 검사도 가능하다.
public static <T> T requireNonNull(T obj) {
    if (obj == null)
        throw new NullPointerException();
    return obj;
}

// 메세지 자체를 전달할 수 있다.
public static <T> T requireNonNull(T obj, String message) {
    if (obj == null)
        throw new NullPointerException(message);        
    return obj;
}

// 메세지를 반환하는 Supplier 객체를 전달 받아 lazy 하게 작동한다.
public static <T> T requireNonNull(T obj, Supplier<String> messageSupplier) {
    if (obj == null)
        throw new NullPointerException(messageSupplier == null ?
            null : messageSupplier.get());
    return obj;
}
```
- Objects.requireNonNull 메서드의 장점
  - 유연하고, 사용하기도 편하니  더이상 null 검사를 수동으로 하지 않아도 된다.
  - 원하는 예외 메시지도 지정할 수 있다.
  - 입력을 그대로 반환하므로 값을 사용하는 동시에 null 검사를 수행할 수 있다.
  - 반환값은 그냥 무시하고 필요한 곳 어디서든 순수한 null 검사 목적으로 사용해도 된다.
#### 자바 9에 추가된, java.util.Objects의 범위 검사 기능
- 범위 검사 메서드
  - `Objects.checkFromIndexSize()`
  - `Objects.checkFromToIndex()`
  - `Objects.checkIndex()`
- 위 범위 검사 메서드의 한계
  - null 검사 메서드만큼 유연하지 않다. 
  - 예외 메세지를 지정할 수 없고, 리스트와 배열 전용으로 설계됐다. 
  - 닫힌 범위(closed range; 양 끝단 값을 포함하는)는 다루지 못한다.
- 그래도! 이런 제약이 걸림돌이 되지 않는 상황에서는 유용하고 편하다.

<br>

## assert 문 사용
- `public` 이 아닌 메서드라면 단언문(`assert`)를 사용해 매개변수 유효성을 검증할 수 있다.
- `private` 메서드는 객체 스스로가 호출할 것임이 명확하기 때문이다.
- `assert` 는 유효성 검사와 다른 두가지가 있다.
  - 첫째. 실패하면 `AssertionError` 를 던진다.
  - 둘째. 런타임에 아무런 효과도, 아무런 성능 저하도 없다.
    - java 실행할 때 명령줄에서 `-ea` 또는 `--enableassertions` 플래그를 설정하면 런타임에 영향을 준다.
```java
private static void sort(long a[], int offset, int length) {
    // 아래 단언문들은 자신이 단언한 조건이 무조건 참이라고 선언한다.
    assert a != null;
    assert offset >= 0 && offset <= a.length;
    assert length >= 0 && length <= a.length - offset;
    // ... 메서드 바디 수행
}

@Test
void assert테스트() {
    long[] a = new long[10];
    int offset = 3;
    int length = -1;
    sort(a, offset, length);
}
// 성공하면 Paas, 실패하면 AssertionError 
```
<br>

## 추가 부가적으로 설명하자면
- **생성자 매개변수**의 유효성 검사는 클래스 불변식을 어기는 객체가 만들어지지 않게 하는데 꼭 필요하다.
- 메서드 바디 실행 전에 유효성 검사를 실시하라고 했는데, 이 규칙에도 *예외*가 있다.
  - 유효성 검사 비용이 지나치게 높거나 실용적이지 않을 때
  - 계산 과정에서 암묵적으로 검사가 수행될 때
    - 예시. `Collections.sort(List)` 의 경우, 리스트 안의 객체들은 모두 상호 비교될 수 있어야하는데, 만약 상호 비교될 수 없는 타입의 객체가 들어있다면 객체 비교시 ClassCastException을 던진다. 즉 sort 메서드에 앞서 리스트 안의 모든 객체가 상호 비교될 수 있을지 검사해봐야 실익이 없다.
- 이번 아이템을 *매개변수에 제약을 두는 게 좋다*로 해석해서는 안된다. 사실은 그 반대! 메서드는 최대한 범용적으로 설계해야 한다.
