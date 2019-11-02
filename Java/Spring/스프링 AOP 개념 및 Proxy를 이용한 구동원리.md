안녕하세요. 
이전 포스팅에서 스프링-Mybatis 모듈을 연동하는 간단한 실습을 진행했다면,이번엔 @Aspect를 이용한 스프링 AOP의 개념과 구동원리에 대해서 공부를 하였습니다.
웹 서비스를 운영하면서 다양한 핵심 비즈니스 로직이 존재하게 되는데 그때마다 고객이 해당 비즈니스 서비스를 호출할때마다 보안인증, 트랜잭션 처리, 로깅같은 꼭 필요하지만 중요하지 않는 공통기능의 코드를 작성할 필요성을 느끼게 됩니다. 그때마다 비즈니스 로직을 가진 메소드에 해당 공통기능의 코드를 넣게 된다면 가독성과 유지보수성 측면에서 좋지 않다고 생각하기 때문에 이런경우 스프릥 AOP를 이용하면 좋을거 같다 생각하여 리뷰하게 되었습니다.

## 스프링 AOP(Aspect Oriented Programming)
AOP는 관점지향 프로그래밍이라는으로 "기능을 핵심 비즈니스 기능과 공통기능으로 '구분'하고,모든 비즈니스 로직에 들어가는 공통기능의 코드를 개발자의 코드 밖에서 필요한 시점에 적용하는 프로그래밍 방식입니다. 이게 무슨 강아지같은 소리인지... 처음에는 이해가 안됬지만.. 역시 코딩은 해보는게 답인거 같아서... 그냥 한번 뭐라도 해보자는 마인드로 간단하게 구글링해본 결과 AOP에 대한 정말 퀄리티가 훌륭한 포스팅들이 넘쳐 흘렀습니다. 
컨시브이가 주특기인 저한테는 그중에서 정말 따라하기 쉬운 간단한 코드를 실습하면서 AOP에 대한 기본적인 개념에 대해서 공부하였습니다.


### AOP란?

- 로깅, 예외, 트랜잭션 처리 같은 코드들은 횡단 관심(Crosscutting Concerns)
- 핵심 비즈니스 로직은 핵심 관심(Core Concerns)

