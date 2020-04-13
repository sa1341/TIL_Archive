## DI(Dependency Injection)와 서비스 로케이터

밥 아저씨로 유명한 로버트 C 마틴은 소프트웨어를 두 개의 영역으로 구분해서 설명하고 있는데, 한 영역은 고수준 정책 및 저수준 구현을 포함한 어플리케이션 영역이고 또 다른 영역은 어플리케이션이 동작하도록 각 객체들을 연결해 주는 메인 영역입니다. 본 장에서는 어플리케이션 영역과 메인 영역에 대해 살펴보고, 메인 영역에서 객체를 연결하기 위해 사용되는 방법인 DI(dependency injection: 의존성 주입)와 서비스 로케이터에 대해 알아보도록 하겠습니다.


## 어플리케이션 영역과 메인 영역

간단한 비디오 포멧 변환기를 개발한다고 할 때, 요구 사항은 다음과 같습니다.

- 파일의 확장자를 이용해서 비디오 파일의 포맷을 변환합니다.
- 변환 요청을 등록하면 순차적으로 변환 작업을 수행합니다.
- 비디오 형식의 변환 처리는 오픈 소스인 ffmpeg을 이용하거나 구매 예정인 변환 솔루션을 사용할 수 이썽야 합니다.

- 명령 행에서 변환할 원본 파일과 변환 결과로 생성될 파일을 입력합니다.

- 변환 작업을 요청하면 순차적으로 변환을 처리합니다.

위 기능을 제공하는데 있어 변화가 발생하는 부분은 다음의 두가지입니다.

- 변환 요청 정보 저장: 파일에 보관한다 또는 DB에 보관합니다.
- 변환 처리: ffmpeg을 사용합니다 또는 솔루션을 사용합니다.


transcoder 패키지는 비디오 변환을 위한 핵심 기능을 제공합니다. 작업을 순차적으로 처리하기 위한 JobQueue와 비디오 변환을 위한 Transcoder, 그리고 JobQueue에서 작업을 가져와 Transcoder를 실행하는 Worker가 존재합니다.

JobQueue와 Transcoder는 변화되는 부분을 추상화한 인터페이스로서, transcoder 패키지의 다른 코드에 영향을 주지 않으면서 확장할 수 있는 구조를 갖고 있습니다.
JobQueue는 파일을 이용하는 구현과 DB를 이용하는 구현의 두 가지가 제공되며, Transcoder 역시 ffmpeg과 솔루션을 이용하는 두 가지 버전의 구현이 제공됩니다. JobQueue와 Transcoder를 구현한 콘크리트 클래스들은 이 두 인터페이스를 상속받아 구현하였기 때문에, Worker 클래스는 이들 콘크리트 클래스에 의존하지 않습니다. (즉, 의존 역전 원칙을 적용하였습니다.)


Worker 클래스는 JobQueue에 저장된 객체로부터 JobData를 가져와 Transcoder를 이용해서 작업을 실행하는 책임이 있습니다. Worker 클래스 코드 구성은 다음과 비슷할 것입니다.


```java
public class Worker {

    public void run() {
        JobQueue jobQueue = ...; // JobQueue를 구합니다.
        Transcoder transcoder = ...; // Transcoder를 구합니다.
     

        while(someRunningCodition) {
            JobData jobData = jobQueue.getJob();
            transcoder.transcode(jobData.getSource(), jobData.getTarget());
        }
    }
}
```

Worker가 제대로 동작하려면 jobQueue나 Transcoder를 구현한 콘크리트 클래스의 객체가 필요합니다. 비슷하게 JobCLI 클래스도 다음 코드처럼 JobQueue에 작업 데이터를 넣어야 하는데, 이를 수행하려면 JobQueue를 구현한 객체를 구해야 합니다.

```java
public class JobCLI {
    public void interact() {
        printInputSourceMesssage();
        String source = getSourceFromConsole();
        printInputTargetMessage();
        String target = getTargetFromConsole();

        JobQueue jobQueue = ...; // JobQueue를 구합니다.
        jobQueue.addJob(new JobData(source, target));
    }
}
```

Worker와 JobCLI가 동작하려면 JobQueue와 Transcoder의 실제 객체를 구할 수 있는 방법이 필요합니다. 이를 위해 Locator라는 객체를 사용하기로 결정했다고 해봅시다. Locator 클래스는 아래 코드에서 보는 것처럼 사용할 객체를 제공해 줍니다.


