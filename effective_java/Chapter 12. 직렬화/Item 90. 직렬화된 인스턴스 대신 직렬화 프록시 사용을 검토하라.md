### 목차
- [직렬화 프록시 패턴이란](#직렬화-프록시-패턴이란)
- [직렬화 프록시 패턴의 장점](#직렬화-프록시의-장점)
- [직렬화 프록시 패턴의 한계](#직렬화-프록시의-한계)

## 직렬화 프록시 패턴이란
```java
public final class Period implements Serializable {
    private final Date start;
    private final Date end;

    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0) {
            throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
        }
    }

    // 자바의 직렬화 시스템이 바깥 클래스의 인스턴스 말고, SerializationProxy 의 인스턴스를 반환하게 하는 역할
    // 이 메서드 덕분에 직렬화 시스템은 바깥 클래스의 직렬화된 인스턴스를 생성해낼 수 없다.
    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    // 불변식을 훼손하고자 하는 시도를 막을 수 있는 메소드
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("프록시가 필요합니다");
    }

    // 바깥 클래스의 논리적인 상태를 정밀하게 표현하는 중첩 클래스 (Period 의  직렬화 프록시)
    private class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 234098243823485285L;
        private final Date start;
        private final Date end;

        //생성자는 단 하나여야 하고, 바깥 클래스의 인스턴스를 매개변수로 받고 데이터를 복사해야 함
        SerializationProxy(Period p) {
            this.start = p.start;
            this.end = p.end;
        }

        // 역직렬화 시 직렬화 시스템이 직렬화 프록시를 다시 바깥 클래스의 인스턴스로 반환해줌
        // 역직렬화는 불변식을 깨트릴 수 있다는 불안함이 있는데, 이 메서드가 불변식을 깨트릴 위험이 적은 정상적인 방법으로 역직렬화한 인스턴트를 얻게 해준다.
        private Object readResolve() {
            return new Period(start, end);
        }
    }
}
```

## 직렬화 프록시의 장점
- 방어적 복사처럼, 직렬화 프록시 패턴은 가짜 바이트 스트림 공격과 내부 필드 탈취 공격을 프록시 수준에서 차단해준다.
- 직렬화 프록시는 Period 필드를 final로 선언해도 되므로 Period 클래스를 진정한 불변으로 만들 수 있다.
- 어떤 필드가 기만적인 직렬화 공격의 목표가 될지 고민하지 않아도 되며, 역직렬화 때 유효성 검사를 수행하지 않아도 된다.
- 직렬화 프록시 패턴은 역직렬화한 인스턴스와 원래의 직렬화된 인스턴스의 클래스가 달라도 정상 작동한다.

## 직렬화 프록시의 한계
- 클라이언트가 멋대로 확장할 수 있는 클래스에는 적용할 수 없다.
- 객체 그래프에 순환이 있는 클래스에 적용할 수 없다.
- 방어적 복사보다 느리다