![스크린샷 2019-05-25 오후 10 54 53](https://user-images.githubusercontent.com/22395934/58370336-3415f700-7f40-11e9-9060-02a8a0e0912c.png)

>> AOP는 핵심관심과 횡단 관심을 완벽하게 분리할 수 없는 OOP의 한계를 극복하도록 도와줍니다.



### AOP 용어
AOP 소스예제를 살펴보기 전에 간단하게 용어정리를 해보았습니다.

조인포인트(Joinpoint): 클라이언트가 호출하는 모든 비즈니스 메소드, 조인포인트 중에서 포인트컷이 되기 때문에 포인트컷의 후보라고 할 수 있습니다.

포인트컷(Pointcut): 특정 조건에 의해 필터링 된 조인포인트, 수많은 조인포인트 중에 특정 메소드에서만 공통기능을 수행시키기 위해 사용됩니다.

어드바이스(Advice): 공통기능의 코드, 독립된 클래스의 메소드로 작성합니다.

위빙(Weaving): 포인트컷으로 지정한 핵심 비즈니스 로직을 가진 메소드가 호출될 때, 어드바이스에 해당하는 공통기능의 메소드가 삽입되는 과정을 의미합니다. 위빙을 통해서 공통기능과 핵심 기능을 가진 새로운 프록시를 생성하게 됩니다.

Aspect: 포인트컷과 어드바이스의 결합입니다. 어떤 포인트컷 메소드에 대해 어떤 어드바이스 메소드를 실행할지 결정합니다.

아래 표는 어드바이스의 동작 시점입니다.

|  <center>동작시점</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| Before | <center>메소드 실행 전에 동작 </center> 
| After | <center>메소드 실행 후에 동작 </center> 
| After-returning | <center>메소드가 정상적으로 실행된 후에 동작</center> 
| After-throwing | <center>예외가 발생한 후에 동작</center> 
| Around | <center>메소드 호출 이전, 이후, 예외발생 등 모든 시점에서 동작<center> 

### 포인트컷 표현식
포인트컷을 이용하면 어드바이스 메소드가 적용될 비즈니스 메소드를 정확하게 필터링 할 수 있습니다.

#### 지시자(PCD, AspectJ pointcut designators)의 종류

execution: 가장 정교한 포인트컷을 만들수 있고, 리턴타입 패키지경로 클래스명 메소드명(매개변수)
within: 타입패턴 내에 해당하는 모든 것들을 포인트컷으로 설정
bean: bean이름으로 포인트컷

|  <center>표현식</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| * | <center>모든 리턴타입 허용 </center> 
| void | <center>리턴타입이 void인 메소드 선택 </center> 
| !void | <center>리턴타입이 void가 아닌 메소드 선택</center> 

#### 패키지 지정
|  <center>표현식</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| com.jun.demo.controller | <center>정확하게 com.jun.demo.controller 패키지만 선택 </center> 
| com.jun.demo.controller\.\. | <center>com.jun.demo.controller 패키지로 시작하는 모든 패키지 선택 </center> 

#### 클래스 지정
|  <center>표현식</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| MemberDTO | <center>정확하게 MemberDTO 클래스만 선택 </center> 
| *DTO | <center>이름이 DTO오로 끝나는 클래스만 선택 </center> 
| BaseObject+ | <center>클래스 이름 뒤에 '+'가 붙으면 해당 클래스로부터 파생된 모든 자식 클래스 선택, 인터페이스 이름 뒤에 '+'가 붙으면 해당 인터페이스를 구현한 모든 클래스 선택</center> 

#### 메소드 지정
|  <center>표현식</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| *(\.\.) | <center>모든 메소드 선택 </center> 
| update*(..) | <center>메소드명이 update로 시작하는 모든 메소드 선택 </center>


#### 매개변수 지정

|  <center>표현식</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| (\.\.) | <center>모든 매개변수</center> 
| (*) | <center>반드시 1개의 매개변수를 가지는 메소드만 선택 </center> 
| (com.jun.demo.dto.MemberDTO) | <center>매개변수로 MemberDTO를 가지는 메소드만 선택. 꼭 풀패키지명을 명시해줘야 함</center> 
| (!com.jun.demo.dto.MemberDTO) | <center>매개변수로 MemberDTO를 가지지 않는 메소드만 선택</center> 
| (Integer,..) | <center>한개 이상의 매개변수를 가지되, 첫번째 매개변수의 타입이 Integer인 메소드만 선택</center> 
| (Integer, *) | <center>반드시 두 개의 매개변수를 가지되, 첫번째 매개변수의 타입이 Integer인 메소드만 선택</center>


## JoinPoint 인터페이스

어드바이스 메소드를 의미있게 구현하려면 클라이언트가 호출한 비즈니스 메소드의 정보가 필요합니다. 예를들면 예외가 발생하였는데, 예외발생한 메소드의 이름이 무엇인지 등을 기록할 필요가 있을 수 있습니다. 이럴때 JoinPoint 인터페이스가 제공하는 유용한 API들이 있습니다.

|  <center>메소드</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| Signature getSignature() | <center>클라이언트가 호출한 메소드의 시그니처(리턴타입, 매개변수) 정보가 저장된 Signature 객체를 리턴 </center> 
| Object getTarget() | <center>클라이언트가 호출한 비즈니스 메소드를 포함하는 비즈니스 객체를 리턴 </center> 
| Object[] getArgs() | <center> </center> 


### Sinature API

|  <center>메소드</center> |  <center>설명</center> | 
|:--------|:--------:|--------:|
| String getName() | <center>클라이언트가 호출한 메소드 이름 리턴 </center> 
| String toLongString() | <center>클라이언트가 호출한 비즈니스 메소드의 리턴타입, 이름, 매개변수(시그니처)를 패키지 경로까지 포함하여 리턴) </center> 
| String toShortString() | <center>클라이언트가 호출한 메소드 시그니처를 축약한 문자열로 리턴</center> 
| String getDeclaringTypeName() | <center>클라이언트가 호출한 메소드를 가지는 클래스 풀패키지명을 리턴 </center> 