```java
public class Worker {

    public void run() {
        JobQueue jobQueue = Locator.getInstance().getJobQueue();
        Transcoder transcoder = Locator.getInstance().getTranscoder();

        while(someRunningCondition) {
            ...
        }
    }
}

public class JobCLI {

    public void interact() {
        ...
        JobQueue jobQueue = Locator.getInstance().getJobQueue();
        jobQueue.addJob(new JobData(source, target));
        ...
    }
}
```

Locator 클래스는 Worker와 jobCLI가 사용할 JobQueue 객체와 Transcoder 객체를 제공하는 기능을 정의하고 있으며, 아래처럼 구현하였습니다.

```java
public class Locator {

    private static Locator instance;

    public static Locator getInstance() {
        return instance;
    }

    public static void init(Locator locator) {
        this.instance = locator;
    }

    private JobQueue jobQueue;
    private Transcoder transcoder;

    public Locator(JobQueue jobQueue, Transcoder transcoder) {
        this.jobQueue = jobQueue;
        this.transcoder = transcoder;
    }

    public JobQueue getJobQueue() { return jobQueue; }
    public Transcoder getTranscoder() { return transcoder; }
}
```

Locator 클래스는 Worker 클래스와 같은 패키지에 위치시켜야 합니다. 이렇게 같은 패키지에 위치시켜야하는 이유는 Locator를 다른 패키지에 위치시킬 경우 패키지 간 순환 의존이 발생하기 때문입니다. 만약에 Locator 클래스가 다른 패키지인 locator 패키지에 위치한다면 transcoder 패키지는 locator 패키지에 의존하게 되고, Locator 클래스가  JobQueue 타입과 Transcoder 타입에 의존하므로 다시 locator 패키지가 transcoder 패키지에 의존하게 됩니다. 이런 순환 의존이 발생할 경우 한 패키지의 변경이 다른 패키지에 영향을 줄 가능성이 높아지기 때문에, 순환 의존은 발생시키지 않는 것이 향후 유지 보수에 유리합니다.

Locator 객체를 이용해서 JobQueue 객체와 Transcoder 객체를 설정해 주면, Worker와 JobCLI는 설정한 객체를 사용하게 됩니다. 여기서 질문이 발생합니다. 그렇다면 과연 누가 Locator 객체를 초기화 해 줄 것인가? 그리고 JobCLI 객체와 Worker 객체를 생성하고 실행해 주는건 누구일까요?

드디어 메인(main) 영역이 출현할 차례가 되었습니다. 메인 영역은 다음 작업을 수행합니다.

- 어플리케이션 영역에서 사용될 객체를 생성합니다.
- 각 객체 간의 의존 관계를 설정합니다.
- 어플리케이션을 실행합니다.

이 어플리케이션을 위한 메인 영역 코드는 아래와 같이 작성할 수 있습니다.

```java
public class Main {

    public static void main(String[] args) {
        // 상위 수준 모듈인 transcoder 패키지에서 사용할
        // 하위 수준 모듈 객체 생성
        JobQueue jobQueue = new FileJobQueue();
        Transcoder trascoder = new FfmpegTrascoder();

        // 상위 수준 모듈이 하위 수준 모듈을 사용할 수 있도록 Locator 초기화
        Locator locator = new Locator(jobQueue, trascoder);
        Locator.init(locator);

        // 상위 수준 모듈 객체를 생서앟고 실행
        final Worker worker = new Worker();
        Thread t = new Thread(new Runnable() {
            public void run() {
                worker.run();
            }
        });

        JobCLI cli = new JobCLI();
        cli.interact();
    }
}
```

Main 클래스의 main() 메소드는 어플리케이션을 실행하는데 필요한 저수준 모듈 객체를 먼저 생성하고, Locator를 설정합니다. 이를 통해 Worker와 JobCLI는 FileJobQueue 객체와 FfmpegTranscoder 객체를 사용할 수 있게 됩니다.

메인 영역은 어플리케이션 영역의 객체를 생성하고, 설정하고, 실행하는 책임을 갖기 때문에, 애플리케이션 영역에서 사용할 하위 수준의 모듈을 변경하고 싶다면 메인 영역을 수정하게 됩니다. 예를 들어, FileJobQueue 객체 대신 DbJobQueue 객체를 사용하고 싶다면 메인 영역에서 FileJobQueue 대신 DbJobQueue 객체를 생성하고 조립해 주면 됩니다.

위 코드에서 알 수 있는 것은 모든 의존은 메인 영역에서 어플리케이션 영역으로 향한다는 것입니다. 반대의 경우인 어플리케이션 영역에서 메인 영역으로의  의존은 존재하지 않습니다. 이는 메인 영역을 변경한다고 해도 어플리케이션 영역은 변경되지 않는다는 것을 뜻합니다. 따라서 어플리케이션에서 사용할 객체를 교체하기 위해 메인 영역의 코드를 수정하는 것은 어플리케이션 영역에는 어떠한 영향을 끼지지 않습니다. 이번 포스팅에서는 객체들을 연결하기 위한 용도로 Locator 클래스를 사용했는데, Main 클래스는 이 Locator 클래스를 통해서 Worker 객체와 JobCLI 객체가 필요로 하는 객체를 설정했습니다.

