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
