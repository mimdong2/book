import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;

public class Item90 {

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




}
