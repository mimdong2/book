import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class Item85 {

    static class Person implements Serializable {
        private String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @DisplayName("Serializable 를 구현한 Person 객체 직렬화 후, 역직렬화 테스트")
    @Test
    void writeObjectTest() throws IOException {
        Person person = new Person("minjung", 30);

        byte[] serializedPerson;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(person);
                // 직렬화된 person 객체
                serializedPerson = baos.toByteArray();
            }
            // Item85$Person4�}��ƶ I ageL namet Ljava/lang/String;xp   t minjung
            System.out.println(baos);
        }

        Person deserializedPerson = null;

        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedPerson)) {
            try (ObjectInputStream ois = new ObjectInputStream(bais)) {
                // 역직렬화된 Person 객체
                deserializedPerson = (Person) ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(deserializedPerson.name);    // minjung
        System.out.println(deserializedPerson.age);     // 30
    }

}
