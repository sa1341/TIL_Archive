## @WebMvcTest
MVC를 위한 테스트로 웹에서 테스트하기 힘든 컨트롤러를 테스트하는데 적합합니다. 웹 상에서 요청과 응답에 대해 테스트할 수 있습니다. 뿐만 아니라 시큐리티 혹은 필터까지 자동으로 테스트하며 수동으로 추가/삭제까지 가능한 유용한 녀석입니다.

@WebMvcTest 어노테이션을 사용하면 MVC 관련 설정인 @Controller, @ControllerAdvice, @JsonComponent와 Filter, WebMvcConfigurer, HandlerMethodArgumentResolver만 로드되기 때문에 @SpringBootTest 어노테이션보다 가볍게 테스트 할 수 있습니다.

참고로 @WebMvcTest와 @SpringBootTest랑 같이 쓰면 에러가 발생합니다. 그 이유는 서로 MockMvc를 설정하기 때문에 충돌이 나서 그런것 같습니다.

아래 코드는 단순한 Book 클래스입니다. 제목과 출간일자 필드만 가지고 있습니다.

```java
package com.jun.demo.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.OneToMany;
import java.time.LocalDateTime;

@NoArgsConstructor // 컴파일시 기본생성자를 만들어주는 어노테이션
@Getter
public class Book {

    private Integer idx;
    private String title;
    private LocalDateTime publishedAt;


    @Builder 
    public Book(String title, LocalDateTime publishedAt){
        this.title = title;
        this.publishedAt = publishedAt;
    }
}
````
@Builder 어노테이션을 생성자 상단에 선언시 하게 되면 생성자에 포함된 필드만 빌더에 포함됩니다.

```java
package com.jun.demo.controller;

import com.jun.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public String getBookList(Model model){

        model.addAttribute("bookList", bookService.getBookList());

        return "book";
    }
}
```
BookController에서 책 리스트를 받아오는 테스트 입니다.
/books URL이 GET방식으로 요청이 들어오면 현재 BookService 클래스에 책 목록을 요청하여 'bookList'라는 키 값으로 데이터값을 넘기는 컨트롤러를 만들겠습니다. 컨틀로러에서 반환되는 뷰이름은 'book'으로 지정하였습니다.

```java
package com.jun.demo.service;

import com.jun.demo.domain.Book;

import java.util.List;

public interface BookService {

    List<Book> getBookList();

}
```
BookService 인터페이스를 생성하였지만 실제로 이 인터페이스를 구현하는 구현체는 만들지 않고 mock(위조, 가짜의) 데이터를 이용해 테스트 하겠습니다.


```java
package com.jun.demo;

import com.jun.demo.controller.BookController;
import com.jun.demo.domain.Book;
import com.jun.demo.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given; //Behavior Driven Development TDD라는 추상적인 의미를 행위로 바꾼 개발법.
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@WebMvcTest(BookController.class) //BookController만 로드됨 
public class BookControllerTest {

    @Autowired
    private MockMvc mvc; // 컨트롤러를 흉내내는 녀석입니다.

    @MockBean // 가짜 구현체 역할을 하는 녀석을 주입
    private BookService bookService;

    @Test
    public void Book_MVC_테스트() throws Exception {

        Book book = new Book("Spring Boot Book", LocalDateTime.now());
        given(bookService.getBookList()).willReturn(Collections.singletonList(book));
        
       
        mvc.perform(get("/books"))  // when 테스트할 대상을 실행하는 영역
                .andExpect(status().isOk()) // then 검증하는 영역
                .andExpect(view().name("book"))
                .andExpect(model().attributeExists("bookList"))
                .andExpect(model().attribute("bookList", contains(book)));


    }
}
```
@WebMvcTest 인자로 BookController.class를 넣어주므로써 해당 컨트롤러만 빈으로 로드되기 때문에 @SpringBootTest 어노테이션을 사용할때보다 더 가볍고 빠르게 테스트를 수행 할 수가 있습니다. 

MockMvc는 컨트롤러 테스트 시 모든의존성을 로드하는 것이 아닌 BookController 관련 빈만 로드하여 가벼운 MVC 테스트를 수행합니다. 

여기서 가짜 객체를 흔히 목 객체라고 하는데 목 객체는 실제 객체는 아니지만 특정 행위를 지정하여 실제 객체처럼 동작하게 만들 수 있습니다.

먼저, Spring Boot book이란 이름의 Book 객체를 하나 생성하여 BookService의 getBookList()메서드가 생성한 Book 객체를 포함하는 리스트를 반환하도록 설정했습니다.

Collections.singletonList()는 한개의 객체만 있는 immutable한 리스트를 반환합니다. Arrays.asList()는 배열을 리스트로 변환하지만 add, remove 메소드를 지원하지 않는 크기가 고정된 자료구조 입니다. 개인적으로 요소가 1개이고 변경을 안한다는 가정하에 전자를 사용하면 메모리 관점에서 더 효율적이라고 합니다.

given()을 사용하여 getBookList() 메서드의 실행에 대한 반환값을 미리 정의해 두었습니다. 

이런식으로 MockMvc를 사용하면 해당 URL의 상탯값, 반환값에 대한 테스트를 수행할 수 있습니다.
 

- andExpect(status().isOk()): http 상탯값이 200인지 테스트
- andExpect(view().name("book")): 반환하는 뷰의 이름이 'book'인지 테스트
- andExpect(model().attributeExists("bookList")): 모델의 프로퍼티 중 'bookList'라는 프로퍼티가 존재하는지 테스트
- andExpect(model().attribute("bookList", contains(book))): 모델의 프로퍼티 중 'bookList'라는 프로퍼티에 book 객체가 담겨져 있는지 테스트
