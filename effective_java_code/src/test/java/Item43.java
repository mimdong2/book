import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Item43 {

    public class Person {
        private String name;

        public Person(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Test
    void 람다방식() {
        Person person1 = new Person("김00");
        Person person2 = new Person("이00");
        Person person3 = new Person("박00");
        Person person4 = new Person("김00");
        List<Person> people = Arrays.asList(person1, person2, person3, person4);

        Map<String, Integer> counts = new HashMap<>();
        for (Person person : people) {
            counts.merge(person.getName(), 1, (existingValue, provideValue) -> existingValue + provideValue);
        }
        System.out.println(counts);
    }

    @Test
    void 메서드참조방식() {
        Person person1 = new Person("김00");
        Person person2 = new Person("이00");
        Person person3 = new Person("박00");
        Person person4 = new Person("김00");
        List<Person> people = Arrays.asList(person1, person2, person3, person4);

        Map<String, Integer> counts = new HashMap<>();
        for (Person person : people) {
            counts.merge(person.getName(), 2, Integer::sum);
        }
        System.out.println(counts);
    }

    public class SoLoooooooooooooooooooongName {
        public void function() {
            // 람다방식
            execute(() -> action());
            // 메서드 참조 방식
            execute(SoLoooooooooooooooooooongName::action);
        }
        private void execute(Supplier<?> supplier) {

        }
        private static Object action() {
            return null;
        }
    }


}
