### 목차
- [@Override 애너테이션 이란](#override-애너테이션-이란)
- [@Override 를 사용하지 않은 경우](#override-를-사용하지-않은-경우)
- [@Override 를 사용해라!](#override-를-사용해야-한다)
- [예외로, 추상메서드를 구현할 때](#단-예외적으로-추상메서드를-구현할-때를-제외하고는-override-를-사용하라)

## @Override 애너테이션 이란
- 자바에서 기본적으로 제공하는 애너테이션으로, 메서드 선언에만 달 수 있다.
- 상위 타입의 메서드를 재정의 했음을 뜻한다.
- `@Override` 애너테이션을 일관되게 사용하면 악명 높은 버그를 예방한다.
- IDE 에서 잘 달아주는 `@Override` 를 굳이 지우지 말자.

<br>

## @Override 를 사용하지 않은 경우
```java
public class Bigram {
private final char first;
private final char second;

public Bigram(char first, char second) {
    this.first = first;
    this.second = second;
}

public boolean equals(Bigram bigram) {
    return bigram.first == first && bigram.second == second;
}

public int hashCode() {
    return 31 * first + second;
}
}

@Test
public void aa부터zz출력() {
Set<Bigram> s = new HashSet<>();
for (int i = 0; i < 10; i++) {
    for (char ch = 'a'; ch <= 'z'; ch++) {
        s.add(new Bigram(ch, ch));
    }
}
System.out.println(s.size());
// 260 이 출력됨
// `Set`을 사용했으니 aa~zz 까지 26 이 출력되길 기대하였으나 260이 출력된다.
}
```
- `equals` 를 재정의(overriding)한게 아니라 다중정의(overloading) 했기 때문이다.
    - `Object`의 `equals` 는 `Object` 를 매개변수 타입으로 갖으며, `==` 연산자와 같이 객체 식별성(identity)만을 확인한다.
    ```java
    public boolean equals(Object obj) {
    return (this == obj);
    }
    ```
- Set 에서 equals(Object) 를 호출하게 되었고, Bigram 에서는 같은 값을 가지더라도, 다른 값으로 인식하였기 때문이다.

<br>

## @Override 를 사용해야 한다.
- `Method does not override method from its superclass` 컴파일 에러가 나며, 오버로딩이 잘못 되었음을 알려준다.
```java
@Override
public boolean equals(Bigram bigram) {
    return bigram.first == first && bigram.second == second;
}
```
- @Override 를 사용하니 잘못된 부분을 명확히 알려주니! 수정해서 올바르게 사용할 수 있게 된다.
```java
@Override
public boolean equals(Object bigram) {
    if (! (bigram instanceof Bigram)) {
        return false;
    }
    Bigram b = (Bigram) bigram;
    return b.first == first && b.second == second;
}
```

<br>

## 단 예외적으로, 추상메서드를 구현할 때를 제외하고는 `@Override` 를 사용하라.
- 구체 클래스에서 상위 클래스의 추상 메서드를 재정의 할 때는 굳이 @Override를 달지 않아도 된다. (단다고 해로운 것도 없다.)
- 구체 클래스인데 아직 구현하지 않은 추상 메서드가 남아 있다면 컴파일러가 그 사실을 바로 알려주기 때문이다.
- 
```java
public abstract class Animal {
    public abstract void eat();
}

public class Dog extends Animal {
    public void eat(String food) {
    }
}

// 컴파일 에러가 뜬다!
// Class 'Dog' must either be declared abstract or implement abstract method 'eat()' in 'Animal'
```