import org.junit.jupiter.api.Test;

import java.util.EnumSet;

public class Item53 {

    static int sum(int... numbers) {
        int sum = 0;
        for (int number : numbers) {
            sum += number;
        }
        return sum;
    }

    @Test
    void 가변인수메서드사용() {
        System.out.println(sum(1, 2, 3));      // 6
        System.out.println(sum());                      // 0
        System.out.println(sum(1));            // 1
    }

    static int min(int... numbers) {
        if (numbers.length == 0) {
            throw new IllegalArgumentException("인수가 1개 이상 필요!!!!");
        }
        int min = numbers[0];
        for (int i = 1; i < numbers.length; i++) {
            if (numbers[i] < min) {
                min = numbers[i];
            }
        }
        return min;
    }

    static int min(int firstNumber, int... remainNumbers) {
        int min = firstNumber;
        for (int remainNumber : remainNumbers) {
            if (remainNumber < min) {
                min = remainNumber;
            }
        }
        return min;
    }

}
