### 목차
- [배열과 제네릭 타입의 차이 두가지](#배열과-제네릭-타입의-중요한-차이-2가지)
- [차이로 인해 배열과 제네릭은 잘 어우러지지 않는다](#차이로-인해-배열과-제네릭을-함께-쓰기란-어렵다)
- [배열보다 리스트를 써야하는 이유](#배열보다-리스트를-써야하는-이유)


## 배열과 제네릭 타입의 중요한 차이 2가지
1. 첫번째. 공변 VS 불공변
- 공변은 함께 변한다의 뜻으로, 상속과 같은 관계가 있다면 그 관계가 유지된 채로 함께 변한다는 의미다.
- 배열은 공변이고, 제네릭은 불공변이다.
- 배열은 공변이다 : Sub가 Super의 하위 타입이라면 배열 Sub[]은 배열Super[]의 하위타입이 된다.
    ```
    Object[] objects = new Long[1];
    objects[0] = "나는 String이라서 타입이 달라 넣을 수 없다."; 
    // 문법상 허용하지만 런타임 시, ArrayStoreException을 던진다.
    ```
- 제네릭은 불공변이다 : 서로 다른 타입 Type1과 Type2가 있을 때, `List<Type1>`은 `List<Type2>`의 하위 타입도 아니고 상위 타입도 아니다.
    ```
    List<Object> ol = new ArrayList<Long>();
    // 호환되지 않는 타입이다. 즉 컴파일 에러가 난다.
    ```
- 즉, 컴파일 시 에러를 확인할 수 있는 제네릭 타입을 사용하는 것이 좋다!!!
2. 두번째. 실체화와 소거
- 배열은 실체화된다.
    ```
    // .java
    public static void main(String[] args) {
        String[] strings = new String[3];
        strings[0] = "1";
        strings[1] = "2";
        strings[2] = "3";
    }

    // .class
    public static void main(String[] var0) {
        String[] var1 = new String[]{"1", "2", "3"};
    }
    ```
- 제네릭은 소거된다.
    ```
    // .java
    public static void main(String[] args) {
        List<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");

        System.out.println("hello = " + strings);
    }

    // .class
    public static void main(String[] var0) {
        ArrayList var1 = new ArrayList(); // 타입정보가 사라졌다?
        var1.add("1");
        var1.add("2");
        var1.add("3");
        System.out.println("hello = " + var1);
    }
    ```
- 베열은 런타임에도 자신이 담기로한 원소의 타입을 인지하고 확인하는 반면, 제네릭은 런타임 시점에 타입 정보를 알 수 없다.
- '소거'는 제네릭이 지원되기 이전의 레거시 코드와 제네릭 코드를 함께 사용할 수 있게 하는 메커니즘이라고 볼 수 있겠다.

<br>

## 차이로 인해, 배열과 제네릭을 함께 쓰기란 어렵다.
배열은 제네릭 타입, 매개변수화 타입, 타입 매개변수로 사용할 수 없다.  
`new List<E>[], new List<String>[], new E[]` -> 이렇게 쓸 수 없다. 컴파일 에러가 나온다.

<br>

## 배열보다 리스트를 써야하는 이유
차이로 인해 배열과 리스트를 섞어 쓰기는 쉽지 않다. 그러면 둘 중 하나를 써야 하는데, 타입에 더 안전하고 런타임이 아닌 컴파일 시점에 에러를 잡아주는 리스트를 사용하자!