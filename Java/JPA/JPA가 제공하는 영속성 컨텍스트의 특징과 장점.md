## 영속성 컨텍스트의 특징
영속성 컨텍스트는 식별자 값(@Id로 테이블의 기본 키와 매핑한 값)으로 구분합니다.
따라서 영속 상태는 식별자 값이 반드시 있어야 합니다. 식별자 값이 없으면 예외가 발생하는걸 알아두셔야 합니다.
영속성 컨텍스트에 엔터티를 저장하면 이 엔터티는 바로 데이터베이스에 저장되지 않습니다. 트랜잭션 커밋이 수행되기 전까지 영속성 컨텍스트에 있다가 커밋을 수행하면 플러시라는걸 호출하여 쓰기지연 SQL 저장소라는 메모리 공간에 쌓인 sql문을 데이터베이스에 동기화하는 작업을 수행하고 이것을 플러시라고 합니다. 플러시는 아래서 더 자세하게 다루어 보겠습니다.

### 영속성 컨텍스트(Persistence Context)의 장점
- 1차 캐시
- 동일성 보장
- 트랜잭션을 지원하는 쓰기 지연
- 변경 감지
- 지연 로딩

위에서 말한 영속성 컨텍스트의 장점에 대해서 리뷰하고 어떤 이점이 있는지 엔터티를 CRUD하는 예제를 보면서 하나씩 살펴보겠습니다.

### 엔터티 조회

```java
Member member = new Member();
member.setId("member1");
member.setUsername("회원");

em.save(member);
```

이 코드를 실행하면 영속 컨텍스트 내부에 존재하는 1차 캐시에 회원 엔터티를 저장합니다. 
회원 엔터티는 아직 데이터베이스에 저장되지 않는 상태입니다. 1차 캐시는 Collection인 Map 형태의 자료구조로 Key와 value 값을 가지고 있는 영역이라고 생각하시면 됩니다. Key는 식별자 값으로 @Id이고, value 값으로는 해당 member 인스턴스 즉 엔터티 값이 들어가 있습니다. 그리고 식별자 값은 데이터베이스 기본 키와 매핑되어 있습니다. 


따라서 영속성 컨텍스트에 데이터를 저장하고 조회하는 모든 기준은 데이터베이스의 기본 키 값입니다.

엔터티 조회는 아래코드와 같습니다.

```java
Member member = em.find(Member.class, "member1");
```
find() 메소드를 보면 첫 번째 파라미터는 엔터티 클래스 타입이고, 두번째는 조회할 엔터티의 식별자 값 입니다. 이 메소드를 호출하면 먼저 1차 캐시에서 엔터티를 찾고 만약 존재하지 않는다면 데이터베이스에서 조회를 하여 엔터티를 생성합니다. 그리고 1차 캐시에 저장한 후에 영속 상태의 엔터티를 반환합니다.

