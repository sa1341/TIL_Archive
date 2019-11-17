## 엔터티 매핑
JPA를 사용하는데 가장 중요한 일은 엔티티와 테이블을 정확히 매핑하는 일입니다.
매핑 어노테이션을 숙지하고 사용해야 하는데 JPA는 다양한 매핑 어노테이션을 지원합니다.
아래와 같이 크게 4가지로 분류할 수 있습니다.

- 객체와 테이블 매핑: @Entity, @Table
- 기본 키 매핑: @Id
- 필드와 컬럼 매핑: @Column
- 연관관계 매핑: @ManyToOne, @JoinColumn, @OneToMany

### @Entity
JPA를 사용해서 테이블과 매핑할 클래스는 @Entity 어노테이션을 필수로 붙여야 합니다.
@Entity가 붙은 클래스는 JPA가 관리하는 것으로, 엔터티라 부릅니다. 

|  <center>속성</center> |  <center>기능</center> | <center>기본값</center> 
|:--------|:--------:|--------:|
| <cemter>name</cemter> | <center>JPA에서 사용할 엔터티 이름을 지정한다. 보통 기본값인 클래스 이름을 사용한다. 만약 다른 패키지에 이름이 같은 엔터티 클래스가 있다면 이름을 지정해서 충돌하지 않도록 해야 한다.</center> | <center>설정하지 않으면 클래스 이름을 그대로 사용합니다.</center> |

@Entity 적용 시 주의사항
- 기본 생성자는 필수다(파라미터가 없는 public 또는 protected 생성자).
- final 클래스, enum, interface, inner 클래스에는 사용 할 수 없다.
- 저장할 필드에 final을 사용하면 안 된다.

JPA가 엔터티 객체를 생성할 때 기본 생성자를 사용하므로 이 생성자는 반드시 있어야 합니다. 자바는 생성자가 하나도 없으면 다음과 같이 기본 생성자를 자동으로 만듭니다.
ex)
public Member() {} //기본 생성자

만약 생성자를 하나 이상 만들면 자바는 기본 생성자를 자동으로 만들지 않기 때문에 이때는 개발자가 직접 기본 생성자를 만들어야 합니다.

public Member() {} //직접 만든 기본 생성자

//임의의 생성자
public Member(String name){
    this.name = name;
}

### @Table
@Table은 엔터티와 매핑할 테이블을 지정한다. 생략하면 매핑한 엔터티 이름을 테이블 이름으로 사용합니다. 

|  <center>속성</center> |  <center>기능</center> | <center>기본값</center> 
|:--------|:--------:|--------:|
| name | <center>매핑할 테이블 이름</center> | <center>엔터티 이름을 사용한다.</center> |
| catalog| <center>catalog 기능이 있는 데이터베이스에서 catalog를 매핑한다.</center> 
| schema | <center>schema 기능이 있는 데이터베이스에서 schema를 매핑한다.</center> 
| uniqueConstraints | <center>DDL 생성 시에 유니크 제약조건을 만든다. 2개 이상의 복합 유니크 제약조건도 만들 수 있다. 참고로 이 기능은 스키마 자동생성 기능을 사용해서 DDL을 만들 때만 사용한다.</center> 

### 다양한 매핑 사용
JPA 시작하기 장에서 개발하던 회원 관리 프로그램에 다음 요구사항이 추가 되었습니다.

1. 회원은 일반 회원과 관리자로 구분됩니다.
2. 회원 가입일과 수정일이 있어야 합니다.
3. 회원을 설명할 수 있는 필드가 있어야 합니다. 이 필드는 길이 제한이 없습니다.

아래와 같이 위 요구사항을 만족하는 회원 엔터티에 기능을 추가 할 수 있습니다.