## AOP 코드예제
간략하게 스프링 AOP 관련해서 용어정리를 하였습니다. 이제 스프링 AOP가 적용된 간단한 소스코드를 살펴보겠습니다. 

해당코드는 간단하게 @RestController를 어노테이션을 적용한 controller에서 클라이언트로부터 요청이오면 해당요청에 매핑되는 메소드를 호출할때마다 얼마나 빠르게 응답하는지 확인하기 위해서 공통모듈로 로깅과 StopWatch 객체를 이용하여 메소드 리턴 시간을 측정하는 것을 구현해보았습니다.

```java
mport org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LogAspect {

    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //ProceedingJoinPoint 클래스는 JoinPoint 인터페이스를 상속하는 인터페이서.. 인자는 스프링 컨테이너가 넘겨준다.
    @Around("execution(* com.jun.demo.controller.HelloController.*(..))") //포인트컷
    public Object logging(ProceedingJoinPoint pjp) throws Throwable{

        StopWatch stopWatch = new StopWatch();
        
        stopWatch.start();
        logger.info("start -" + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());
        Object result = pjp.proceed();
        logger.info("finished -" + pjp.getSignature().getDeclaringTypeName() + " / " + pjp.getSignature().getName());

        stopWatch.stop();
        logger.info("Timer Stop - Elapsed time :" + stopWatch.getTotalTimeMillis());

        return result;
    }
}

```

```java
@RestController
public class HelloController {

    @GetMapping("/sample")
    public SampleVO makeSample(){

        SampleVO vo = new SampleVO();
        vo.setVal1("v1");
        vo.setVal2("v2");
        vo.setVal3("v3");
        System.out.println(vo);

        return vo;
    }

}

```


