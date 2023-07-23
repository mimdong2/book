### 목차
- [비검사 경고 란](#비검사-경고-란)
- [비검사 경고를 제거할 수 없다면](#비-검사-경고를-제거할-수-없다면)
- [@SuppressWarnings("unchecked") 이란](#suppresswarningsunchecked-이란)


## 비검사 경고 란
- unchecked warning
- 에러는 아니지만 컴파일러가 보여주는 다양한 경고다.
- 비검사 경고를 확인하기 위해서는 컴파일러에 `-Xlint:uncheck` 옵션을 추가한다.
- 비검사 경고의 예시
    ```
    Set<String> names = new HashSet();
    ```
    위 코드에서는 다음과 같은 경고 메세지를 볼 수 있다.
    ```
    Raw use of parameterized class 'HashSet' 
    Unchecked assignment: 'java.util.HashSet' to 'java.util.Set<java.lang.String>' 
    ```
    인스턴스로 생성한 `HashSet` 을 로 타입으로 사용했기 때문인데, 이를 해결하기 위해서는 `HsahSet<String>` 과 같이 타입 매개변수를 명시하던가, `HashSet<>` 과 같이 다이아몬드 연산자(자바 7~)를 사용해야 한다.
- 모든 비검사 경고는 런타임에 `ClassCastException` 을 일으킬 수 있는 잠재적 가능성을 뜻한다.

<br>

## 비 검사 경고를 제거할 수 없다면?
> 할 수 있는 한 모든 비검사 경고를 제거하라. 모두 제거하면 그 코드는 타입 안전성이 보장된다.

비 검사 경고를 제거하는 노력을 포기하지 말고 해야한다. 다만, 못할 때도 있다.  
타입 안전한다고 확신할 수 있다면 @SuppressWarnings("unchecked")를 달아 경고를 숨길 수 있다. 단, 경고를 숨기기로 한 근거를 주석으로 남겨야한다.

<br>

## @SuppressWarnings("unchecked") 이란
참고로, Suppress 는 막다, 억압하다의 뜻이고 Warnings 는 경고이다. 즉, 경고를 막는다는 뜻이다.
```
@Target({TYPE, FIELD, METHOD, PARAMETER, CONSTRUCTOR, LOCAL_VARIABLE, MODULE})
@Retention(RetentionPolicy.SOURCE)
public @interface SuppressWarnings {
    String[] value();
}
```
`@Target` 을 보면, 해당 어노테이션이 모든 곳에 다 쓰일 수 있다. 그렇다고 클래스에 박아두고 쓰면 다른 경고를 놓칠 수 있기 때문에, 가능한 좁은 범위에 적용하는 것이 좋다.  

<br>

## 부록으로..
IntelliJ IDEA 에 [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) 플러그인 설정하여 사용해봐야 겠다.  
참고링크 : [새로 입사한 개발자가 프로젝트에 기여하는 방법 한 가지](https://helloworld.kurly.com/blog/fix-style-with-command/)
