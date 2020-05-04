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

예를 들어, 아래 코드처럼 로그인 버튼과 로그아웃 버튼에 동일한 OnClickListener 객체를 등록할 수 있습니다.

```java
public class MyActivity extends Activity implements View.OnClickListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ...
        // 두 개의 버튼에 동일한 onClickListener 객체 등록
        Button loginButton = getViewById(R.id.main_login);
        loginButton.setOnClickListener(this);
        Button logoutButton = (Button) findViewById(R.id.main_logoutbtn);
        logoutButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) { // OnClickListener의 메서드
        
        if (v.getId() == R.id.main_loginbtn) {
            login(id, password);    
        } else if (v.getId() == R.id.main_logoutbtn) {
            logout();
        }
    }
}
```

한 옵저버 객체를 여러 주제 객체에 등록하면, 옵저버 객체에서 각 주제 객체를 구분할 수 있는 방법이 필요합니다.
위 코드에서는 옵저버 객체의 메서드인 onClick() 메서드에서 주제 객체인 Button 객체를 구분하기 위해 ID 값을 사용하였습니다. ID 값 외에 아래 코드처럼 객체 레퍼런스를 사용할 수도 있을 것입니다.

```java
@Override
public void onClick(View v) { // OnClickListener 메서드
    if (v == loginButton) {
        login(id, password);
    } else if (v == logoutButton) {
        logout();
    }
}
```

앞서 statusChecker 예제나 안드로이드 예제는 모두 주제 객체를 위한 추상 타입을 제공하고 있습니다. 예를 들어, StatusChecker는 상위 타입인 StatusSubject 추상 클래스가 존재하고, 안드로이드의 Button 클래스는 상위 타입인 View가 존재합니다. StatusSubject 클래스와 View 클래스는 모두 옵저버 객체를 관리하기 위한 기능을 제공한다는 공통점이 있습니다.

```java
// StatusChecker 클래스
public void add(StatusObserver observer) { ... }

// view 클래스
public void setOnClickListener(OnClickListener o) { ... }
```
한 주제에 대한 다양한 구현 클래스가 존재한다면, 위 코드처럼 옵저버 객체 관리 및 통지 기능을 제공하는 추상 클래스를 제공함으로써 불필요하게 동일한 코드가 여러 주제 클래스에서 중복되는 것을 방지할 수 있을 것입니다. 하지만 해당 주제 클래스가 한개뿐이라면 옵저버 관리를 위한 추상 클래스를 따로 만들 필요는 없을 것 입니다.

## 옵저버 패턴 구현의 고려 사항
옵저버 패턴을 구현할 때에는 다음 내용을 고려해야 합니다

- 주제 객체의 통지 기능 실행 주체
- 옵저버 인터페이스의 분리
- 통지 시점에서의 주제 객체 상태
- 옵저버 객체의 실행 제약 조건

옵저버 패턴을 구현할 때에 고려할 첫 번째 사항은 옵저버에 통지하는 시점을 결정하는 주체가 누가 되느냐에 대한 것입니다. 앞서 StatusChecker 예에서는 등록된 옵저버에 통지하는 주체가 StatusChecker 클래스였습니다.

```java
public class StatusChecker extends StatusSubject {

    public void check() {
        Status status = loadStatus();

        if (status.isNotNormal()) {
            super.notifyStatus(status); // StatusChecker가 옵저버에 대한 통지 요청
        }
    }
}
```

그런데, 필요에 따라 StatusChecker를 사용하는 코드에서 통지 기능을 수행할 수도 있을것입니다. 예를 들어, 여러 StatusChecker 객체로부터 상태 정보를 읽어와 이들이 모두 비정상인 경우에만 통지를 하고 싶다고 해봅시다.

이 경우 아래 코드에서 보는 것처럼 StatusChecker 객체를 사용하는 코드에서 통지 기능을 실행할 수 있을 것입니다.

```java
StatusChecker checker1 = ...;
StatusChecker checker2 = ...;

checker1.check();
checker2.check();

if (checker1.isLastStatusFault() && checker2.isLastStatusFault()) {
    checker1.notifyStatus();
    checker2.notifyStatus();
}
```