Worker 객체와 JobCLI 객체는 Locator를 이용해서 필요한 객체를 가져온 뒤에 원하는 기능을 실행하였습니다. 이렇게 사용할 객체를 제공하는 책임을 갖는 객체를 서비스 로케이터라고 부릅니다.

서비스 로케이터 방식은 로케이터를 통해서 필요로 하는 객체를 직접 찾는 방식인데, 이 방식에는 몇 가지 단점이 존재합니다. 그래서, 서비스 로케이터를 사용하기보다는 외부에서 사용할 객체를 주입해 주는 DI방식을 사용하는 것이 일반적이다. 본 장에서는 먼저 DI가 무엇인지 알아보고, 그 뒤에서 서비스 로케이터와 비교해 보도록 하겠습니다.


## DI(Dependency Injection)을 이용한 의존 객체 사용

사용할 객체를 직접 생성할 경우, 아래 코드처럼 콘크리트 클래스에 대한 의존이 발생하게 됩니다.

```java
public class Worker {

    public void run() {
        JobQueue jobQueue = new FileJobQueue(); // DIP 위반
    }

}
```
콘크리트 클래스를 직접 사용해서 객체를 새엇ㅇ하게 되면 의존 역전 원칙을 위반하게 되며, 결과적으로 확장 폐쇄 원칙을 위반하게 됩니다. SOLID 원칙에서 설명했듯이 이는 변화에 경직된 유연하지 못한 코드를 만들게 됩니다. 또한, 서비스 로케이터를 사용하면 서비스 로케이터를 통해서 의존 객체를 찾게 되는데, 이 경우 몇가지 단점이 발생합니다.

이런 단점을 보완하기 위한 방법이 DI(Dependency Injection; 의존 주입)입니다. DI는 필요한 객체를 직접 생성하거나 찾지 않고 외부에서 넣어주는 방식입니다. DI 자체의 구현은 매우 간단한데, 사용할 객체를 전달받을 수 있는 방법을 제공하면 DI를 적용하기 위한 모든 준비가 끝납니다.

예를 들어, 아래 코드와 같이 Worker 클래스에 사용할 객체를 전달받을 수 있는 생성자를 추가하는 것으로 DI를 적용할 수 있게 됩니다.

```java
public class Worker {
    private JobQueue jobQueue;
    public Transcoder transcoder;

    // 외부에서 사용할 객체를 전달받을 수 있는 방법 제공
    public Worker(JobQueue jobQueue, Transcoder transcoder) {
        this.jobQueue = jobQueue;
        this.transcoder = transcoder;
    }

    public void run() {
        while(someRunningCodition) {
            JobData jobData = jobQueue.getJob();
            transcoder.transcode(jobData.getSource(), jobData.getTarget());
        }
    }
}
```

Worker 클래스와 비슷하게 JobCLI 클래스도 생성자를 통해서 사용할 JobQueue 객체를 전달받도록 수정할 수 있을 것입니다.

```java
public class JobCLI {

    private JobQueue jobQueue;

    public JobCLI(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void interact() {
        ...
        jobQueue.addJob(new JobData(source, target));
        ...
    }
}
```

생성자를 이용해서 의존 객체를 전달받도록 구현하면, 이제 메인 영역의 Main 클래스는 아래와 같이 바뀝니다.

```java
public class Main() {
    public static void main(String[] args) {

        // 상위 수준 모듈인 transcoder 패키지에서 사용할
        // 하위 수준 모듈 객체 생성
        JobQueue jobQueue = new FileJobQueue();
        Transcoder transcoder = new FfmpegTranscoder();

        // 상위 수준 모듈 객체를 생성하고 실행
        final Worker worker = new Worker(jobQueue, transcoder);

        Thread t = new Thread(new Runnable() {
            public void run() {
                worker.run();
            }
        });

        JobCLI cli = new JobCLI(jobQueue);
        cli.interact();
    }   
}
```

