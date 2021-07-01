# Java 직렬화를 하는 이유가 무엇일까?

예전에 회사 프로젝트를 보면 클래스에서 Serializable 인터페이스를 구현한 것을 자주 볼 수 있었습니다. 그 당시에는 객체를 바이너리로 변환하기 위해 직렬화 인터페이스를 구현한다는 정도만 생각을 했었는데, 정확히 왜 하는지 궁금해서 찾아보았습니다.

## 1. Java 직렬화란?

- Java 직렬화는 자바 시스템 내부에서 사용되는 객체 또는 데이터들을 외부의 자바 시스템에서도 사용할 수 있도록 `바이트(byte)` 형태로 데이터 변환하는 기술과 바이트로 변환된 데이터를 다시 객체로 변환하는 역직렬화를 포함합니다.

- 시스템적으로 JVM의 Runtim Data Area(Heap 또는 스택영역)에 상주하고 있는 객체 데이터를 바이트 형태로 변환하는 기술과 직렬화된 바이트 형태의 데이터를 객체로 변환해서 JVM으로 상주시키는 형태를 말하기도 합니다.


## 2. Java 직렬화 예제 코드

`기본(primitive)타입과 java.io.Serializable` 인터페이스를 상속받은 객체는 직렬화 할 수 있는 기본 조건을 가집니다.

#### Member.java

```java
import java.io.Serializable;

public class Member implements Serializable {

    private String name;
    private String email;
    private int age;

    public Member(String name, String email, int age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @Override
    public String toString() {
        return String.format("Member{name='%s', email='%s', age='%s',", name, email, age);
    }
}
```

### 직렬화 및 역직렬화 방법

ObjectOutputStream 객체와 ObjectInputStream 객체를 이용합니다. 

```java
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

public class ObjectSerializableExam{

    public static void main(String[] args) throws Exception {
        Member member = new Member("임준영", "a790077714@gmail.com", 30);
        byte[] serializedMember;

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try(ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                oos.writeObject(member);
                // 직렬화된 member 객체
                serializedMember = baos.toByteArray();
            }
        }
        // base64로 인코딩한 문자열
        String base64Member = Base64.getEncoder().encodeToString(serializedMember);

        // base64로 디코딩한 문자열
        byte[] deserializedMember = Base64.getDecoder().decode(base64Member);

        try(ByteArrayInputStream bais = new ByteArrayInputStream(deserializedMember)) {
            try(ObjectInputStream ois = new ObjectInputStream(bais)) {
                Object objectMember = ois.readObject();
                // member 객체로 역직렬화
                Member readMember = (Member) objectMember;
                System.out.println(member);
            }
        }
    }
}
```

## 3. 직렬화 종류

- 문자열 형태의 직렬화 방법
    - 직접 데이터를 문자열 형태로 확인 가능한 직렬화 방법입니다. 범용적인 API나 데이터를 변환하여 추출할 때 많이 사용됩니다. 표형태의 다량의 데이터를 직렬화시 CSV가 많이 쓰이고 구조적인 데이터 이전에는 XML을 많이 사용했지만 최근에는 JSON을 많이 사용하고 있습니다.

## 4. 직렬화를 주로 어디에 사용하는가?

JVM의 메모리에만 상주되어 있는 객체 데이터를 그대로 영속화(persist)가 필요할 때 사용됩니다. 시스템이 종료되더라도 없어지지 않는 장점을 가지며 영속화된 데이터이기 때문에 네트워크로 전송도 가능합니다. 그리고 필요할 때 직렬화 된 객체 데이터를 가져와서 역직렬화하여 객체를 바로 사용할 수 있게 됩니다. 


- 서블릿 세션들은 대부분 세션의 Java 직렬화를 지원하고 있습니다. 단순히 세션을 서블릿 메모리 위에서 운용한다면 직렬화가 필요없지만, 파일로 저장하거나 세션 클러스터링, DB를 저장하는 옵션등을 선택하게 되면 세션 자체가 직렬화되어 저장되어 전달됩니다. 

- 캐시에서도 직렬화는 사용됩니다. 주로 DB를 조회한 후 가져온 데이터 객체 같은 경우 실시간 형태로 요구하는 데이터가 아니라면 메모리, 외부 저장소, 파일 등을 저장소를 이용해서 데이터 객체를 저장한 후 동일한 요청이 오면 DB를 다시 요청하는 것이 아니라 저장된 객체를 찾아서 응답하게 하는 형태를 캐시를 사용한다라고 말합니다. 캐시를 이용하면 DB 리소스를 절약할 수 있기 때문에 많은 시스템에서 자주 활용됩니다. 이렇게 캐시할 부분을 자바 직렬화된 데이터를 저장해서 사용됩니다.

## 5. serialVersionUID 사용 시 주의할점

serialVersionUID는 Java 직렬화 및 역직렬화 할때 필요한 버전 정보입니다. 만약 객체를 직렬화하고 클래스에 멤버변수가 추가된다면 `java.io.InvalidClassException` 예외가 발생하게 됩니다. Java 직렬화 스펙을 살펴보면 아래와 같습니다.

- serialVersionUID 필수 값은 아닙니다.
- 호환 가능한 클래스는 serialVersionUID 값이 고정되어 있습니다.
- serialVersionUID가 선언되어 있지 않으면 클래스의 기본 해쉬값을 사용합니다. 

위의 스펙을 살펴보면 변경에 취약한 클래스가 변경되면 역직렬화 시에 예외가 발생할 수 있으니 개발자가 serialVersionUID 값을 직접 관리해주어야 혼란을 줄일 수 있다는 것을 의미하기도 합니다.

하지만 그럼에도 불구하고 또 다른 문제가 발생할 수 있습니다.

만약 기존의 직렬화된 객체의 멤버 변수의 타입이 바뀐다면 마찬가지로 `java.io.InvalidClassException` 예외가 발생합니다. 이걸 보면 Java 직렬화는 상당히 타입에 엄격하다는 것을 알 수 있습니다.

즉, 특별한 문제가 없으면 Java 직렬화 버전 serialVersionUID 값은 개발 시 직접 관리해줘야 합니다. 값이 동일하다면 멤버 변수 및 메서드 추가는 크게 문제가 되지 않습니다. 또한 멤버 변수 제거 및 이름 변경은 오류는 발생하지 않고 데이터는 누락됩니다.


## 6. 결론

Java 직렬화를 사용할 경우 역직렬화를 할 때 예외가 생길 수 있다는 사실을 인지하고 반드시 예외 처리를 해야합니다. 또한 자주 변경되는 비즈니스적인 데이터를 Java 직렬화을 사용하지 않습니다. 긴 만료시간을 가지는 데이터는 JSON 등 다른 포맷을 사용하여 저장해야 합니다. 


#### 참조 블로그: https://techblog.woowahan.com/2551/