```java
@Entity
@Table(name = "MEMBER") // Member 엔티티를 테이블 명 MEMBER로 매핑
public class Member

@Id //인스턴스 변수 id를 DB 테이블 MEMBER 기본키 ID로 매핑
@Column(name = "ID")
private String id;

@Column(name = "NAME")
private String username;

private Integer age;
//자바의  enum을 사용해서 회원의 타입을 구분하였습니다. 일반 회원은 USER, 관리자는
//ADMIN입니다. 자바의 enum을 사용하려면 @Enumerated 어노테이션으로 매핑해야 합니다.
@Enumerated(EnumType.STRING)
private RoleType roleType;

//자바의 날짜 타입은 @Temporal을 사용해서 매핑해야 합니다. 
@Temporal(TemporalType.TIMESTAMP)
private Date createDate;

@Temporal(TemporalType.TIMESTAMP)
private Date lastModifiedDate;

//회원을 설명하는 필드는 길이 제한이 없고, 따라서 데이터베이스 varchar 타입 대신에
//CLOB 타입으로 저장해야 합니다. @Lob를 사용한다면 CLOB, BLOB 타입을 매핑할 수 있습니다.
@Lob
private String description;


public enum RoleType{
    ADMIN, USER
}

```


### 데이터베이스 스키마 자동 생성 기능
JPA는 데이터베이스 스키마를 자동으로 생성하는 기능을 지원합니다. 클래스의 매핑정보를 보면 어떤 테이블에 어떤 컬럼을 사용하는지 알 수 있습니다. JPA는 이 매핑정보와 데이터베이스 방언을 사용해서 데이터베이스 스키마를 생성합니다. 

저 같은경우에는 스프링 부트에서 생성한 application.yml 파일에 아래와 같이 정의합니다.

```yml
spring:
  jpa:
    hibernate:
      ddl-auto: create 
```
이 속성을 추가하면 어플리케이션 실행 시점에 데이터베이스 테이블을 자동으로 생성합니다.

#### hibernate.ddl-auto 속성
  <center>옵션</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| create | <center>기존 테이블을 삭제하고 새로 생성한다. DROP + CREATE </center> 
| create-drop| <center>create 속성에 추가로 어플리케이션을 종료할 때 생성한 DDL을 제거한다. DROP + CREATE + DROP</center> 
| update | <center>데이터베이스 테이블과 엔터티 매핑정보를 비교해서 변경 사항만 수정한다.</center> 
| validate | <center>데이터베이스 테이블과 엔터티 매핑정보를 비교해서 차이가 있으면 경고를 남기고 애플리케이션을 실행하지 않는다. 이설정은 DDL을 수정하지 않는다.</center> 
| none | <center>자동 생성 기능을 사용하지 않으려면 hibernate.ddl-auto 속성 자체를 삭제하거나 유효하지 않는 옵션 값을 주면 된다.<center> 
 
> 운영 서버에서 create, create-drop, update처럼 DDL을 수정하는 옵션은 절대 사용하면 안됩니다. 오직 개발 서버나 개발 단계에서만 사용해야 합니다. 이 옵션들은 운영 중인 데이터베이스 컬럼을 삭제할 수 있기 때문입니다.

- 개발 초기 단계는 create 또는 update
- 초기화 상태로 자동화된 테스트를 진행하는 개발자 환경과 CI 서버는 create 또는 create-drop
- 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none


### DDL 생성 기능
회원 이름은 필수로 입력되어야 하고, 10자를 초과하면 안 되는 제약조건이 추가되었습니다.
스키마 자동 생성하기를 통해 만들어지는  DDL에 이 제약조건을 추가해 봅시다.

```java
@Entity
@Table(name = "MEMBER")
public class Member{

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME", nullable = false, length = 10) // 추가
    private String username;
}
```

위 코드에서 @Column 매핑정보의 nullable 속성 값을 false로 지정하면 자동 생성되는 DDL에 not null 제약조건을 추가할 수 있습니다. 그리고 length 속성 값을 사용하면 자동 생성되는 DDL에 문자의 크기를 지정할 수 도 있습니다. 

### 기본키 매핑
JPA가 제공하는 데이터베이스 기본 키 생성 전략은 다음과 같습니다.