수정된 Main 클래스를 보면 Worker 생성자를 호출할 때, Worker 객체가 사용할 JobQueue 객체와 Transcoder 객체를 전달하고 있습니다. 동일하게 JobCLI 객체를 생성할 때에도, 이 객체가 사용할 JobQueue 객체를 전달하고 있습니다. 여기서 알 수 있는건 Worker 객체나 JobCLI 객체는 스스로 의존하는 객체를 찾거나 생성하지 않고, main() 메서드에서 생성자를 통해 이들이 사용할 객체를 주입한다는 점입니다. 이렇게 누군가가 외부에서 의존하는 객체를 넣어주기 때문에, 이런 방식을 의존 주입이라고 부르는 것입니다.

DI를 통해서 의존 객체를 관리할 때에는 객체를 생성하고 각 객체들을 의존 관계에 따라 연결해주는 조립 기능이 필요합니다. 위 코드에서 Main 클래스가 조립기의 역할을 함께 하고 있는데, 조립기를 별도로 분리하면 향후에 조립기 구현 변경의 유연함을 얻을 수 있습니다.
예를 들어, 조립기를 아래코드와 같이 구현했다고 합시다.

```java
public class Assembler {
    public void createAndWriter() {
        JobQueue jobQueue = new FileJobQueue();
        Transcoder transcoder = new FfmpegTranscoder();
        this.worker = new Worker(jobQueue, transcoder);
        this.jobCLI = new JobCLI(jobQueue);
    }

    public Worker getWorker() {
        return this.worker;
    }

    public JobCLI getJobCLI() {
        return this.jobCLI;
    }
    ...
}
```

Assembler 클래스의 createAndWire() 메서드는 어플리키에션 영역에서 사용할 객체를 생성하고 생성자를 이용해서 의존 객체를 전달해 주고 있습니다. 또한, 실행 대상이 되는 객체를 제공하기 위한 메서드인 getWorker()와 getJobCLI()를 제공하고 있습니다.

이제 Main 클래스는 Assembler에게 객체 생성과 조립 책임을 위임한 뒤에 Assembler가 생성한 Worker 객체와 JobCLI 객체를 구하는 방식으로 변경됩니다.

```java
public class Main {
    public static void main(String[] args) {
        Assembler assembler = new Assembler();
        assembler.createAndWire();
        final Worker worker = assembler.getWorker();
        Jobcli jobCli = assembler.getJobCLI();
        ...
    }
}
```

이렇게 객체 조립 기능이 분리되면, 이후에 XML 파일을 이용해서 객체 생성과 조립에 대한 정보를 설정하고, 이 XML 파일을 읽어와 초기화 해주도록 구현을 변경할 수 있을 것입니다.
이 대목에서 뭔가 떠오르는 것이 있을 것입니다. 그렇습니다 자바를 주로 사용하고 있다면, 웹 개발에서 많이 사용되는 스프링 프레임워크가 떠오를 것인데, 스프링 프레임워크가 바로 객체를 생성하고 조립해주는 기능을 제공하는 DI 프레임워크입니다.

## 생성자 방식과 설정 메소드 방식
DI를 적용하려면 의존하는 객체를 전달받을 수 있는 방법을 제공해야 하는데, 이 방법에는 크게 다음 두 가지 방식이 존재합니다.

- 생성자 방식
- 설정 메서드 방식

생성자 방식은 생성자를 통해서 의존 객체를 전달받는 방식입니다. 앞에서 봤던 예가 바로 생성자 방식을 사용했습니다.

```java
public class JobCLI {

    private JobQueue jobQueue;


    public JobCLI(jobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void interact() {
        ...
        this.jobQueue.addJob(jobData);
        ...
    }
}
```

생성자를 통해 전달받은 객체를 필드에 보관한 뒤, 메서드에서 사용하게 됩니다.

설정 메소드 방식은 메서드를 이용해서 의존 객체를 전달받습니다. 아래 코드는 설정 메서드 방식의 예입니다.


```java
public class Worker {

    private jobQueue jobQueue;
    private Trranscoder transcoder;

    public void setJobQueue(JobQueue jobQueue) {
        this.jobQueue = jobQueue;
    }

    public void setTranscoder(Transcoder transcoder) {
        this.transcoder = transcoder;
    }

    public void run() {
        while(someRunningCondition) {
            JobData jobData = jobQueue.getJob();
            transcoder.transcode(jobData.getSource(), jobData.getTarget());
        }
    }
}
```

setJobQueue() 메서드와 setTranscoder() 메서드는 파라미터로 전달받은 의존 객체를 필드에 보관하며, 다른 메서드에서는 필드를 사용해서 의존 객체의 기능을 실행합니다. 위 코드에서는 자바빈 프로퍼티 규약에 따라 리턴 타입이 void이고 메서드 이름이 setXXXX()의 형식을 갖도록 작성했지만, 다음과 같이 작성해도 무방합니다.

