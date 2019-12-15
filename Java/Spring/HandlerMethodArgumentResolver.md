# Spring HandlerMethodArgumentResolver

대부분 웹 어플리케이션에서 사용자들이 로그인 인증 후 서버에서 세션을 생성하고 발급을 해주게 됩니다. 그때마다 컨트롤러에서 세션정보를 요구하는 특정 메소드를 수행하게 되면 아래 코드처럼 중복적인 코드를 작성해야 되는 불필요한 상황이 발생하게 됩니다.

```java
@Controller
public class indexController{

    @GetMapping("/")
    public String index(Model model, HttpSession httpSession){
        // session 값을 추출해야 하는 코드
        User user = (User) httpSession.getAttribute("user");
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }
} 
```

이러한 불필요한 코드를 작성하는 상황을 해결하기 위해서
HandlerMethodArgumentResolver 인터페이스에 대해서 알게 되었습니다.

## HandlerMethodArgumentResolver

HandlerMethodArgumentResolver는 컨트롤러 메소드에서 특정 조건에 맞는 파라미터가 있을 때 원하는 값을 바인딩 해주는 인터페이스입니다.

스프링에서는 Controller에서 @RequestBody 어노테이션을 사용해 Request의 Body 값을 받아올 때, @PathVariable 어노테이션을 사용해 Request의 Path Parameter 값을 받아올 때 이 HandlerMethodArgumentResolver를 사용해서 값을 받아옵니다.

# HandlerMethodArgument  구현 예제
먼저, 어노테이이션 기반으로 세션 객체를 바인딩할 수 있도록 @LoginUser 클래스 어노테이션을 작성합니다.


## 1. 어노테이션 작성
```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {


}
```

@interface
- 이 파일을 어노테이션 클래스로 지정합니다ㅏ.
- LoginUser라는 이름을 가진 어노테이션이 생성되었다고 보면 됩니다.

@Target(ElementType.PARAMETER)

- 이 어노테이션이 생성될 수 있는 위치를 지정합니다.
- PARAMETER로 지정했으니 메소드의 파라미터로 선언된 객체에서만 사용할 수 있습니다.


@Retention(RetentionPolicy.RUNTIME)

- 어노테이션 유지 정책을 설정합니다. RUNTIME은 바이트 코드 파일까지 어노테이션 정보를 유지하면서 리플렉션을 이용해서 런타임 시에 해당 어노테이션의 정보를 얻을 수 있습니다.


## Customizing 한 Session 객체 작성

```java
import com.junyoung.book.springboot.domain.user.User;
import lombok.Getter;
import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String picture;


    public SessionUser(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
```
도메인 모델을 직접 세션에 저장하면 다른 엔티티와 연관관계를 가질 때 대상에 자식들이 포함되어 성능이슈, 부수 효과가 발생할 확률이 있어 Dto로 직렬화를 구현해서 세션에 저장하는게 좋습니다.


## resolver 작성

이제 가장 중요한 HandlerMethodArgumentResolver를 상속받은 LoginUserArgumentResolver를 작성합니다.

HandlerMethodArgumentResolver를 상속받은 객체는 아래 두개의 메소드를 구현해야 합니다.


```java
public boolean supportsParameter(MethodParameter parameter);

public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception;
```


- supportsParameter 메소드는  파라미터가 Resolver에 의해 수행될 수 있는 타입인지 `true / false`를 리턴합니다. 만약 true를 리턴한다면 resolveArgument() 메소드를 실행합니다.

- resolveArgument 메소드는 실제로 파라미터와 바인딩을 할 객체를 리턴합니다.
NativeWebRequest를 통해 클라이언트 요청이 담긴 파라미터를 컨트롤러보다 먼저 받아서 작업을 수행할 수 있습니다.

```java
import com.junyoung.book.springboot.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        boolean isLoginUserAnnotation =  parameter.getParameterAnnotation(LoginUser.class) != null;

        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());

        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}
```
LoginUserArgumentResolver의 supportsParameter(MethodParameter parameter) 메소드는 컨트롤러의 메소드의 파라미터를 가져와서 LoginUser.class 어노테이션이 적용 유무와, 실제로 해당 세션 객체의 타입이 SessionUser.class 인지 검증 후 둘다 조건이 맞다면 `true`를 리턴하여 resolveArgument() 메소드를 수행하도록 작성하였습니다.


## resolver 등록
마지막으로 작성한 LoginUserArgumentResolver를 스프링에 등록합니다.

```java
import com.junyoung.book.springboot.config.auth.LoginUserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserArgumentResolver loginUserArgumentResolver;


    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserArgumentResolver);
    }
}
```

spring Boot에서 `WebMvcConfigurer`는 자동 구성된 Spring MVC 구성을 큰 변경없이 추가적인 조작을 하기 위해서 구현합니다. 이를 통해서 자동 구성된 Spring MVC를 개발자가 완벽히 제어할 수 있습니다. 



## 컨트롤러 작성

```java
import com.junyoung.book.springboot.config.auth.LoginUser;
import com.junyoung.book.springboot.config.auth.dto.SessionUser;
import com.junyoung.book.springboot.service.PostsService;
import com.junyoung.book.springboot.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class IndexController {

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){

        if(user != null){
            model.addAttribute("userName", user.getName());
        }

        return "index";
    }
}
```

@LoginUser SessionUser user를 세션정보가 필요한 컨트롤러의 메소드 파라미터로 넣어주면서 기존에 (User) HttpSession.getAttribute("user")로 가져오던 ㅔ쎤 정보 값이 개선 되었습니다.

이제는 어느 컨트롤러든지 `@LoginUser`만 사용하면 세션 정보를 가져올 수 있게 되었습니다.
