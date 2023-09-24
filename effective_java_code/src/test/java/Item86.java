import org.junit.jupiter.api.Test;

import java.io.*;

public class Item86 {

    static class Person implements Serializable {
        private static final long serialVersionUID = 2L;

        private String name;
        private int age;
        private double height;
        private double weight;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    @Test
    void serialize() throws IOException {
        Person person = new Person("sun", 31);

        String SERIALIZE_OBJECT_FILE_PATH = "/Users/mimdong/personSerialize.txt";

        try (FileOutputStream fileOutputStream = new FileOutputStream(SERIALIZE_OBJECT_FILE_PATH)) {
            try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
                objectOutputStream.writeObject(person);
            }
        }
    }

    @Test
    void deserialize() throws IOException {
        Person person = null;

        String SERIALIZE_OBJECT_FILE_PATH = "/Users/mimdong/personSerialize.txt";

        try (FileInputStream fileInputStream = new FileInputStream(SERIALIZE_OBJECT_FILE_PATH)) {
            try (ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
                person = (Person) objectInputStream.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println(person.name);
        System.out.println(person.age);
        System.out.println(person.weight);
        System.out.println(person.height);
    }
}