결과화면
![스크린샷 2019-05-25 오후 11 45 37](https://user-images.githubusercontent.com/22395934/58370929-40ea1900-7f47-11e9-96dc-626c7100acff.png)

LogAspect는 AOP를 정의하는 클래스로 @Aspect, @Componet로 이클래스가 AOP가 바라보는 관점을 정의하고 스프링 컨테이너가 관리하는 bean으로 등록하는 것을 정의하였습니다.


@Around는 위의 AOP용어 설명처럼 어드바이스 동작 시점을 정의하였습니다.
저는 메소드 실행 전/후에 공통기능을 핵심 비즈니스 로직에 적용하였습니다.
```java
 @Around("execution(* com.jun.demo.controller.HelloController.*(..))")
```
@Around에 표현식을 사용하였는데 지시자로 execution을 사용하여 정교한 포인트컷을 만들었습니다. 먼저 *은 리턴타입을 의미하는데 모든 리턴타입을 허용한다는 의미이고, 두번째로는 패키지를 지정, 세번째는 클래스 HelloController로 지정하였습니다.
그리고 포인트컷으로 지정할 메소드와 매개변수를 지정하였는데 *은 해당 클래스의 모든 메소드를 포인트컷으로 지정하고, (\.\.)은 매개변수의 개수와 상관없이 모든 매개변수를 지정한다는 의미입니다.

공통기능을 정의한 메소드 logging에 매개변수로 ProceedingJoinPoint 객체는
타켓대상의 핵심 관심에 대한 정보를 제공하는 역할을 하고 있습니다. 그리고 JoinPoint 인터페이스를 상속하는 인터페이스로 스프링 컨테이너에서 제공하고 있습니다.
이때 Around 어드바이스만 다른 어드바이스와 약간 다른데, ProceedingJoinPoint 객체를 인자로 선언해야합니다. 그렇지 않으면 에러가 발생합니다.


위의 코드는 Object 객체를 리턴하도록 하였는데, 그 이유는 스프링 AOP 동작원리에 있습니다. 스프링 AOP는 Proxy(대행자)를 통해서 수행하게 됩니다.
즉 proceed()에서 정상적으로 메서드를 실행한 후 리턴 값을 주는데 가로채서 어떤 action 을 한 후에 기존 리턴 값을 되돌려 주지 않으면 가로챈 프록시가 결과 값을 변경하거나 지워버린것과 다름이 없습니다. 위의 코드는 단순하게 전/후로 시간을 측정하여 로깅을 찍어주고 기존 비즈니스 로직이 실행될 수 있게 pjp.proceed();를 호출하였습니다.



## AOP 동작원리

### 프록시(Proxy)를 이용하여 AOP를 구현

![스크린샷 2019-05-25 오후 11 21 28](https://user-images.githubusercontent.com/22395934/58370635-d97e9a00-7f43-11e9-90d2-da22954fa302.png)

프록시는 타겟을 감싸서 타겟의 요청을 대신 받아주는 랩핑(Wrapping) 오브젝트입니다. 
호출자(클라이언트)에서 타겟을 호출하게 되면 타겟이 아닌 타겟을 감싸고 있는 프록시가 호출되어, 타겟 메소드 실행전에 선처리, 타겟 메소드 실행 후, 후처리를 실행시키도록 구성되어있습니다.

 
스프링 AOP에서는 런타임시에 Weaving을 통해서 프록시 객체를 생성하게 됩니다.
생성방식으로는 첫번째로 JDK Dynamic Proxy가 있는데 타겟대상이 Interface를 구현하는 클래스면 인터페이스를 기반으로 프록시 객체를 생성하기 때문에 인터페이스에 정의되지 않는 메서드에 대해서는 AOP가 적용되지 않는 단점이 있습니다.

두번째로는 CGLIB가 있는데 타켓대상이 인터페이스를 구현하고 있지 않고 바로 클래스를 사용한다면, 스프링은 CGLIB를 이용하여 클래스에 대한 프록시 객체를 생성합니다. CBLIB는 대상 클래스를 상속받아 구현합니다. 따라서 클래스가 final인 경우에는 프록시를 생성할 수 없습니다.

좀더 구체적으로 설명하기에는 아직 공부를 못했기 때문에 이 부분에 대해서는 다음에 더 준비해서 포스팅하겠습니다.

프록시 객체를 자세하게 까보지 않아서 구체적인 동작원리는 모르겠지만 맴버로 타켓객체와 Aspect로 정의된 공통모듈을 가지는 객체를 가지고 았지 않을까 뇌피셜로 생각만 해봤습니다. 클라이언트의 요청이 오면 포인트컷과 어드바이스가 결합하는 Weaving과정에서 새로운 Proxy객체가 생성되면서 공통기능과 타켓의 핵심 비즈니스로직을 수행하지 않을까 생각도 해봤습니다.

 프록시를 이용해서 보조업무를 처리하는 예제 포스팅을 보게 되면서 조금이나마 프록시에 대해서 이해하게 되었고, AOP 개념이 스프링에 한정되지 않는것을 알게 되었습니다.

결론은.. AOP의 장점은 이렇습니다.
- 단순 복사 붙여넣기 -> 핵심 비즈니스 로직에 공통기능의 코드 중복이 많아져 코드분석과 유지보수를 어렵게 만듭니다.
- AOP를 통해 부가적인 공통코드를 효율적으로 관리합니다.


[참조: https://ooz.co.kr/201](https://ooz.co.kr/201)
[참조: https://jeong-pro.tistory.com/171](https://jeong-pro.tistory.com/171)
