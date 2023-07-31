### 목차
- [ordinal 메서드란](#열거-타입의-ordinal-메서드란)
- [ordinal 메서드를 쓰면 안되는 이유](#ordinal-메서드를-쓰면-안되는-이유)
- [ordinal 메서드를 사용하지 말고 인스턴스 필드를 써라](#ordinal-사용하지-말고-인스턴스-필드에-저장)
- [부록. JPA 에서 ENUM 사용 시 주의](#부록-jpa에서의-enum-사용)

## 열거 타입의 ordinal 메서드란
> ordinal()  
> 모든 열거 타입이 제공하는 메서드로, 전체 열거 객체 중 몇 번째 열거 객체인지 알려준다.  
> 열거 객체의 순번은 0번 부터 시작한다.  
> **그러나, ordinal()패₩
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
