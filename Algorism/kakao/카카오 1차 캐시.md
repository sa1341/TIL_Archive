# 문제 설명
## 캐시

지도개발팀에서 근무하는 제이지는 지도에서 도시 이름을 검색하면 해당 도시와 관련된 맛집 게시물들을 데이터베이스에서 읽어 보여주는 서비스를 개발하고 있다.

어피치는 제이지에게 해당 로직을 개선하라고 닦달하기 시작하였고, 제이지는 DB 캐시를 적용하여 성능 개선을 시도하고 있지만 캐시 크기를 얼마로 해야 효율적인지 몰라 난감한 상황이다.

어피치에게 시달리는 제이지를 도와, DB 캐시를 적용할 때 캐시 크기에 따른 실행시간 측정 프로그램을 작성하시오.

### 입력 형식
- 캐시 크기(cacheSize)와 도시이름 배열(cities)을 입력받는다.
- cacheSize는 정수이며, 범위는 0 ≦ cacheSize ≦ 30 이다.
- cities는 도시 이름으로 이뤄진 문자열 배열로, 최대 도시 수는 100,000개이다.
- 각 도시 이름은 공백, 숫자, 특수문자 등이 없는 영문자로 구성되며, 대소문자 구분을 하지 않는다. 도시 이름은 최대 20자로 이루어져 있다.

### 출력 형식
- 입력된 도시이름 배열을 순서대로 처리할 때, "총 실행시간"을 출력한다.

### 조건
- 캐시 교체 알고리즘은 LRU(Least Recently Used)를 사용한다.
- cache hit일 경우 실행시간은 1이다.
- cache miss일 경우 실행시간은 5이다.

### 입출력 예제

![image](https://user-images.githubusercontent.com/22395934/117797941-8e263900-b28b-11eb-8193-d6b1eb7fad87.png)


이 문제는 사실 Cache 알고리즘 중에 가장 유명한 `LRU(Least Recently Used)` 알고리즘을 알고 있다면 쉽게 풀 수 있는 문제였지만, 나의 잘못된 상식으로 인해서 푸는데 애를 먹었습니다. 그래서 LRU에 대해서 다시 알아보고 문제를 풀었습니다.

## LRU(Least Recently Used) 알고리즘이란?

기본적으로 캐시에서 메모리를 다루기 위해 사용되는 알고리즘입니다. 캐시가 사용하는 리소스의 양은 제한되어 있고, 캐시는 빠르게 데이터를 저장하고 접근할 수 있어야 합니다. 이를 위해 `LRU` 알고리즘은 메모리 상에서 가장 최근에 사용된 적이 없는 캐시의 메모리부터 대체하여 새로운 데이터를 갱신시켜줍니다.

#### LRU 알고리즘 호출 과정 
![Untitled Diagram (1)](https://user-images.githubusercontent.com/22395934/117798925-93d04e80-b28c-11eb-9bb9-452f1e08abc3.png)


위 그림은 LRU 알고리즘을 이해하는데 제일 도움이 되어서 직접 그려봤습니다. 캐시 저장 수용량이 3이라고 가정을 해보겠습니다.

만약 1, 2, 3이라는 데이터들을 LinkedList에 넣으면 3, 2, 1 순으로 저장이 되어 있습니다. 여기에서 2라는 데이터를 찾는다면 캐시에서 2가 있기 때문에 해당 2를 제거하고 3 -> 2, 가장 오래동안 참조되지 않은 1은 리스트의 맨 마지막으로 이동합니다. 

이 경우 4를 캐시에 저장할 경우 캐시 수용량이 3이기 때문에 가장 오래동안 참조되지 않은 리스트의 맨 마지막에 있는 1이 삭제가 되고, 4는 리스트 맨 앞에 오게 됩니다. 

이것을 아래 Java 문제풀이로 옮겨봤습니다.

## 문제 풀이

```java
import java.util.LinkedList;

public class OnecacheExam {

    static final int CACHE_HIT = 1;
    static final int CACHE_MISS = 5;

    public int solution(int cacheSize, String[] cities) {
        if (cacheSize == 0) return 5 * cities.length;

        int answer = 0;
        LinkedList<String> queue = new LinkedList<>();


        for (String city : cities) {
            city = city.toUpperCase();

            if (queue.remove(city)) {
                // 캐시 성공일 경우
                queue.addFirst(city);
                answer += CACHE_HIT;

            } else {
                // 캐시 미스일 경우
                int currentSize = queue.size();

                if (currentSize == cacheSize) {
                    queue.pollLast();
                }
                queue.addFirst(city);
                answer += CACHE_MISS;
            }
        }


        return answer;
    }


    public static void main(String[] args) {
        OnecacheExam onecacheExam = new OnecacheExam();
        int cacheSize = 3;
        String[] cities = {"Jeju", "Pangyo", "Seoul", "NewYork", "LA", "Jeju", "Pangyo", "Seoul", "NewYork", "LA"};
        int result = onecacheExam.solution(cacheSize, cities);
        System.out.println("result: " + result);
    }
}
```

Queue의 구현체인 LinkedList를 사용하여 각 캐시의 데이터들을 관리하였습니다. remove() 메서드는 해당 리스트에 item이 존재하면 삭제하고 true를 리턴합니다. 만약 false를 리턴하였다면 LinkedList에 데이터가 존재하지 않은 것으로 판단하고, else 문에서 캐시 사이즈와 리스트의 크기가 같으면 리스트의 마지막에 위치한 데이터를 삭제하고, 해당 데이터를 리스트 맨 앞에 캐싱하는 방식으로 구현했습니다.

