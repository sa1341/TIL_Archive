## 옵저버(Observer) 패턴
웹 사이트의 상태를 확인해서 응답 속도가 느리거나 연결이 안 되면 모니터링 담당자에게 이메일로 통지해 주는 시스템을 만들기 위해, 상태를 확인하는 StatusChecker 클래스를 다음과 같이 구현할 수 있을 것입니다.

```java
publc class StatusChecker {
    private EmailSender emailSender;

    public void check() {
        Status status = loadStatus();

        if(status.isNotNormal())
            eamilSender.sendEmail(status);
    }
}
```

이메일뿐만 아니라 긴급한 메시지는 SMS로 바로 알려주는 기능을 추가해 달라는 요구가 들어왔습니다. 이를 반영하기 위해 코드는 다음과 같이 바뀝니다.

```java
public class StatusChecker {

    private EmailSender emailSender;
    private SmsSender smsSender;

    public void check() {
        Status status = loadStatus();
        if(status.isNotNormal()) {
            emailSender.sendEmail(status);
            smsSender.sendEmail(status);
        }
    }
}
```

만약 회사 내부에서 사용하는 메신저로도 메시지를 보내 달라는 요구가 들어온다면 어떻게 될까요? 시스템의 문제 상황을 알려주는 방식이 추가될 때마다, StatusChecker 클래스도 아래 그림처럼 변경됩니다.

![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/80308952-c38cef80-880c-11ea-9a9d-6e88e5531383.png)


StatusChecker는 시스템의 상태가 불안정해지면 이 사실을 EmailSender, SmsSender, Messenger 객체에게 알려주는데, 여기서 핵심은 상태가 변경될 때 정해지지 않은 임의의 객체에게 변경 사실을 알려준다는 점입니다. 이렇게 한 객체의 상태 변화를 정해지지 않는 여러 다른 객체에게 통지하고 싶을 때 사용되는 패턴이 옵저버(Observer) 패턴 입니다.

