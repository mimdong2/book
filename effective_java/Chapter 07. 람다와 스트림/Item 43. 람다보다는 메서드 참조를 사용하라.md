### 목차
- [메서드 참조의 간결함](#메서드-참조의-간결함)
- [메서드 참조의 유형](#메서드-참조의-유형)
- [단, 람다가 메서드 참조보다 간결한 경우도 있다.](#람다가-메서드-참조보다-간결한-경우도-있다)
- [람다보다는 메서드 참조를 사용하라](#결론)

## 메서드 참조의 간결함
- 람다가 익명 클래스보다 나은 점 중 가장 큰 특징은 *간결함*인데, 람다보다 더 간결하게 만드는 방법이 **메서드 참조**이다.
#### 람다 보다 메서드 참조를 사용하게 간결하게 표현한 사례
- 이 코드는 자바 8 때 `Map`에 추가된 `merge` 메서드를 사용했다.
  - `merge` 메서드는 키, 값, 함수를 인자로 받으며 주어진 키가 맵 안에 있다면 주어진 {키, (두번째 인수)값} 쌍을 그대로 저장한다.
  - 반대로 키가 맵 안에 없다면, (세번째 인수로 받은) 함수를 현재값과 주어진 값에 적용한 다음, 그 결과로 현재 값을 덮어쓴다. 즉 {키, 함수의 결과} 쌍을 저장한다.
- 함수의 매개변수 `existingValue`, `provideValue` 는 크게 하는 일 없이 공간을 많이 차지한다.
  - 람다는 두 인수의 합을 단순히 반환한다.
  - 자바 8 이 되면서, `Integer` 클래스(와 모든 기본타입의 박싱 타입)는 이 람다와 기능이 같은 정적 메서드 `sum` 을 제공하기 시작했다.
```java
public class Person {
private String name;

public Person(String name) { this.name = name; }

public String getName() { return name; }
}

@Test
void 람다방식() {
    Person person1 = new Person("김00");
    Person person2 = new Person("이00");
    Person person3 = new Person("박00");
    Person person4 = new Person("김00");
    List<Person> people = Arrays.asList(person1, person2, person3, person4);

    Map<String, Integer> counts = new HashMap<>();
    for (Person person : people) {
        counts.merge(person.getName(), 1, (existingValue, provideValue) -> existingValue + provideValue);
    }
    System.out.println(counts); // {박00=1, 이00=1, 김00=2}
}

@Test
void 메서드참조방식() {
    Person person1 = new Person("김00");
    Person person2 = new Person("이00");
    Person person3 = new Person("박00");
    Person person4 = new Person("김00");
    List<Person> people = Arrays.asList(person1, person2, person3, person4);

    Map<String, Integer> counts = new HashMap<>();
    for (Person person : people) {
        counts.merge(person.getName(), 1, Integer::sum);
    }
    System.out.println(counts); // {박00=1, 이00=1, 김00=2}
}
```

<br>

## 메서드 참조의 유형
| 메서드 참조 유형  | 예                    | 같은 기능을 하는 람다                 | 설명                                                               |
|------------|----------------------|------------------------------|------------------------------------------------------------------|
| 정적         | `Integer::parseInt`  | `str -> Integer.parseInt(str)` |                                                                  |
| 한정적(인스턴스)  | `Instant.now()::isAfter` | `Instant then = Instant.now();`<br/> `t-> then.isAfter(t)` | 수신 객체(참조 대상 인스턴스)를 특정한다.<br/>함수 객체가 받는 인수와 참조되는 메서드가 받는 인수가 똑같다. |
| 비한정적(인스턴스) | `String::toLowerCase` | `str -> str.toLowerCase()`  | 수신 객체를 특정하지 않는다. 함수 객체를 적용하는 시점에 수신 객체를 알려준다.                    |
| 클래스 생성자    | `TreeMap<K,V>::new`  | `() -> new TreeMap<K,V)`  | 팩터리 객체로 사용됨                                                      |
| 배열 생성자     | `Int[]::new`         | `len -> new int[len]`   | 팩터리 객체로 사용됨                                                      |

<br>

## 람다가 메서드 참조보다 간결한 경우도 있다.
- 메서드와 람다가 같은 클래스에 있는 경우, 람다가 더 간결한 코드를 작성할 수 있다.
```java
public class SoLoooooooooooooooooooongName {
    public void function() {
        // 람다방식
        execute(() -> action());
        // 메서드 참조 방식
        execute(SoLoooooooooooooooooooongName::action);
    }
    private void execute(Supplier<?> supplier) {
        
    }
    private static Object action() {
        return null;
    }
}
```
- 자바 API 사례  
`Function.identity()` 보다는 `(x -> x)` 가 더 짧고 명확하다.

<br>

## 결론
가독성을 위해 람다 대신 메서드 참조 방식을 사용하자. 하지만 람다 방식이 더 가독성이 좋다면 람다 방식을 쓰자.