```java
public void configure(JobQueue jobQueue, Transcoder transcoder) {
    this.jobQueue = jobQueue;
    this.transcoder = transcoder;
}

// 메서드 체이닝이 가능하도록 기턴 타입을 void에서 Worker로 변경
public Worker setJobQueue(JobQueue jobQueue) {
    this.jobQueue = jobQueue;
    return this;
}

public Worker setTranscoder(Transcoder transcoder) {
    this.transcoder = transcoder;
    return this;
}
```

설정 메서드를 어떻게 구현할지 여부는 사용할 DI 프레임워크에 따라 달라질 수 있습니다.

예를 들어, 스프링 프레임워크의 초기 버전은 public void setSome(Some some) 형식의 설정 메서드를 지원했기에 한 번에 여러 의존 객체를 전달하려면 생성자 방식을 사용해야 했습니다.

생성자 방식이나 설정 메서드 방식을 이용해서 의존 객체를 주입할 수 있게 되었다면, 조립기는 생성자와 설정 메서드를 이용해서 의존 객체를 전달하게 됩니다.

```java
public class Assembler {
    public void createAndWire() {
        JobQueue jobQueue = new FileJobQueue();
        Transcoder transcoder = new FfmpegTranscoder();

        this.worker = new Worker();
        //  설정 메서드로 의존 객체를 받음
        this.worker.setJobQueue(jobQueue);
        this.worker.setTranscoder(transcoder);  

        // 생성자로 의존 객체 받음 
        this.jobCLI = new JobCLI(jobQueue);
    }

    public Worker getWorker() {
        return this.worker;
    }

    public JobCLI getJobCLI() {
        return this.jobCLI;
    }
    ...
}
```


### 각 방식의 장단점

DI 프레임워크가 의존 객체 주입을 어떤 방식까지 지원하느냐에 따라 달라지겠지만, 생성자 방식과 설정 메서드 방식 중에서 개발자들은 생성자 방식을 더 많이 선호합니다.

그 이유는 생성자 방식은 객체를 생성하는 시점에 필요한 모든 의존 객체를 준비할 수 있기 때문입니다. 생성자 방식은 생성자를 통해서 필요한 의존 객체를 전달받기 때문에, 객체를 생성하는 시점에 의존 객체가 정상인지 확인할 수 있습니다.

```java
public class JobCLI {
    private JobQueue jobQueue;

    public JobCLI(JobQueue jobQueue) {
        // 생성자 방식은 객체 생성 시점에서 의존 객체가 완전한지 
        // 확인할 수 있음
        if(jobQueue == null) 
            throw new IllegalArgumentException();
        
        this.jobQueue = jobQueue;
    }
}
```

생성 시점에 의존 객체를 모두 받기 때문에, 한 번 객체가 생성되면 객체가 정상적으로 동작함을 보장할 수 있게 됩니다.

```java
// 정상 생성인 경우
JobCLI jobCli = new JobCLI(jobQueue);
jobCli.interact(); // jobQueue 의존 객체가 존재합니다.

// 비정상 생성인 경우
JobCLI jobCli = new JobCLI(null); // 생성 시점에 익셉션 발생
jobCli.interact(); // 이 코드는 실행되지 않음
```

생성자 방식을 사용하려면 의존 객체가 먼저 생성되어 있어야 하므로, 의존 객체를 먼저 생성할 수 없다면 생성자 방식을 사용할 수 없게 됩니다.

생성자 방식과 달리 설정 메서드 방식은 객체를 생성한 뒤에 의존 객체를 주입하게 됩니다. 이 경우 의존 객체를 설정하지 못한 상태에서 객체를 사용할 수 있게 되므로, 객체의 메서드를 실행하는 과정에서 NullPointException이 발생할 수 있게 됩니다.

```java
Worker worker = new Worker();
// 객체 생성 후, 의존 객체를 실수로 설정하지 않음
// worker.setJobQueue(jobQueue);
// worker.setTranscoder(transcoder);

// jobQueue와 trnascoder가 null이므로, NullPointException 발생
worker.run();
```

생성자 방식과 달리 설정 메서드 방식은 객체를 생성한 이후에 의존 객체를 설정할 수 있기 때문에, 어떤 이유로 인해 의존할 객체가 나중에 생성된다면 설정 메서드 방식을 사용해야 합니다.

```java
Worker worker = new Worker();
worker.setJobQueue(new FileJobQueue(someValueAfterCreatingWorkerObject));
```

또 의존할 객체가 많을 경우, 설정 메서드 방식은 메서드 이름을 통해서 어떤 의존 객체가 설정되는지 보다 쉽게 알수 있으며, 이는 코드 가독성을 높여주는 효과가 있습니다.

