## 클린 코더스 강의 1. OOP 

백명석 선생님의 클린코드라는 강의가 나온지 6년이나 되었지만, 이제 막 프로그래밍 세계에 입문한 저한테는 정말 배울게 많은 강의였습니다. 가장 기억에 남는 말씀은 개발자들이 많이하는 실수는 객체지향 프로그래밍을 할때 기능을 중심으로 생각하는게 아니라 해당 클래스가 가지고 있는 데이터 중심으로 사고를 한다는거 였습니다.

에를들어 글이나 기사를 작성하는 서비스에 대한 네이밍을  WriteArticleService가 아니고 ArticleService라고 작명한는 것도 기능이 아니라 데이터 중심으로 생각을 해서 발생하는 문제입니다. 

1강의 핵심은 돌아가는 코드도 중요하지만 사람이 읽을 수 있는 코드에 초점을 맞춘 강의였습니다. 리펙토링을 하는 습관을 항상 가져야되고... 아무리 바쁘더라도 리펙토링을 미루면 안됩니다. 개발을 빨리 한다고 해도 코드가 지저분하고 돌아가는데만 신경을 쓴다면 나중에 유지보수하는데 상당한 골치를 겪을 확률이 높습니다. 

이번 시간에는 공통된 데이터나 프로세스를 제공하는 객체들을 하나의 타입(인터페이스)로 추상화 하는 코드에 대해서 배웠습니다.

