import org.junit.jupiter.api.Test;

import java.util.Date;

public class Item50 {

    public final class Period {
        private final Date start;
        private final Date end;

        public Period(Date start, Date end) {
            this.start = new Date(start.getTime());
            this.end = new Date(end.getTime());
            if(this.start.compareTo(this.end) > 0) {
                throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
            }

//            if (start.compareTo(end) > 0) {
//                throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
//            }
//            this.start = start;
//            this.end = end;
        }

        public Date start() {
            return start;
        }
        public Date end() {
            return end;
        }
    }

    @Test
    void 불변식을깨트리다() {
        Date start = new Date();
        Date end = new Date();
        Period p1 = new Period(start, end);
        System.out.println("초기 end >>>>>>>>>>> " + p1.end());
        end.setYear(78);
        System.out.println("end 변경 후, end >>>> " + p1.end());
    }

    @Test
    void 클론사용() {
        Date start = new Date();
        Date end = new Date();
        Period p1 = new Period(start, end);
        System.out.println("초기 end >>>>>>>>>>> " + p1.end());
        Object clone = start.clone();
    }

    @Test
    void 또깨트리자() {
        Date start = new Date();
        Date end = new Date();
        Period p = new Period(start, end);
        System.out.println("초기 end >>>>>>>>>>> " + p.end());
        p.end.setYear(79); // p 의 내부를 변경했다.
        System.out.println("end 변경 후, end >>>> " + p.end());
    }


}