```java
Worker worker = new Worker();
worker.setJobQueue(...); // 메서드 이름으로 의존 객체를 알 수 있습니다.
worker.setTranscoder(...);
worker.setLogSender(...);
worker.setStateListener(...);
```

## DI와 테스트
단위 테스트는 한 클래스의 기능을 테스트하는데 초점을 맞춥니다. 예를 들어, 아래 그림과 같은 구조로 클래스와 인터페이스를 구현하고 있다고 합시다.

![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/79063306-c56a9500-7cdb-11ea-9c95-73c6b98fcba8.png)


아직 FileJobQueue 클래스나 FfmpegTrnascoder 클래스의 구현이 완료되지 않는 상황입니다. 이 상태에서 Worker 클래스가 정상적으로 동작하는지 확인하려면 어떻게 해야 할까요? 이럴 경우 Mock 객체를 사용해서 해결할 수도 있습니다.

JobQueue 인터페이스를 상속한 FileJobQueue 클래스와 DbJobQueue 클래스 그리고 Transcoder 인터페이스를 상속한 FfmpegTranscoder 클래스와 SolTranscoder 클래스의 구현이 아직 완료되지 않더라도, 우리는 Mock 객체를 이용해서 Worker 클래스를 테스트할 수 있습니다. 특히, Worker 클래스가 DI 패턴을 따른다면, 생성자나 설정 메서드를 이용해서 Mock 객체를 쉽게 전달할 수 있다.

```java
@Test
public void shouldRunSuccessfully() {
    JobQueue mockJobQueue = ...; // Mockito 등을 이용해서 Mock 객체 생성
    Transcoder mockTranscoder = ...; // Mock 객체 생성

    Worker worker = new Worker();
    worker.setJobQueue(mockJobQueue);
    worker.setTranscoder(mockTrnascoder);
    worker.run(); // Mock 객체를 이용한 실행
}
```
DI를 사용하지 않는 상황을 생각해 봅시다. 이 경우 JobQueue의 구현 객체를 구할 수 있는 방법이 필요합니다. JobQueue의 구현 객체를 구할 수 있도록 하기 위해 JobQueue를 추상 클래스로 바꾼 뒤에 static 메서드를 이용해서 JobQueue 객체를 제공하는 방법을 선택했다고 합시다.

```java
public abstract class JobQueue {
    // static 메서드로 JobQueue의 구현 객체 제공
    public static JobQueue getInstance() {
        return new DbJobQueue();
    }
}
```
이제 Worker 클래스는 JobQueue.getInstance() 메서드를 이용해서 JobQueue 객체를 구하고 필요한 기능을 실행할 것입니다.

```java
public class Worker {
    public void run() {
        JobQueue jobQueue = JobQueue.getInstance();
        when(someRunningCondition) {
            JobData jobData = jobQueue.getJob();
            ...
        }
    }
}
```

Worker 클래스의 run() 메서드가 정상적으로 동작하는지 테스트를 해야 하는데, 아직 DbJobQueue 클래스의 구현이 완성되지 않습니다. 그런데 , 위 코드에서는 JobQueue.getInstance() 메서드를 이용해서 사용할 JobQueue 객체를 제공하므로, Worker 클래스를 테스트하려면 JobQueue.getInstance() 메서드가 Mock 객체를 리턴하도록 코드를 수정해야 합니다. 한 클래스의 테스트 때문에 다른 클래스의 코드를 변경해 주는 상황이 발생하는 것입니다. 게다가 Mock 객체를 이용한 테스트를 마치면, 다시 원래대로 JobQueue 코드를 되돌려야 합니다. Worker 코드의 일부가 변경되어 다시 Worker 클래스를 테스트하려면 똑같은 과정을 또 해야 합니다.

이런 문제가 DI를 적용했을 때에는 발생하지 않습니다. DbJobQueue의 구현이 완료되었는지 여부에 상관없이 Mock 객체를 이용해서 Worker 클래스를 테스트할 수 있고, Mock 객체를 생성하기 위해 기존의 다른 코드를 변경할 필요가 없게 됩니다.


## 스프링 프레임워크 예
대표적인 DI 프레임워크인 스프링 프레임워크는 생성자 방식과 설정 메서드 방식을 모두 지원하고 있습니다. 스프링은 XML 파일을 이용해서 객체를 어떻게 생성하고 조립할지를 설정합니다. 

XML 설정 파일을 작성했다면, 스프링 프레임워크가 제공하는 클래스를 이용해서 XML 파일에 설정된 객체를 생성하고 조립할 수 있습니다. 다음은 스프링 프레임워크를 이용하는 코드의 일부를 보여주고 있다.


