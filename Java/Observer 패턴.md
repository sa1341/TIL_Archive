## Observer 패턴 예제


옵저버 패턴은 한 객체의 상태가 바뀔 경우 다른 객체들에게 변경됐다고 알려주는 경우를 들 수 있습니다.
상태를 가지고 있는 주체 객체와 상태의 변경을 알아야 하는 관찰 객체(Observer Object)가 존재하며 이들의 관계는 1:1이 될 수도 있고 1:N이 될 수가 있습니다
서로의 정보를 넘기고 받는 과정에서 정보의 단위가 클 수록, 객체들의 규모가 클 수록, 객체들의 관계가 복잡할 수 있도록 점점 구현하기 어려워지고 복잡성이 매우 증가할 것이다. `이러한 기능을 할 수 있도록 가이드라인을 제시해 주는것이 바로 옵저버 패턴입니다.`

구체적인 에를 들면 매일매일 새로운 뉴스를 제공하는 뉴스 프로그램이 있다고 가정하겠습니다. 이 프로그램은 새로운 뉴스를 취합하여 정보를 가지고 있습니다. 그리고 이 뉴스 프로그램에서 나온 뉴스를 구독하고 싶어하는 Observer를 정의해봅시다. 이들은 뉴스 프로그램이 새로운 뉴스를 생성할 때마다 이 소식을 바로 받을 수 있습니다.
그렇다면 일단 NewsProgram이라는 객체와 Observer라는 객체가 필요하게 됩니다.
이러한 객체의 클래스를 설계할 때 인터페이스를 이용한 객체간의 느슨한 결합을 권장하게 됩니다. 즉, A에 B가 있다라는 것을 의미하며 한 객체에 객체를 포함시키는 것이 아닌 인터페이스를 포함하는 방식으로 많이 사용합니다.

우리가 사용하려는 옵저버 패턴에서도 이러한 방식처럼 인터페이스를 활용합니다. 여기서는 크게 두가지 역할을 합니다. 하나는 Publisher의 역할, 또 하나는 Observer의 역할입니다. 일단 이들의 인터페이스를 정의한 후 이를 implement 한 클래스들을 이용합니다.

Publisher를 구현한 NewsProgram 클래스는 정보를 제공하는 Publisher가 되며 Observer 객체들을 가지고 있습니다. 그리고 Observer 인터페이스를 구현한 AnnualSubscriber(매년 정기구독자), EventSubscriber(이벤트 고객)클래스는 NewsMachine이 새로운 뉴스를 notifyObserver()를 호출하면서 알려줄 때마다 Update가 호출됩니다. 이를 토대로 Java 코드를 작성해보겠습니다.


#### Observer 인터페이스 정의
```java
public interface Observer {
    public void update(String title, String news);
}
```

#### Publisher 인터페이스 정의
```java

public interface Publisher {
    public void add(Observer observer);
    public void delete(Observer observer);
    public void notifyObserver();
}
```

#### NewsMachine 클래스
```java
import java.util.ArrayList;
import java.util.List;

public class NewsProgram implements Publisher {

    private List<Observer> observers;
    private String title;
    private String news;


    public NewsProgram() {
        observers = new ArrayList<>();
    }

    @Override
    public void add(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void delete(Observer observer) {

        int index = observers.indexOf(observer);
        observers.remove(index);

    }

    @Override
    public void notifyObserver() {

        for (Observer observer : observers){

            observer.update(title, news);
        }

    }

    public void setNewsInfo(String title, String news){

        this.title = title;
        this.news = news;
        notifyObserver();
    }

    public String getTitle() {
        return title;
    }

    public String getNews() {
        return news;
    }
}
```


#### AnnualSubscriber 클래스 정의
```java
public class AnnualSubscriber implements Observer {

    private String newsString;
    private Publisher publisher;

    public AnnualSubscriber(Publisher publisher) {
        this.publisher = publisher;
        publisher.add(this);
    }

    @Override
    public void update(String title, String news) {
        this.newsString = title + "\n----------\n " + news;
        display();
    }

    private void display(){
        System.out.println("\n\n오늘의 뉴스\n=================================\n\n" + newsString);
    }

    public void withdraw(){
        publisher.delete(this);
    }

}
```


