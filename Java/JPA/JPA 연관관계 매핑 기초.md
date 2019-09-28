## 연관관계 매핑 기초
> 이번 공부의 목표는 객체의 참조와 테이블의 외래 키를 매핑하는 것이 목표입니다.

### 단방향 연관관계
연관관계 중에선 다대일(N:1) 단방향 관계를 가장 먼저 이해해야 합니다. 
예를 회원과 팀의 관계를 통해서 설명하겠습니다.

- 회원과 팀이 있습니다.
- 회원은 하나의 팀에만 소속될 수 있다.
- 회원과 팀은 다대일 관계다.


![스크린샷 2019-09-22 오전 1 04 44](https://user-images.githubusercontent.com/22395934/65375939-6f99b580-dcd5-11e9-8c73-246564383204.png)

![스크린샷 2019-09-22 오전 12 42 43](https://user-images.githubusercontent.com/22395934/65375945-7de7d180-dcd5-11e9-823b-4e2a9172500f.png)

 #### 객체 연관관계
 - 회원 객체는 Member.team 필드(맴버변수)로 팀 객체와 연관관계를 맺습니다.
 - 회원 객체와 팀 객체는 단방향 관계입니다. 회원은 Member.team 필드를 통해서 팀을 알수 있지만 반대로 팀은 회원을 알 수 없습니다. 예를 들어  member -> team의 조회는 member.getTeam()으로 가능하지만 반대 방향인 team -> member를 접근하는 필드는 없습니다.

 #### 테이블 연관관계
 - 회원 테이블은 TEAM_ID 외래 키로 팀 테이블과 연관관계를 맺습니다.
 - 회원 테이블과 팀 테이블은 양방향 관계입니다. 회원 테이블의 TEAM_ID 외래 키를 통해서 회원과 팀을 조인할 수 있고 반대로 팀과 회원도 조인할 수 있습니다. 예를 들어 MEMBER 테이블의 TEAM_ID 외래 키 하나로 MEMBER JOIN TEAM과  TEAM JOIN MEMBER 둘 다 가능합니다.

 ex) 아래와 같이 외래 키 하나로 양방향으로 조인하는지 살펴보겠습니다.
 SELECT * FROM MEMBER M JOIN TEAM T ON M.TEAM_ID = T.TEAM_ID

 다음은 반대인 팀과 회원을 조인하는 SQL 입니다.
 SELECT * FROM TEAM T JOIN MEMBER M ON T.TEAM_ID = M.TEAM_ID

 #### 객체 연관관계와 테이블 연관관계의 가장 큰 차이점
 참조를 통한 연관관계는 언제나 단방향 입니다. 객체간에 연관관계를 양방향으로 만들고 싶으면 반대쪽에도 필드를 추가해서 참조를 보관해야 합니다. 결국 연관관계를 하나 더 만들어야 합니다. 이렇게 양쪽에서 서로 참조하는 것을 양방향 연관관계라고 합니다.
 하지만 정확히 이야기하면 이것은 양방향 관계가 아니고 서로 다른 단방향 관계 2개 입니다. 반면에 테이블은 외래 키 하나로 양방향으로 조인이 가능 합니다.


단방향 연관관계
 ```java
class A{
    B b;
}
class B{}
 ```
양방향 연관관계
```java
class A{
    B b;
}

class B{
    A a;
}
```

객체 연관관계 vs 테이블 연관관계 정리
- 객체는 참조(주소)로 연관관계를 맺는다.
- 테이블은 외래 키로 연관관계를 맺습니다.

객체는 참조를 사용하지만 테이블은 join을 사용합니다.

- 참조를 사용하는 객체의 연관관계는 단방향입니다.
A -> B(a.b)

- 외래키를 사용하는 테이블의 연관관게는 양방향입니다.
A JOIN B 가 가능하면 반대로 B JOIN A 도 가능합니다.

- 객체를 양방향으로 참조하려면 단방향 연관관계를 2개 만들어야 합니다.
A -> B(a.b)
B -> A(b.a)

### 순수한 객체 연관관계
아래 코드는 순수하게 객체만 사용한 연관관계를 알기 위해서 작성하였습니다.
JPA를 사용하지 않는 순수한 회원과 팀 클래스 코드입니다.

```java
public class Member {

    private String id;

    private Team team_id; //팀의 참조를 보관

    private String username;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Team getTeam_id() {
        return team_id;
    }

    public void setTeam_id(Team team_id) {
        this.team_id = team_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
```

```java
public class Team {
    
    private String id;
    private String name;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

회원1과 회원2를 팀에 소속시키는 코드입니다.
```java
public static void main(String[] args){

    Member member1 = new Member("member1", "회원1");
    Member member2 = new Member("member2", "회원2");
    Team team1 = new Team("team1", "팀1");

    member1.setTeam(team1);
    member2.setTeam(team1);
    
    Team findTeam = member1.getTeam();
}
```
위의 코드를 보면 회원1과 회원2는 팀1에 소속 되었습니다. 그리고 다음 코드로 회원1이 속한 팀을 조회할 수 있습니다.

```java
Team findTeam = member1.getTeam();
```

> 이처럼 객체는 참조를 사용해서 연관관계를 탐색할 수 있는데 이것을 객체 그래프 탐색이라고 합니다.


### 객체 관계 매핑
이제 JPA를 사용해서 둘을 매핑해 보겠습니다.
```java
package com.web.community.domain;

import javax.persistence.*;

@Entity
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    private String id;

    private String username;
    
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team; //팀의 참조를 보관

    //연관관계 설정
    public void setTeam_id(Team team) {
        this.team = team;
    }
    
    // Getter, Setter ...
}
```

```java
package com.web.community.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Team {

    @Id
    @Column(name = "TEAM_ID")
    private String id;
    
    private String name;
    
    // Getter, Setter ...
}
```

위의 코드는 Member, Team 엔티티를 매핑한 코드입니다.

- 객체 연관관계:  회원 객체의  Member.team 필드 사용
- 테이블 연관관계: 회원 테이블의 MEMBER.TEAM_ID 외래키 컬럼을 사용

```java
@ManyToOne
@JoinColumn(name = "TEAM_ID")
private Team team;
```
회원 엔티티에 있는 연관관계 패밍부분인데 연관관계를 매핑하기 위한 새로운 어노테이션들 있습니다.

@ManyToOne: 이름 그대로 다대일(N:1)관계라는 매핑 정보입니다. 회원과 팀은 다대일 관계입니다. 연관관계를 매핑 할 때 이렇게 다중성을 나타내는 어노테이션을 필수로 사용해야 합니다.

@JoinColumn(name = "TEAM_ID"): 조인 컬럼은 외래 키를 매핑할 때 사용합니다.
name 속성에는 매핑할 외래 키 이름을 지정합니다. 회원과 팀 테이블은 TEAM_ID 외래 키로 연관관계를 맺으므로 이 값을 지정하면 됩니다. 이 어노테이션은 생략할 수 있습니다.


- @ManyToOne 주요 속성

|  <center>속성</center> |  <center>기능</center> | <center>기본값</center> 
|:--------|:--------:|--------:|
| optional | <center>false로 설정하면 연관된 엔티티가 항상 있어야 합니다.</center> | <center>true</center> |
| fetch| <center>글로벌 패치 전략을 설명합니다.</center> | <center>@ManyToOne=FetchType.EAGER @OneToMany=FetchType.LAZY</center> 
| cascade | <center>영속성 전이 기능을 사용합니다.</center> 
| targetEntity | <center>연관된 엔티티의 타입 정보를 설정한다. 이 기능은 거의 사용하지 않습니다. 컬렉션을 사용해도 제네릭으로 타입 정보를 알 수 있습니다.</center> 

- targetEntity 속성의 사용 예 입니다.
```java
@OneToMany
private List<Member>members; //제네릭으로 타입 정보를 알 수 있습니다.