![Untitled Diagram](https://user-images.githubusercontent.com/22395934/80309198-714cce00-880e-11ea-8358-4b3ecb1720ef.png)

옵저버 패턴은 크게 주제(subject) 객체와 옵저버(observer) 객체가 등장하는데, 주제 객체는 다음과 두 가ㅏ지 책임을 갖습니다.

- 옵저버 목록을 관리하고, 옵저버를 등록하고 제거할 수 있는 메서드를 제공합니다. 위 그림에서 add() 메서드와 remove() 메서드가 각각 옵저버를 목록에 등록하고 삭제하는 기능을 제공합니다.

- 상태의 변경이 발생하면 등록된 옵저버에 변경 내역을 알립니다. 위 그림에서 notifyStatus() 메서드가 등록된 옵저버 객체의 onAbnormalStatus() 메서드를 호출합니다.

StatusSubject 클래스는 아래 코드와 같이 옵저버 목록을 List와 같은 타입으로 보간할 수 있을 것입니다.

```java
public abstract class StatusSubject {
    private List<StatusObserver> observers = new ArrayList<StatusObserver> ();

    public void add(StatusObserver observer) {
        observers.add(observer);
    }

    public void remove(StatusObserver observer) {
        observers.remove(observer);
    }

    public void notifyStatus(Status status) {
        for(StatusObserver observer : observers) {
            observer.onAbnormalStatus(status);
        }
    }
}
```

notifyStatus() 메서드는 observers List에 등록된 각 StatusObserver 객체의 onAbnormalStatus() 메서드를 호출하는데, 이렇게 옵저버 객체의 메서드를 호출하는 방식으로 상태에 변화가 생겼음을 옵저버 객체에게 알립니다.

Status의 상태 변경을 알려야 하는 StatusChecker 클래스는 아래 코드와 같이 StatusSubject 클래스를 상속받아 구현합니다.

```java
public class StatusChecker extends StatusSubject {

    public void check() {
        Status status = loadStatus();

        if(status.isNotNormal()) {
            super.notifyStatus(status);
        }
    }

    private Status loadStatus() {
        ...
    }
}
```

StatusChecker 클래스는 비정상 상태가 감지되면 상위 클래스의 notifyStatus() 메서드를 호출해서 등록된 옵저버 객체들에 상태 값을 전달합니다.
옵저버 객체를 구현한 클래스는 주제 객체가 호출하는 메서드에서 필요한 기능을 구현하면 됩니다. 위 예제 코드의 경우 StatusSubject 타입 객체에 등록되는 옵저버 인터페이스인 StatusObserver는 아래 코드와 같이 주제 객체로부터 상태 변화를 전달받을 수 있는 메서드인 onAbnormalStatus() 메서드를 정의하고 있습니다.

옵저버 구현 클래스인 StatusEmailSender 클래스는 아래 코드처럼 StatusObserver 인터페이스를 상속받아 상태 변화를 통지 받을 때 수행해야 할 기능을 구현하게 됩니다.

```java
public interface StatusObserver {

    void onAbnormalStatus(Status status);

}
```

### 콘크리트 옵저버 클래스 구현 예

```java
public class StatusEmailSender implements StatusObserver {

    @Override
    public void onAbnormalStatus(Status status) {
        sendEmail(status);
    }

    private void sendEmail(Status status) {
        ... // 이메일 전송 코드
    }
}
```


주제 객체의 상태에 변화가 생길 때 그 내용을 통지받도록 하려면, 옵저버 객체를 주제 객체에 등록해 주어야 합니다.
예를 들어, 시스템의 상태가 비정상이 될 때 StatusChecker 객체가 StatusEmailSender 객체에 통지하게 하려면 다음 코드처럼 StatusEmailSender 객체를 StatusChecker 객체에 옵저버로 등록해 주어야 합니다.

```java
StatusChecker checker = new StatusChecker();
checker.add(new StatusEmailSender()); // 옵저버로 등록
```   

위와 같이 옵저버로 등록되면, 시스템이 비정상 상태가 될 때마다 StatusChecker 객체가 StatusEmailSender 객체의 onAbnormalStatus() 메서드를 호출해서 상태 정보를 통지해줍니다. 따라서 StatusEmailSender 객체는 시스템이 비정상 상태가 될 때 담당자에게 이메일로 통보해 줄 수 있게 됩니다.

옵저버 패턴을 적용할 때의 장점은 주제 클래스 변경 없이 상태 변경을 통지 받을 옵저버를 추가할 수 있다는 점입니다.
예를 들어, 장애가 발생할 때 SMS를 이용해서 문자를 전송하고 싶다면, 해당 기능을 구현한 옵저버 객체를 StatusChecker 객체에 등록해 주기만 하면 됩니다.

```java
StatusChecker checker = ...;

// 새로운 타입의 옵저버가 추가되어도 StatusChecker 코드는 바뀌지 않습니다.
StatusObserver faultObserver = new FaultStatusSMSSender();
checker.add(faultObserver);
checker.add(new StatusEmailSender());
```

## 옵저버 객체에게 상태 전달 방법

옵저버 객체가 기능을 수행하기 위해 주제 객체의 상태가 필요할 수 있습니다. 예를 들어, FaultStatusSMSSender 클래스는 장애 상태인 경우에만 SMS를 전송하고, 응답 속도가 느려진 상태처럼 장애 이외의 비정상 상태인 경우에는 메시지를 전송하지 않도록 구현할 수 있을 것입니다. 이 경우 FaultStatusSMSendr 클래스는 상태 값을 확인해야 합니다. 지금까지 작성한 예에서는 아래 코드에서 보듯이 주제 객체에서 옵저버 객체에 상태 값을 전달했습니다.

```java
public abstract class StatusSubject {
    private List<StatusObserver> observers = new ArrayList<StatusObserver> ();
    ...
    public void notifyStatus(Status status) {
        for (StatusObserver observer: observers) 
            observer.onAbnormalStatus(status); // 상태를 옵저버에 전달
        
    }
}
    // 옵저버는 파라미터로 전달받은 상태 값을 사용
public class FaultStatusSMSSender implements StatusObserver {
    public void onAbnormalStatus(Status status) {

        if(status.isFault()) { // 전달 받은 상태 값을 사용
            sendSMS(status);
        }
    }
    ...
}


```

위 코드에서 FaultSMSSender 클래스는 onAbnormalStatus() 메서드를 통해서 전달 받은 status 객체만으로 원하는 기능을 구현하는데 부족함이 없습니다. 하지만, 경웨 따라서 옵저버 객체의 메서드를 호출할 때 전달한 객체만으로는 옵저버의 기능을 구현할 수 없을 수도 있습니다.

이런 경우에는 옵저버 객체에서 콘크리트 주제 객체에 직접 접근하는 방법을 사용하기도 합니다. 아래 코드는 옵저버 객체에서 특정 타입의 주제 객체를 사용하는 코드의 예를 보여주고 있습니다.

```java
public class SpecialStatusObserver implements StatusObserver {
    private StatusChecker statusChecker;
    private Siren siren;

    public SpecialStatusObserver(StatusChecker statusChecker) {
        this.statusChecker = statusChecker;
    }

    public void onAbnormalStatus(Status status) {
        // 특정 타입의 주제 객체 접근
        if(status.isFault() && statusChecker.isContinuousFault()) {
            siren.begin();
        }
    }
}
```
SpecialStatusObserver 클래스의 onAbnormalStatus() 메서드는 status 파라미터와 statusChecker 필드를 이용해서 사이렌의 실행 조건을 판단하고 있습니다.

이 코드를 보면 아래 그림처럼 SpecialStatusObserver 클래스에서 StatusChecker 클래스로의 의존이 발생하게 되는데, 이렇게 콘크리트 옵저버 클래스(SpecialStatusObserver)는 필요에 따라 특정한 콘크리트 주제 클래스(StatusChecker)에 의존하게 됩니다.

![Untitled Diagram (2)](https://user-images.githubusercontent.com/22395934/80496985-d508ff80-89a4-11ea-8553-f24821abf478.png)

## 옵저버에서 주제 객체 구분

옵저버 패턴이 가장 많이 사용되는 영역을 꼽르라면 GUI 프로그래밍 영역일 것입니다. 버튼이 눌릴 때 로그인 기능을 호출한다고 할 때, 버튼이 주제 객체가 되고 로그인 모듈을 호출하는 객체가 옵저버가 됩니다.

예를 들어, 안드로이드에서는 다음과 같이 OnClickListener 타입의 객체를 Button 객체에 등록하는데, 이 때 OnClickListener 인터페이스가 옵저버 인터페이스가 됩니다.

```java
public class MyActivity extends Activity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ...
        Button loginButton = getViewById(R.id.main_login);
        loginButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { // OnClickListener의 메서드
        login(id, password);
    }
}
```

한 개의 옵저버 객체를 여러 주제 객체에 등록할 수도 있을 것입니다. GUI 프로그래밍을 하면 이런 상황이 빈번하게 발생합니다.


