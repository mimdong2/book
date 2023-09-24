

## 객체의 물리적 표현과 논리적 표현이 같다면 기본 직렬화 형태라도 무방하다.
- "이상적인 직렬화 상태" 로는 물리적 표현과 논리적 내용이 같은 것이다.
    - 물리적 표현 → 코드로 어떻게 구현했는지
    - 논리적 표현 → 실제로 어떤 것을 의미하는지
- 사람의 성명을 간략히 표현한 다음 예는 기본 직렬화 형태를 써도 괜찮다.
```java
public class Name implements Serializable {
    private final Stirng lastName;
    private final String firstName;
    private final String middleName;
}
```

## 직렬화 형태에 적합하지 않은 예로, 객체의 물리적인 표현과 논리적인 표현이 다른 경우의 문제점
- 논리적 표현으로는 일련의 문자열을 표현하지만, 물리적 표현으로는 문자열들을 이중 연결 리스트로 연결했다.
```java
public final class StringList implements Serializable {
    private int size = 0;
    private Entry head = null;

    private static class Entry implements Serializable {
        String data;
        Entry next;
        Entry previous;
    }
    // ... 생략
}
```
- 물리적 표현과 논리적 표현의 차이가 클 때 기본 직렬화 형태를 사용하면 문제가 생긴다.
    - 공개API가 현재의 내부 표현 방식에 영구히 묶인다.
        - 예를 들어, 향후 버전에서는 연결 리스트를 사용하지 않게 바꾸더라도 관련 처리는 필요해진다. 따라서 코드를 절대 제거할 수 없다.
    - 너무 많은 공간을 차지할 수 있다.
        - 위의 StringList 클래스를 예로 들면, 기본 직렬화를 사용할 때 각 노드의 연결 정보까지 모두 포함되고 이런 정보는 내부 구현에 해당하고, 직렬화 형태에 가치가 없다. 네트워크로 전송하는 속도만 느려진다.
    - 시간이 너무 많이 걸릴 수 있다.
        - 직렬화 로직은 객체 그래프의 위상에 관한 정보를 알 수 없으니, 직접 순회할 수밖에 없다.
    - 스택 오버플로를 일으킬 수 있다.
        - 기본 직렬화 형태는 객체 그래프를 재귀 순회한다. 호출 정도가 많아지면 이를 위한 스택이 감당하지 못할 것이다.