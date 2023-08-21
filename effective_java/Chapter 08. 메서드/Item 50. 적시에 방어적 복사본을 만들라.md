### 목차
- [불변식에 대해 방어적으로 프로그래밍 하자](#불변식에-대해-방어적으로-프로그래밍-하자)
- [불변식을 지키지 못한 경우 1](#불변식을-지키지-못한-경우-1)
- [불변식을 지키지 못한 경우 2](#불변식을-지키지-못한-경우-2)
- [부가적으로...](#부가적으로)
- [정리하자면](#정리하자면)

## 불변식에 대해 방어적으로 프로그래밍 하자
- 어떤 객체든 그 객체의 허락 없이는 외부에서 내부를 수정하는 일은 불가능하다. 하지만 주의를 기울이지 않으면 자기도 모르게 내부를 수정하도록 허락하는 경우가 생긴다.
- 즉, **클라이언트가 불변식을 깨뜨리려 혈안이 되어 있다고 가정하고 방어적으로 프로그래밍해야 한다.**

<br>

## 불변식을 지키지 못한 경우 1
- 예시. 기간(Period)을 표현하는 다음 클래스는 한번 값이 정해지면 변하지 않도록 할 생각이었다.
    ```java
    public final class Period {
        private final Date start;
        private final Date end;
    
        public Period(Date start, Date end) {
            if (start.compareTo(end) > 0) {
                throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
            }
            this.start = start;
            this.end = end;
        }
    
        public Date start() {
            return start;
        }
        public Date end() {
            return end;
        }
    }
    
    @Test
    void 불변식을깨트리다() {
        Date start = new Date();
        Date end = new Date();
        Period p1 = new Period(start, end);
        System.out.println("초기 end >>>>>>>>>>> " + p1.end());
        end.setYear(78);
        System.out.println("end 변경 후, end >>>> " + p1.end());
    }
    ```
- 테스트 결과 : 불변식이 깨진다.
    ```java
    초기 end >>>>>>>>>>> Mon Aug 21 20:44:41 KST 2023
    end 변경 후, end >>>> Mon Aug 21 20:44:41 KST 1978
    ```
- 불변식이 깨진 이유
  - `Date` 가 가변이라는 사실!!
- `Date` 에 대해서 지금의 자바는 어떻게 해야할까?
  - `Date` 대신 불변인 Instant 를 사용하라. (`LocalDateTime` 이나 `ZoneDateTime` 을 사용해도 된다.)
  - `Date` 는 낡은 API 이니 새로운 코드를 작성할 때는 더이상 이용하지 말자.
- 위 상황 말고도 다른 상황에서 불변식은 깨질 수 있으니 다음과 같이 사용해야 한다. 
  - 예시. Period 인스턴스의 내부를 보호하려면 **생성자에서 받은 가변 매개변수 각각을 방어적으로 복사(defensive copy)**해야 한다.
  - 방어적 복사를 한 후, Period 인스턴스 안에서는 원본이 아닌 복사본을 사용한다.
    ```java
    public final class Period {
        private final Date start;
        private final Date end;

        public Period(Date start, Date end) {
            this.start = new Date(start.getTime());
            this.end = new Date(end.getTime());
            if(this.start.compareTo(this.end) > 0) {
                throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
            }
        }

        public Date start() {
            return start;
        }
        public Date end() {
            return end;
        }
    }

    @Test
    void 불변식을깨트리다() {
        Date start = new Date();
        Date end = new Date();
        Period p1 = new Period(start, end);
        System.out.println("초기 end >>>>>>>>>>> " + p1.end());
        end.setYear(78);
        System.out.println("end 변경 후, end >>>> " + p1.end());
    }
    ```
- 테스트 결과 : 불변식이 깨지지 않는다.
    ```java
    초기 end >>>>>>>>>>> Mon Aug 21 20:44:41 KST 2023
    end 변경 후, end >>>> Mon Aug 21 20:44:41 KST 2023
    ```
- 즉, 위와 같이 매개변수의 유효성을 검사하기 전에 방어적 복사본을 만들고, 이 복사본으로 유효성을 검사해야 한다.
  - 멀티스레드 환경의 경우, 원본 객체의 유효성을 검사한 후, 복사본을 만드는 그 찰나의 취약한 순간에 다른 스레드가 원본 객체를 수정할 위험이 있기 때문이다.
- 추가로 방어적 복사에, `Date` 의 `clone` 메서드를 사용해서는 안된다.
  - Date 는 final 이 아니므로 clone 이 Date 가 정의한 게 아닐 수 있다. 즉 `clone` 이 악의를 가진 하위 클래스의 인스턴스를 반환할 수도 있다.
  - 예컨대 이 하위 클래스는 start 와 end 필드의 참조를 private 정적 리스트에 담아뒀다가 공격자에게 이 리스트에 접근하는 길을 열어줄 수도 있다.
  - **즉, 이런 공격을 막기 위해서는 매개변수가 제 3자에 의해 확장될 수 있는 타입이라면 방어적 복사본을 만들 때 `clone` 을 사용해서는 안된다.**

<br>

## 불변식을 지키지 못한 경우 2
- 예시. 생성자를 수정하여 불변식이 깨지는 것을 막았지만, Period 는 아직도 변경 가능하다. 접근자 메서드가 내부의 가변 정보를 직접 드러내기 때문이다.
  ```java
  public final class Period {
      private final Date start;
      private final Date end;

      public Period(Date start, Date end) {
          this.start = new Date(start.getTime());
          this.end = new Date(end.getTime());
          if(this.start.compareTo(this.end) > 0) {
              throw new IllegalArgumentException(start + "가 " + end + "보다 늦다.");
          }
          
      public Date start() {
          return start;
      }
      public Date end() {
          return end;
      }
  }
  
  @Test
  void 또깨트리자() {
      Date start = new Date();
      Date end = new Date();
      Period p = new Period(start, end);
      System.out.println("초기 end >>>>>>>>>>> " + p.end());
      p.end.setYear(79); // p 의 내부를 변경했다.
      System.out.println("end 변경 후, end >>>> " + p.end());
  }
  ```
- 테스트 결과 : 또 깨진다.
  ```java
  초기 end >>>>>>>>>>> Mon Aug 21 20:44:41 KST 2023
  end 변경 후, end >>>> Mon Aug 21 20:44:41 KST 1979
  ```
- 접근자가 가변 필드의 방어적 복사본을 반환하면, Period 는 완벽한 불변으로 거듭난다.
  ```java
  public Date start() {
      return new Date(start.getTime());
  }
  public Date end() {
      return new Date(end.getTime());
  }
  ```
- 이렇게 완벽한 불변으로 만들어...
  - 아무리 악의적인 혹은 부주의한 프로그래머라도 시작 시각이 종료 시각보다 나중일 수 없다는 불변식을 위배할 방법은 없다.
  - Period 자신 말고는 가변 필드에 접근할 방법이 없다.
  - 모든 필드가 객체 안에 완벽하게 캡슐화되었다.

<br>

## 부가적으로...
- 생성자와 달리 접근 메서드에서는 방어적 복사에 clone 을 사용해도 된다. Period 가 가지고 있는 Date 객체는 java.util.Date 임이 확실하기 때문이다.
- 단, 인스턴스를 복사하는데는 일반적으로 생성자나 정적 팩터리를 쓰는 게 좋다. (아이템 13)

<br>

## 정리하자면
- 클래스가 클라이언트로부터 받는 혹은 클라이언트로 반환하는 구성요소가 가변이라면 그 요소는 반드시 방어적으로 복사해야 한다.
- 복사 비용이 너무 크거나 클라이언트가 그 요소를 잘못 수정할 일이 없음을 신뢰한다면 방어적 복사를 수행하는 대신 해당 구성요소를 수정했을 때의 책임이 클라이언트에 있음을 문서에 명시하도록 하자.