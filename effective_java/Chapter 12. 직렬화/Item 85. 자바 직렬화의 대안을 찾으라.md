### 목차
- [직렬화란](#직렬화란)
- [바이트 스트림이란](#바이트-스트림이란)
- [객체에서 바이트 직렬화를 어떻게 하는가](#객체에서-바이트-직렬화를-어떻게-하는가)
- [직렬화가 문제가 되는 이유](#직렬화가-문제가-되는-이유)
- [그럼 어떻게 하는가](#그럼-어떻게)


## 직렬화란
- 넓은 의미로 직렬화는 어떤 데이터를 다른 데이터의 형태로 변환하는 것을 말합니다.
- 이팩티브 자바에서 말하는 직렬화(Serializable)란 바이트 스트림으로의 직렬화로 객체의 상태를 바이트 스트림으로 변환하는 것을 의미합니다.
- 반대로 바이트 스트림에서 객체의 상태로 변환하는 건 역직렬화(Deserializable)라고 부릅니다.
- 직렬화를 사용하는 이유로는 자바에서 표현된 객체를 목적지에 보냈다고 했을 때 그 곳에서 아 이게 자바 객체구나~하고 바로 알 수가 없습니다. 목적지에서 객체를 알 수 있는 방법이 없습니다. 그래서 모두 다 알 수 있는 것으로 변환을 해줘야 합니다. 여기서 변환을 도와주는 방법이 직렬화이며 직렬화를 통해서 바이트 스트림으로 변환해줄 수 있습니다.

## 바이트 스트림이란
- 스트림은 데이터의 흐름입니다. 데이터의 통로라고도 이야기하는데요.
- 예를 들어, 웹 개발을 하다보면 클라이언트에서 서버에게 데이터를 보내는 일이 있습니다. 이처럼 스트림은 클라이언트와 서버같이 어떤 출발지와 목적지로 입출력하기 위한 통로를 말합니다.
- 자바는 이런 입출력 스트림의 기본 단위를 바이트로 두고 있고 입력으로는 InputStream, 출력으로는 OutputStream라는 추상클래스로 구현되어 있습니다.
- 굳이 바이트 스트림으로 변환하는 이유로는 바이트인 이유는 컴퓨터에서 기본으로 처리되는 최소 단위가 바이트이기 때문입니다. 완전 최소 단위로 가면 비트로도 처리할 수 있겠지만 표현할 수 있는 방법이 0과 1로 너무 적어 하나의 단위로 묶었다고 합니다. 이렇게 바이트 스트림으로 변환해야 네트워크, DB로 전송할 수 있고 목적지에서도 처리를 해줄 수 있습니다. 쉽게 생각하면 '출발지와 목적지 모두 알아들을 수 있는 byte라는 언어로 소통할 수 있도록' 이라고 말할 수 있겠네요.

## 객체에서 바이트 직렬화를 어떻게 하는가?
- 객체에 Serializable 이라는 인터페이스를 구현하면 됩니다.
- 참고로 Serializable 은 안에 아무것도 선언되어있지 않습니다. 이를 마커 인터페이스라고 부르는데요. 말 그대로 직렬화를 할 수 있는 객체라고 알려주는 것입니다.
```java
static class Person implements Serializable {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}

@DisplayName("Serializable 를 구현한 Person 객체 직렬화 후, 역직렬화 테스트")
@Test
void writeObjectTest() throws IOException {
    Person person = new Person("minjung", 30);

    byte[] serializedPerson;
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(person);
            // 직렬화된 person 객체
            serializedPerson = baos.toByteArray();
        }
    }

    Person deserializedPerson = null;

    try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedPerson)) {
        try (ObjectInputStream ois = new ObjectInputStream(bais)) {
            // 역직렬화된 Person 객체
            deserializedPerson = (Person) ois.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    System.out.println(deserializedPerson.name);
    System.out.println(deserializedPerson.age);
}
```

## 직렬화가 문제가 되는 이유
- 직렬화의 근본적인 문제는 공격 범위가 너무 넓고 지속적으로 더 넓어져 방어하기 어렵다는 점입니다.
- 직렬화를 하고 나서 역직렬화를 할 때 문제가 됩니다.
    - 객체를 읽는 readObject 메서드는 클래스 패스에 존재하는 거의 모든 타입의 객체를 만들어낼 수 있습니다. 반환 타입이 Object입니다
    - 바이트 스트림을 역직렬화하는 과정에서 해당 타입 안의 모든 코드를 수행할 수 있습니다. 객체를 아예 불러올 수 있으므로 모든 코드를 수행할 수 있습니다.
    - 그렇기에 타입 전체가 전부 공격 범위에 들어가게 됩니다.
    - 용량도 다른 포맷에 비해서 몇 배 이상의 크기를 가집니다
    - 또한 역직렬화 폭탄을 맞을 수도 있습니다.

## 그럼 어떻게?
- 가장 좋은 방법은 아무것도 역직렬화하지 않는 것이라고 합니다. 여러분이 작성하는 새로운 시스템에서 자바 직렬화를 써야할 이유는 전혀 없다.
- 객체와 바이트 시퀀스를 변환해주는 다른 메커니즘이 많이 있다. 이 방식들은 자바 직렬화의 여러 위험을 회피하면서 다양한 플랫폼 지원, 우수한 성능, 풍부한 지원 도구, 활발한 커뮤니티와 전문가 집단 등 수많은 이점까지 제공한다. 이런 메커니즘들도 직렬화 시스템이라 불리기도 하지만, 이 책에서는 자바 직렬화와 구분하고자 크로스-플랫폼 구조화된 데이터 표현이라 한다. (예. JSON)
- 직렬화를 피할 수 없고 역직렬화한 데이터가 안전한지 완전히 확신할 수 없다면 java 9에 나온 ObjectInputFilter를 사용하는 것도 방법입니다. 이는 데이터 스트림이 역직렬화되기 전에 필터를 적용해서 특정 클래스를 받아들이거나 거부할 수 있습니다.