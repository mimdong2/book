import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

public class Item88 {

    public final class Period {
        private final Date start;
        private final Date end;

        /**
         * @param  start 시작 시각
         * @param  end 종료 시각; 시작 시각보다 뒤여야 한다.
         * @throws IllegalArgumentException 시작 시각이 종료 시각보다 늦을 때 발생한다.
         * @throws NullPointerException start나 end가 null이면 발생한다.
         */
        public Period(Date start, Date end) {
            this.start = new Date(start.getTime()); // 가변인 Date 클래스의 위험을 막기 위해 새로운 객체로 방어적 복사를 한다.
            this.end = new Date(end.getTime());

            if (this.start.compareTo(this.end) > 0) {
                throw new IllegalArgumentException(start + " after " + end);
            }
        }

        public Date start() { return new Date(start.getTime()); }
        public Date end() { return new Date(end.getTime()); }
        public String toString() { return start + " - " + end; }
        // ... 나머지 코드는 생략

        // 불변식을 깨뜨리도록 조작된 바이트 스트림
        private static final byte[] serializedForm = {
                (byte)0xac, (byte)0xed, 0x00, 0x05, 0x73, 0x72, 0x00, 0x06,
                0x50, 0x65, 0x72, 0x69, 0x6f, 0x64, 0x40, 0x7e, (byte)0xf8,
                0x2b, 0x4f, 0x46, (byte)0xc0, (byte)0xf4, 0x02, 0x00, 0x02,
                0x4c, 0x00, 0x03, 0x65, 0x6e, 0x64, 0x74, 0x00, 0x10, 0x4c,
                0x6a, 0x61, 0x76, 0x61, 0x2f, 0x75, 0x74, 0x69, 0x6c, 0x2f,
                0x44, 0x61, 0x74, 0x65, 0x3b, 0x4c, 0x00, 0x05, 0x73, 0x74,
                0x61, 0x72, 0x74, 0x71, 0x00, 0x7e, 0x00, 0x01, 0x78, 0x70,
                0x73, 0x72, 0x00, 0x0e, 0x6a, 0x61, 0x76, 0x61, 0x2e, 0x75,
                0x74, 0x69, 0x6c, 0x2e, 0x44, 0x61, 0x74, 0x65, 0x68, 0x6a,
                (byte)0x81, 0x01, 0x4b, 0x59, 0x74, 0x19, 0x03, 0x00, 0x00,
                0x78, 0x70, 0x77, 0x08, 0x00, 0x00, 0x00, 0x66, (byte)0xdf,
                0x6e, 0x1e, 0x00, 0x78, 0x73, 0x71, 0x00, 0x7e, 0x00, 0x03,
                0x77, 0x08, 0x00, 0x00, 0x00, (byte)0xd5, 0x17, 0x69, 0x22,
                0x00, 0x78
        };

        public static void main(String[] args) {
            Period p = (Period) deserialize(serializedForm);
            System.out.println(p.start);
            System.out.println(p.end);
        }

        static Object deserialize(byte[] sf) {
            try {
                return new ObjectInputStream(new ByteArrayInputStream(sf)).readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }
    }



}