- 직접할당:  기본 키를 어플리케이션에서 직접 할당합니다.
- 자동생성: 대리 키 사용 방식

 IDENTITY: 기본 키 생성을 데이터베이스에 위임한다.
 
 SEQUENCE: 데이터베이스 시퀀스를 사용해서 기본 키를 할당한다.
 
 TABLE: 키 생성 테이블을 사용한다.

 자동 생성 전략이 이렇게 다양한 이유는 데이터베이스 벤더마다 지원하는 방식이 다르기 때문입니다. 오라클은 시퀀스를 제공하지만, MySQL은 시퀀스를 제공하지 않고 대신에 기본 키 값을 자동으로 채워주는 AUTO_INCREMENT 기능을 제공합니다. 따라서 SEQUENCE나 IDENTITY 전략은 사용하는 데이터베이스에 의존합니다. 

```java
//IDENTITY 매핑 코드
@Entity
public class Board{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

}
```

```java
//IDENTITY 사용 코드
private static void logic(EntityManager em){

    Board board = new Board();
    em.save(board);
    System.out.println("board.id = " + board.getId());
}
//출력: board.id = 1
```
위의 코드는  em.save()를 호출해서 엔터티를 저장한 직후에 할당된 식별자 값을 출력하였습니다. 출력된 값 1은 저장 시점에 데이터베이스가 생성한 값을 JPA가 조회한 것입니다.

#### IDENTITY 전략과 최적화
IDENTITY 전략은 데이터를 데이터베이스에 INSERT한 후에 기본 키 값을 조회할 수 있다.따라서 엔터티에 식별자 값을 할당하려면 JPA는 추가로 데이터베이스를 조회해야 합니다.

> 주의사항
엔터티가 영속 상태가 되려면 식별자가 반드시 필요합니다. 그런데 IDENTITY 식별자 생성 전략은 엔터티를 데이터베이스에 저장해야 식별자를 구할 수 있으므로 em.save()를 호출하는 즉시 INSERT SQL이 데이터베이스에 전달됩니다. 따라서 이전 략은 트랜잭션을 지원하는 쓰기지연이 동작하지 않습니다.


#### SEQUENCE 전략 
데이터베이스 시퀀스는 유일한 값을 순서대로 생성하는 특별한 데이터베이스 오브젝트 입니다.
SEQUENCE 전략은 이 시퀀스를 사용해서 기본 키를 생성합니다. 이 전략은 시퀀스를 지원하는 오라클, H2, PostgreSQL에서 사용할 수 있습니다.

```java
@Entity
@SequenceGenerator(
    name = "BOARD_SEQ_GENERATOR",
    sequenceName = "BOARD_SEQ", //매핑할 데이터베이스 시퀀스 이름
    initialValue = 1, allocationSize = 1)
public class Board{


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
    generator = "BOARD_SEQ_GENERATOR")
    private Long id;
}
```

위의 코드에서 BOARD_SEQ_GENERATOR라는 시퀀스 생성기를 등록했습니다. 이 시퀀스 생성기를 실제 데이터베이스 BOARD_SEQ 시퀀스와 매핑합니다.
이제부터 id 식별자 값은 BOARD_SEQ_GENERATOR 시퀀스 생성기가 할당합니다.

```java
//SEQUENCE 사용 코드
private static void logic(EntityManager em){

    Board board = new Board();
    em.save(board);
    System.out.println("board.id = " + board.getId());
}
//출력: board.id = 1
```

>IDENTITY 전략과 SEQUENCE 전략의 차이점
시퀀스 사용코드는 IDENTITY 전략과 같지만 내부 동작 방식은 다릅니다. SEQUENCE 전략은 em.save()를 호출 할 때 먼저 데이터베이스 시퀀스를 사용해서 식별자를 조회합니다. 그리고 조회한 식별자를 엔터티에 할당한 후에 엔터티를 영속성 컨텍스트에 저장합니다. 이후 트랜잭션을 커밋해서 플러시가 일어나면 데이터베이스에 저장을 합니다. 반대로 이전에 설명했던 IDENTITY 전략은 먼저 엔터티를 데이터 베이스에 저장한 후에 식별자를 조회해서 엔터티의 식별자에 할당합니다.