![스크린샷 2019-09-25 오후 9 02 23](https://user-images.githubusercontent.com/22395934/65599090-00270d00-dfd8-11e9-82a8-8f4ded70500c.png)

위의 클래스 다이어그램에서 해당 객체들의 공통점은 바로 로그를 수집하고 iterator() 메소드를 이용해서 수집한 로그를 반복처리하는 역할을 하는 객체입니다.

만약 이 로직을 변경하지 않고 로그 수집 방법을 변경하기 위해서는 인터페이스를 재사용 하는 것입니다. 인터페이스를 재사용하면 Client는 구현 변경에 대해서 영향을 받지 않습니다. 한마디로 추상화를 통해 유연함을 얻기 위한 규칙입니다. 

### concrete class를 직접 사용하는 경우
```java
public class FlowController{

    private final Parser parser = new Parser();

    public void process(){
        FileLogCollector collector = new FileCollector();

        FileLogSet logset = collector.collect();

        Iterator<String> it = logSet.iterate();

        FileLogWriter writer = new FileLogWriter();

        for(String line = it.next; line != null;){
            String parsedLine = parser.parse(line);
            writer.write(parsedLine);
        }

    }
}
```
### interface 추상화
```java
public class FlowController{

    private final Parser parser = new Parser();

    public void process(){
        LogCollector collector = new FileCollector();

        LogSet logset = collector.collect();

        Iterator<String> it = logSet.iterate();

        LogWriter writer = new FileLogWriter();

        for(String line = it.next; line != null;){
            String parsedLine = parser.parse(line);
            writer.write(parsedLine);
        }

    }
}
```

위의 소스에서는 LogCollector, LogSet을 interface로 두어서 타입을 추상화 하여 클라이언트에게 구체적인 모습은 드러내지 않고 변경을 유연하다는 걸 보여줍니다.

아래에 백명석 강사님이 설명하신 변경에 유연한 코드를 따라서 만들어본 예제입니다.
mills 단위로 경과시간을 구해주는 ProceuduralStopWatch가 클래스를 설계하였습니다.

```java
// 스탑워치의 역할을 해야하는 클래스
public class ProceduralStopWatch {
    
    private long startTime;
    private long stopTime;

    public long getElapsedTime(){
        return stopTime - startTime;
    }
}
```


```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProceduralStopWatchTest {

    private long expectedElapsedTime = 100l;

    @Test
    public void should_return_elapsed_mills() {
        
        ProceduralStopWatch stopWatch = new ProceduralStopWatch();
        //mills 단위로 인스턴스 변수 초기화
        stopWatch.startTime = System.currentTimeMillis();

        doSomething();

        stopWatch.stopTime = System.currentTimeMillis();
       
         assertThat(elapsedTime, is(greaterThanOrEqualTo(expectedElapsedTime);
    
    }
}
```
위의 코드를 보면 doSomething() 메소드가 얼마나 걸리는지 밀리타임으로 경과된 시간을 구하는 프로그램입니다. 만약 여기서 nano 단위로 경과된 시간을 구하라는 요구사항이 추가 된다면 데이터 구조 변경이 유발됩니다.

```java
public class ProceduralStopWatch {
    
    private long startTime;
    private long stopTime;
    private long startNanoTime;
    private long stopNanoTime;


    public long getElapsedTime(){
        return stopTime - startTime;
    }

    public long getElapsedNanoTime(){
        return stopNanoTime - startNanoTime;
    }   
}
```

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProceduralStopWatchTest {

    private long expectedElapsedTime = 100l;

    @Test
    public void should_return_elapsed_mills() {
        
        ProceduralStopWatch stopWatch = new ProceduralStopWatch();
        //mills 단위로 인스턴스 변수 초기화
        stopWatch.startTime = System.nanoTime();

        doSomething();

        stopWatch.stopTime = System.nanoTime();
       
        long elapsedTime = stopWatch.getElapsedNanoTime();

        assertThat(elapsedTime, is(greaterThanOrEqualTo(expectedElapsedTime * (long)pow(10, 6))));
    
    }
}
```

 nano 단위의 경과 시간을 구하는 기능이 추가되었을 뿐인데 해당 데이터를 사용하는 모든 코드를 수정해야 합니다. 프로젝트 규모가 커질수록 이렇게 수정하는데 많은 시간이 할애됩니다. 

 객체지향으로 해당 클래스를 설계한다면 다음과 같이 클라이언트에 영향을 안마치는 코드를 짤수가 있습니다.
 
 ```java
public class ProceduralStopWatch {
    
    private long startTime;
    private long stopTime;


    public void start(){
        this.startTime = System.nanoTime();
    }

    public void stop(){
        this.stopTime = System.nanoTime();
    }

    public Time getElapsedTime(){
        return new Time(stopTime - startTime);
    }
}
 ```

```java
public class Time(){

    private long nano;

    public Time(long nano){
        this.nano = nano;
    }

    public void getMilliTime(){
        return (long)(nano / pow(10, 6));
    }

    public void geNanoTime(){
        return nano;
    }
    
}
```

```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProceduralStopWatchTest {

    private long expectedElapsedTime = 100l;

    @Test
    public void should_return_elapsed_mills() {
        
        ProceduralStopWatch stopWatch = new ProceduralStopWatch();
        
        // startTime 필드에 값을 할당하지 않고 기능 실행
        stopWatch.start();

        doSomething();
       
        // stopTime 필드에 값을 할당하지 않고 기능 실행
        stopWatch.stop();
       
        Time time = stopWatch.getElapsedTime();    

         assertThat(time.getNanoTime, is(greaterThanOrEqualTo(expectedElapsedTime * (long)pow(10, 6))));
    
    }
}
```

이렇게 내부적으로 구현 내용을 감추게 되면 클라이언트에 영향을 안 미치게 됩니다.
Tell, Don't Ask라는 뜻으로 데이터를 요청해서 변경하고 저장하라고 하지말고 무슨 기능을 실행하라는 뜻입니다. 데이터를 잘 알고 있는 객체에게 기능을 수행하라고 요청하면 클라이언트 입장에서는 그 객체가 어떤 방법을 수행해도 원하는 값만 잘 던져주면 되는거죠...

> 마지막으로 객체는 각각 역할을 가지고 있습니다. 역할이란 객체가 가지는 책임들의 집합을 의미합니다. 항상 객체지향적으로 사고하는 습관을 기르기 위해서 데이터보다 기능중심으로 먼저 생각해야 된다는걸 숙지해야 합니다.





