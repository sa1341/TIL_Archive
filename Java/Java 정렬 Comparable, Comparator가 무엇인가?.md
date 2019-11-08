# Java 정렬 Comparable, Comparator가 무엇인가?

배열이나 Collection 프레임워크 등에서 sort() 를 사용하면 컴퓨터가 알아서 정렬을 해줍니다.

여기서 사용되는 sort() 는 Comparable 구현에 의해 정렬된 것인데, 

오늘은 자바 정렬 Java Comparable과 Comparator에 대해 알아보겠습니다.


Comparable - 기본 정렬기준을 구현하는데 사용.
Comparator - 기본 정렬기준 외에 다른 기준으로 정렬하고자할 때 사용. 

여기서 Arrays.sort(sports), Arrays.sort(names)는 String 의 Comparable 구현에 의해 정렬된 것입니다.

```java
public final class String
    implements java.io.Serializable, Comparable<String>, CharSequence 
```

Comparable 을 구현하고 있는 클래스들은 같은 타입의 인스턴스끼리 서로 비교할 수 있는 클래스들, String, Integer, Date, File 등과 같은 것들입니다. 

그리고 기본적으로는 작은 값에서 큰 값의 순서, 오름차순 형태로 구현되도록 만들어져 있습니다.
이것을 상단 Comparable에 명시되어 있는 < 기본 정렬기준 > 으로 보면 될 것 같습니다.

> 주의사항
 배열타입을 정렬할때는 Arrays.sort() 메소드를 써야하지만, Collection 타입의 객체의 요소를 정렬할때는 Collections.sort() 를 적용해야 합니다.

# 객체 정렬
SoccerPlayer는 이름, 포지션, 나이 속성을 갖고 있고 각각 setter와 getter를 선언해줬습니다.. 
여기서 동일하게 SoccerPlayer 객체를 생성하고, 배열이나 ArrayList 형태로 만들었을 경우 Arrays.sort(), Collections.sort() 는 작동할까요?

답부터 말하자면 `오류`가 발생합니다.
이유는 정렬을 시도했지만, 객체내의 어떤 변수를 기준으로 정렬할지 정하지 않았기 때문입니다.
String타입의 배열이나 ArrayList는 값이 하나지만, 객체를 정렬할 경우 객체 내의 어떤 변수로 정렬할지 만들어줘야 합니다..


이때 아래 코드처럼 Comparable을 implements한 뒤 compareTo 메소드를 구현한다면 해결할 수 있습니다.

```java
public class SoccerPlayer implements Comparable<SoccerPlayer> {

    private String name;
    private String position;
    private int age;


    public SoccerPlayer(String name, String position, int age) {
        this.name = name;
        this.position = position;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }


    @Override
    public int compareTo(SoccerPlayer player) {
        return name.compareTo(player.getName());
    }
}
````
SoccerPlayer 클래스에서 Comparable<SoccerPlayer>를 implements 하였습니다.
그리고 하단에는 compareTo를 오버라이드하여 코드를 따로 만들었습니다.
매개변수로 SoccerPlayer 객체를 받고, 리턴값으로는 객체의 이름을 비교하는 구문을 넣어줬습니다.

여기서 본인이 정렬하고자 하는 클래스를 `Comparable<클래스명>` 형태로 넣어주는 것을 잊어서는 안됩니다.

출처: https://cwondev.tistory.com/15 [잡동사니 정보공유]

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoccerComparable {

    public static void main(String[] args) {

        List<SoccerPlayer> list = new ArrayList<SoccerPlayer>();

        list.add(new SoccerPlayer("손흥민","공격수",28));
        list.add(new SoccerPlayer("박지성","수비수",35));
        list.add(new SoccerPlayer("차두리","미드필더",35));
        list.add(new SoccerPlayer("조진웅","골기퍼",29));

        // 객체를 정렬할 경우 객체 내의 어떤 변수로 정렬할지 만들어 주지 않으면 에러가 발생합니다.
        // 해당 객체가 Comparable 인터페이스를 구현하고 compareTo 메소드를 오버라이드 해서 정렬기준을 명시해줘야 객체를 정렬 가능 합니다.
        Collections.sort(list);

        for (SoccerPlayer player : list){
            System.out.println(player.getName());
        }
    }
}
```

### 실행결과

```java
박지성
손흥민
조진웅
차두리

Process finished with exit code 0
```

위의 실행결과를 보면 오름차순으로 정렬이 된것을 알 수 있습니다.
SoccerPlayer 클래스에서 Comparable을 implements 했고, 하단에 compareTo를 Override하며 이름비교 코드를 만들어준 덕분입니다.

이 부분을 이름순서가 아닌 나이순서로 바꿔줄 수도 있고, 포지션을 기준으로 해도 무방합니다.
본인이 필요한 기준을 정해 코딩을 하면 됩니다.


# Comparator 
Comparable을 implements 하지 않고도 오브젝트의 특정 변수를 기준으로 정렬하는 방법은 있습니다.

이 때 사용하는 방법이 바로 `Comparator` 입니다. 

`Comparator`를 사용하면 정렬기준을 본인이 원하는대로 바꾸는 것이 가능하다. 


```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SoccerComparator {

    public static void main(String[] args) {

        List<SoccerPlayer> list = new ArrayList<SoccerPlayer>();

        list.add(new SoccerPlayer("손흥민", "공격수", 28));
        list.add(new SoccerPlayer("박지성", "수비수", 35));
        list.add(new SoccerPlayer("차두리", "미드필더", 35));
        list.add(new SoccerPlayer("조진웅", "골기퍼", 29));

        // 객체를 정렬할 경우 객체 내의 어떤 변수로 정렬할지 만들어 주지 않으면 에러가 발생합니다.
        // 해당 객체가 Comparable 인터페이스를 구현하고 compareTo 메소드를 오버라이드 해서 정렬기준을 명시해줘야 객체를 정렬 가능 합니다.
        Collections.sort(list, new Comparator<SoccerPlayer>() {
            @Override
            public int compare(SoccerPlayer p1, SoccerPlayer p2) {
                if (p1.getAge() > p2.getAge()) {
                    return 1;
                }else if(p1.getAge() < p2.getAge()){
                    return -1;
                }else {
                    return 0;
                }
            }
        });

        for (SoccerPlayer player : list) {
            System.out.println(player.getName() + " " + player.getAge());
        }
    }
}
```

### 실행결과
```java
손흥민 28
조진웅 29
박지성 35
차두리 35

Process finished with exit code 0
```

SoccerPlayer 클래스에 Comparable을 implements하지 않은 상태로 메인함수 내에서 Collections.sort()를 만들었습니다. 

여기서는 Collections.sort(playerList, new Comparator<SoccerPlayer>() { } 형태로 구현해야 하며,
이때 compare 메소드가 나타나 내부 코딩을 마무리해야 합니다. 

저는 축구선수의 나이순서대로 정렬이 되도록 구현했고, 결과는 정상적으로 나이 순서대로 나타났습니다. 

Comparable을 사용하든 Comparator를 사용하든 어떻게 구현하느냐에 따라 정렬이 진행되니 참고해야 합니다.

> 결론
Comparable 구현 후 내부의 compareTo 메소드를 오버라이드해 정의해야 하는데, 이 정의 결과에 따라 정렬값이 나옵니다.
또한, 오브젝트의 다른 값으로 비교를 원한다면 compareTo를 하나하나 바꿔줄 필요 없이, Comparator를 이용하면 됩니다.



##### 참조: https://cwondev.tistory.com/15 [잡동사니 정보공유]
