import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
public class Item37 {

    public class Snack {
        enum Flavor { HOT, SWEET, SOUR, SALTY }

        final String name;
        final Flavor flavor;

        public Snack(String name, Flavor flavor) {
            this.name = name;
            this.flavor = flavor;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    void ordinal_인덱싱_사용_금지() {
        // 과자의 맛을 기준으로 분류하기 위해 Set 배열을 만들고, 배열의 사이즈는 맛의 개수로 초기화
        Set<Snack>[] snackByFlavor = (Set<Snack>[]) new Set[Snack.Flavor.values().length];

        // Set 배열을 초기화 한다.
        for (int i = 0; i < snackByFlavor.length; i++) {
            snackByFlavor[i] = new HashSet<>();
        }

        Snack[] shoppingSnack = {
                new Snack("바나나", Snack.Flavor.SWEET),
                new Snack("감자깡", Snack.Flavor.SALTY),
                new Snack("빅파이", Snack.Flavor.SWEET),
                new Snack("오감자", Snack.Flavor.SWEET),
                new Snack("썬칩", Snack.Flavor.HOT),
                new Snack("에낙", Snack.Flavor.HOT),
                new Snack("오레오", Snack.Flavor.SWEET),
                new Snack("콘칩", Snack.Flavor.SALTY),
                new Snack("아이셔", Snack.Flavor.SOUR)
        };

        // 과자 여러개를 맛 별로 분리한다.
        for (Snack snack : shoppingSnack) {
            snackByFlavor[snack.flavor.ordinal()].add(snack);
        }

        // 결과 출력
        // HOT -> [에낙, 썬칩], SWEET -> [빅파이, 오감자, 오레오, 바나나], SOUR -> [아이셔], SALTY -> [콘칩, 감자깡],
        for (int i = 0; i < snackByFlavor.length; i++) {
            // 형식 : Flavor.SWEET -> [바나, 빅파이, 오감자, 오레오]
            System.out.printf("%s -> %s, ", Snack.Flavor.values()[i], snackByFlavor[i]);
        }
    }

    @Test
    void enummap을_사용하자() {
        // 과자의 맛을 기준으로 분류하기 위해 EnumMap을 사용하자.
        Map<Snack.Flavor, Set<Snack>> snackByFlavor = new EnumMap<>(Snack.Flavor.class);


        // Map 을 초기화 해준다.
        for (Snack.Flavor flavor : Snack.Flavor.values()) {
            snackByFlavor.put(flavor, new HashSet<>());
        }

        Snack[] shoppingSnack = {
                new Snack("바나나", Snack.Flavor.SWEET),
                new Snack("감자깡", Snack.Flavor.SALTY),
                new Snack("빅파이", Snack.Flavor.SWEET),
                new Snack("오감자", Snack.Flavor.SWEET),
                new Snack("썬칩", Snack.Flavor.HOT),
                new Snack("에낙", Snack.Flavor.HOT),
                new Snack("오레오", Snack.Flavor.SWEET),
                new Snack("콘칩", Snack.Flavor.SALTY),
                new Snack("아이셔", Snack.Flavor.SOUR)
        };

        // 과자 여러개를 맛 별로 분리한다.
        for (Snack snack : shoppingSnack) {
            snackByFlavor.get(snack.flavor).add(snack);
        }

        // 결과 출력
        // {HOT=[에낙, 썬칩], SWEET=[빅파이, 오감자, 오레오, 바나나], SOUR=[아이셔], SALTY=[콘칩, 감자깡]}
        System.out.println(snackByFlavor);
    }

    @Test
    void 스트림사용() {
        Snack[] shoppingSnack = {
                new Snack("바나나", Snack.Flavor.SWEET),
                new Snack("감자깡", Snack.Flavor.SALTY),
                new Snack("빅파이", Snack.Flavor.SWEET),
                new Snack("오감자", Snack.Flavor.SWEET),
                new Snack("썬칩", Snack.Flavor.HOT),
                new Snack("에낙", Snack.Flavor.HOT),
                new Snack("오레오", Snack.Flavor.SWEET),
                new Snack("콘칩", Snack.Flavor.SALTY),
//                new Snack("아이셔", Snack.Flavor.SOUR)
        };

        // 스트림사용1 - EnumMap 이용하지 않음
        // {SALTY=[감자깡, 콘칩], SWEET=[바나나, 빅파이, 오감자, 오레오], HOT=[썬칩, 에낙]}
        System.out.println(Arrays.stream(shoppingSnack)
                .collect(Collectors.groupingBy(p -> p.flavor)));

        // 스트림사용2 - EnumMap 이용하여 데이터와 열거타입을 매핑
        // {HOT=[에낙, 썬칩], SWEET=[오감자, 오레오, 빅파이, 바나나], SALTY=[콘칩, 감자깡]}
        System.out.println(Arrays.stream(shoppingSnack)
                .collect(Collectors.groupingBy(p -> p.flavor,
                        () -> new EnumMap<>(Snack.Flavor.class), Collectors.toSet())));
    }
}
