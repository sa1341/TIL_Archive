# 인터셉터란?

Interceptor는 `가로채는 것(요격기)`을 의미합니다. 클라이언트(브라우저)에서 요청 Url을 Controller라는 표현계층에 전송을 하면 해당 요청 Url과 매핑되는 메소드를 실행되기 전 후에 어떤 작업을 수행하기 위해서 Url을 가로채는 주체라고 생각하면 됩니다. 

대표적으로 Url Mapping 된 Controller를 거치는 전, 후 처리를 할 수 있도록 도와주는 요소를 말합니다. 주로 세션 검증, 로그 처리 같은 행위가 간단한 예시 입니다.

`Interceptor`는 Spring-WebMvc에 포함되어 있습니다. Spring Boot에서 gradle을 빌더로 사용할 때 build.gradle 파일에 dependency에 `spring-boot-starter-web`을 가져옴으로 해결할 수 있습니다.

```java
dependencies{
  implementation'org.springframework.boot:spring-boot-starter-web'
}
```

## Intercpetor 예제 코드

기본 인터페이스는 `HandlerInterceptor` 이고, 이를 구현해서 Interceptor class를 작성해도 되고, `HandlerInterceptorAdapter`를 구현할 수도 있습니다. 하지만 `Java 1.8` 이상부터는 HandlerInterceptor의 메소드가 default로 선언되어 있기 때문에 필요한 메소드만 선택해서 구현할 수 있기 때문에 굳이 HandlerInterceptorAdapter를 이용하지 않아도 됩니다. 


```java
@Component
public class HttpInterceptor extends HandlerInterceptor {

	private static final Logger logger = Logger.getLogger(HttpInterceptor.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request,
							 HttpServletResponse response,
							 Object handler) {
		logger.info("================ Before Method");
		return true;
	}
	
	@Override
	public void postHandle( HttpServletRequest request,
							HttpServletResponse response,
							Object handler,
							ModelAndView modelAndView) {
		logger.info("================ Method Executed");
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request,
								HttpServletResponse response, 
								Object handler, 
								Exception ex) {
		logger.info("================ Method Completed");
	}
}
```

- preHandle(): Controller의 메소드를 실행하기 전에 처리해주는 메소드입니다.
주로 세션 검증에서 많이 사용합니다.

- postHandle(): Controller의 메소드를 실행 후에 처리를 해주는 메소드입니다.
세션 검증 후 로그인 인증이 되어있다면 해당 사용자 정보를 세션에 저장하는 로직을 넣을 수도 있습니다. 또한 view에 전달할 ModelAndView 객체를 이용해서 특정 작업을 수행도 할 수 있습니다.

- afterCompletion(): Controller의 메소드를 실행 후 view를 클라이언트에게 전송 후에 처리를 해주는 메소드입니다.


## Config
Interceptor를 등록하기 위해서 WebMvcConfigurer를 이용합니다. Interceptor를 등록한 후 적용할 경로, 제외할 경로를 지정해줄 수 있습니다.

```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    //HandlerInterceptor를 구현한 개발자가 작성한 Intercepotor 클래스
    private final JwtInterceptor jwtInterceptor;

    private static final String[] EXCLUDE_PATHS = {
            "/member/**",
            "/error/**"
    };

    // 개발자가 구현한 Interceptor를 regisry 객체를 통해서 Interceptor 추가
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(EXCLUDE_PATHS);
    }
}
```

Spring Boot에서 `WebMvcConfigurer`는 자동 구성된 Spring MVC 구성을 큰 변경없이 추가적인 조작을 하기 위해서 구현합니다. 이를 통해서 자동 구성된 Spring MVC를 개발자가 완벽히 제어할 수 있습니다. 


> boot 2.0 + Java 1.8 + spring 5.0 이상의 버전을 사용하면서 WebMvcConfigurer의 대두분 메서드에 default로 선언 되었습니다. 덕분에 모든 메서드를 구현해야하는 강제력이 사라졌습니다. 필요한 메서드만 선택해서 오버라이드하면 됩니다.

# Controller

```java
@Slf4j
@Controller
public class HelloController {

    @RequestMapping("/memberInfo")
    public void memberInfo(Model model){
        log.info("postHandle() 메소드 수행 전");
        String id = "sa1341";
        model.addAttribute("userName", id);
    }
}
```

#### 실행 결과
![스크린샷 2019-12-07 오후 9 50 19](https://user-images.githubusercontent.com/22395934/70374927-9c0bb800-193b-11ea-838e-3182491aa268.png)