```java
ApplicationContext context = new ClassPathApplicationContext(new String[] {"config.xml"});
Worker worker = (Worker) context.getBean("worker");
JobCLI jobCli = (JobCLI) context.getBean("jobCli");
jobCli.interact();
worker.run();
```

ClassPathApplicationContext 클래스는 XML 파일을 분석한 뒤, XML 파일에 정의되어 있는 방법으로 객체를 생성하고 연결해 주는 조립기의 역할을 수행합니다.

스프링이 객체를 생성하는 과정을 완료하면, 위 코드에서 보듯이 getBean() 메서드로 객체를 구해서 원하는 기능을 실행하면 됩니다.

여기서 외부 설정 파일을 사용할 경우의 장점은 의존할 객체가 변경될 때 자바 코드를 수정하고 컴파일 할 필요가 없이 XML 파일만 수정해 주면 된다는 점입니다. 스프링은 XML 기반의 다양한 설정 방법을 제공하고 있어서 보다 유연하게 객체 조립을 설정할 수 있습니다. 

XML 설정 파일을 사용하는 방식은 개발자가 입력한 오타에 다소 취약합니다. XML 파일에 입력한 클래스 이름에 오타가 있을 경우, 이 사실을 알아내려면 프로그램을 실행해봐야 합니다. 프로그램을 실행할 때 XML에 오류가 있을 경우 스프링 프레임워크는 익셉션을 발생시키는데, 이 익셉션이 발생되어야 비로소 XML의 오류를 알 수 있는 것입니다. 물론, 이클립스와 인텔리제이 같은 개발 도구의 스프링 플러그인을 활용하면 XML 설정파일에 포함된 오류를 보다 빠르게 찾을 수 있긴 하지만, 모든 XML 설정 오류를 잡아 주지는 못합니다.

XML을 사용할 때의 문제점을 해소하기 위한 방안으로 스프링 3버전부터는 자바 코드 기반의 설정 방식이 추가되었습니다. 예를 들어, 앞서 작성했던 XML 파일을  아래와 같은 자바 코드 기반으로 교체할 수 있습니다.

```java
@Configuration
public class TranscoderConfig {

    @Bean
    public JobQueue jobQueue() {
        return new FileJobQueue();
    }

    @Bean
    public Transcoder ffmpegTranscoder() {
        return new FfmpegTranscoder()
    }

    @Bean
    public Worker worker() {
        return new Worker(fileJobQueue(), ffmpegTranscoder());
    }

    @Bean
    public jobCLI jobCli() {
        JobCLI jobCli = new JobCLI();
        jobCli.setJobQueue(fileJobQueue());
        return jobCli;
    }
}
```

자바 기반의 설정의 장점은 오타로 인한 문제가 거의 발생하지 않는다는 점입니다. 잘못이 있을 경우 컴파일 과정에서 다 드러나기 때문에 IDE를 사용하면 설정 코드를 작성하는 시점에서 바로 확인할 수 있습니다. 반면에 의존 객체를 변경해야 할 경우, 앞서 XML 파일을 이용할 때는 파일만 변경해주면 됐지만 자바 기반 설정에서는 자바코드를 수정해서 다시 컴파일하고 배포해 주어야 하는 단점이 있습니다.

## 서비스 로케이터를 이용한 의존 객체 사용
프로그램 개발 환경이나 사용하는 프레임워크의 제약으로 인해 DI 패턴을 적용할 수 없는 경우가 있습니다. 예를 들어, 모바일 앱을 개발할 때 사용되는 안드로이드 플랫폼의 경우는 화면을 생성할 때 Activity 클래스를 상속받도록 하는데, 이 때 안드로이드 실행 환경은 정해진 메서드만을 호출할 뿐, 안드로이드 프레임워크가 DI 처리를 위한 방법을 제공하지는 않습니다.

```java
public class MainActivity extends Activity {
    private someService someService;

    // 안드로이드 프레임워크가 실행해 주지 않음, 즉 DI 할 수 없음
    public void setSomeService(SomeService someService) {
        this.someService = someService;
    }

    // 안드로이드 프레임 워크에 의해 실행됨
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ...
    }
}
```
물론, 안드로이드 프로젝트에서 소스 코드를 조작해서 DI 처리를 해주는 도구가 존재하지만, 이 방법이 널리 사용되고 있지는 않습니다. 안드로이드처럼 실행환경의 제약 때문에 DI 패턴을 적용할 수 없는 경우에는 의존 객체를 찾는 다른 방법을 모색해야 하는데, 그 방법 중 하나인 서비스 로케이터에 대해서 살펴보도록 하겠습니다.

