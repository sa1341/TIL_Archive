
## @DataJpaTest

@DataJpaTest 어노테이션은 JPA 관련 테스트 설정만 로드합니다. DataSource의 설정이 정상적인지, JPA를 사용하여 데이터를 제대로 생성, 수정, 삭제하는지 등의 테스트가 가능합니다. 그리고 가장 좋은점은.. 무려 내장형 데이터베이스를 사용하여 실제 데이터베이스를 사용하지 않고 테스트 데이터베이스로 테스트할 수 있는.. 개꿀같은 장점이 있습니다.

@DataJpaTest는 기본적으로 @Entity 어노테이션이 적용된 클래스를 스캔하여 스프링 데이터 JPA 저장소를 구성합니다. 만약 최적하한 별도의 데이터소스를 사용하여 테스트하고 싶다면 기본 설정된 데이터소스를 사용하지 않도록 아래와 같이 설정해도 됩니다.


```java
RunWith(SpringRunner.class)
@DataJpaTest
@ActiveProfiles("...")
@AutoConfigureTestDatabase(replace = 
@AutoConfigureTestDatabase.Replace.NONE)
public class JpaTest {

}
```
@AutoConfigureTestDatabase 어노테이션의 기본 설정값인 Replace.Any를 사용하면 기본적으로 내장된 임베디드 데이터베이스를 사용합니다. 위의 코드에서 Replace.NONE로 설정하면 @ActiveProfiles에 설정한 프로파일 환경값에 따라 데이터 소스가 적용됩니다. yml 파일에서 프로퍼티 설정을 spring.test.database.replace: NONE으로 변경하면 됩니다.

> @DataJpaTest는 기본적으로 @Transactional 어노테이션을 포함하고 있습니다. 그래서 테스트가 완료되면 자동으로 롤백하기 때문에 직접 선언적 트렌젝션 어노테이션을 달아줄 필요가 없습니다. 

만약에 @Transactional 기능이 필요하지 않다면 아래와 같이 줄 수 있습니다.

```java
@RunWith(SpringRunner.class)
@DataJpaTest
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class JpaTest {
    ...
}
```

그리고 어떤 테스트 데이터베이스를 사용할 것인지도 선택 할 수 있습니다. spring.test.database.connection: H2와 같이 프로퍼티를 설정하는 방법과 @AutoConfigureTestDatabase(connection = H2) 어노테이션으로 설정하는 방법이 있습니다. 

@DataJpaTest에서 EntityManager의 대체제로 만들어진 테스트용 TestEntityManager를 사용하면 persist, flush, find 등과 같은 기본적인 JPA테스트가 가능합니다. 아래 간단하게 도메인 객체에 대한 JPA 테스트를 수행할 수 있게 Book 클래스에 JPA 관련 어노테이션을 추가하고 BookRepository 인터페이스를 생성하였습니다.


#### Entity
```java
package com.jun.jpacommunity.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {

    @Id
    @GeneratedValue
    private Integer idx;

    private String title;

    private LocalDateTime publishedAt;

    @Builder
    public Book(String title, LocalDateTime publishedAt) {
        this.title = title;
        this.publishedAt = publishedAt;
    }
}
```

####  Repository

```java
package com.jun.jpacommunity.repository;

import com.jun.jpacommunity.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Integer> {

}
```


#### Test 코드
```java
package com.jun.jpacommunity.repository;


import com.jun.jpacommunity.domain.Board;
import com.jun.jpacommunity.domain.Book;
import com.jun.jpacommunity.domain.Member;
import org.hamcrest.collection.IsEmptyCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class JpaTest {

    private final static String BOOT_TEST_TITLE = "Spring Boot Test Book";

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private BookRepository bookRepository;

    @Test
    public void Book_저장하기_테스트() throws Exception {
        //given
        Book book1 = Book.builder()
                .title("타짜")
                .publishedAt(LocalDateTime.now())
                .build();

        //when
        testEntityManager.persist(book1);

        //then
        assertEquals(book1, testEntityManager.find(Book.class, book1.getIdx()));

     }

     @Test
     public void BookList_저장하고_검색_테스트() throws Exception {

         //given
         Book book1 = Book.builder()
                 .title(BOOT_TEST_TITLE +"1")
                 .publishedAt(LocalDateTime.now())
                 .build();

         testEntityManager.persist(book1);

         Book book2 = Book.builder()
                 .title(BOOT_TEST_TITLE +"2")
                 .publishedAt(LocalDateTime.now())
                 .build();

         testEntityManager.persist(book2);

         Book book3 = Book.builder()
                 .title(BOOT_TEST_TITLE +"3")
                 .publishedAt(LocalDateTime.now())
                 .build();

         testEntityManager.persist(book3);


         //when
         List<Book> bookList = bookRepository.findAll();

         //then
         assertEquals(bookList.size(), 3);
         assertEquals(book1, bookList.get(0));
      }


    @Test
    public void BookList_저장하고_삭제_테스트() throws Exception {

        //given
        Book book1 = Book.builder().title(BOOT_TEST_TITLE + "1").
                publishedAt(LocalDateTime.now()).build();

        testEntityManager.persist(book1);

        Book book2 = Book.builder().title(BOOT_TEST_TITLE + "2").
                publishedAt(LocalDateTime.now()).build();

        testEntityManager.persist(book2);

        //when
        bookRepository.deleteAll();
        //then
        assertThat(bookRepository.findAll(), IsEmptyCollection.empty());
    }
}
```
요즘에 TDD 방식으로 테스트 하는 습관을 기르기 위해서 인텔리제이에서 제공해주는 라이브 템플릿으로 간단하게 give, when, then으로 테스트 영역을 구분하여 테스트를 수행하였습니다. 

- Book_저장하기_테스트(): testEntityManager로 persist() 기능이 정상 동작되는지 테스트를 수행하였습니다.

- BookList_저장하고_검색_테스트(): Book 3개를 저장한 후 저장된 Book의 개수가 3개가 맞는지, bookList에 0번째 인덱스에 저장된 book1 객체가 포함 되어있는지 테스트를 수행하였습니다.

- BookList_저장하고_삭제_테스트(): 저장된 Book 중에서 2개가 제대로 삭제되었는지 테스트를 하였습니다.


#### 참조: https://hyper-cube.io/2017/08/10/spring-boot-test-2/, 처음배우는 스프링 부트2
      