Button처럼 주제 객체의 상태가 바뀔 때마다 옵저버에게 통지를 해 주어야 한다면, 주제 객체에서 직접 통지 기능을 실행하는 것이 구현에 유리합니다. 왜냐면, 주제 객체를 사용하는 코드에서 통지 기능을 실행한다면 상태를 변경하는 모든 코드에서 통지 기능을 함께 호출해 주어야 하는데, 이런 방식은 통지 기능을 호출하지 않는 등 개발자의 실수를 유발할 수 있기 때문입니다.

반대로, 한 개 이상의 주제 객체의 연속적인 상태 변경 이후에 옵저버에게 통지를 해야 한다면, 주제 객체가 아닌 주제 객체의 상태를 변경하는 코드에서 통지 기능을 실행해 주도록 구현하는 것이 통지 시점을 관리하기가 수월합니다.

옵저버 패턴을 구현할 때, 두 번째로 고려할 점은 옵저버의 인터페이스 개수에 대한 것입니다.
예를 들어, GUI 컴포넌트들은 마우스 클릭 이벤트, 터치 이벤트, 드래그 이벤트 등 다양한 이벤트를 제공합니다. 이렇게 한 주제 객체가 통지할 수 있는 상태 변경 내역의 종류가 다양한 경우에는 각 종류 별로 옵저버 인터페이스를 분리해서 구현하는 것이 좋습니다. 모든 종류의 상태 변경을 하나의 옵저버 인터페이스로 처리할 경우, 옵저버 인터페이스는 다음과 같이 거대해 질 것입니다.

```java
public interface EventObserver {
    public void onClick(View v);
    public void onScroll(View v);
    public void onTouch(View v);
    ...
}
```

위 코드처럼 모든 종류의 상태 변화를 수신하는 옵저버 인터페이스가 존재할 경우, 콘크리트 옵저버 클래스는 모든 메서드를 구현해 주어야 합니다. 실제로 콘크리트 옵저버 클래스에서 구현할(관심 있는) 메서드가 onClick()뿐이라 하더라도 아래 코드처럼 나머지 메서드의 구현을 만들어 주어야 합니다.(즉, 불필요한 코드를 만들어야 합니다.)

```java
public class OnlyClickObserver implements EventObserver {

    public void onClick(View v) {
        ...// 이벤트 처리 코드
    }

    public void onScroll(View view) { /* 빈 구현 */}
    public void onTouch(View view) { /* 빈 구현 */}
    ... // 다른 메서드의 빈 구현
}
```

주제 객체 입장에서도 각 상태마다 변경의 이유가 다르기 때문에, 이들을 한 개의 옵저버 인터페이스로 관리하는 것은 향후에 변경을 어렵게 만드는 요인이 될 수 있습니다. 옵저버 타입이 한 개일 경우, 클릭, 스크롤, 터치 이벤트를 통지하는 코드가 서로 강하게 연결될 가능성이 높아지는데, 이 경우 옵저버 목록을 관리하고 옵저버 이벤트를 통지하는 코드의 복잡도가 증가하게 되며, 이는 곧 기존 이벤트를 제거하거나 새로운 종류의 이벤트 추가를 어렵게 만드는 원인이 될 수 있습니다.

옵저버 패턴을 구현할 때 주의해야 할 세 번째 사항은 통지 시점에서 주제 객체의 상태에 결함이 없어야 한다는 것입니다.

예를 들어, 한 주제 클래스의 코드를 아래와 같이 구현했다고 합시다.

```java
public class AnySubject extends SomeSubject {

    @Override
    public void changeState(int newValue) {
        // 아래 코드가 실행되기 전에 옵저버가 상태를 조회
        if (isStateSome()) {
            state += newValue;
        }
    }
}
```

