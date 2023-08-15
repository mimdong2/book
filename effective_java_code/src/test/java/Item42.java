import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.DoubleBinaryOperator;
import java.util.function.Function;

public class Item42 {

    @Test
    void 익명클래스() {
        List<String> words = new ArrayList<>(List.of("a", "abc", "bc", "ab"));
        Collections.sort(words, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.compare(o1.length(), o2.length());
            }
        });
        System.out.println(words);
    }

    @Test
    void 람다() {
        List<String> words = new ArrayList<>(List.of("a", "abc", "bc", "ab"));
        words.sort(Comparator.comparingInt(String::length));
        System.out.println(words);
    }

    public enum Operation {
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
        };

        private final String symbol;

        @Override
        public String toString() {
            return symbol;
        }

        Operation(String symbol) { this.symbol = symbol; }

        public abstract double apply(double x, double y);
    }

    public enum OperationWithLambda {
        PLUS("+", (x, y) -> x + y),
        MINUS("-", (x, y) -> x - y);

        private final String symbol;
        private final DoubleBinaryOperator op;

        @Override
        public String toString() {
            return symbol;
        }

        OperationWithLambda(String symbol, DoubleBinaryOperator op) {
            this.symbol = symbol;
            this.op = op;
        }

        public double apply(double x, double y) {
            return op.applyAsDouble(x, y);
        }
    }

    public enum Pay {
        NAVER("네이버", money -> money/100);
//        KAKAO("카카오", money -> {
//            System.out.println(name);       // 인스턴스는 런타임에 만들어지므로 접근 불가능
//            System.out.println(money);
//            return money/10;
//        });

        private final String name;
        private final Function<Integer, Integer> op;

        Pay(String name, Function<Integer, Integer> op) {
            this.name = name;
            this.op = op;
        }
    }

}
