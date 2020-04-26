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