이 코드에서 changeState() 메서드는 상위 클래스에 정의된 changeState() 메서드를 호출합니다. 그런데, 상위 타입의 changeState() 메서드에서 옵저버에 통지를 하게 되면 어떻게 될까요? 이 경우 super.changeState() 이후의 코드를 실행하기 전에 옵저버 객체가 AnySubject의 상태 값을 조회하게 됩니다. 그런데 AnySubject 클래스는 super.changeState() 코드를 실행한 이후에 다시 상태를 변경하게 되므로, 결과적으로 옵저버 객체는 완전하지 못한 상태 값을 조회하게 되는 것입니다.

옵저버 객체가 올바르지 않는 상태 값을 사용하게 되는 문제가 발생하지 않도록 만드는 방법 중의 하나는 상태 변경과 통지 기능에 템플릿 메서드 패턴을 적용하는 것입니다. 아래 코드는 템플릿 메서드 패턴을 적용한 예를 보여주고 있습니다.

```java
// 상위 클래스
public class SomeSubject {

    // 템플릿 메서드로 구현
    public void changeState(int newState) {
        internalChangeState(newState);
        notifyObserver();
    }

    protected void internalChangeState(int newState) {
        ...
    }
}


// 하위 클래스
public class AnySubject extends SomeSubject {
    // internalChangeState() 메서드 실행 이후에, 옵저버에 통지
    @Override
    public void internalChangeState(int newValue) {
        super.internalChangeState(newValue);
        if(isStateSome()) {
            state += newValue;
        }
    }
}
```

위 코드에서 상위 클래스의 changeState() 메서드는 internalChangeState() 메서드를 호출한 뒤에 notifyObserver() 메서드를 호출해서 옵저버에게 상태 변화를 통지하고 있습니다.
따라서 AnySubject 클래스의 internalChangeState() 메서드에서 상태 변화를 마무리한 다음에 옵저버 객체가 상태 값을 접근하게 되므로, 옵저버는 완전한  상태 값을 사용할 수 있게 됩니다.

옵저버 패턴을 구현할 때 마지막으로 주의해야할 사항은 옵저버 객체의 실행에 대한 제약 규칙을 정해야 한다는 것입니다.

예를 들어, 주제 객체가 옵저버에 통지하기 위해 사용되는 메서드를 아래와 가팅 구현했다고 합시다.

```java
public void notifyToObserver() {
    for(StatusObserver o: observers) {
        o.onStatusChange();
    }
}

public void changeState(int newState) {
    internalChangeState(newState);
    notifyToObserver();
}
```

만약 10개의 옵저버 객체가 있고, 각 옵저버 객체의 onStatusChange() 메서드마다 실행 시간이 십 분 이상 걸린다면 어떻게 될까요? 이 경우, changeState() 메서드를 호출한 코드는 모든 옵저버 객체의 onStatusChange() 메서드 실행이 종료될 때까지 100분이 이상 기다려야 한다. 또는 한 개의 옵저버로 인해 다른 옵저버의 실행이 지연되는 상황이 발생할 수도 있다.

따라서 옵저버 인터페이스를 정의할 때에는 옵저버 메서드의 실행 제한에 대한 명확한 기준이 필요합니다. 예를 들어,StatusObserver.onStatusChange() 메서드는 수 초 이내에 응답해야 하고 긴 작업을 수행해야 할 경우 별도 쓰레드로 실행해야 한다는 등의 제약 조건이 필요합니다. 안드로이드의 경우 사용자 이벤트에 대해 5초 이상 프로그램이 응답하지 않으면, ANR 대화 상자를 띄어 프로그램 종료 여부를 확인하는데, 이는 사용자 이벤트를 처리하는 코드는 5초 이내에 응답을 처리해야 한다는 기준을 제시하고 있는 것입니다.

이외에 생각해 볼만한 고려사항들이 있습니다. 예를 들어, 옵저버 객체에서 주제 객체의 상태를 다시 변경하면 어떻게 구현할 것인가에 대한 문제나 옵저버 자체를 비동기로 실행하는 문제 등을 생각해 볼 수 있습니다. 이런 문제 주어진 상황에 따라 대답이 달라질 수 있으므로, 실제 옵저버 패턴을 적용할 때 한 번 고민해 보기 바랍니다.
