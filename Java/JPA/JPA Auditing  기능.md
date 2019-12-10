# JPA Auditing이란?

Java에서 ORM 기술인 JPA를 사용하여 도메인을 관계형 데이터베이스 테이블에 매핑할 때 공통적으로 도메인들이 가지고 있는 필드나 컬럼들이 존재합니다. 대표적으로 생성일자, 수정일자, 식별자 같은 필드 및 컬럼이 있습니다. 도메인마다 공통으로 존재한다는 의미는 결국 코드가 중복된다는 말과 일맥상통합니다. 
데이터베이스에서 `누가, 언제`하였는지 기록을 잘 남겨놓아야 합니다. 그렇기 때문에 생성일, 수정일 컬럼은 대단히 중요한 데이터 입니다.


그래서 JPA에서는 `Audit`이라는 기능을 제공하고 있습니다. Audit은 `감시하다, 감사하다`라는 뜻으로 Spring Data JPA에서 시간에 대해서 자동으로 값을 넣어주는 기능입니다. 도메인을 영속성 컨텍스트에 저장하거나 조회를 수행한 후에 update를 하는 경우 매번 시간 데이터를 입력하여 주어야 하는데, audit을 이용하면 자동으로 시간을 매핑하여 데이터베이스의 테이블에 넣어주게 됩니다.

# Auditi 사용 예제

build.grade에 의존성 추가

```java
dependencies {
compile('org.springframework.boot:spring-boot-starter-web')
compile('org.projectlombok:lombok')
compile('org.springframework.boot:spring-boot-starter-data-jpa')
}
```

기본적으로 스프링 부트에서 gradle로 의존성을 관리하게 될 경우 `spring-boot-starter-data=jpa`만 추가해도 Audit을 하는데는 문제가 없습니다.

>참고로 자바 1.8 이상부터는 기존의 문제가 있던 Date, Calander 클래스를 사용하지 않고 LocalDate, LocalDateTime 클래스를 사용합니다. 또한 LocalDateTime 객체와 테이블 사이의 매핑이 안되던 이슈는 하이버네이트 5.2 버전부터 해결이 되었습니다.

```java
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@Getter
@MappedSuperclass 
@EntityListeners(AuditingEntityListener.class) 
public abstract class BaseTimeEntity{

    // Entity가 생성되어 저장될 때 시간이 자동 저장됩니다.
    @CreatedDate
    private LocalDateTime createdDate;

    // 조회한 Entity 값을 변경할 때 시간이 자동 저장됩니다.
    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```

|  <center>어노테이션</center> |  <center>설명</center> | 
|:--------|:--------:|
|  @MappedSuperclass | <center>JPA Entity 클래스들이 해당 추상 클래스를 상속할 경우 createDate, modifiedDate를 컬럼으로 인식</center> | 
| @EntityListeners(AuditingEntityListener.class | <center>해당 클래스에 Auditing 기능을 포함</center> | 
|  @CreatedDate | <center>Entity가 생성되어 저장될 때 시간이 자동 저장</center> | T를 Supplier가 제공한 Collection에 저장 |
|  @LastModifiedDate | <center>조회한 Entity의 값을 변경할 때 시간이 자동 저장</center> | 

# 클래스 상속

```java

import com.junyoung.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
```


```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// JPA Auditing 활성화
@EnableJpaAuditing 
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        // 내장 WAS를 실행합니다.
        SpringApplication.run(Application.class, args);
    }
}
```


Posts 클래스가 `@MappedSuperclass`가 적용된 BaseTimeEntity 추상 클래스를 상속하기 때문에 JPA가 생성일자, 수정일자 컬럼을 인식하게 됩니다. 그리고 영속성 컨텍스트에 저장 후 BaseTimeEntity 클래스의 Auditing 기능으로 인해서  트랜잭션 커밋 시점에 플러시가 호출할 때 하이버네이트가 자동으로 시간 값을 채워주는것을 확인 할 수가 있습니다.

스프링 부트의 엔트리 포인트인 실행 클래스에 @EnableJpaAuditing 어노테이션을 적용하여 JPA Auditing을 활성화 해야하는 것을 잊지 말아야 합니다.


# 테스트 수행

```java

    @Test
    public void BaseTimeEntity_등록() throws Exception{
        //given
        LocalDateTime now = LocalDateTime.of(2019,6,4,0,0,0);

        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        //when
        List<Posts> postsList = postsRepository.findAll();


        //then
        Posts posts = postsList.get(0);

        System.out.println(">>>>>>>>>>>>> createdDate="+ posts.getCreatedDate() + ", modifiedDate=" + posts.getModifiedDate());

        assertThat(posts.getCreatedDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);
    }
```

간단하게 테스트 코드를 작성하여 실제로 Auditing 기능이 활성화 되어 하이버네이트가 생성일자, 수정일자 값을 자동으로 채워주는지 확인해봤습니다.

## 실행결과

![스크린샷 2019-12-11 오전 12 37 54](https://user-images.githubusercontent.com/22395934/70543883-818d4500-1bae-11ea-9d88-b6abb979358e.png)


#### 참조: 스프링 부트와 AWS로 혼자 구현하는 웹서비스
