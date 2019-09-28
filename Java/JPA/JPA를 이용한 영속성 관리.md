## JPA 그게 뭔데?
JPA(Java Persistence API)는 ORM(Object Relational Management) 기술 표준으로 스프링 부트 교재를 보면 대부분 실려있는 기술 스펙 입니다.

JPA가 어떤기술인지 알아보기전에 대체 왜 JPA가 세상에 나오게 됬는지에 대해서나 알게되면 확실히 기억에도 오래남는거 같아서 저의 얄팍한 지식으로 살펴보겠습니다.
스프링 프레임워크로 프로젝트를 해본 사람이라면 간단하든 복잡하든 DB를 사용하면서 대부분의 객체들을 테이블에 매핑하는 데이터베이스 관련 작업을 Spring JDBC Template이나 Mybatis를 이용하여 해보셨을 겁니다. 
하지만 프로젝트 규모가 커지면서 객체와 테이블을 매핑하기 위해서 반복적이고 지루한 JDBC API를 호출하게 되고 이 과정에서 코드의 가독성과 유지보수적 측면에서 많은 효율성이 떨어지게 됩니다. 간단하게 개발자들이 해당 객체를 DB에 저장하기 위해서 테이블을 설계하는데 많은 시간을 소모하게 됩니다.
이렇게 되면 핵심 비즈니스 로직을 테스트하는데 소모하는 시간도 줄어들기 때문에 심각한 에러를 초래할 수 있고, 이러한 상황이 계속 무한루프 되는 악순환이 발생하는 것이죠.

이러한 문제점을 해결하기 위해서 JPA라는 걸출한 기술이 등장하게 되었습니다. JPA는 위에서 설명한것처럼 ORM을 정의하는 자바진영의 기술 표준입니다.  이러한 JPA 구현체는 여러개가 있지만 제가 아는 대표적인 구현체는 Hibernate 입니다. 
저는 Hibernate를 기준으로 이야기 하겠습니다. 
![스크린샷 2019-06-16 오후 11 00 55](https://user-images.githubusercontent.com/22395934/59565156-a20a9580-908a-11e9-8ae9-5f311f17779b.png)

JPA가 제공하는 기능은 크게 두가지가 있습니다.
1. 엔터티와 테이블을 매핑하는 설계하는 부분
2. 매핑한 엔터티를 실제 사용하는 부분

위에 말하는 엔터티는 쉽게말해서 클래스이지만 그냥 클래스가 아니고 JPA에게 테이블과 매핑한다고 알려주고 클래스 위에 @Entity가 적용된 클래스를 엔터티라고 부릅니다.
이렇게 매핑한 엔터티를 엔터티 매니저를 통해서 영속성 컨테스트(Persistence Context)에 관리하게 됩니다.

```java
@Getter
@Setter
@ToString
@Entity // Database 테이블과 매핑될 클래스를 엔터티로 지정
@Table(name="tbl_members") // 테이블명을 지정함, 생략시 클래스명으로 테이블명됨
@EqualsAndHashCode(of = "uid")
public class Member {

    @Id //@Id가 적용된 맴버변수는 실제 매핑될 테이블의 식별자 값인 PK를 의미한다.
    private String uid;
    private String upw;
    private String uname;
}
```


엔터티 매니저는 엔터티를 저장, 수정, 삭제, 조회하는 등 엔터티와 관련된 모든 일을 처리합니다.

즉, 엔터티를 관리하는 관지라인 샘이죠 개발자 입장에서는 엔터티 매니저는 엔터티를 저장하는 가상의 데이터베이스로 생각하면 이해하기 쉽습니다.

### 엔터티 매니저 팩토리와 엔터티 매니저

![IMG_0008](https://user-images.githubusercontent.com/22395934/59565989-c0758e80-9094-11e9-96b7-2d26c442f507.PNG)

데이터베이스를 하나만 사용하는 애플리케이션은 일반적으로 EntityManagerFactory를 하나만 생성합니다. EntityManager를 생성하기 위해서는 META-INF/persistence.xml이라는 클래스 패스에서 설정 정보를 사용해서 EntityManagerFactory를 먼저 생성해야 합니다.

```java
// 엔터티매니저 팩토리를 생성
EntityManagerFactory emf = Persistence.createEntityManagerFactory("jpabook");
//엔터티매니저를 생성
EntityManager em = emf.createEntityManager();
```
EntityManagerFactory는 EntityManager를 생산하는 공장이라고 생각하시면 됩니다. 공장을 만드는 비용은 상당히 크기때문에 한개만 만들어서 애플리케이션 전체에서 공유도록 설계되어 있습니다. 반면에 EntityManager를 생산하는 비용은 거의 들지 않습니다.

EntityManager는 Entity를 영속성 컨텍스트에 관리하고 있습니다. 이것은 JPA를 이해하는데 가장 중요한 이유입니다. 영속성 컨텍스트는 엔터티를 영구 저장하는 환경이라는 뜻입니다. 

### 엔터티의 생명주기

![스크린샷 2019-06-17 오전 12 35 01](https://user-images.githubusercontent.com/22395934/59566208-c9b42a80-9097-11e9-8e63-8f813aad3609.png)


비영속일때는 엔터티 객체를 생성했했지만 아직 순수한 객체 상태이며 아직 저장하지 않았습니다.
따라서 영속성 컨텍스트나 데이터베이스와는 전혀 관련이 없기때문에 이것을 비영속 상태라고 합니다.

ex) 비영속 상태
```java
Member member = new Member();
member.setId("member1");
member.setUsername("회원1");
```

영속상태는 엔터티 매니저를 통해서 엔터티를 영속성 컨텍스트에 저장합니다. 이렇게 영속성 컨텍스트가 관리하는 엔터티를 영속 상태라고 합니다. 이제 회원 엔터티는 비영속 상태에서 영속상태가 되었습니다. 

ex) 영속 상태
```java
//비영속 상태인 member 인스턴스를 영속상태로 변경
em.persist(member);
```
em.find()나 JPQL을 사용해서 조회한 엔터티도 영속성 컨텍스트가 관리하는 영속상태입니다.
![스크린샷 2019-06-17 오전 12 54 15](https://user-images.githubusercontent.com/22395934/59566374-798a9780-909a-11e9-9129-d930dfd012ab.png)
결국 영속 상태라는 것은 영속성 컨텍스트에 의해 관리된다는 뜻입니다.

준영속은 영속성 컨텍스트가 관리하던 영속상태의 엔터티를 영속성 컨텍스트가 관리하지 않으면 준영속 상태가 됩니다. 특정 엔터티를 준영속 상태로 만들려면 em.detach()와 영속성컨텍스트를 닫는 em.close(), 영속성 컨텍스트를 초기화하는 em.clear()를 호출하면 영속상태의 엔터티는 준영속상태가 됩니다.

ex) 준영속 상태
```java
em.detach(member);
```

삭제는 엔터티를 영속성 컨텍스트와 데이터베이스에서 삭제합니다.
ex) 삭제 상태
em.remove(member);

오늘은 간단하게 JPA의 개념과 등장이유 그리고 엔터티 매니저를 생성하는 엔터티매니저 팩토리,엔터티를 관리하는 엔터티 매니저, 영속성 컨텍스트의 개념, 엔터티 생명주기에 대해서만 리뷰하였습니다.

다음 포스팅에서는 영속성 컨텍스트의 특징과 영속성 컨텍스트에서 관리하는 엔터티를 데이터 베이스에 동기화하는 플러시(flush)에 대해서 더 자세하게 다루어 보겠습니다.
