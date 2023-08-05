### 목차
- [명명 패턴 이란](#명명-패턴이란)
- [명명 패턴의 단점](#명명-패턴의-단점-3가지)
- [명명 패턴의 단점 해결하는 애너테이션을 사용하자](#애너테이션을-사용하게-되면-명명-패턴의-단점을-해결한다)
- [애너테이션 사용 방법 - 기본](#애너테이션-사용-방법)
- [애너테이션 사용 방법 - 매개변수를 하나 받음](#애너테이션-사용-방법---매개변수-하나를-받음)
- [애너테이션 사용 방법 - 배열 매개변수를 받음](#애너테이션-사용-방법---배열-매개변수를-받음)
- [애너테이션 사용 방법 - 반복 가능한 애너테이션](#애너테이션-사용-방법---반복-가능한-애너테이션)

## 명명 패턴이란?
> 명명 패턴? 메서드에 이름을 특정 이름으로 시작하게 만들어서, 해당 이름이 들어가면 실행하도록 만들기 위한 패턴
- 전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다.

<br>

## 명명 패턴의 단점 3가지
- 오타가 나면 안된다.
    - 이름으로 구분하기 때문에 오타가 나면 무시된다.
    - 예를 들어, Junit 3 버전까지는 테스트 메서드 이름을 test로 시작하게 했다. 실수로 tsetSafety라는 이름으로 메서드를 만들면 Junit 은 해당 메서드가 테스트 메서드인지 모르고 지나쳐 테스트를 통과했다고 오해할 수 있다.
- 올바른 프로그램 요소에서만 사용되리라 보증할 수 없다.
    - 예를 들어, Junit 3 에서는 테스트 메서드의 이름을 test로 시작해야 할 뿐, 클래스 이름은 관심이 없다. 즉, 클래스 이름을 TestSafety로 만들었고, 이 클래스에 정의된 메서드들이 테스트로 수행되길 바랄 수도 있지만 Junit 은 경고 메세지 출력은 물론 테스트 수행도 하지 않는다.
- 프로그램 요소를 전달할 마땅한 방법이 없다.
    - 패턴 실행에 필요한 인자를 매개변수로 넘길 방법이 마땅치 않다.

<br>

## 애너테이션을 사용하게 되면 명명 패턴의 단점을 해결한다.
- 애너테이션은 오타가 나면 컴파일조차 안된다.
- `@Target` 또는 애너테이션 프로세서의 검사를 통해 제한 가능
- 애너테이션에 매개변수로 전달 가능

<br>

## 애너테이션 사용 방법 - 기본
#### 애너테이션 정의
```java
/**
* 테스트 메서드임을 선언하는 애너테이션이다.
* 매개변수 없는 정적 메서드 전용이다. (???)
*/
@Retention(RetentionPolicy.RUNTIME) // @Test 애너테이션이 런타임에도 유지되어야 한다.
@Target(ElementType.METHOD) // 메서드 선언에만 사용가능하다. 클래스, 필드 등의 선언에서 사용 불가
public @interface Test41 {
}
```
- 애너테이션 선언에 다는 애너테이션을 `메타애너테이션`이라 한다.
    - `@Retention` : 애너테이션의 스코프를 결정한다.
    - `@Target` : 애너테이션이 적용될 대상을 결정한다.
<br>

#### 애너테이션을 활용한 프로그램 작성
```java
public class Sample {
    @Test41 public static void m1() {}

    public static void m2() {}
    
    @Test41 public static void m3() {
        throw new RuntimeException("fail");
    }
}
```
- @Test41 과 같이 아무 매개변수 없이 단순히 대상에 마킹한다는 뜻에서 `마커애너테이션`이라고 한다.
- m1 메서드는 @Test41 이 선언 되었으므로 @Test41을 수행한다.
- m2 메서드는 @Test41 이 선언되지 않았으므로 @Test41이 수행되지 않는다.
- m3 메서드는 @Test41 가 선언되었지만, 약속되지 않은 예외를 던지기 때문에 @Test41 에 실패한다.

<br>

#### 애너테이션을 처리하는 프로그램 작성
- @Test41 이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다. 마커애너테이션은 표시일 분이다.
- 즉, 이 애너테이션에 관심이 있는 프로그램에 추가 정보를 제공하며, 예제로 이 애너테이션을 처리하는 프로그램을 만들어보자.
```java
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
```
- RunTest 로 Sample 을 실행했을 때의 출력 메세지는 다음과 같다.
    ```java
    public static void com.kt.cloud.appconfiguration.service.Item41$Sample.m1() 성공
    public static void com.kt.cloud.appconfiguration.service.Item41$Sample.m3() 실패 : java.lang.RuntimeException: fail
    잘못 사용한 @Test41: public static void com.kt.cloud.appconfiguration.service.Item41$Sample.m5(java.lang.String)
    성공: 1, 실패: 2
    ```
    - 테스트 메서드가 예외를 던지면 리프렉션 메커니즘이 InvocationTargetException 로 감싸서 다시 던진다. (m3 메서드)(???)
    - InvocationTargetException 이외의 예외가 발생한 경우 : @Test 애너테이션을 잘 못 사용한 경우로 인스 턴스 메서드, 매개변수가 있는 메서드, 호출할 수 없는 메서드 등에 달았을 것이다. (m5 메서드)

<br>

## 애너테이션 사용 방법 - 매개변수 하나를 받음
#### 애너테이션 정의
```java
/**
* 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션
*/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}
```
- ExceptionTest 애너테이션의 매개변수 타입은 `Class<? extends Throwable>` 이다.
- Throwable 을 확장한 Class 의 객체 라는 뜻이며, 즉, 모든 예외(와 오류) 타입을 수용한다.

<br>

#### 애너테이션을 활용한 프로그램 작성
```java
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
```
- m1 메서드는 성공해야한다.
- m2 메서드는 다른 예외가 발생하여 실패해야한다. (기대한 예외와 발생한 예외가 달라 실패)
- m3 메서드는 예외가 발생하지 않아 실패해야 한다.

<br>

#### 애너테이션을 처리하는 프로그램 작성
```java
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
```
- RunTest 로 Sample 을 실행했을 때의 출력 메세지는 다음과 같다.
    ```java
    테스트 public static void com.kt.cloud.appconfiguration.service.Item41$Sample2.m3() 실패 : 예외를 던지지 않음
    테스트 public static void com.kt.cloud.appconfiguration.service.Item41$Sample2.m2() 실패 : 기대한 예외(java.lang.ArithmeticException), 발생한 예외(java.lang.ArrayIndexOutOfBoundsException: Index 2 out of bounds for length 0)
    테스트 public static void com.kt.cloud.appconfiguration.service.Item41$Sample2.m1() 성공 : 기대한 예외(java.lang.ArithmeticException)를 던짐, 발생한 예외(java.lang.ArithmeticException: / by zero)
    성공: 1, 실패: 2
    ```

<br>

## 애너테이션 사용 방법 - 배열 매개변수를 받음
#### 애너테이션 정의
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ManyExceptionTest {
    Class<? extends Throwable>[] value();
}
```
- ManyExceptionTest 애너테이션의 매개변수 타입은 `Class<? extends Throwable>[]` 이다.
- 예외를 여러 개 명시하고 그중 하나가 발생하면 성공하게 만들 수 있다.

<br>

#### 애너테이션을 활용한 프로그램 작성
```java
public class Sample3 {
    @ManyExceptionTest({IndexOutOfBoundsException.class, NullPointerException.class})
    public static void m1() {
        List<String> list = new ArrayList<>();
        list.addAll(5,null);
    }
}
```
- 원소가 여러개인 배열을 지정할 때는 원소들을 중괄호로 감싸고 쉼표로 구분하면 된다.
- m1 메서드는 성공해야한다.

<br>

#### 애너테이션을 처리하는 프로그램 작성
```java
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
```
- RunTest 로 Sample 을 실행했을 때의 출력 메세지는 다음과 같다.
    ```java
    성공: 1, 실패: 0
    ```

<br>

## 애너테이션 사용 방법 - 반복 가능한 애너테이션
#### 애너테이션 정의
```java
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
```
- @Repeatable 을 단 애너테이션은 하나의 프로그램 요소에 여러번 달 수 있다.
- @Repeatable 을 단 애너테이션을 반환하는 컨테이너 애너테이션을 정의해야 하고, @Repeatable에 컨테이너 애너테이션의 class 객체를 매개 변수로 전달해야 한다.
- 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
- 컨테이너 애너테이션 타입에는 적절한 정책(@Retention, @Target)을 명시해야 한다.

<br>

#### 애너테이션을 활용한 프로그램 작성
```java
public class Sample4 {
    @RepeatableExceptionTest(IndexOutOfBoundsException.class)
    @RepeatableExceptionTest(NullPointerException.class)
    public static void m1() {
        List<String> list = new ArrayList<>();
        list.addAll(5,null);
    }
}
```

<br>

#### 애너테이션을 처리하는 프로그램 작성
```java
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
```
- 반복 가능 애너테이션을 처리할 때는 주의를 요한다.
    - 하나만 달았을 때와의 구분을 위해 컨테이너 애너테이션 타입이 적용된다.
    - `getAnnotationsByType` 은 반복가능 애너테이션과 컨테이너 애너테이션을 구분하지 않아서 모두 가져온다.
    - `isAnnotationPresent` 은 반복가능애너테이션과 컨테이너 애너테이션을 명확히 구분한다.
- RunTest 로 Sample 을 실행했을 때의 출력 메세지는 다음과 같다.
    ```java
    성공: 1, 실패: 0
    ```