### 필드와 컬럼 매핑: 레퍼런스
#### @Enumerated
자바의 enum 타입을 매핑할 때 사용합니다.

속성 | 기능 | 기본값 |
:--------|:--------:|--------:| 
| @Enumerated | EnumType.ORIGINAL: enum 순서를 데이터베이스에 저장  EnumType.STRING: enum 이름을 데이터베이스에 저장 | EnumType.ORIGINAL |

ex)
```java
enum RoleType{
    ADMIN, USER
}
```
다음은 enum 이름으로 매핑합니다.
@Enumerated(EnumType.STRING)
private RoleType roleType;

member.setRoleType(RoleType.ADMIN); //-> DB에 문자 ADMIN으로 저장됩니다.

@Enumerated를 사용하면 편리하게 enum 타입을 데이터베이스에 저장할 수 있습니다.

EnumType.ORDINAL은 enum에 정의된 순서대로 ADMIN은 0, USER은 1값이 데이터베이스에 저장됩니다.
- 장점: 데이터베이스에 저장되는 데이터 크기가 작습니다.
- 단점: 이미 저장된 enum의 순서를 변경할 수 없습니다.


EnumType.STRING은 enum 이름 그대로 ADMIN은 'ADMIN', USER는 'USER'라는 문자로 데이터베이스에 저장된다.
- 장점: 저장된 enum의 순서가 바뀌거나 enum이 추가되어도 안전합니다.
- 단점: 데이터베이스에 저장되는 데이터 크기가 ORDINAL에 비해서 큽니다.


#### @Transient
이 필드는 매핑하지 않습니다. 따라서 데이터베이스에 저장하지 않고 조회되지도 않습니다. 객체에 임시로 어떤값을 보관하고 싶을때 사용합니다.

```java
@Transient
private Integer temp;
```

#### @Access
JPA가 엔터티 테이블에 접근하는 방식을 지정합니다.
테이블에 매핑할때 필드에 적용된 매핑정보를 읽은 다음에 테이블에 매핑을 하기 때문에 필드 접근방식을 지정하는 방법을 다르게 설정할 수 있습니다.

- 필드 접근: AccessType.FIELD로 지정합니다. 필드에 직접 접근합니다. 필드 접근 권한이 private이어도 접근할 수 있습니다.

- 프로퍼티 접근: AccessType.PROPERTY로 지정합니다. 접근자 Getter를 사용합니다.


```java
//필드 접근 코드
@Entity
@Access(AccessType.FIELD)
public class Member{

    @Id
    private String id;

    private String data1;

    private String data2;

    ...
}
```
@Id가 필드에 있으므로 @Access(AccessType.FIELD)로 설정한 것과 같습니다. 따라서 @Access는 생략해도 됩니다.

```java
//프로퍼티 접근 코드
@Entity
@Access(AccessType.PROPERTY)
public class Member{

   
    private String id;

    private String data1;

    private String data2;

     @Id
     public String getId(){
         return id;
     }

     @Column
     public String getData1(){
         return data1;
     }

     public String getData2(){
         return data2;
     }
}
```

@Id가 프로퍼티에 있으므로 @Access(AccessType.PROPERTY)로 설정한 것과 같습니다. @Access는 생략해도 됩니다.

```java
//필드, 프로퍼티 접근 코드 함께 사용
@Entity
public class Member{

    @Id
    private String id;

    @Transient
    private String firstName;

    @Transient
    private String lastName;

     @Access(AccessType.PROPERTY)
     public String getFullName(){
         return firstName + lastName;
     }
}
```

@Id가 필드에 있으므로 기본은 필드 접근 방식을 사용하고, getFullName()만 프로퍼티 접근 방식을 사용합니다. 따라서 회원 엔터티를 저장하면 회원 테이블의 FULLNAME 컬럼에 firstName + lastName의 결과가 저장됩니다.
