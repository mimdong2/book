import org.junit.jupiter.api.Test;

import java.util.Objects;

public class Item49 {

    @Test
    void 유효성검사API() {
//        Objects.checkFromIndexSize();
//        Objects.checkFromToIndex();
//        Objects.checkIndex();
//        Objects.requireNonNull()
    }

    private static void sort(long a[], int offset, int length) {
        assert a != null;
        assert offset >= 0 && offset <= a.length;
        assert length >= 0 && length <= a.length - offset;

    }

    @Test
    void assert테스트() {
        long[] a = new long[10];
        int offset = 3;
        int length = -1;
        sort(a, offset, length);
    }
}