![스크린샷 2019-06-24 오후 11 12 02](https://user-images.githubusercontent.com/22395934/60025880-84b27880-96d5-11e9-959c-244eab88b67a.png)


이러한 방식으로 한번 DB에서 조회를 한 후에 영속 컨텍스트에 저장하면 메모리에 있는 1차 캐시에서 바로 불러오기 때문에 성능상의 이점을 누릴 수 있습니다.

### 엔터티 등록

엔터티 매니저를 사용해서 엔터티를 영속성 컨텍스트에 등록하는 과정을 살펴보겠습니다.

```java
EntityManager em = emf.createEntityManager();
EntityTransaction transaction = em.getTransaction();
//엔터티 매니저는 데이터 변경 시 트랜잭션을 시작합니다.
transaction.begin(); // 트랜잭션 시작

em.save(memberA);
em.save(memberB);
//여기까지 Insert SQL을 데이터베이스에 보내지 않습니다.

transaction.commit(); // 트랜잭션 커밋
```
엔터티 매니저는 트랜잭션을 커밋하기 직전까지 데이터베이스에 엔터티를 저장하지 않고 내부 쿼리를 쓰기지연 SQL저장소에 Insert 쿼리문들을 차곡차곡 쌓아두었다가 실제로 트랜잭션을 커밋하는 시점에서 모아둔 쿼리를 데이터베이스에 보내는데 이것을 트랜잭션을 지원하는 쓰기지연이라고 합니다.  

트랜잭션을 커밋하는 시점에 엔터티 매니저는 우선 영속성 컨텍스트를  플러시를 합니다. 전에 올렸던 포스팅에서 플러시를 언급했었었는데요. 플러시란 영속성 컨텍스트의 변경 내용을 데이터 베이스에 동기화하는 작업입니다. 이때 등록, 수정, 삭제한 엔터티를 데이터베이스에 반영합니다. 한마디로 쓰기 지연 SQL 저장소에 모인 쿼리를 데이터베이스에 보내는 작업이라고 생각하시면 됩니다. 이렇게 영속성 컨텍스트 변경 내용을 데이터베이스에 동기화한 후에 실제 데이터베이스 트랜잭션을 커밋합니다.

어차피... 데이터를 저장하는 즉시 등록 쿼리를 데이터베이스에 보내거나 데이터를 저장하면 등록 쿼리를 데이터베이스에 보내지않고 메모리에 모아두다가 트랜잭션을 커밋할 때 모아둔 쿼리를 데이터베이스에 보내는 방법 모두 다 트랜잭션 범위 안에서 실행되므로 둘의 결과는 같습니다. 결과적으로... 트랜잭션을 커밋하기 전까진 아무 소용이 없는거죠 어떻게든 커밋 직전에만 데이터베이스에 SQL문을 전달하기만 하면 됩니다. 이것이 트랜잭션을 지원하는 쓰기 지연이 가능한 이유입니다.

> 이러한 기능을 잘만 활용한다면 모아둔 등록 쿼리를 데이터베이스에 한 번에 전달해서 성능을   최적화 할 수 있습니다. 


### 엔터티 수정
SQL을 사용하면 수정 쿼리를 직접 작성해야 합니다. 그런데 프로젝트가 점점 커지고 요구사항이 늘어나면서 수정 쿼리도 점점 추가됩니다.  다음 아래와 같은 수정쿼리가 있습니다.

```sql
update MEMBER
SET
    NAME=?
    AGE=?
WHERE
    id=?
```
회원의 이름과 나이를 변경하는 기능을 개발했는데 회원의 등급을 변경하는 기능이 추가되면 회원의 등급을 다음과같이 수정 쿼리를 추가로 작성해야합니다.
```sql
UPDATE MEMBER
SET
   GRADE=?
WHERE
   id=?
```
보통은 이렇게 2개의 수정 쿼리를 작성한다. 물론 둘을 합쳐서 다음과 같이 하나의 수정 쿼리만을 사용해도 됩니다. 

```sql
UPDATE MEMBER
SET 
   NAME=?
   AGE=?
   GRADE=?
WHERE
   id=?
```
하지만 이렇게 합쳐진 쿼리를 사용해서 이름과 나이를 변경하는 데 실수로 등급 정보를 입력하지 않거나, 등급을 변경하는데 실수로 이름과 나이를 입력하지 않을 수도 있습니다.
이런 개발방식의 문제점은 수정 쿼리가 많아지는 것은 물론이고 비즈니스 로직을 분석하기 위해 SQL을 계속 확인해야 합니다. 결국 직접적이든 간접적이든 비즈니스 로직이 SQL에 의존하게 됩니다.

이러한 문제점을 해결하기 위해서 JPA에서는 변경감지 기능을 제공합니다.
JPA로 엔터티를 수정할 때는 단순히 엔터티를 조회해서 데이터만 변경하면 됩니다.
엔터티의 데이터만 변경했는데 어떻게 데이터베이스에 반영이 될까... 이렇게 엔터티의 변경사항을 데이터베이스에 자동으로 반영하는 기능을 변경감지라고 합니다.

JPA는 엔터티를 영속성 컨텍스트에 보관할 때, 최초 상태를 복사해서 저장해두는데 이것을 스냅샷이라고 합니다. 그리고 플러시 시점에 스냅샷과 엔터티를 비교하여 변경된 엔터티를 찾습니다.

![스크린샷 2019-06-26 오후 11 17 05](https://user-images.githubusercontent.com/22395934/60187438-8d878380-9868-11e9-9519-8a9b6427417c.png)


위의 그림이 가장 잘 설명하는거 같아서 첨부하였습니다 ㅎㅎ.... 변경 감지는 영속성 컨텍스트가 제공하는 기능으로... 엔터티가 영속성 컨텍스트에 존재하지 않으면 당연히 변경된 데이터를 가진 엔터티는 DB에 변경된 상태로 저장되지 않습니다. 오직 영속상태의 엔터티만 적용됩니다. 


### 엔터티 삭제
엔터티를 삭제하려면 먼저 삭제 대상 엔터티를 조회해야 합니다.
```java
Member memberA = em.find(Member.class, "memberA); //삭제 대상 엔터티 조회
em.remove(memberA);

em.remove();
```
em.remove()에 삭제 대상 엔터티를 넘겨주면 엔터티를 삭제합니다. 물론 엔터티를 즉시 삭제하는 것이 아니라 엔터티 등록과 비슷하게 삭제쿼리를 쓰기 지연 SQL 저장소에 등록합니다.
트랜잭션을 커밋해서 플러시를 호출하면 실제 데이터베이스에 삭제 쿼리를 전달합니다. 참고로 em.remove(memberA)를 호출하는 순간 memberA는 영속성 컨텍스트에서 제거됩니다. 
이렇게 삭제된 엔터티는 재사용하지 말고 자연스럽게 GC(가비지 컬렉션)의 대상이 되도록 두는 것이 좋습니다.


## 플러시란?
플러시는(flush())는 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영합니다. 플러시를 실행하면 구체적으로 다음과 같은 일이 일어납니다.

1. 변경 감지가 동작해서 영속성 컨텍스트에 있는 모든 엔터티를 스냅샷과 비교해서 수정된 엔터티를 찾습니다. 수정된 엔터티는 수정 쿼리를 만들어 쓰기 지연 SQL 저장소에 등록합니다.

2. 쓰기 지연 SQL 저장소의 쿼리를 데이터 베이스에 전송합니다.(등록, 수정, 삭제, 쿼리)

영속성 컨텍스트를 플러시하는 방법은 총 3가지 입니다.

1. em.flush()를 직접 호출합니다.

2. 트랜잭션 커밋 시 플러시가 자동으로 호출됩니다.

3. JPQL 쿼리 실행 시 플러시가 자동 호출 됩니다.

### 직접 호출
엔터티 매니저의 flush()의 메소드를 직접 호출해서 영속성 컨텍스트를 강제로 플러시 합니다.
테스트나 다른 프레임워크와 JPA를 함께 사용할 때를 제외하고 거의 사용하지 않습니다.

### 트랜잭션 커밋 시 플러시 자동 호출
데이터베이스에 변경 내용을 SQL로 전달하지 않고 트랜잭션만 커밋하면 어떤 데이터도 데이터베이스에 반영되지 않습니다. 따라서 트랜잭션을 커밋하기 전에 꼭 플러시를 호출해서 영속성 컨텍스트의 변경 내용을 데이터베이스에 반영해야 합니다.
JPA는 이런 문제를 예방하기 위해서 트랜잭션을 커밋할 때 플러시를 자동으로 호출합니다.

### JPQL 쿼리 실행 시 플러시 자동 호출
JPQL이나 Criteria같은 객체지향 쿼리를 호출할 때도 플러시가 실행된다.
예시를 통해서 한번 알아보겠습니다.
```java
//save 메소드는 JPA 구현체인 하이버네이트에서 제공해주는 함수로 persist처럼 해당 엔터티를 영속화 시킵니다.
em.save(memberA);
em.save(memberB);
em.save(memberC);

//중간에 JQPL 실행
query = em. createQuery("select m from Member m", Member.class);
List<Member> members = query.getResultList();
```
먼저 em.save()나 em.persist()를 호출해서 엔터티 memberA, memberB, memberC를 영속 상태로 만들었습니다. 이 엔터티들은 영속성 컨텍스트에는 있지만 아직 데이터베이스에는 반영되지 않았습니다. 이때 JPQL를 실행하면 JQPL은 SQL로 변환되어 데이터베이스에서 엔터티를 조회합니다. 그런데 memberA, memberB, memberC는 아직 데이터베이스에 없으므로 쿼리 결과로 조회되지 않습니다. 따라서 쿼리를 실행하기 직전에 영속성 컨텍스트를 플러시해서 변경 내용을 데이터베이스에 반영해야 합니다.
마찬가지로 JPA는 이런 문제를 예방하기 위해 JPQL을 실행할 때도 플러시를 자동 호출합니다.

지금까지 영속 컨텍스트가 제공하는 기능과 엔터티를 조회, 등록, 수정, 삭제 작업을 수행할 때 어떠한 과정을 거치는지.. 그 과정속에서 플러시를 호출하는 메커니즘까지 살펴보았습니다. 
