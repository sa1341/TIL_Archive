 # TDD, 리팩토링
오늘은 이화여대에서 `Dev Festoval`이 개최를 하여서 같은 개발직종에 종사하고 있는 대학교 친구랑 참가비 1만원을 내고 컨퍼런스에 참여하였습니다.

그중에서 관심있는 세션을 3개정도 들었습니다.
첫번째 세션은 현업에서 종사중인 Lisa라는 분의 빠르고 지속적으로 성장하는 방법에 대한 세션이였습니다.

그중에 가장 인상 깊었던 말씀은 하루에 10분 정도 투자해서 5F 회고를 하시는 거였는데 5F는 아래와 같은 약자를 가지고 있습니다.

# 5F

- Fact (사실은 무엇인가?)
- Feeling (무엇을 느꼈는가?)
- Find (무엇을 발견하였는가?)
- Future Action (미래에 해야할 행동)
- Feed Back (피드백)

나이도 젊으신 개발자였는데 항상 자신의 부족한점을 극복하려고 노력하는 모습이 정말 멋있고 인상 깊으셨습니다.
저도 하루하루 살아가면서 비슷하게나마.. 5F를 실천하려고 노력해야겠습니다.

두번째 세션은 `어떤 오픈소스에 참여할까?`로 마찬가지로 흥미진진한 세션이였습니다.

이번에는 대학생 신분인데 벌써부터 오픈소스에 기여하여 발표를 하는 것을 보고 나는...이 나이때까지 무엇을 했나... 반 강제적으로 5F를 하게 되었습니다.
주로 깃허브의 중요성을 강조하셨던게 기억이 남았습니다. 깃허브는 개인 프로젝트를 할때 백업용으로도 쓰지만 현업에서는 협업 및 버전관리용으로 쓰는것을 강조하셨고 기본적으로 GIT을 다룰줄 알아야 된다고 강조하셨습니다. 

그리고 오픈소스를 참여할때 어떤 오픈소스를 건드려야 될지 좋은 팁을 주셨는데 주로 오픈소스는 최근까지 논의가 활발하게 이루어진 오픈소스에 참여하는게 좋다고 하셨습니다. 최근까지 논의한 오픈소스를 어떻게 판별하는지는 최근까지 오픈소스 관리자의 피드백이 있으면 그 오픈소스는 지금도 활발하게 많은 Contributer가 참여하는 오픈소스이기 때문에 저 자신이 성장할 확률도 높습니다.

그리고 커밋 메시지를 남길때 대충 남기지 말고 그것 자체만으로도 협업하는 개발자들이 이해시킬 수 있도록 작성해야 하는걸 강조하셨습니다.

# TDD, 리팩토리의 중요성

마지막으로 제가 들은 세션은.. 사실 이것을 듣기 위해 왔다고 해도 과언이 아닌 우아한테스크코스 교육을 담당하고 계시고 TDD에 관한 책도 저술하신 박재성님의 의식적인 연습으로 TDD, 리팩토링 연습하기 세션을 들었습니다.

기대했던만큼 돈이 아깝지 않을정도로 훌륭한 퀄리티의 세션이였습니다.
제가 앉은 자리는 맨 뒤쪽이여서... 리팩토링을 적용할 코드가 보이지 않았지만 다행히 구글링을 통해서 오늘 세션에서 봤던 리팩토링 예제코드를 보고 분석할 수 있었습니다.

간단하게 리팩토링 예제를 리뷰해보겠습니다.

아래 코드는 어떤 특정 구분자를 가진 문자열 숫자들을 전달하는 경우 구분자를 기준으로 분리한 각 숫자의 합을 return 하는 코드입니다.

박재성님은 웹, 모바일 UI나 DB에 의존관계를 가지지 않는 요구사항으로 연습을 해야하고, 회사 프로젝트에 연습하지 말고 장난감 프로젝트를 활용하라고 하셨습니다.



```java
public class StringCalculator {

    public static void main(String[] args) {

        String text = "1,3,5,7,9";

    
    }

    public static int splitAndSum(String text){
        int result = 0;
        if(text == null || text.isEmpty()){
            return 0;
        }else{
            String[] values = text.split(","); 
            for (String value : values) {
                result += Integer.parseInt(value);
            }
        }
        return result;
    }
}
```
가장 먼저 위의 코드는 `if문과 else문으로 구성이 되어있습니다.` 리팩토링에서는 else 예약어를 쓰지 않는것을 지향하고 있습니다.
그리고 `splitAndSum()`에는 문자열이 비어있는지 검사하고 문자열을 구분자를 기준으로 분리하여 각 숫자의 합을 구하는 등 많은 일들을 하고 있는게 보이고 있습니다.

> 이 코드를 메소드가 한 가지 일만 하도록 아래와 같이 구현해보겠습니다.


# 메소드 분리로 리팩토링 적용 후 코드

```java
public class StringCalculator {

    public static void main(String[] args) {

        String text = "1,3,5,7,9";

        System.out.println(splitAndSum(text));
    }

    // 아래 메소드가 한 가지 일만 하도록 구현하고 있습니다.
    public static int splitAndSum(String text){

        if(isBlank(text)){
            return 0;
        }

        return sum(toInt(text.split(",")));
    }

    public static int[] toInt(String[] values){

        int[] numbers = new int[values.length];


        for (int i = 0; i < values.length; i++) {
            numbers[i] = Integer.parseInt(values[i]);
        }
        return numbers;
    }

    public static int sum(int[] numbers){

        int sum = 0;

        for (int number : numbers){
            sum += number;
        }

        return sum;
    }

    public static boolean isBlank(String text){
        return text == null || text.isEmpty();
    }
}
```

메소드 분리를 통해 리팩토링된 코드를 보면 각 메소드가 하나의 일만 수행하는 것을 알 수가 있습니다.

이것은  `compose method 패턴`을 적용하여 메소드(함수)의 의도가 잘 드러나도록 `동등한 수준의 작업`을 하는 여러 단계로 나누었습니다.


확실히 리팩토링을 통해서 이전 코드랑 비교했을때 가독성이 높아진 것을 알 수가 있습니다.

개발자들이 개발을 잘하는것도 중요하지만 돌아가는 프로그램에 초점을 맞추기 보다는 유지보수성과 기능 확장을 위해서 유연한 코드를 작성하는게 중요하다고 다시 한번 이번 세션을 통해 느꼈습니다. 유연한 코드를 작성하가 위해서는 협업하는 사람들이 코드를 읽기 쉽게 작성하는 거랑 일맥상통하는데 이러한 능력을 갖추기 위해서는 한 번에 한 가지 명확하고 구체적인 목표를 가지고 리팩토링을 연습하는 습관을 기르는게 중요하다고 생각합니다.
