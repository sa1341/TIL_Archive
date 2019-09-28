안녕하세요
이번 포스팅은  Spring Data JPA를 이용하여 동적으로 SQL을 처리하는 Querydsl에 대해 진행하겠습니다.

## Querydsl이란?
쿼리를 처리하다 보면 다양한 상황에 맞게 쿼리를 생성하는 경우가 많습니다.
대표적인 케이스가 다양한 검색 조건에 대해서 쿼리를 실행해야 하는 경우라고 할 수 있습니다.
쿼리 메소드나 @Query를 이용하는 경우에 개발 속도는 좋지만 고정적인 쿼리만을 생산합니다.
이러한 이유로 동적인 상황에 대한 처리를 위해서 Querydsl이라는 것을 이용합니다.

단순 문자열(JDBC, Mybatis, JPQL)과 비교해서 Fluent API(Querydsl)를 사용할 때의 장점은 다음과 같습니다.

1. IDE의 코드 자동 완성 기능 사용
2. 문법적으로 잘못된 쿼리를 허용하지 않습니다.
3. 도메인 타입과 property를 안전하게 참조할 수 있습니다.
4. 도메인 타입의 리팩토링을 더 잘 할 수 있습니다.
 
> 요약하면 Querydsl은 타입에 안전한 방식으로 쿼리를 실행하기 위한 목적으로 만들어졌습니다.
즉, Querydsl의 핵심 원칙은 타입 안전성(Type safety)입니다.
그것이 가능한 이유는 문자열이 아닌 메서드 호출로 쿼리가 수행되기 때문입니다.

저는 Maven을 이용해서 Querydsl에 대해 의존성과 플러그인을 설정하겠습니다.
Querydsl을 이용하기 위해서는 다음과 같은 과정을 수행해야 합니다.

- pom.xml의 라이브러리와 Maven 설정의 변경 및 실행
- Predicate의 개발
- Repository를 통한 실행



1. Querydsl 의존성 라이브러리를 추가합니다.
```xml
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-apt</artifactId>
    <version>${querydsl.version}</version>
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.querydsl</groupId>
    <artifactId>querydsl-jpa</artifactId>
    <version>${querydsl.version}</version>
</dependency>

```


2. pom.xml에 Querydsl 플러그인을 추가합니다.
~~~xml
 <plugin>
        <groupId>com.mysema.maven</groupId>
        <artifactId>apt-maven-plugin</artifactId>
        <version>1.1.3</version>
        <executions>
            <execution>
                <goals>
                <goal>process</goal>
                </goals>
                <configuration>
                    <outputDirectory>target/generated-sources/java</outputDirectory>
                    <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
                </configuration>
            </execution>
        </executions>
 </plugin>
~~~

플러그인에서 outputDirectory 태그에 경로를 적어주었는데요. Querydsl에서는 JPA를 처리하기 위해서 엔터티 클래스를 생성하는 방식을 이용합니다. 이를 'Qdomain'이라고 하는데,
이 도메인 클래스는 위의 해당경로에 생성이 됩니다.

정상적으로 설정이 되었다면 프로젝트 내에 target/generated-sources/java 디렉토리에 Qdomain 클래스가 생성되는 것을 볼수 있습니다.
이때 주의할 사항은 해당 경로는 패스에 존재하지 않기 때문에 Qdomain을 찾지 못한다고 에러가 발생 할 수 있습니다. 따라서 아래와 같이 해당경로를 패스에 추가를 해줘야 합니다.

