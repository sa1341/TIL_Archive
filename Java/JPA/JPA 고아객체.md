# JPA 고아객체

실무에서 JPA를 사용해보면서 그 중에서 부모 엔티티가 자식 엔티티의 라이프 사이클을 관리할 때 주로 cascade와 orphanRemoval 옵션을 자주 사용했었는데, 이 둘을 주로 언제 같이 쓰는지 궁금해서 간단한 실습을 통해서 테스트 해봤습니다.

## 1. 영속성 전이(CASCADE)

영속성 전이는 특정 엔티티를 영속 상태로 만들 때 연관 된 엔티티도 함께 영속 상태로 만들 때 사용하는 방법입니다.

> 부모 엔티티를 저장할 때 자식 엔티티를 저장하거나, 부모 엔티티를 삭제할 때 자식 엔티티도 같이 삭제하는 등의 방식으로 사용됩니다.

## 2. 고아객체란?

고아 객체는 부모 엔티티가 사라진 자식 엔티티를 의미합니다. Java는 RDB(관계형 데이터베이스)와 다르게 서로 다른 참조를 통해서 연관관계를 가지고 있습니다. 만약 어플리케이션 단에서 참조가 제거되더라도 DB 테이블 상에서는 삭제가 되지 않습니다. 

이럴 경우 부모에서 자식의 참조를 제거한다면 DB에서도 그 관계를 삭제해줘야 합니다.
JPA에서는 `orphanRemoval`를 사용하여 고아 객체 설정이 가능합니다.

## 3. 간단한 예제 코드

간단하게 팀 엔티티와 회원 엔티티를 설계하고 `@OneToMany`와 `@ManyToOne` 양방향 연관관계로 설정하였습니다.

#### Team 엔티티

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Team {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "team_id")
    private Long id;

    private String name;

    private Team(String name) {
        this.name = name;
    }

    public static Team of(final String name) {
        return new Team(name);
    }

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Member> members = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addMember(final Member member) {
        members.add(member);
        member.addTeam(this);
    }
}

// TeamRepository
public interface TeamRepository extends JpaRepository<Team, Long> {
}
```

#### Member 엔티티

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "member_id")
    private Long id;

    private String name;

    private int age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    public void addTeam(final Team team) {
        setTeam(team);
    }

    private Member(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public static Member of(final String name, final int age) {
        return new Member(name, age);
    }

    private void setTeam(Team team) {
        this.team = team;
    }
}

// MemberRepository
public interface MemberRepository extends JpaRepository<Member, Long> {
}
```

간단하게 IntelliJ 툴을 사용하여 팀 엔티티와 회원 엔티티를 insert 후 테스트를 진행하였습니다.

![image](https://user-images.githubusercontent.com/22395934/125069018-25bfd080-e0f1-11eb-9edf-8f2e1f81b2a5.png)


![image](https://user-images.githubusercontent.com/22395934/125068961-10e33d00-e0f1-11eb-8880-df8619a82c23.png)


### 테스트 코드 작성


```java
@Rollback(value = false)
@Test
public void 회원을_삭제한다() throws Exception {
    Team team = teamRepository.findById(1L).orElseThrow(() -> new IllegalArgumentException());

    List<Member> members = team.getMembers();
    members.remove(0);
}
```

만약 Team.java 파일에서 `orphanRemoval = true` 옵션을 주지 않고 위 테스트를 실행하면 delete 쿼리문이 나가지 않은것이 확인됩니다.

![image](https://user-images.githubusercontent.com/22395934/125069750-1bea9d00-e0f2-11eb-84e0-04335da6e595.png)


`orphanRemoval = true`를 주게 되는 경우 아래와 같이 부모 엔티티에서 자식 엔티티의 참조를 제거하면 플러시 호출 시 delete 쿼리가 정상으로 나가는 것을 확인할 수 있었습니다.

![image](https://user-images.githubusercontent.com/22395934/125069918-5ce2b180-e0f2-11eb-870c-1692391b1300.png)

## 4. 영속성 전이와 고아 객체를 언제 사용해야 하나?

영속성 전이 옵션 값을 `ALL`로 줄 경우 부모 엔티티를 삭제할 경우 자식 엔티티도 삭제가 됩니다. 그 이유는 영속성 전이 옵션을 ALL로 주게 되면서 자식 엔티티의 라이프 사이클을 부모가 관리하기 때문입니다.

고아 객체 같은 경우에는 애플리케이션 단에서 참조가 제거 될 경우 DB 테이블에서도 제거되야 할 경우에 사용합니다. 하지만 고아 객체 설정을 하지 않으면 members.remove(0)은 적용되지 않습니다.

주의할 점은 만약 자식 엔티티를 다른 곳에서 참조하고 있다면 참조 무결성 제약조건으로 인해서 다른 이슈가 발생할 수 있으니 되도록이면 어떤 엔티티를 오직 자신만이 소유하고 있는 엔티티에 고아객체를 설정하는 것이 이슈가 덜 생길 수 있습니다.

> 영속성 전이와 고아 객체 제거를 설정하면 부모 엔티티에서 자식의 생명 주기를 관리 할 수 있습니다. 
