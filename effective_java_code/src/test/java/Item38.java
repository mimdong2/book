import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public class Item38 {

    public interface Operation {
        double apply (double x, double y);
    }

    public enum SeoulOperation implements Operation {
        PLUS("+") {
            @Override
            public double apply(double x, double y) {
                return x + y;
            }
        },
        MINUS("-") {
            @Override
            public double apply(double x, double y) {
                return x - y;
            }
        },
        TIMES("*") {
            @Override
            public double apply(double x, double y) {
                return x * y;
            }
        },
        DIVIDE("/") {
            @Override
            public double apply(double x, double y) {
                return x / y;
            }
        }
        ;

        private final String symbol;

        SeoulOperation(String symbol) {
            this.symbol = symbol;
        }
    }

    public enum SuwonOperation implements Operation {
        // SeoulOperation 과 다르게 SuwonOperation 은 더하기,빼기만 있고 결과에 *2를 한다.
        PLUS("+") {
            @Override
            public double apply(double x, double y) {
                return (x + y) * 2;
            }
        },
        MINUS("-") {
            @Override
            public double apply(double x, double y) {
                return (x - y) * 2;
            }
        }
        ;

        private final String symbol;

        SuwonOperation(String symbol) {
            this.symbol = symbol;
        }
    }

    private static <T extends Enum<T> & Operation> void test (Class<T> opEnumType, double x, double y) {
        for (Operation op : opEnumType.getEnumConstants()) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }
    private static void test (Collection<? extends Operation> opSet, double x, double y) {
        for (Operation op : opSet) {
            System.out.printf("%f %s %f = %f%n", x, op, y, op.apply(x, y));
        }
    }

    @Test
    public void 한정적타입매개변수() {
        double x = 1.4;
        double y = 2.0;

        System.out.println("----SEOUL----");
        test(SeoulOperation.class, x, y);
        System.out.println("----SUWON----");
        test(SuwonOperation.class, x, y);
    }

    @Test
    public void 한정적와일드카드() {
        double x = 1.4;
        double y = 2.0;

        System.out.println("----SEOUL----");
        test(EnumSet.allOf(SeoulOperation.class), x, y);
        System.out.println("----SUWON----");
        test(EnumSet.allOf(SuwonOperation.class), x, y);
        System.out.println("----SEOUL, SUWON 의 PLUS----");
        test(Set.of(SuwonOperation.PLUS, SeoulOperation.PLUS), x, y);
    }

    @Test
    public void EnumSet과EnumMap제한() {
        // 가능
        EnumSet<SeoulOperation> s = EnumSet.of(SeoulOperation.PLUS, SeoulOperation.MINUS);

        // 불가능!!!!!
//        EnumSet<Operation> ss = EnumSet.of(SeoulOperation.PLUS, SeoulOperation.MINUS);
//        EnumSet.of(SeoulOperation.PLUS, SuwonOperation.PLUS);
    }

}


