# 숫자 n의 각 자리수로 분열 가능한 횟수 구하기

어떤 숫자 n을 각 자리의 숫자로 나누었을 때, 나누어떨어지면 그 숫자로 분열 가능하다고 합니다.
예를 들어 2232이라는 숫자는 2와 3 두 개의 숫자로 구성되어 있습니다. 또한, 2232는 2로도 나누어떨어지고, 3으로도 나누어 떨어집니다. 따라서 분열 가능한 횟수는 2입니다.

2232의 예에서와같이 2가 여러 번 나오더라도 분열 가능한 횟수를 셀 때는 2 한 번, 3 한 번으로 세며 중복해서 나오는 숫자 2는 고려하지 않습니다.
숫자 n이 매개변수로 주어질 때, n이 분열 가능한 횟수를 return 하도록 solution 함수를 완성하세요.
[※ 숫자는 0으로 나눌 수 없음을 유의하세요.]
제한사항

* n은 1015 이하의 자연수입니다.


## 입출력 예

![스크린샷 2019-12-14 오후 9 06 13](https://user-images.githubusercontent.com/22395934/70848477-9a9b3c00-1eb5-11ea-8195-fa272a8a6b85.png)





입출력 예 설명


입출력 예 #1



문제의 예제와 같습니다.
입출력 예 #2

1234의 경우 다음과 같습니다.
* 1234는 1로 나누어떨어짐
* 1234는 2로 나누어떨어짐
* 1234는 3으로 나누어떨어지지 않음
* 1234는 4로 나누어떨어지지 않음
따라서 분열 가능한 횟수는 2이므로 2를 return 합니다.


```java
import java.util.HashSet;

public class FindDivisibleNumber {

    public int solution(long n) {

        int answer = 0;
        // 각 자리수에 해당 하는 값을 중복없이 저장하기 위한 HashSet 객체 생성
        HashSet<Integer> hashSet = new HashSet<>();
        String number = String.valueOf(n);

        getDistinctNumber(n, hashSet, number);

        hashSet.forEach(s -> System.out.println(s));

        for (int val : hashSet){
            if(n % val == 0) {
                answer++;
            }
        }

        return answer;
    }

    private void getDistinctNumber(long n, HashSet<Integer> hashSet, String number) {

        for (int i = 0; i < number.length(); i++) {
            int remain = 0;

            // 각 자리수 추출
            remain = (int) n % 10;
            hashSet.add((int) remain);
            n = n / 10;
        }
    }


    public static void main(String[] args) {

        FindDivisibleNumber divideN = new FindDivisibleNumber();
        int n = 2322;

        System.out.println(divideN.solution(n));

    }
}
```

각각의 자리수를 중복없이 추출하기 위해 List 타입을 사용하여 contains() 메소드를 이용하여 중복이 되지 않는 각 자리수를 추출하려 했지만, 
HashSet 객체를 이용하는것이 더 적절하다고 판단하여 사용하였습니다. 

