### 목차
- [Serializable을 구현하면 릴리즈 뒤는 수정하기 어렵다.](#serializable을-구현하면-릴리즈-뒤에는-수정하기-어렵다)
- [버그와 보안 구멍이 생길 위험이 높아진다.](#버그와-보안-구멍이-생길-위험이-높아진다)
- [신버전을 릴리즈할 때 테스트할 것이 늘어난다.](#해당-클래스의-신버전을-릴리즈할-때-테스트할-것이-늘어난다)
- [상속용으로 설계된 클래스, 인터페이스는 Serializable을 확장하면 안된다.](#상속용으로-설계된-클래스는-serializable을-구현하면-안되며-인터페이스도-serializable을-확장하면-안된다)
- [내부 클래스는 Serializable을 구현하면 안된다.](#내부-클래스는-serializable을-구현하면-안된다)

## Serializable을 구현하면 릴리즈 뒤에는 수정하기 어렵다.
- 클래스가 Serializable을 구현하게 되면 직렬화된 바이트 스트림 인코딩도 하나의 공개 API가 된다. 때문에 이 클래스가 널리 퍼지면 그 직렬화 형태도 영원히 지원해야한다.
- 즉, Serializable을 구현한 순간부터 해당 객체의 직렬화 형태는 Java 직렬화에 묶이는 것이다. 기본 직렬화 형태에서는 private와 package-private 수준의 필드마저도 API로 공개가 된다. 즉, 캡슐화가 깨진다.
- person 객체를 ObjectOutputStream을 통해 직렬화를 한 뒤에 이를 FileOutputStream을 통해 `/Users/mimdong/personSerialize.txt` 에 객체 내용을 저장하면 다음과 같이 나타난다.
```java
@Test
void serialize() throws IOException {
    Person person = new Person("sun", 31);

    String SERIALIZE_OBJECT_FILE_PATH = "/Users/mimdong/personSerialize.txt";

    try (FileOutputStream fileOutputStream = new FileOutputStream(SERIALIZE_OBJECT_FILE_PATH)) {
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(person);
        }
    }
}
```
- 직렬화 결과를 보면 private 필드인 name과 age가 있는 것을 볼 수 있다. 이 때문에 캡슐화가 깨진다는 이야기를 하는 걸로 보임.
```
mimdong@mimdongs-MacBook-Pro ~ % cat personSerialize.txt
Item85$Person4�}��ƶIageLnametLjava/lang/String;xptsun%
```
- `SERIALIZE_OBJECT_FILE_PATH`에 저장된 직렬화 객체를 역직렬화 하려면 다음과 같이 하면된다.
```java
@Test
void deserialize() throws IOException {
    Person person = null;

    String SERIALIZE_OBJECT_FILE_PATH = "/Users/mimdong/personSerialize.txt";

    try (FileInputStream fileInputStream = new FileInputStream(SERIALIZE_OBJECT_FILE_PATH)) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            person = (Person) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    System.out.println(person.name);
    System.out.println(person.age);
}
```
- 단, 만약에 Person에 새로운 필드가 필요하다고 가정한다. 키 height와 몸무게 weight 정보가 추가되었는데 이렇게 필드가 추가된 경우 앞서 직렬화된 객체를 역직렬화할 수 없다. 기본적으로 serialVersionUID는 정의하지 않으면 해당 객체의 hashCode를 기반으로 설정이 되는데 height와 weight가 추가되면서 serialVersionUID이 바뀐 것이다. 때문에 실패가 발생한다.
```
java.io.InvalidClassException: Item86$Person; local class incompatible: stream classdesc serialVersionUID = -310980397754763054, local class serialVersionUID = -6238126966846281559
```
- 위와 같은 문제를 방지하기 위해서는 serialVersionUID를 관리해야한다.
- serialVersionUID를 1로 정의하면 필드가 추가되더라도 해당 직렬화 정보에 serialVersionUID가 1인 경우 Person이라는 것을 알 수 있기 때문에 Person 객체로 다시 역직렬화가 된다.
```java
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

## 버그와 보안 구멍이 생길 위험이 높아진다.
- 객체를 생성하는 가장 기본적인 방법은 생성자를 이용하는 것이다.
- 근데 `ObjectInputStream.readObject` 는 객체를 만들어 낼 수 있는 마법같은 메서드이다. 즉, 객체를 Serializable로 구현하면 생성자 이외에 객체를 생성할 수 있는 숨은 생성자가 생기는 것이다.

## 해당 클래스의 신버전을 릴리즈할 때 테스트할 것이 늘어난다.
- 앞서 본 Serializable의 문제점과 같이 구버전의 직렬화 형태가 신버전에서 역직렬화가 가능한지, 그 역도 가능한지 테스트해야한다. 즉, 테스트의 양이 직렬화 가능 클래스의 수와 릴리즈 횟수에 비례한다.
- 릴리즈 할 때마다 반드시 양방향 직렬화/역직렬화가 가능한지 확인하고 원래의 객체를 충실히 복제가능한지 반드시 확인해야한다.

## 상속용으로 설계된 클래스는 Serializable을 구현하면 안되며, 인터페이스도 Serializable을 확장하면 안된다.
- 이 규칙을 따르지 않고 Serializable을 확장, 구현하면 앞서 언급한 Java 직렬화의 문제를 고스란히 하위 구현 클래스들이 가지게 된다.

## 내부 클래스는 Serializable을 구현하면 안된다.
- 내부 클래스는 바깥 인스턴스의 참조와 유효 범위 안의 지역변수들을 저장하기 위해 컴파일러가 자동으로 생성한 필드가 추가된다. 익명 클래스와 지역 클래스의 이름 짓는 규칙이 언어 명세에도 없기 때문에 이 필드들이 클래스 정의에 어떻게 추가되는지도 정의되지 않았다. 따라서 내부 클래스의 직렬화 형태는 불분명하므로 Serializable을 구현하면 안된다.
- 단, 정적 멤버 클래스는 Serializable 구현으로 Java 직렬화가 가능하다.