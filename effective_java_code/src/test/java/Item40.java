import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

public class Item40 {

    public class Bigram {
        private final char first;
        private final char second;

        public Bigram(char first, char second) {
            this.first = first;
            this.second = second;
        }

//        @Override
//        public boolean equals(Bigram bigram) {
//            return bigram.first == first && bigram.second == second;
//        }

        @Override
        public boolean equals(Object bigram) {
            if (! (bigram instanceof Bigram)) {
                return false;
            }
            Bigram b = (Bigram) bigram;
            return b.first == first && b.second == second;
        }


        public int hashCode() {
            return 31 * first + second;
        }
    }

    @Test
    public void aa부터zz출력() {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            for (char ch = 'a'; ch <= 'z'; ch++) {
                s.add(new Bigram(ch, ch));
            }
        }
        System.out.println(s.size());
    }

    public abstract class Animal {
        public abstract void eat();
    }

    public class Dog extends Animal {
        public void eat(String food) {
        }

        @Override
        public void eat() {

        }
    }
}
