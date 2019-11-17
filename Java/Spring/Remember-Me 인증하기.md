# Remeber-Me 인증하기
웹에서의 로그인 처리는 크게 HttpSession을 이용하는 방식과, 쿠키(Cookie)를 이용하는 방식이 있습니다. HttpSession을 이용하는 방식을 흔히 `세션방식`이라고 합니다. 세션방식은 모든 데이터를 서버에서 보관하고, 브라우저는 단순히 `키(key)`에 해당하는 `세션ID`만을 이용하기 때문에 좀 더 안전하다는 장점이 있지만, 브라우저가 종료되면 사용할 수가 없기 때문에 모바일과 같은 환경에서 불편함이 있습니다.

쿠키를 이용하는 방식은 브라우저에 일정한 정보를 담은 `쿠키`를 전송하고, 브라우저는 서버에 접근할 때 주어진 쿠키를 같이 전송합니다. 이때 서버에서는 쿠키에 유효기간을 지정할 수 있기 때문에 브라우저가 종료되어도 다음 접근 시 유효기간이 충분하다면 정보를 유지할 수 있습니다.

`Remember-Me`는 최근 웹 사이트에서는 `로그인 유지`라는 이름으로 서비스되는 기능입니다. 이 방식은 쿠키를 이용해서 사용자가 로그인했던 정보를 보관하는 것입니다.

스프링 시큐리티의 `Remember-Me` 기능은 기본적으로 사용자가 로그인했을 때의 특정한 토큰 데이터를 2주간 유지되도록 쿠키를 생성합니다. 브라우저에 전송된 쿠키를 이용해서 로그인 정보가 필요하면 저장된 토큰을 이용해서 다시 정보를 사용합니다.

아래와 같이 로그인 폼에 `remember-me` 체크박스 처리하는 부분을 만들었습니다.

```html
<form action="" method="post" class="form form--login">
    <div class="form__field">
        <label class="fontawesome-user" for="username"><span class="hidden">Username</span></label>
        <input type="text" id="username" name="username" class="form__input" placeholder="Username" required>
    </div>

    <div class="form__field">
        <label class="fontawesome-lock" for="password"><span class="hidden">Password</span></label>
         <input type="password" id="password" name="password"  class="form__input" placeholder="Password" required>
    </div>

    <p>
    <label for="remember-me">로그인 유지하기</label>
    <input type="checkbox" id="remember-me" name="remember-me"  class="form__input" />
    </p>

    <input type="hidden" th:name="${_csrf.parameterName}"
                   th:value="${_csrf.token}" />
    <div class="form__field">
                <input type="submit" value="Log In">
    </div>
</form>
```


![스크린샷 2019-11-18 오전 12 31 41](https://user-images.githubusercontent.com/22395934/69009652-d3b9bc80-099a-11ea-9074-1a0318feb2b8.png)


# remember-me를 데이터베이스에 보관하기
스프링 시큐리티는 기본적으로 `remember-me` 기능을 사용하기 위해서 `Hash-based Token` 저장 방식과 `Persistent Token` 저장 방식을 사용할 수 있습니다.

`remember-me` 쿠키의 생성은 기본적으로 `username`과 쿠키의 만료시간, 패스워드를 Base-64 방식으로 인코딩한 것입니다. 따라서.. 사용자가 패스워드를 변경하면 정상적인 값이라도 로그인이 될 수 없다는 단점을 가지고 있습니다.

이를 해결하기 위해서 가능하면 데이터베이스를 이용해서 처리하는 방식이 권장됩니다.
데이터베이스를 이용하는 설정은 `org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl`이라는 긴 이름의 클래스를 이용합니다.

원한다면 `PersistentTokenRepository`라는 인터페이스를 이용해서 자신이 원하는 대로 구현이 가능합니다만, 관련된 모든 SQL을 직접 개발해야 하므로 번거러운 점이 있습니다.

우선 토큰을 보관할 수 있는 테이블을 아래와 같이 생성합니다.(MySQL 기준)

```sql
create table persistent_logins(
    username varchar(64) not null,
    series varchar(64) primary key,
    token varchar(64) not null,
    last_used timestamp not null
);
```

# SecurityConfig에서의 설정
SecurityConfig에서는 rememberMe()를 처리할 때 JdbcTokenRepositoryImpl을 지정해주어야 하는데 기본적으로 DataSource가 필요하므로 주입해 줄 필요가 있습니다.

```java
@EnableWebSecurity
@Slf4j
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomerAuthenticationProvider customerAuthenticationProvider;

    private final JpaUserService jpaUserService;

    private final DataSource dataSource;
```

HttpSecurity에서는 JdbcTokenRepositoryImpl을 이용할 것이므로 간단한 메소드를 이용해서 처리합니다.

```java
private PersistentTokenRepository getJDBCRepository(){
        
    JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
    repo.setDataSource(dataSource);
    return repo;
}
```

마지막으로 `HttpSecurity`가 이를 이용하도록 configure() 메서드의 내부를 수정합니다.

`remember-me` 기능을 설정하기 위해서는 UserDetailsService를 이용해야 한다는 조건이 있습니다. 만든 후에 HttpSecurity 인스턴스에 간단히 rememberMe()를 이용해 주면 처리가 가능합니다.

```java
http.rememberMe()
.key("jpa")
.userDetailsService(jpaUserService)
.tokenRepository(getJDBCRepository())
.tokenValiditySeconds(60*60*24);
```

마지막에 추가한 tokenValiditySeconds()는 쿠키의 유효시간을 초단위로 설정하는데 사용합니다. 코드에서는 24시간을 유지하는 쿠키를 생성합니다.

# 브라우저에서 토큰정보
SecurityConfig의 설정을 추가한 후에 화면에서 `remember-me`를 선택한 로그인을 하면 `persistent_logins`테이블에 생성된 토큰의 값이 기록되는 것을 확인할 수 있습니다.

![스크린샷 2019-11-18 오전 12 48 21](https://user-images.githubusercontent.com/22395934/69009875-314f0880-099d-11ea-80cc-0d9c1b07398d.png)

# MySQL에서 토큰정보

![스크린샷 2019-11-18 오전 12 48 41](https://user-images.githubusercontent.com/22395934/69009876-314f0880-099d-11ea-9d24-dad912688878.png)


이제 쿠키의 생성은 패스워드가 아니라 series에 있는 값을 기준으로 하게 됩니다. 사용자가 `remember-me`를 한 상태에서 로그아웃을 하면 스프링 시큐리티가 자동으로 데이터베이스에서 토큰정보를 삭제하는것을 확인할 수 있습니다.


![스크린샷 2019-11-18 오전 12 56 36](https://user-images.githubusercontent.com/22395934/69010018-54c68300-099e-11ea-9986-95ace094bdd2.png)
