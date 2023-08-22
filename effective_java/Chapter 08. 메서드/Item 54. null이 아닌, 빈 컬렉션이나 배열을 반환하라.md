### 목차
- [컬렉션에 값이 없으면 null로 반환해도 되나](#컬렉션에-값이-없으면-null-로-반환해도-되나)
- [빈 컨테이너를 반환해야 하는 이유](#빈-컨테이너를-반환해야-하는-이유)
- [빈 컨테이너를 반환하는 경우 주의할 점](#빈-컨테이너를-반환하는-경우-주의할-점)

## 컬렉션에 값이 없으면 null 로 반환해도 되나?
- 컬렉션이나 배열 같은 컨테이너가 비었을 때, null 을 반환하는 메서드를 사용할 때면, 항상 방어 코드를 넣어줘야 한다.
  - 클라이언트에서 방어 코드를 빼먹으면 오류가 발생할 수도 있다.
  - 한편, null을 반환하는 쪽에서도 이 상황을 특별히 취급해줘야 해서 코드가 더 복잡해진다고 한다.
```java
// @return 매장 안의 모든 치즈 목록을 반환한다. 단, 재고가 없다면 null 을 반환한다.
public List<Cheese> getCheeses() {
	return cheesesInStock.isEmpty() ? null : new ArrayList<>(cheesesInStock);
}

// 방어코드
List<Cheese> cheeses = shop.getCheeses();
if (cheeses != null) {
    // ...    
}
```

<br>

## 빈 컨테이너를 반환해야 하는 이유
빈 컨테이너를 할당하는 데도 비용이 드니 `null` 을 반환하는 쪽이 낫다는 주장도 있지만, 이것은 틀렸다!!!!
- 성능 분석 결과 빈 컨테이너 할당이 성능 저하 주범이라고 확인되지 않는 한 이 정도의 성능 차이는 신경 쓸 수준이 못된다. (아이템 64)
- 빈 컬렉션과 배열은 굳이 새로 할당하지 않고도 반환할 수 있다.
```java
// 빈 컬렉션을 반환하는 전형적인 코드로, 대부분의 상황에서는 이렇게 하면 된다.
public List<Cheese> getCheeses() {
        return new ArrayList<>(cheesesInStock);
}
```

<br>

## 빈 컨테이너를 반환하는 경우 주의할 점
가능성은 작지만, 사용 패턴에 따라 빈 컬렉션 할당이 성능을 눈에 띄게 떨어뜨릴 수도 있다. 해법으로는 `빈 불변 컬렉션`을 반환하는 것이다. <br>
불변 객체는 자유롭게 공유해도 안전하기 때문이다. (아이템 17)
#### 컬렉션의 경우,
- `List` 의 경우 `Collections.emptyList()`, `Set` 의 경우 `Collections.emptySet()`, `Map` 의 경우 `Collections.emptyMap()` 을 사용하면 된다.
- 단, 이 역시 최적화에 해당하니 꼭 필요할 때만 사용하자. 최적화가 필요하다고 판단되면 수정 전과 후의 성능을 측정하여 실제로 성능이 개선되는지 꼭 확인하자.
```java
// 최적화 - 빈 컬렉션을 매번 새로 할당하지 않도록 했다.
public List<Cheese> getCheeses() {
    return cheesesInStock.isEmpty() ? Collections.emptyList() : new ArrayList<>(cheesesInStock);
}
```
#### 배열의 경우
- 절대 `null` 을 반환하지 말고 길이가 0 인 배열을 반환하라.
- 보통은 정확한 길이의 배열을 반환하면 된다. 그 길이가 0 일 수도 있을 뿐이다.
```java
// 길이가 0 일 수도 있는 배열을 반환하는 올바른 방법
// toArray() 메서드에 건넨 길이 0짜리 배열은 반환타입(Cheese[])을 알려주는 역할을 한다.
public Cheese[] getCheeses() {
    return cheesesInStock.toArray(new Cheese[0]);
}
```
- 위 방식이 성능을 떨어뜨릴 것 같다면 길이 0 짜리 배열을 미리 선언해두고 매번 그 배열을 반환하면 된다. 길이가 0인 배열은 모두 불변이기 때문이다.
```java
// 최적화 - 빈 배열을 매번 새로 할당하지 않도록 했다.
private static final Cheese[] EMPTY_CHEESE_ARRAY = new Cheese[0];
public Cheese[] getCheeses() {
    return cheesesInStock.toArray(EMPTY_CHEESE_ARRAY);
}
```
- 성능 개선을 목적으로 toArray() 에 넘기는 배열을 미리 할당하는 것은 추천하지 않는다. 오히려 성능이 떨어진다는 연구 결과도 있다.
```java
// BAD - 배열을 미리 할당하면 성능이 나빠진다.
return cheesesInStock.toArray(new Cheese[cheesesInStock.size()]);
```
