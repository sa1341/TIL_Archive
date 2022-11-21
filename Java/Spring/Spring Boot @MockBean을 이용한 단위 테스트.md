# Spring Boot  @MockBean을 이용한 단위 테스트  

Spring Boot로 개발을 REST API 개발을 하면서 repository, service 계층을 위주로만 TDD를 수행하였고, 표현 계층인 Controller 테스트는 소홀하게 대한점이 있어서 다시 공부를 하였습니다.


#### 표현 계층 (Controller)

```java
@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@ResponseBody
public ResponseEntity<UserDto> dummyTest(@RequestBody final UserDto userDto) throws Exception {
         UserResponseDto responseDto = userService.findUser(userDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
 }
```

위의 코드는 간단하게 클라이언트에서 id, password를 JSON String으로 서버에 전달하면 서버에서는 DTO로 매핑하여 서비스 계층에 전달하여 리턴 결과로 DB에서 관리하는 User 테이블의 식별 값과 아이디를 JSON으로 리턴하는 API를 만들었습니다.


#### TEST 코드

```java
@RunWith(SpringRunner.class)
@WebMvcTest({UserController.class, JwtAuthenticationInterceptor.class})
public class UserControllerTests {

    @MockBean
    private UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void findUserById_테스트() throws Exception {

        UserDto userDto = new UserDto();
        userDto.setUsername("sa1341");
        userDto.setPassword("wnsdud2");

        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(1);
        responseDto.setUsername("sa1341");

        //given
        given(userService.findUser(any())).willReturn(responseDto);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(userDto);

        //when
        ResultActions resultActions = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andDo(print());

        //then
        resultActions
                     .andExpect(status().isOk())
                     .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                     .andExpect(jsonPath("$.username", is("sa1341")))
                     .andDo(print());
    }
}
```

@WebMvcTest 어노테이션이 사용되면 스프링 WebApplication 관련된 Bean들만 등록하기 때문에 통합 테스트보다 빠르고, 통합 테스트를 진행하기 어려운 테스트도 진행 할 수 있는 장점이 있습니다. 예를 들어서 Third Party 모듈을 연계할 경우에는 Mock이라는 가짜 객체를 만들어서 임의로 테스트가 가능합니다.

단점이라면 요청에서 응답까지 모든 테스트를 Mock 기반으로 테스트하기 때문에 실제 환경에서는 정상적인 동작을 장담할 수 없습니다.
 

위의 코드를 보면 given / when / then 절로 나누어서 테스트를 수행하였습니다.

만약 서비스 계층이 완전히 구현되지 않은 상태에서 임의로 테스트를 하고 싶다면 스프링 부트에서 제공해주는 mockito를 사용하여 가짜 서비스 객체를 주입해서 테스트를 할 수 있습니다.


1. given에서는 가짜 서비스 mock 객체로 반환 값을 처리합니다.

```java
given(userService.findUser(any())).willReturn(responseDto);
```

>
given() 메서드에서는 Controller에서 UserService의 findUser() 메서드를 호출 할 경우 any()를 주었기 때문에 어떤 값이 들어와도 위에서 명시적으로 선언한 UserResponseDto 객체를 반드시 리턴하도록 설정하였습니다.

2. when에서 MockMvc 객체를 사용하여 JSON String 데이터를 전달하여 미리 정의된 객체를 반환받아서 처리합니다.

3. then에서 요청에 대한 응답 값을 검증합니다.


#### 실행 결과
![image](https://user-images.githubusercontent.com/22395934/115011572-c08d8200-9ee9-11eb-99a9-3f60d89180d7.png)