## 서비스 로케이터 구현
서비스 로케이터는 어플리케이션에서 필요로하는 객체를 제공하는 책임을 갖습니다. 서비스 로케이터는 다음과 같이 의존 대상이 되는 객체 별로 제공 메서드를 정의합니다.

```java
public class ServiceLocator {
    public Transcoder geTranscoder() {...}
    public jobQueue getJobQueue() {...}
}
```

의존 객체가 필요한 코드에서는 ServiceLocator가 제공하는 메서드를 이용해서 필요한 객체를 구한 뒤 알맞은 기능을 실행합니다.

```java
public class Worker {

    public void run() {
        ServiceLocator locator = ...; // Locator를 구하는 방법
        JobQueue jobQueue = locator.getJobQueue(); // getJobQueue를 구합니다.
        Transcoder transcoder = locator.getTranscoder();

        while(someRunningCondition) {
            JobData jobData = jobQueue.getJob();
            transcoder.transcode(jobData.getSource(), jobData.getTarget());
        }
    }
}
```

서비스 로케이터가 올바르게 동작하려면 서비스 로케이터 스스로 어떤 객체를 제공해야 할지를 알아야 합니다. 예를 들어, 위 코드에서 locator.getJobQueue()가 어떤 객체를 리턴해야 할지에 대해 ServiceLocator가 알 수 있어야 합니다. 앞서 DI를 사용할 때 메인 영역에서 객체를 생성했던 것과 비슷하게, 서비스 로케이터를 사용하는 경우에도 메인 영역에서 서비스 로케이터가 제공할 객체를 초기화 해줍니다.

서비스 로케이터는 어플리케이션 영역의 객체에서 직접 접근하기 때문에 서비스 로케이터는 어플리케이션 영역에서 위치하게 됩니다. 메인 영역에서는 서비스 로케이터가 제공할 객체를 생성하고, 이 객체를 이용해서 서비스 로케이터를 초기화 해줍니다.

```java
//생성자를 이용해서 객체를 등록 받는 서비스 로케이터 구현
public class ServiceLocator {

    private JobQueue jobQueue;
    private Transcoder transcoder;

    public ServiceLocator(JobQueue 
    jobQueue, Transcoder transcoder) {
        this.jobQueue = jobQueue;
        this.transcoder = transcoder;
    }

    public JobQueue getJobQueue() {
        return jobQueue;
    }

    public Transcoder getTranscoder() {
        return transcoder;
    }      

    // 서비스 로케이터 접근 위한 static 메서드
    public static ServiceLocator instance;

    public static void load(ServiceLocator locator) {
        ServiceLocator.instance = locator;
    }
    
    public static ServiceLocator getInstance() {
        return instance;
    }
}
```

메인 영역의 코드에서 ServiceLocator의 생성자를 이용해서 서비스 로케이터가 제공할 객체를 설정해 주고, ServiceLocator.load() 메서드를 이용해서 메인 영역에서 사용할 ServiceLocator를 객체를 초기화 합니다.

```java
public static void main(String[] args) {
    //의존 객체 생성
    FileJobQueue jobQueue = new FileJobQueue();
    FfmpegTranscoder transcoder = new FfmpegTranscoder();

    // 서비스 로케이터 초기화
    ServiceLocator locator = new ServiceLocator(jobQueue, transcoder);
    ServiceLocator.load(locator);

    // 어플리케이션 코드 실행
    Worker worker = new Worker();   
    JobCLI jobCli = new JobCLI();
    jobCli.interact();
    ...
}
```

어플리케이션 영역 코드에서는 서비스 로케이터가 제공하는 메서드를 이용해서 필요한 객체를 구한 뒤, 해당 객체의 기능을 실행하게 됩니다.

```java
public class Worker {
    public void run(){
        //서비스 로케이터 이용해서 의존 객체 구함
        ServiceLocator locator = ServiceLocator.getInstance();
        JobQueue jobQueue = locator.getJobQueue();
        Transcoder transcoder = locator.getTranscoder();

    // 의존 객체 사용
    while(someRunningCondition) {
        JobData jobData = jobQueue.getJob();
        transcoder.transcode(jobData.getSource(), jobData.getTarget()); 
    }
  }
}
```

서비스 로케이터가 제공할 객체의 종류가 많을 경우, 서비스 로케이터 객체를 생성할 때 한번에 모든 객체를 전달하는 것은 코드 가독성을 떨어뜨릴 수 있습니다. 이런 경우에는 각 객체마다 별도의 등록 메서드를 제공하는 방식을 취해서 서비스 로케이터 초기화 부분의 가독성을 높여 줄 수 있습니다.