@@OneToMany(targetEntity=Member.class)
private List members; // 제네릭이 없으면 타입 정보를 알 수 없습니다.
```

### 연관관계 사용
실제로 연관관계를 등록, 수정, 삭제, 조회하는 예쩨를 코드로 알아보겠습니다.

#### 저장
연관관계를 매핑한 엔티티를 아래와 같은 코드로 저장해보았습니다.
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommunityApplicationTests {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void testSave() {

        Team team1 = new Team("team1", "팀1");
        teamRepository.save(team1);

        Member member1 = new Member("member1", "맴버1");
        member1.setTeam(team1);

        memberRepository.save(member1);
    }
}
```
가장 핵심은 member1.setTeam(team1); 회원 -> 팀 참조 부분입니다.
memberRepository.save(member1);

회원 엔티티는 팀 엔티티를 참조하고 저장했습니다. JPA는 참조한 팀의 식별자(Team_id)를 외래 키로 사용해서 적절한 등록 쿼리를 생성합니다. 이때 실행한 SQL은 다음과 같습니다. 

```sql
insert into team (team_id, name) values ('team1', '팀1')
insert into member (team_id, username, member_id) values ('team1', 'member1', '회원1')
```

실제로 데이터가 잘 입력되었는지 MySQL 데이터베이스에서 확인해보았습니다.
![스크린샷 2019-09-22 오후 6 29 22](https://user-images.githubusercontent.com/22395934/65385218-f480df80-dd66-11e9-9330-19f34159c9ae.png)


#### 조회
연관관계가 있는 엔티티를 조회하는 방법은 크게 2가지 입니다.
1. 객체 그래프 탐색
2. 객체지향 쿼리 사용(JPQL)

- 객체 그래프 탐색
방금 저장한 대로 회원1이 팀1에 소속해 있다고 가정합니다.
member.getTeam()을 사용해서 member와 연관된 team 엔티티를 조회할 수 있습니다.

```java
Optional<Member> findMember = memberRepository.findById("member1");
findMember.ifPresent(member -> System.out.println(member.getId() +" " + member.getUsername() + " " + member.getTeam().getId()));
```

결과 값
![스크린샷 2019-09-22 오후 7 50 51](https://user-images.githubusercontent.com/22395934/65386118-52ff8b00-dd72-11e9-98a7-464158da86c3.png)

이처럼 객체를 통해 여관된 엔티티를 조회하는 것을 객체 그래프 탐색이라고 합니다.

- 객체지향 쿼리 사용
객체지향 쿼리인 JPQL에서 연관관계를 어떻게 사용하는지 알아보겠습니다.
예를들어서 회원을 대상으로 조회하는데 팀1에 소속된 회원만 조회하려면 회원과 연관된 팀 엔티티를 검색 조건으로 사용해야 합니다. SQL은 연관된 테이블을 조인해서 검색조건을 사용하면 됩니다. JPQL도 조인을 지원하지만 문법이 약간 다릅니다.

저같은 경우에는 스프링 부트로 개발을 하고 있기 때문에 책에 내온 내용과 다르게 JPQL을 짜서 조인을 해보았습니다.

```java
//Repository 클래스에서 쿼리메소드를 작성하였습니다.
public interface MemberRepository extends CrudRepository<Member, String> {
    //테이블이 아니라 엔티티를 중심으로 쿼리를 작성하면 JPA가 분석하여 SQL을 만들어서 데이터베이스에 해당쿼리를 전송합니다. 
    @Query("select m from Member m join m.team t where t.name = ?1")
    public List<Member> findByMember(String id);   
}

 @Test
    public void testSave() {

        Team team1 = new Team("team1", "팀1");
        teamRepository.save(team1);

        Member member1 = new Member("member1", "맴버1");
        member1.setTeam(team1);

        memberRepository.save(member1);

        List<Member> resultList = memberRepository.findByMember("팀1");

        for(Member member : resultList){
            System.out.println(member.getUsername());
        }
    }
    // 결과 값: 맴버1
```

JPQL의 from Member m join m.team t 부분을 보면 회원이 팀과 관계를 가지고 있는 필드(m.team)를 통해서 Member와 Team을 조인했습니다. 그리고 where 절을 보면 조인한 t.name를 검색조건으로 사용해서 팀1에 속한 회원만 검색했습니다.

참고로 ?1은 첫번째로 들어온 파라미터를 바인딩 받는 문법입니다.

이때 실행되는 SQL은 다음과 같습니다.
```sql
SELECT m from Member m join Team t on m.TEAM_ID = t.TEAM_ID
WHERE TEAM_NAME = '팀1';
```
실행된 SQL과 JPQL을 비교하면 JPQL은 객체(엔티티)를 대상으로 하고 SQL보다 간결합니다. 

#### 수정
```java
@Test
    public void updateRelation(){

        Team team1 = new Team("team1", "팀1");
        teamRepository.save(team1);

        Team team2 = new Team("team2", "팀2");
        teamRepository.save(team2);

        Member member1 = new Member("member1", "맴버1");
        member1.setTeam(team1);

        memberRepository.save(member1);


        Member findMember = memberRepository.findByMemberId("member1");

        System.out.println(findMember.getUsername());

        findMember.setTeam(team2);
    }
```
실행되는 수정 SQL은 다음과 같다.
```sql
UPDATE MEMBER
SET TEAM_ID='team2', ...
WHERE ID='member1'
```
수정은 update() 같은 메소드가 없습니다. 단순히 불러온 엔티티의 값만 변경해두면 커밋할 때 플러시가 일어나면서 변경감지 기능이 작동합니다. 
이것은 연관관계를 수정할 때도 같은데, 참조하는 대상만 변경하면 나머지는 JPA가 자동으로 처리합니다.

#### 연관관계 제거

```java
@Test
public void deleteRelation(){
    Member member1 = MemberRepository.findByMemberId("member1");
    member1.setTeam(null);
}
```
#### 연관된 엔티티 삭제 
연관된 엔티티를 삭제하려면 기존에 있던 연관관계를 먼저 제거하고 삭제해야 합니다. 그렇지 않으면 외래 키 제약조건으로 인해, 데이터베이스에서 오류가 발생합니다.
팀1에는 회원1과 회원2가 소속되어 있습니다. 이때 팀1을 삭제하려면 연관관계를 먼저 
끊어야 합니다.

```java
member1.setTeam(null);
member2.setTeam(null);
em.remove(team);
```

다음에는 양방향 연관관계에 대해서 자세히 공부 후 리뷰해보겠습니다.


### 양방향 연관관계
회원에서 팀으로 접근하는 다대일 단방향 매핑을 공부해보았습니다. 이번에는 반대 방향인 팀에서 회원으로 접근하는 관계를 추가해보겠습니다. 그래서 회원에서 팀으로 접근하고 반대 방향인 팀에서 회원으로 접근 할 수 있도록 양방향 연관관계로 매핑하겠습니다.

![스크린샷 2019-09-23 오후 9 05 25](https://user-images.githubusercontent.com/22395934/65424611-eb673f80-de46-11e9-8f18-32e950ee5f77.png)

먼저 객체 연관관계를 보면 회원과 팀은 다대일 관계입니다. 반대로 팀에서 회원은 일대다 관게 입니다. 일대다 관계는 여러 건과 여러관계를 맺을 수 있으므로 컬렉션을 사용해야 합니다.  Team.members를 List 컬렉션으로 추가했습니다. 

- 회원 -> 팀(member.team)
- 팀 -> 회원(Team.members)

데이터베이스 테이블에서는 외래 키 하나로 양방향으로 조회 할 수 있습니다.
따라서 처음부터 양뱡향 관게입니다. 그러므로 데이터베이스에 추가 할 내용은 전혀 없습니다... 

왜냐하면 처음에 언급한것 처럼 MEMBER JOIN TEAM <-> TEAM JOIN MEMBER도 가능하기 때문이죠

#### 양방향 연관관계 매핑

##### 회원 엔티티
```java
@Entity
@NoArgsConstructor
@ToString(exclude = "team")
public class Member {

    @Id
    @Column(name = "MEMBER_ID")
    private String id;

    private String username;

    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team; //팀의 참조를 보관

    public Member(String id, String username) {
        this.id = id;
        this.username = username;
    }

    //연관관계 설정
    public void setTeam(Team team) {
        this.team = team;
    }
}
```

##### 팀 엔티티
```java
@Entity
@NoArgsConstructor
public class Team {

    @Id
    @Column(name = "TEAM_ID")
    private String id;

    private String name;
    
    //추가 
    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();

    public Team(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
```
위의 팀 엔티티 코드에서 팀과 회원은 일대다 관계입니다. 따라서 List<Member> members를 추가했습니다. 그리고 일대다 관계를 매핑하기 위해서 @OneToMany 매핑 정보를 사용했습니다. mappedBy 속성은 양방향 매핑일 때 사용하는데 
반대쪽 매핑이 Member.team이므로 team을 값으로 주었습니다.

#### 일대다 컬렉션 조회
```java
@Test
public void biDirection(){
List<Member> lists = teamRepository.getMemberList("team1");
    for (Member member : lists) {
        logger.info(member.getId() + " " + member.getUsername());
    }
}
//결과 member1 맴버1
//결과 member2 맴버2
```

### 연관관계의 주인
@OneToMany는 직관적으로 이해가 됩니다. 문제는 mappedBy 속성입니다. 단순히 @OneToMany만 있으면 되지 mapperBy는 왜 사용하는지 모르겠습니다. 사실 객체에는 연관관게/라는 것이 없습니다. 서로 다른 단방향 연관관계 2개를 애플리케이션 로직으로 잘 묶어서 양방향인 것처럼 보이게 할 뿐 입니다. 반면에 데이터베이스 테이블은 앞서 말한것 처럼 외래 키 하나로 양쪽이 서로 조인할 수 있습니다. 따라서 테이블은 외래 키 하나만으로 양방향 연관관계를 맺습니다.

객체 연관관계
- 회원 -> 팀 연관관계 1개(단방향)
- 팀 -> 회원 연관관계 1개(단방향)

테이블 연관관계
- 회원 <-> 팀의 연관관계 1개(양방향)

다시 말해서 테이블은 외래 키 하나로 두 테이블의 연관관계를 관리합니다.
엔티티를 단방향으로 매핑하면 참조를 하나만 사용하므로 이 참조로 외래 키를 관리하면 됩니다. 그런데 엔티티를 양방향으로 매핑하면 회원 -> 팀, 팀 -> 회원 두곳에서 서로를 참조합니다. 따라서 객체의 연관관계를 관리하는 포인트는 2곳으로 늘어납니다.

여기서 문제점이 있습니다. 엔티티를 양뱡향으로 설정하면 객체의 참조는 둘인데 외래 키는 하나입니다. 따라서 둘 사이에 차이가 발생합니다. 
이런 차이로 인해 JPA에서는 두 객체 연관관계 중 하나를 정해서 테이블의 외래키를 관리해야 하는데 이것을 연관관계의 주인이라 합니다.

#### 양방향 매핑의 규칙: 연간관계의 주인
양방향 연관관계 매핑 시 지켜야할 규칙이 있는데 두 연관관계 중 하나를 연관관계의 주인으로 정해야 합니다. 연관관계의 주인만이 데이터베이스 연관관계와 매핑되고 외래 키를 관리(등록, 수정, 삭제)할 수 있습니다. 반면에 주인이 아닌 쪽은 읽기만 할 수 있습니다.

어떤 연관관계를 주인으로 정할지는 mappedBy 속성을 사용하면 됩니다.

1. 주인은 mappedBy 속성을 사용하지 않습니다.
2. 주인이 아니면 mappedBy 속성을 사용해서 속성의 값으로 연관관계의 주인을 지정해야 합니다.

- 회원 -> 팀(member.team) 방향

```java
public class Member {
    @ManyToOne
    @JoinColumn(name = "TEAM_ID")
    private Team team; //팀의 참조를 보관
```

- 팀 -> 회원(team.members) 방향
```java
public class Team { 
    @OneToMany(mappedBy = "team")
    List<Member> members = new ArrayList<>();
```

> 연관관계의 주인을 정한다는 것은 사실 외래 키 관리자를 선택하는 것입니다.

여기서는 회원 테이블에 있는 TEAM_ID 외래 키를 관리할 관리자를 선택해야 합니다.
만약에 회원 엔티티에 있는 Member.team을 주인으로 선택하면 자기 테이블에 있는 외래 키를 관리하면 됩니다. 하지만 팀 엔티티에 있는 Team.members를 주인으로 선택하면 물리적으로 전혀 다른 테이블의 외래 키를 관리해야 합니다. 왜냐하면 이 경우 Team.members가 있는 Team 엔티티는 TEAM 테이블에 매핑되어 있는데 관리해야할 외래 키는 MEMBER 테이블에 있기 때문입니다.


> 연관관계의 주인은 외래 키가 있는 곳으로 설정하면 됩니다.
주인이 아닌 Team.members에는 mappedBy="team" 속성을 사용해서 주인이 아님을 설정하면 됩니다. 여기서 mappedBy의 값으로 사용된 team은 연관관계의 주인인 Member 엔티티의 team 필드를 말합니다.

결론은 연관관계의 주인만 데이터베이스 연관관계와 매핑되고 외래 키를 관리할 수 있습니다. 주인이 아닌 반대편은 읽기만 가능하고 외래키를 변경하지는 못합니다.


#### 양방향 연관관계의 주의점
양방향 연관관계를 설정하고 가장 흔히 하는 실수는 연관관계의 주인에는 값을 입력하지 않고, 주인이 아닌 곳에만 값을 입력하는 경우입니다. 데이터베이스에 외래 키 값이 정상적으로 저장되지 않으면 이것부터 의심해봐야 합니다.

이것도 코드로 예제를 확인해 보겠습니다.
```java
public void testSaveNonOwner(){
        

        Team team1 = new Team("team1", "팀1");
        teamRepository.save(team1);

        //맴버1 저장
        Member member1 = new Member("member1", "맴버1");
        memberRepository.save(member1);
        //맴버2 저장
        Member member2 = new Member("member2", "맴버2");
        memberRepository.save(member2);

        //주인이 아닌 곳만 연관관계 설정
        team1.getMembers().add(member1);
        team1.getMembers().add(member2);

        teamRepository.save(team1);

}
```

회원을 조회한 결과 값

![스크린샷 2019-09-23 오후 11 41 43](https://user-images.githubusercontent.com/22395934/65435589-bb766700-de5b-11e9-9c15-7f53a6f0aa88.png)

외래 키 TEAM_ID에 team1이 아닌 null 값이 입력되어 있는데, 연관관계의 주인이 아닌 Team.members에만 값을 저장했기 때문입니다. 다시 한 번 강조하지만 연관관계의 주인만이 외래 키의 값을 변경할 수 있습니다.

#### 순수한 객체까지 고려한 양방향 연관관계
객체지향 관점에서 양쪽 방향에 모두 값을 입력해주는것이 가장 안전합니다.
양쪽 방향 모두 값을 입력하지 않으면 JPA를 사용하지 않는 순수한 객체 상태에서 심각한 문제가 발생할 수 있습니다.

예를 들어 JPA를 사용하지 않고 엔티티에 대한 테스트 코드를 작성한다고 가정해봅시다. ORM은 객체와 관계형 데이터베이스 둘 다 중요합니다. 데이터베이스뿐만 아니라 객체도 함께 고려해야 합니다.

```java
public void test순수한객체_양방향(){
    
    //팀1
    Team team1 = new Team("team1", "팀1");
    //맴버1 저장
    Member member1 = new Member("member1", "맴버1");
    //맴버2 저장
    Member member2 = new Member("member2", "맴버2"); 
 
    member1.setTeam(team1);
    member2.setTeam(team1);
   
    List<Member> members = team1.getMembers();
    System.out.println("members.size = " + members.size());
}
//결과: members.size = 0
```
예제코드는 JPA를 사용하지 않는 순수한 객체입니다. 코드를 보면 Member.team에만 연관관계를 설정하고 반대 방향은 연관관계를 설정하지 않았습니다. 그래서 결국 팀에 소속된 회원이 몇 명인지를 출력해보면 0이 출력됩니다. 이것은 우리가 기대하는 양방향 연관관계가 아닙니다.

양방향은 양쪽 다 관계를 설정해야 합니다. 
```java
public void test순수한객체_양방향(){  

    Team team1 = new Team("team1", "팀1");
    Member member1 = new Member("member1", "맴버1");
    Member member2 = new Member("member2", "맴버2"); 
 
    member1.setTeam(team1);
    team1.getMembers().add(member1);

    member2.setTeam(team1);
    team1.getMembers().add(member2);

    List<Member> members = team1.getMembers();
    System.out.println("members.size = " + members.size());
}
//결과: members.size = 2
```
위의 코드는 양쪽 모두 관계를 설정했고, 기대했던 2가 출력되었습니다.
이제 JPA를 사용해서 위의 코드를 완성해봅십다.
```java
public void testORM_양방향(){  
    
    Team team1 = new Team("team1", "팀1");
    teamRepository.save(team1);

    Member member1 = new Member("member1", "맴버1");
    
    member1.setTeam(team1);
    team.getMembers().add(member1);
    memberRepository.save(member1);
    
    Member member2 = new Member("member2", "맴버2"); 
    
    member2.setTeam(team1);
    team.getMembers().add(member2);
    memberRepository.save(member2);
}
```

위 코드에서 양쪽에 연관관계를 설정했습니다. 순수한 객체 상태에서도 동작하며, 테이블의 외래 키도 정상 입력됩니다. 물론 외래 키 값은 연관관계의 주인인 Member.team 값을 사용합니다.

member.team: 연관관계의 주인, 이 값으로 외래 키를 관리합니다.
Team.getMembers().add(member1); // 주인이 아님. 저장 시 사용되지 않습니다.

결론은 객체의 양방향 연관관계는 객체 테이블 모두 관계를 맺어주여야 합니다.

#### 연관관계 편의 메소드 작성 시 주의사항
사실 setTeam() 메소드에는 버그가 있습니다.
```java
member1.setTeam(teamA); //1
member1.setTeam(teamA); //2
List<Member> memberList = teamA.getMembers(); //여전히 member1이 조회
```
위의 코드를 보면 먼저 member1.setTeam(teamA)를 호출한 직후 모습입니다.
 
![스크린샷 2019-09-24 오후 7 35 11](https://user-images.githubusercontent.com/22395934/65504663-73f6e600-df02-11e9-8e74-ab9db6fd0776.png)

다음으로 member.setTeam(teamB)를 호출한 직후 객체 연관관계인 그림을 봅시다.
![스크린샷 2019-09-24 오후 7 37 23](https://user-images.githubusercontent.com/22395934/65504810-c46e4380-df02-11e9-8af6-c0bc8b5398ea.png)

문제는 teamB로 변경할 때 teamA -> member1 관계를 제거하지 않는 겁니다....
연관관계를 변경할 때는 기존 팀이 있으면 기존 팀과 회원의 연관관계를 삭제하는 코드를 추가해야 합니다. 아래 코드처럼 수정합니다.
```java
public void setTeam(Team team){

    if(this.team != null){
        this.team.getMembers().remove(this);
    }
    this.team = team;
    team.getMembers().add(this);
}
```
이 코드는 객체에서 서로 다른 단방향 연관관계 2개를 양뱡향인 것처럼 보이게 하려고 얼마나 많은 고민과 수고가 필요한지를 보여주고 있습니다. 반면에 관계형 데이터베이스는 외래 키 하나로 문제를 단순히 해결합니다. 
결국엔 양방향 연관관계를 사용하려면 로직을 견고하게 작성해야 합니다.

양방향의 장점은 반대방향으로 객체 그래프 탐색 기능이 추가된 것뿐입니다.
- 단방향 매핑만으로 테이블과 객체의 연관관계 매핑은 이미 완료되었습니다.
- 단방향을 양방향으로 만들면 반대방향으로 객체 그래프 탐색 기능이 추가됩니다.
- 양방향 연관관계를 매핑하려면 객체에서 양쪽 방향을 모두 관리해야 합니다.
