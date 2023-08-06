import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Item41 {

    /**
     * 테스트 메서드임을 선언하는 애너테이션이다.
     * 매개변수 없는 정적 메서드 전용이다. (???)
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Test41 {
    }

    public class Sample {
        @Test41 public static void m1() {}
        public static void m2() {}
        @Test41 public static void m3() {
            throw new RuntimeException("fail");
        }
        @Test41
        public static void m5(String aa) {}
    }

     public class RunTest {
         int tests = 0;
         int passed = 0;
         Class<?> testClass;

         public RunTest(Class<?> testClass) {
             this.testClass = testClass;
         }

         public void run() {
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test41.class)) {  // @Test41 애너테이션이 달린 메서드 선별
                    tests++;
                    try {
                        method.invoke(null);
                        System.out.println(method + " 성공");
                        passed++;
                    } catch (InvocationTargetException e) {
                        // 테스트 메서드가 예외를 던지면 리프렉션 메커니즘이 InvocationTargetException 로 감싸서 다시 던진다. (???)
                        Throwable cause = e.getCause();
                        System.out.println(method + " 실패 : " + cause);
                    } catch (Exception e) {
                        // InvocationTargetException 이외의 예외가 발생한 경우 : @Test 애너테이션을 잘 못 사용한 경우
                        System.out.println("잘못 사용한 @Test41: " + method);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    @Test
    void 애너테이션처리() {
        RunTest runTest = new RunTest(Sample.class);
        runTest.run();
    }


    /**
     * 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ExceptionTest {
        Class<? extends Throwable> value();
    }

    public class Sample2 {
        @ExceptionTest(ArithmeticException.class)
        public static void m1() {
            int i = 0;
            i = i / i;
        }
        @ExceptionTest(ArithmeticException.class)
        public static void m2() {
            int[] a = new int[0];
            a[2] = 1;
        }
        @ExceptionTest(ArithmeticException.class)
        public static void m3() {
        }
    }

    public class RunTest2 {
        int tests = 0;
        int passed = 0;
        Class<?> testClass;

        public RunTest2(Class<?> testClass) {
            this.testClass = testClass;
        }

        public void run() {
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ExceptionTest.class)) {  // @ExceptionTest 애너테이션이 달린 메서드 선별
                    tests++;
                    try {
                        method.invoke(null);
                        System.out.printf("테스트 %s 실패 : 예외를 던지지 않음%n", method);
                    } catch (InvocationTargetException e) {
                        // 테스트 메서드가 예외를 던지면 리프렉션 메커니즘이 InvocationTargetException 로 감싸서 다시 던진다. (???)
                        Throwable cause = e.getCause();
                        Class<? extends Throwable> value = method.getAnnotation(ExceptionTest.class).value();
                        if (value.isInstance(cause)) {
                            passed ++;
                            System.out.printf("테스트 %s 성공 : 기대한 예외(%s)를 던짐, 발생한 예외(%s)%n", method, value.getTypeName(), cause);
                        } else {
                            System.out.printf("테스트 %s 실패 : 기대한 예외(%s), 발생한 예외(%s)%n", method, value.getTypeName(), cause);
                        }
                    } catch (Exception e) {
                        // InvocationTargetException 이외의 예외가 발생한 경우 : @ExceptionTest 애너테이션을 잘 못 사용한 경우
                        System.out.println("잘못 사용한 @ExceptionTest: " + method);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    @Test
    void 애너테이션처리_매개변수하나를받는다() {
        RunTest2 runTest2 = new RunTest2(Sample2.class);
        runTest2.run();
    }

    /**
     * 배열 매개변수를 받는 애너테이션 타입
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface ManyExceptionTest {
        Class<? extends Throwable>[] value();
    }

    public class Sample3 {
        @ManyExceptionTest({IndexOutOfBoundsException.class, NullPointerException.class})
        public static void m1() {
            List<String> list = new ArrayList<>();
            list.addAll(5,null);
        }
    }

    public class RunTest3 {
        int tests = 0;
        int passed = 0;
        Class<?> testClass;

        public RunTest3(Class<?> testClass) {
            this.testClass = testClass;
        }

        public void run() {
            for (Method method : testClass.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ManyExceptionTest.class)) {  // @ExceptionTest 애너테이션이 달린 메서드 선별
                    tests++;
                    try {
                        method.invoke(null);
                        System.out.printf("테스트 %s 실패 : 예외를 던지지 않음%n", method);
                    } catch (InvocationTargetException e) {
                        // 테스트 메서드가 예외를 던지면 리프렉션 메커니즘이 InvocationTargetException 로 감싸서 다시 던진다. (???)
                        Throwable cause = e.getCause();
                        int oldPassed = passed;
                        Class<? extends Throwable>[] values = method.getAnnotation(ManyExceptionTest.class).value();
                        for (Class<? extends Throwable> value : values) {
                            if (value.isInstance(cause)) {
                                passed ++;
                                break;
                            }
                        }
                        if (passed == oldPassed) {
                            System.out.printf("테스트 %s 실패 ", method);
                        }
                    } catch (Exception e) {
                        // InvocationTargetException 이외의 예외가 발생한 경우 : @ExceptionTest 애너테이션을 잘 못 사용한 경우
                        System.out.println("잘못 사용한 @ExceptionTest: " + method);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    @Test
    void 애너테이션처리_배열매개변수를받는다() {
        RunTest3 runTest3 = new RunTest3(Sample3.class);
        runTest3.run();
    }

    /**
     * 반복 가능한 애너테이션
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Repeatable(RepeatableExceptionTestContainer.class) // 배열 매개변수를 사용하는 대신에 @Repeatable 사용해보자.
    public @interface RepeatableExceptionTest {
        Class<? extends Throwable> value();
    }

    // 컨테이너 애너테이션
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface RepeatableExceptionTestContainer {
        RepeatableExceptionTest[] value();
    }

    public class Sample4 {
        @RepeatableExceptionTest(IndexOutOfBoundsException.class)
        @RepeatableExceptionTest(NullPointerException.class)
        public static void m1() {
            List<String> list = new ArrayList<>();
            list.addAll(5,null);
        }
    }

    public class RunTest4 {
        int tests = 0;
        int passed = 0;
        Class<?> testClass;

        public RunTest4(Class<?> testClass) {
            this.testClass = testClass;
        }

        public void run() {
            for (Method method : testClass.getDeclaredMethods()) {
                // isAnnotationPresent 은 반복가능애너테이션과 컨테이너애너테이션을 명확히 구분하므로 두 애너테이션 기준으로 선별
                if (method.isAnnotationPresent(RepeatableExceptionTest.class)
                || method.isAnnotationPresent(RepeatableExceptionTestContainer.class)) {
                    tests++;
                    try {
                        method.invoke(null);
                        System.out.printf("테스트 %s 실패 : 예외를 던지지 않음%n", method);
                    } catch (InvocationTargetException e) {
                        // 테스트 메서드가 예외를 던지면 리프렉션 메커니즘이 InvocationTargetException 로 감싸서 다시 던진다. (???)
                        Throwable cause = e.getCause();
                        int oldPassed = passed;
                        // getAnnotationsByType 은 반복가능애너테이션과 컨테이너애너테이션을 구분하지 않아서 모두 가져온다.
                        RepeatableExceptionTest[] values = method.getAnnotationsByType(RepeatableExceptionTest.class);
                        for (RepeatableExceptionTest value : values) {
                            if (value.value().isInstance(cause)) {
                                passed++;
                                break;
                            }
                        }
                        if (passed == oldPassed) {
                            System.out.printf("테스트 %s 실패 ", method);
                        }
                    } catch (Exception e) {
                        // InvocationTargetException 이외의 예외가 발생한 경우 : @ExceptionTest 애너테이션을 잘 못 사용한 경우
                        System.out.println("잘못 사용한 @ExceptionTest: " + method);
                    }
                }
            }
            System.out.printf("성공: %d, 실패: %d%n", passed, tests - passed);
        }
    }

    @Test
    void 애너테이션처리_반복가능매개변수를받는다() {
        RunTest4 runTest4 = new RunTest4(Sample4.class);
        runTest4.run();
    }

}