#### EventSubscriber 클래스 정의
```java
public class EventSubscriber implements Observer {

    private String newsString;
    private Publisher publisher;

    public EventSubscriber(Publisher publisher) {
        this.publisher = publisher;
        publisher.add(this);
    }


    @Override
    public void update(String title, String news) {
        newsString = title + "\n-----------------------------------------\n" + news;
        display();
    }


    private void display(){
        System.out.println("\n\n=== 이벤트 유저 ===");
        System.out.println("\n\n" + newsString);

    }
    
    
    public void withdraw(){
        publisher.delete(this);
    }
}
```

Observer 패턴 예제를 위한 컴포넌트들을 구현했습니다. 이제 Main Class에서 한번 실행시켜 보겠습니다.

```java
public class MainClass {
    public static void main(String[] args) {

        NewsProgram newsProgram = new NewsProgram();
        AnnualSubscriber as = new AnnualSubscriber(newsProgram);
        EventSubscriber es = new EventSubscriber(newsProgram);


        newsProgram.setNewsInfo("명절 대축제", "설날 연휴가 곧 끝나갑니다.");

        newsProgram.setNewsInfo("화요일부터", "출근을 해야합니다.");

    }
}
```

#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/73138235-db38c880-40a3-11ea-8d6a-a166e5b25d53.png)

위의 실행결과와 같이 2가지 뉴스가 각각 AnnualSubscriber와 EventSubscriber 객체에 잘 전달이 됐습니다. 이 두 객체가 생성되는 순간 Publisher가 가지고 있는 
List\<Observer> 목록에 등록이 됩니다.

여기서 AnnualSubscriber와 EventSubscriber 객체에서 Publisher 객체를 맴버 변수로 가지고 있는 이유는 List\<Observer> 목록에서 탈퇴할 때 이 Publisher 레퍼런스를 이용하면 편하기 때문입니다. 위의 코드에서 EventSubscriber 객체인 es 인스턴스를 탈퇴시킨다면 아래와 같이 수정하면 됩니다.

```java
public class EventSubscriber implements Observer {

    private String newsString;
    private Publisher publisher;

    public EventSubscriber(Publisher publisher) {
        this.publisher = publisher;
        publisher.add(this);
    }


    @Override
    public void update(String title, String news) {
        newsString = title + "\n-----------------------------------------\n" + news;
        display();
    }


    private void display(){
        System.out.println("\n\n=== 이벤트 유저 ===");
        System.out.println("\n\n" + newsString);

    }

    // 구독 취소 메소드
    public void withdraw(){
        publisher.delete(this);
    }
}


public class MainClass {
    public static void main(String[] args) {

        NewsProgram newsProgram = new NewsProgram();
        AnnualSubscriber as = new AnnualSubscriber(newsProgram);
        EventSubscriber es = new EventSubscriber(newsProgram);


        newsProgram.setNewsInfo("명절 대축제", "설날 연휴가 곧 끝나갑니다.");
        // es 인스턴스 observer 목록에서 탈퇴
        EventSubscriber.withdraw();
        newsProgram.setNewsInfo("화요일부터", "출근을 해야합니다.");

    }
}
```


#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/73138305-93667100-40a4-11ea-96ff-0802175bfd35.png)


실행화면을 보면 EventSubscriber 객체는 2번째 뉴스부터 구독에서 제외되었습니다.
`Publisher에서 정보나 상태가 변경될 때마다 Observer에게 보내는 방식을 우리가 흔히 알고 있는 푸시(Push)`라고 부릅니다.
반면에 `Observer에서 정보가 필요할 때마다 Publisher에게 요청하는 방식을 풀(Pull)`이라고 합니다.
