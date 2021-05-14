# 스프링 부트 Custom Valid 어노테이션 기능 구현


프론트에서 사용자가 API 요청을 위해 전달한 파라미터들을 검증하겠지만, 표현 계층 (controller)에서도 검증을 하여 안정성을 높이는 것도 중요한 부분이라고 생각합니다.

이 경우에는 javax.validation에서 제공해주는 다양한 검증 어노테이션을 사용할 수 있지만, 업무 비즈니스 환경에 특화된 `custom 검증` 어노테이션이 필요한 경우가 있습니다. 

#### 서비스 로직에 파라미터를 검증하는 로직을 구성하면 아래와 같은 `단점`들이 존재합니다.

>
- 유효성 검증 하는 코드의 길이가 너무 길어집니다.
- 서비스 로직에 대해서 방해가 됩니다.
- 흩어져 있는 경우 어디서 검증되었는지 찾기 힘듭니다.
- 검증 로직이 변경되는 경우 테스트 코드 등, 전체 로직이 흔들릴 수 있습니다.


## 1. 프로젝트 구성

#### 기술 스펙

- 코틀린
- 스프링 부트


#### build.gradle.kts

아래 build 스크립트에서 starter-validation 의존성을 다운받습니다.

```kotlin
dependencies {	
	api("org.springframework.boot:spring-boot-starter-validation")
}
```

사용자가 게시판을 작성하기 위해 API를 호출하는 경우 `작성자, 제목, 내용`의 데이터들을 JSON 포맷으로 전달받기 위한 DTO 객체입니다.

#### BoardDto

```kotlin
data class BoardDto(
    @field:NotBlank
    var author: String,

    @field:NotBlank
    @field:Size(min = 2, max = 20, message = "제목은 2~20자 이내여야 합니다.")
    var title: String,

    @field:NotBlank
    @field:Size(min = 2, max = 30, message = "본문은 2~30자 이내여야 합니다.")
    var content: String
}
```

> 일반적으로 Kotlin에서 검증 어노테이션을 적용할 경우 필드에 적용하도록 `@field:~`로 명시해줘야 합니다.

위에 `@NotBlank`, `@Size`는 `javax.validation` 패키지에서 제공해주는 기본 어노테이션으로 개발자가 직접 정의하지 않고 편하게 사용할 수 있습니다.

여기에 날짜(DateTime)를 저장할 변수를 추가할 경우 날짜 형식이 패턴에 맞게 파라미터로 전달되는지 확인하는 검증 로직이 필요합니다. 이럴 때 custom 검증 어노테이션을 만들어서 사용할 수 있습니다.

## 2. Custom 검증 어노테이션 작성

#### StringFormatDateTime.class

```kotlin
@Constraint(validatedBy = [StringFormatDateTimeValidator::class])
@Target(
    AnnotationTarget.FIELD
)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class StringFormatDateTime(
    val pattern: String = "yyyy-MM-dd HH:mm:ss",
    val message: String = "시간 형식이 유효햐지 않습니다.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
```

JSON에서 날짜(DateTime) 변수에 적용할 `StringFormatDateTime 클래스`를 정의하였습니다. 

#### custom constraint 어노테이션에는 다음과 같은 것들이 필요합니다.

>
- parameter message: ValidationMessages.properties에서 특정 property key를 가리키는 메시지 (제약 조건 위반시 메시지로 사용된다.)
- parameter groups: 유효성 검사가 어떤 상황에서 실행되는지 정의할 수 있는 매개 변수 그룹.
- parameter payload: 유효성 검사에 전달할 payload를 정의할 수 있는 매개 변수.
- @Constraint: ConstraintValidator interface 구현을 나타내는 어노테이션
- @MustBeDocumented는 API의 일부분으로 문서화하기 위해 사용합니다.

`pattern` 변수 값은 String 타입의 날짜 패턴을 `yyyy-MM-dd HH:mm:ss`로 정의하였습니다. 


## 3. Custom Validator 구현체 작성

실제 @StringFormatDateTime만 작성한다고 검증이 끝난게 아니고 실제로 Controller에서 파라미터로 들어온 String 타입의 날짜 값을 검증하기 위한 Custom Validator 구현체를 작성해야 합니다.

```kotlin
class StringFormatDateTimeValidator: ConstraintValidator<StringFormatDateTime, String> {

    private var pattern: String? = null
    
    // pattern 값을 받아오기 위해 구현함. 필수 구현부는 아닙니다.
    override fun initialize(constraintAnnotation: StringFormatDateTime?) {
        this.pattern = constraintAnnotation?.pattern
    }

    // 정상이면 true, 비정상이면 false
    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        return try {
            LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern))
            true
        } catch (e: Exception) {
            false
        }
    }
}
```

