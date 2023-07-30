### 목차
- [ordinal 메서드란](#열거-타입의-ordinal-메서드란)
- [ordinal 메서드를 쓰면 안되는 이유](#ordinal-메서드를-쓰면-안되는-이유)
- [ordinal 메서드를 사용하지 말고 인스턴스 필드를 써라](#ordinal-사용하지-말고-인스턴스-필드에-저장)
- [부록. JPA 에서 ENUM 사용 시 주의](#부록-jpa에서의-enum-사용)

## 열거 타입의 ordinal 메서드란
> ordinal()  
> 모든 열거 타입이 제공하는 메서드로, 전체 열거 객체 중 몇 번째 열거 객체인지 알려준다.  
> 열거 객체의 순번은 0번 부터 시작한다.  
> **그러나, ordinal() 을 사용해서는 안된다.**

<br>

## ordinal 메서드를 쓰면 안되는 이유
```
public enum Ensemble {
	SOLO, DUET, TRIO, QUARTET, QUINTET,
	SEXTET, SETPTET, OCTET, NONET, DECTET;

	public int numberOfMusicians() { return ordinal() + 1 }
}
```
- 기본적으로 제공하는 메소드가 있어서 편리하게 쓸 수 있다는 생각도 든다.
- numberOfMusicians 메서드는 각 앙상블 뮤지션의 수를 반환하는데, ordinal 메서드를 활용하여 값을 리턴하게 되어 있다.
- 하지만 선언한 위치 값을 반환한다는 점이 가장 쎄하다!
  - 선언의 순서를 바꾸는 순간 numberOfMusicians 메서드 오동작한다.
  - 이미 사용 중인 정수와 값이 같은 상수는 추가할 방법이 없다. (TRIO가 이미 있으므로 3명이 연주하는 앙상블 추가 불가)
  - 값을 중간에 비울 수 없어서 더미(dummy) 상수를 추가하는 경우가 생긴다. (12명이 연주하는 앙상블 추가를 위해 11명으로 구성된 앙상블은 없는데 추가를 어떻게할까?)

<br>

## ordinal() 사용하지 말고 인스턴스 필드에 저장
- 해결법은 간단하다. 인스턴스 필드로 선언하는 것이다!
```
public enum Ensemble {
    SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
    SEXTET(6), SETPTET(7), OCTET(8), NONET(9), DECTET(10);

    private final int numberOfMusicians;
    Ensemble(int numberOfMusicians) {
        this.numberOfMusicians = numberOfMusicians;
    }
    public int numberOfMusicians() { return numberOfMusicians; }
}
```
- Enum 의 API 문서에도 ordinal 에 대해도 언급되어 있다.
> Returns the ordinal of this enumeration constant (its position in its enum declaration, where the initial constant is assigned an ordinal of zero). Most programmers will have no use for this method. It is designed for use by sophisticated enum-based data structures, such as java.util.EnumSet and java.util.EnumMap.  
> 간단하게 말하자면 "대부분 프로그래머는 이 메서드를 사용할 일이 없다. 이 메서드는 EnumSet과 EnumMap 같이 열거 타입 기반의 범용 자료구조에 쓸 목적으로 설계되었다" 라는 말입니다.

<br>

## 부록. JPA에서의 ENUM 사용
```
@Entity
public class ParameterGroup {
    @Enumerated(STRING)
    @Column(name="db")
    private Database database;
```
- 이렇게 선언한 Entity 가 있는 경우가 있다.
- JPA는 `@Enumerated` 로 Enum 필드와 테이블 컬럼을 매핑해준다.
- 열거형을 매핑하는 데 사용되는 유형을 설정해야 하는데 `ORDINAL`과 `STRING`이 있다.
- 위에서 ordinal 을 사용하면 안되는 이유 처럼, DB에 위치 값을 저장할 수 있기 때문에 절대 사용하면 안된다고 한다.
```
@Target({METHOD, FIELD}) 
@Retention(RUNTIME)
public @interface Enumerated {

    /** (Optional) The type used in mapping an enum type. */
    EnumType value() default ORDINAL;
}
```
- `@Enumerated` 의 기본값이 `ORDINAL` 이니 반드시 `@Enumerated(STRING)` 이렇게 매핑하여 사용해주자.
