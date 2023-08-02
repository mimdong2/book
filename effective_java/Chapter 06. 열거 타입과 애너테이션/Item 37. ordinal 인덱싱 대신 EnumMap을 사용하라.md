### 목차
- [ordinal 인덱싱을 쓰지 말자](#ordinal-값을-인덱스로-쓰면-안됨)
- [열거 타입을 키로 사용하는 EnumMap을 쓰자](#열거-타입을-키로-사용하는-enummap을-쓰자)
- [스트림을 사용하여 코드 단순화도 가능하다](#스트림을-사용하여-맵을-관리하면-코드를-더-줄일-수-있다)

## ordinal() 값을 인덱스로 쓰면 안됨
배열 또는 리스트의 인덱스로 `ordinal()` 값을 쓰는 경우가 있는데 그러지 말자.
```java
public class Snack {
    enum Flavor { HOT, SWEET, SOUR, SALTY }

    final String name;
    final Flavor flavor;

    public Snack(String name, Flavor flavor) {
        this.name = name;
        this.flavor = flavor;
    }

    @Override
    public String toString() {
        return name;
    }
}

@Test
void ordinal_인덱싱_사용_금지() {
    // 과자의 맛을 기준으로 분류하기 위해 Set 배열을 만들고, 배열의 사이즈는 맛의 개수로 초기화
    Set<Snack>[] snackByFlavor = (Set<Snack>[]) new Set[Snack.Flavor.values().length];

    // Set 배열을 초기화 한다.
    for (int i = 0; i < snackByFlavor.length; i++) {
        snackByFlavor[i] = new HashSet<>();
    }

    Snack[] shoppingSnack = {
            new Snack("바나나", Snack.Flavor.SWEET),
            new Snack("감자깡", Snack.Flavor.SALTY),
            new Snack("빅파이", Snack.Flavor.SWEET),
            new Snack("오감자", Snack.Flavor.SWEET),
            new Snack("썬칩", Snack.Flavor.HOT),
            new Snack("에낙", Snack.Flavor.HOT),
            new Snack("오레오", Snack.Flavor.SWEET),
            new Snack("콘칩", Snack.Flavor.SALTY),
            new Snack("아이셔", Snack.Flavor.SOUR)
    };

    // 과자 여러개를 맛 별로 분리한다.
    for (Snack snack : shoppingSnack) {
        snackByFlavor[snack.flavor.ordinal()].add(snack);
    }

    // 결과 출력
    // HOT -> [에낙, 썬칩], SWEET -> [빅파이, 오감자, 오레오, 바나나], SOUR -> [아이셔], SALTY -> [콘칩, 감자깡],
    for (int i = 0; i < snackByFlavor.length; i++) {
        // 형식 : Flavor.SWEET -> [바나, 빅파이, 오감자, 오레오]
        System.out.printf("%s -> %s, ", Snack.Flavor.values()[i], snackByFlavor[i]);
    }
}
```
- 배열은 제네릭과 호환되지 않으므로 비검사 형번환을 수행해야하고, 깔끔히 컴파일 되지 않는다. ([아이템28](/effective_java/Chapter%2005.%20%EC%A0%9C%EB%84%A4%EB%A6%AD/Item%2028.%20%EB%B0%B0%EC%97%B4%EB%B3%B4%EB%8B%A4%EB%8A%94%20%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%A5%BC%20%EC%82%AC%EC%9A%A9%ED%95%98%EB%9D%BC.md))
- 배열은 각 인덱스가 `ordinal()` 값이므로 의미를 모르니 출력 결과에 직접 레이블을 달아야 한다.
- 배열을 초기화 할때, 정수값을 잘못 입력하면 `ArrayIndexOutOfBoundException` 이 발생할 수도 있다.

<br>

## 열거 타입을 키로 사용하는 EnumMap을 쓰자
```java
@Test
void enummap을_사용하자() {
    // 과자의 맛을 기준으로 분류하기 위해 EnumMap을 사용하자.
    Map<Snack.Flavor, Set<Snack>> snackByFlavor = new EnumMap<>(Snack.Flavor.class);


    // Map 을 초기화 해준다.
    for (Snack.Flavor flavor : Snack.Flavor.values()) {
        snackByFlavor.put(flavor, new HashSet<>());
    }

    Snack[] shoppingSnack = {
            new Snack("바나나", Snack.Flavor.SWEET),
            new Snack("감자깡", Snack.Flavor.SALTY),
            new Snack("빅파이", Snack.Flavor.SWEET),
            new Snack("오감자", Snack.Flavor.SWEET),
            new Snack("썬칩", Snack.Flavor.HOT),
            new Snack("에낙", Snack.Flavor.HOT),
            new Snack("오레오", Snack.Flavor.SWEET),
            new Snack("콘칩", Snack.Flavor.SALTY),
            new Snack("아이셔", Snack.Flavor.SOUR)
    };

    // 과자 여러개를 맛 별로 분리한다.
    for (Snack snack : shoppingSnack) {
        snackByFlavor.get(snack.flavor).add(snack);
    }

    // 결과 출력
    // {HOT=[에낙, 썬칩], SWEET=[빅파이, 오감자, 오레오, 바나나], SOUR=[아이셔], SALTY=[콘칩, 감자깡]}
    System.out.println(snackByFlavor);
}
```
- `Set[]` 보다 `EnumMap` 이 코드상으로 명료하며 성능도 비슷하다.
- 안전하지 않은 형변환은 쓰지 않고, 맵의 키인 열거 타입이 그 자체로 출력용 문자로 제공되니 레이블을 다는 코드를 작성하지 않아도 된다.
- 배열 인덱스를 계산하는 과정에서 오류가 나지도 않는다.
- 내부 구현 방식을 안으로 숨겨서 `Map`의 타입 안전성과 배열의 성능을 모두 얻을 수 있다.
- `EnumMap`의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로, 런타임 제네릭 타입 정보를 제공한다. (아이템33)

<br>

## 스트림을 사용하여 맵을 관리하면 코드를 더 줄일 수 있다.
```java
@Test
void 스트림사용() {
    Snack[] shoppingSnack = {
            new Snack("바나나", Snack.Flavor.SWEET),
            new Snack("감자깡", Snack.Flavor.SALTY),
            new Snack("빅파이", Snack.Flavor.SWEET),
            new Snack("오감자", Snack.Flavor.SWEET),
            new Snack("썬칩", Snack.Flavor.HOT),
            new Snack("에낙", Snack.Flavor.HOT),
            new Snack("오레오", Snack.Flavor.SWEET),
            new Snack("콘칩", Snack.Flavor.SALTY),
//                new Snack("아이셔", Snack.Flavor.SOUR)
    };

    // 스트림사용1 - EnumMap 이용하지 않음
    // {SALTY=[감자깡, 콘칩], SWEET=[바나나, 빅파이, 오감자, 오레오], HOT=[썬칩, 에낙]}
    System.out.println(Arrays.stream(shoppingSnack)
            .collect(Collectors.groupingBy(p -> p.flavor)));

    // 스트림사용2 - EnumMap 이용하여 데이터와 열거타입을 매핑
    // {HOT=[에낙, 썬칩], SWEET=[오감자, 오레오, 빅파이, 바나나], SALTY=[콘칩, 감자깡]}
    System.out.println(Arrays.stream(shoppingSnack)
            .collect(Collectors.groupingBy(p -> p.flavor,
                    () -> new EnumMap<>(Snack.Flavor.class), Collectors.toSet())));
}
```
- EnumMap을 사용할 때 얻는 공간과 성능 이점이 사라진다는 문제가 있으니 주의해서 사용해야한다.
    - 스트림만 사용하면 Flavor 에 속하는 과자가 있을때만 맵을 만든다.
    - EnumMap을 사용하면 모든 Flavor 마다 맵을 만든다.
    - 위 예제처럼 Flavor.SOUR 가 없다면 스트림 버전에서는 맵을 3개 만들고 EnumMap 버전에서는 맵을 4개 만든다.