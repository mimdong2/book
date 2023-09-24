### 목차
- [싱글톤을 직렬화하게 되면?](#싱글톤을-직렬화하게-되면)
- [readResolve 란](#readresolve-란)
- [직렬화가 필요하며 인스턴스가 하나임을 보장하고 싶을 땐 enum 타입을 먼저 고려하라.](#enum-타입으로-만들어라)

## 싱글톤을 직렬화하게 되면?
- 싱글톤으로 구현된 클래스는 인스턴스가 하나만 만들어지는 것을 보장한다.
- 클래스에 implements Serializable 을 추가하는 순간 더 이상 싱글턴이 아니게 된다.
- 기본 직렬화를 쓰지 않더라도, 명시적인 readObject 를 제공하더라도 소용없다.
- 어떤 readObject를 사용하든 이 클래스가 초기화될 때 만들어진 인스턴스와는 별개인 인스턴스를 반환하게 된다.

## readResolve 란?
- 이때 readResolve 메서드를 이용하면 readObject 메서드가 만든 인스턴스를 다른 것으로 대체할 수 있다.
- 즉 역직렬화 과정에서 만들어진 인스턴스 대신에 기존에 생성된 싱글톤 인스턴스를 반환하게 한다.
- 실제로 역직렬화 과정에서 자동으로 호출되는 readObject 메서드가 있더라도 readResolve 메서드에서 반환한 인스턴스로 대체된다.
- 이때 readObject 가 만들어낸 인스턴스는 가비지 컬렉션의 대상이 된다.
- 그 후 `==` 연산자와 `equals` 메서드로 비교하면 true 가 뜬다.
```java
private Object readResolve() {
    // 기존에 생성된 진짜 인스턴스를 반환하고 가짜 인스턴스는 GC 에 맡긴다
    return INSTANCE;
}
```
- 위 readResolve 메서드는 역직렬화한 객체는 무시하고 클래스 초기화 때 만들어진 인스턴스를 반환한다. 따라서 인스턴스의 직렬화 형태는 아무런 실 데이터를 가질 이유가 없으니 모든 인스턴스의 필드를 transient 로 선언해야 한다. 그러니까 readResolve 메서드를 인스턴스의 통제 목적으로 이용한다면 모든 필드는 transient로 선언해야 한다.

## Enum 타입으로 만들어라.
- Enum 타입과 같이 인스턴스가 통제된 직렬화 가능한 클래스를 작성하면 선언된 객체 이외에는 어떠한 인스턴스도 있지 않음을 보장한다.
```java
public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs = { "Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
}
```