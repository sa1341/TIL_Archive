# TIL
## 빌더패턴

## 빌더 패턴(Builder Pattern)
빌더 패턴은 객체를 생성할 때 흔하게 사용하는 패턴입니다.

다음과 같은 코드 스타일로 객체를 생성하는 코드가 있다면, 빌더 패턴을 사용했다고 할 수 있습니다.

```java
 Member.Builder builder = new Member.Builder("sa1341", "junyoung");
        builder.age(28)
               .hobby("soccer");

         Member member = builder.build();
```

언제 Builder 패턴을 사용하는지는 Effective Java에는 다음과 같이 설명합니다.
> 규칙 2. 생성자 인자가 많을 때는 Builder 패턴 적용을 고라하라.

이펙티브 자바에서 말하는 빌더 패턴은 객체 생성을 깔끔하고 유연하게 하기 위한 기법입니다.

객체를 생성하는 방법을 3가지 정도 소개하고 있습니다.
- 점층적 생성자 패턴(telescoping constructor pattern) 
- 자바빈 패턴
- 빌더 패턴

### 점층적 패턴
먼저 점층적 생성자 패턴을 만드는 방법은 다음과 같습니다.
1. 필수 인자를 받는 필수 생성자를 하나 만듭니다.
2. 1개의 선택적 인자를 받는 생성자를 추가합니다.
3. 2개의 선택적 인자를 받는 생성자를 추가합니다.
4. 반복 수행
5. 모든 선택적 인자를 다 받는 생성자를 추가합니다.

```java
//점층적 생성자 패턴 코드의 예: 회원가입 관련 코드
public class Member{
    
    private final String id;
    private final String name;
    private final String hobby;

    //필수 생성자
    public Member(String id){
        this(id, "이름 비공개", "취미 비공개");
    }

    // 1개의 선택적 인자를 받는 생성자
    public Member(String id, String name){
        this(id, name, "취미 비공개");
    }

    //모든 선택적 인자를 다 받는 생성자
    public Member(String id, String name, String hobby){
        this.id = id;
        this.name = name;
        this.hobby = hobby;
    }

}
```

#### 장점
new Member("sa1341", "이름 비공개", "취미 비공개") 같은 호출이 빈번하게 일어난다면, new Member("sa1341")로 대체할 수 있습니다.

#### 단점
- 다른 생성자를 호출하는 생성자가 많으므로, 인자가 추가되는 일이 발생하면 코드를 수정하기 어렵습니다. 또한 코드 가독성도 떨어집니다.

- 특히 인자수가 많을 때 호출 코드만 봐서는 의미를 알기 어렵습니다.

```java
//호출 코드만으로는 각 인자의 의미를 알기 어렵습니다.
Car car = new Car(1,30.2,5,7,8,6,4);
Car car = new Car(3,35,210,24);
Car car = new Car(230,71);
```

### 자바빈 패턴
점층적 패턴의 대안으로 등장한 자바빈 패턴을 소개합니다.
이 패턴은 setter 메서드를 이용해 생성 코드를 읽기 좋게 만드는 것입니다.
```java
Car car = new Car();
car.setName("Hyundai");
car.setNumber(240);
```
#### 장점
- 이제 각 인자의 의미를 파악하기 쉬워졌습니다.
- 복잡하게 여러 개의 생성자를 만들지 않아도 됩니다.

#### 단점
- 객체의 일관성이 깨집니다.
1회의 호출로 객체 생성이 끝나지 않습니다.
즉 한번에 생성하지 않고 생성한 객체에 값을 계속 셋팅해주고 있습니다.

- setter 메서드가 있으므로 변경 불가능(immutable)클래스를 만들 수가 없습니다.
스레드 안전성을 확보하려면 점층적 생성자 패턴보다 많은 일을 해야 합니다.


### 빌더 패턴(Effective java 스타일)
```java
public class Member {

    private final String id;
    private final String name;
    private final int age;
    private final String hobbyy;
    
    public static class Builder {

        private final String id;
        private final String name;
        private int age;
        private String hobby;


        public Builder(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public Builder age(int age){
            this.age = age;
            return this;
        }

        public Builder hobby(String hobby){
            this.hobby = hobby;
            return this;
        }

        public Member build() {
            return new Member(this);
        }

    }

    public Member(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.age = builder.age;
        this.hobbyy = builder.hobby;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getHobbyy() {
        return hobbyy;
    }
}
```

위와 같이 하면 다음과 같이 객체를 생성할 수 있습니다.
```java
Member.Builder builder = new Member.Builder("sa1341", "junyoung");
        builder.age(28)
               .hobby("soccer");

         Member member = builder.build();
```
아래와 같이 사용할 수도 있습니다.
```java
//각 줄마다 builder를 타이핑 하지 않아도 되어 편리합니다.
Member member = new Member.Builder("sa1341","junyoung") // 필수값 입력
    .age(28)
    .hobby("농구")
    .build(); // build() 메소드가 객체를 생성해 돌려줍니다.
```

#### 장점
- 각 인자가 어떤 의미인지 알기 쉽습니다.
- setter 메소드가 없으므로 변경 불가능한 객체를 만들 수 있습니다.
- 한번에 객체를 생성하므로 객체 일관성이 깨지지 않습니다.
- build() 함수가 잘못된 값이 입력되었는지 검증하게 할 수도 있습니다.