![스크린샷 2019-06-01 오후 11 53 22](https://user-images.githubusercontent.com/22395934/58750072-85862f00-84c8-11e9-877f-099e106548c2.png)

저는 현재 해당경로를 소스 폴더에 추가한 상태입니다. IntelliJ에서는 File>Project Structure>Modules에 들어가면 위 화면 같이 구성되어있습니다.
추가방법은 좌측 트리에 target/generated-sources/java 디렉토리를 우클릭하여 Sources를 선택하면 우측 Source Folders에 추가가 됩니다.

이제 간단하게 엔터티 클래스를 이용하여 동적으로 쿼리를 생성하는 예제를 보여드리겠습니다.

먼저, Repository 코드에서 QueryDslPredicateExcutor<T> 인터페이스를 상속하도록 아래와 같이 변경해 줍니다.
```java
public interface BoardRepository extends CrudRepository<Board, Long>, QuerydslPredicateExecutor<Board> {

}
```
QueryDslPredicateExcutor<T> 인터페이스는 다음과 같은 메소드들이 선언되어 있습니다.

|  <center>메소드</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| long count(Predicate) | <center>데이터 전체 개수</center> 
| boolean exists(Predicate)| <center>데이터 존재 여부</center> 
| Iterable<T> findAll(Predicate) | <center>조건에 맞는 모든 데이터</center> 
| Page<T> findAll | <center>조건에 맞는 모든 데이터</center> 
| Iterable<T> findAll(Predicate,Sort) | <center>조건에 맞는 모든 데이터와 정렬<center> 
| T findOne(Predicate)| <center>조건에 맞는 하나의 데이터</center> 


엔터티 클래스
```java
@Getter
@Setter
@ToString
@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long bno;

    private String title;

    private String writer;

    private String content;

    @CreationTimestamp
    private Timestamp regdate;

    @UpdateTimestamp
    private Timestamp updatedate;
}

```

Predicate를 생성 및 테스트를 수행하는 코드입니다.
Predicate는 '확신한다', '단언하다'라는 뜻으로 이 조건이 맞다고 판단하는 근거를 함수로 제공하는 것입니다.

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testPredicate(){

        String type = "t";
        String keyword = "17";
        BooleanBuilder builder = new BooleanBuilder();


        QBoard board = QBoard.board;

        //메소드를 통해서 쿼리문을 생성함.
        if(type.equals("t")){
            builder.and(board.title.like("%" + keyword +"%"));
        }

        //bno 값이 0보다 큰 값을 조건에 추가
        builder.and(board.bno.gt(0L));
        //페이징 처리에 정보를 가진 객체 생성
        Pageable pageable = PageRequest.of(0,10);

        Page<Board> result = boardRepository.findAll(builder, pageable);

        System.out.println("PAGE SIZE: " + result.getSize());
        System.out.println("TOTAL PAGES: " + result.getTotalPages());
        System.out.println("TOTAL COUNT: " + result.getTotalElements());
        System.out.println("NEXT: " + result.nextPageable());

        List<Board> list = result.getContent();

        list.forEach(b -> System.out.println(b));
    }
```

위의 코드를 보면 상황에 따라 조건문이 달라집니다.

- type이 오면 where type = type
- keyword가 오면 where keyword = keyword 

즉, 파라미터가 어떻게 오는지에 따라 where의 조건이 변경되는 것입니다.
이를 해결하기 위한 방법으로 BooleanBuilder를 자주 사용합니다.

QBoard 객체는 엔터티 클래스인 Board 객체를 참조해서 생성이 됩니다.
만약 위와 같이 설정파일과 패스경로에 추가했음에도 생성이 되지 않는다면 pom.xml을 우클릭하여 아래 화면처럼 Generate Sources and Update Folders를 클릭해줍니다.

![스크린샷 2019-06-02 오전 12 23 50](https://user-images.githubusercontent.com/22395934/58750388-c2542500-84cc-11e9-8054-5fbb723b05af.png)

if문에서 필요한 부분만을 BooleanBuilder에 추가하면서 쿼리를 만들었습니다.
메소드로 쿼리를 생성하기 때문에 위에서 언급한것 처럼 안정적이고 문법적인 오류를 허용하지 않습니다. 하지만 where문의 조건을 한눈에 보기 어렵습니다. 지금은 조건문을 많이 추가를 안했지만 조금만 조건이 까다로워지면 추측하기도 힘든 쿼리가 될 것입니다.

and() 메소드를 이용하여 조건을 추가하는 것을 볼 수가 있습니다.
QBoard는 Board의 속성을 이용해서 다양한 SQL에 필요한 구문을 처리할 수 있는 기능이 추가된 형태이므로, like(), get()등을 이용해서 원하는 SQL을 구성하는데 도움을 줍니다.

테스트 코드를 실행하면 다음과 같은 결과를 볼 수 있습니다.
![스크린샷 2019-06-02 오전 12 34 55](https://user-images.githubusercontent.com/22395934/58750492-48bd3680-84ce-11e9-8f91-2200a3d1b7e8.png)

리턴타입을 Page<T>로 설정했기 때문에 데이터를 추출하는 SQL과 개수를 파악하는 SQL이 실행되고, 이때 필요한 조건들이 지정되는 것을 볼 수 있습니다.
