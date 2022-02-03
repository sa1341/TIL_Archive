# Facade 패턴이란?

오늘 새로운 프로젝트를 하면서 파사드 패턴(Facade Pattern)이라는 디자인 패턴을 접하게 되었는데,한번 예제코드를 작성하여 파사드 패턴에 대해서 이해할 수 있도록 정리하였습니다.

`Facade`는 `건물의 정면`을 의미하는 단어로 어떤 소프트웨어의 다른 커다란 코드 부분에 대하여 간략화된 인터페이스를 제공해주는 디자인 패턴을 의미한다고 합니다..

사실 위의 의미만으로는 정확히 파사드 패턴이 어떤 패턴인지 이해가 힘들었습니다. 구글링을 통해서 해당 패턴의 예제코드를 보고 이해할 수 있었습니다.

예를들면 퇴근 후 게임을 하려고 합니다. 그러면 게임을 하기 위해서 사전에 맥주를 마시고, 컴퓨터를 킵니다. 그리고 게임을 플레이 합니다. 

```java
public void play() {

    Beer beer = new Beer(beerName, quantity);
    Computer computer = new Computer(osType);
    Game game = new Game(gameName);

    beer.drink();
    computer.on();
    game.start();
}
```

클라이언트 입장에서는 단순히 게임을 하기 위해서 위의 코드처럼 여러 서브 클래스들에 의존하여 게임을 플레이해야 합니다. 여기서 퍼사드 객체를 사용하면 클라이언트에서 게임을 플레이 하기 위해 사용하는 서브클래스들 사이의 간단한 통합 인터페이스를 제공해주는 역할을 하게 됩니다.

## 예제코드

#### Beer.java

맥주를 나타내는 클래스입니다. 복잡한 서브 클래스들 중 하나입니다.

```java
public class Beer {

    private String name;
    private int quantity;

    public Beer(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    public void drink() {
        System.out.println(name + "을 " + quantity + "ml 마십니다.");
    }
}
```

#### Computer.java

컴퓨터를 나타내는 클래스입니다.

```java
public class Computer {

    private String osType;

    public Computer(String osType) {
        this.osType = osType;
    }

    public void on() {
        System.out.println(osType + "을 켭니다.");
    }

    public void off() {
        System.out.println(osType + "을 끕니다.");
    }
}
```

#### Game.java

게임을 플레이를 하기 위한 클래스입니다.

```java
public class Game {

    private String name;

    public Game(String name) {
        this.name = name;
    }

    public void start() {
        System.out.println(name + "을 시작합니다.");
    }

    public void end() {
        System.out.println(name + "을 종료합니다.");
    }
}
```

#### Facade.java

가장 중요한 Facade 클래스입니다. 복잡한 서브 클래스들에 대한 인스턴스를 가지며 복잡한 호출 방식에 대하여 playGame() 메서드내에서 구현을 하도록 하였습니다.

```java
public class Facade {

    private String gameName;
    private String osType;
    private String beerName;
    private int quantity;

    public Facade(String gameName, String osType, String beerName, int quantity) {
        this.gameName = gameName;
        this.osType = osType;
        this.beerName = beerName;
        this.quantity = quantity;
    }

    public void playGame() {

        Beer beer = new Beer(beerName, quantity);
        Computer computer = new Computer(osType);
        Game game = new Game(gameName);

        beer.drink();
        computer.on();
        game.start();
    }
}

public class FacadeExam {

    public static void main(String[] args) {
        Facade facade = new Facade("젤다 야생의 숨결", "Mac", 
        "클라우드", 500);

        facade.playGame();
    }
}
```

클라이언트 입장에서는 이제 서브 클래스들에 대해서 알 필요가 전혀 없습니다. 단지 Facade 객체의 playGame() 메서드를 호출하면서 서브 클래스들의 복잡한 기능을 수행 할 수 있기 때문입니다.

#### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/126334027-a875b3fe-36a5-4c3f-b312-96332275bd23.png)


> 참조 블로그: https://lktprogrammer.tistory.com/42