ConstraintValidator 인터페이스를 구현하여 isValid() 메서드안에서 LocalDateTime 클래스의 parse 메서드로 파라미터가 @StringFormatDateTime 어노테이션에서 정의한 패턴이랑 일치하는지 확인하는 검증 로직을 작성합니다.

> initialize() 메서드는 편의상 constraintAnnotation(StringFormatDateTime)에서 정의한 패턴을 가져오기 위해 구현하였습니다. 반드시 구현할 필요는 없습니다.


```kotlin
data class BoardDto(
    @field:NotBlank
    var author: String,

    @field:NotBlank
    @field:Size(min = 2, max = 20, message = "제목은 2~20자 이내여야 합니다.")
    var title: String,

    @field:NotBlank
    @field:Size(min = 2, max = 30, message = "본문은 2~30자 이내여야 합니다.")
    var content: String,

    @field:StringFormatDateTime(pattern = "yyyy-MM-dd HH:mm:ss", message = "패턴이 올바르지 않습니다.")
    var createdAt: String
)
```

이제 위 코드에서 Custom 검증 어노테이션인 @StringFormatDateTime을 BoardDto 객체의 createdAt 변수에 적용하였습니다.


#### BoardApi.class

```kotlin
@RequestMapping("/api/v1/boards")
@RestController
class BoardApi(
    @Autowired private val boardService: BoardService,
    @Autowired private val boardSearchService: BoardSearchService
) {

    @PostMapping
    fun save(@RequestBody @Valid boardDto: BoardDto, bindingResult: BindingResult): ResponseEntity<String> {
        boardService.save(boardDto)
    return ResponseEntity.ok(HttpStatus.OK.toString())
}
```

게시글 작성하는 API입니다. `/api/v1/boards`로 요청 시 Jackson2HttpMessageConverter에서 ObjectMapper로 JSON String 포맷을 스프링에서 정의한 DTO 클래스로 매핑하기 전에 검증을 수행합니다.

검증 실패에 대한 예외처리는 전역으로 처리하기 위해 별도로 GlobalExceptionController 클래스를 작성하여 테스트를 수행하였습니다.

## 4. Custom 검증에 대한 에러 응답 처리하기

#### GlobalExceptionController

Error Response를 이쁘게 보여주기 위해 별도 Response DTO 객체를 정의하여 사용자에게 응답해주도록 구현하였습니다.


```kotlin
class ErrorResponse (

    @field:JsonProperty("result_code")
    var resultCode: String? = null,

    @field:JsonProperty("http_status")
    var httpStatus: String? = null,
    var message: String? = null,
    var path: String? = null,
    var action: String? = null,
    var timestamp: LocalDateTime? = null,
    var errors: MutableList<Error>? = mutableListOf()
)

data class Error(
    var field: String? = null,
    var rejectedValue: String? = null,
    var message: String? = null
)
```

```kotlin
@RestControllerAdvice
class GlobalExceptionController {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException, request: HttpServletRequest): ResponseEntity<ErrorResponse> {
        logger.error("handleMethodArgumentNotValidException", e)
        val bindingResult = e.bindingResult
        var errors = mutableListOf<Error>()

        bindingResult.fieldErrors.forEach {
            fieldError ->
            val error = Error().apply {
                this.field = fieldError.field
                this.rejectedValue = fieldError.rejectedValue.toString()
                this.message = fieldError.defaultMessage
            }
            errors.add(error)
        }

        val errorResponse = ErrorResponse().apply {
            this.resultCode = "FAIL"
            this.httpStatus = HttpStatus.BAD_REQUEST.value().toString()
            this.message = "요청에 에러가 발생하였습니다."
            this.path = request.requestURI
            this.action = request.method
            this.timestamp = LocalDateTime.now()
            this.errors = errors
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
```

위의 Custom 검증 실패 시에 처리하는 에러에 대해서 코드를 길게 작성하였는데 테스트를 위해서 간단하게 작성하였습니다. 실무에서는 ErrorCode들을 Enum 타입으로 상태값, 메시지, 상태코드들을 정의하여 간결하게 코드를 작성하여 에러 메시지를 전달 할 수도 있습니다.

### 테스트 수행 결과

![image](https://user-images.githubusercontent.com/22395934/118254384-025c1900-b4e6-11eb-9aeb-985bdff20340.png)
