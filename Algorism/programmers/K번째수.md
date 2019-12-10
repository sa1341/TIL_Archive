# K번째수

## 문제 설명

배열 array의 i번째 숫자부터 j번째 숫자까지 자르고 정렬했을 때, k번째에 있는 수를 구하려 합니다.

예를 들어 array가 [1, 5, 2, 6, 3, 7, 4], i = 2, j = 5, k = 3이라면

array의 2번째부터 5번째까지 자르면 [5, 2, 6, 3]입니다.
1에서 나온 배열을 정렬하면 [2, 3, 5, 6]입니다.
2에서 나온 배열의 3번째 숫자는 5입니다.

배열 array, [i, j, k]를 원소로 가진 2차원 배열 commands가 매개변수로 주어질 때, commands의 모든 원소에 대해 앞서 설명한 연산을 적용했을 때 나온 결과를 배열에 담아 return 하도록 solution 함수를 작성해주세요.

> 제한사항

- array의 길이는 1 이상 100 이하입니다.
- array의 각 원소는 1 이상 100 이하입니다.
- commands의 길이는 1 이상 50 이하입니다.
- commands의 각 원소는 길이가 3입니다.

## 입출력 예

![스크린샷 2019-12-10 오후 4 29 47](https://user-images.githubusercontent.com/22395934/70505025-5123b780-1b6a-11ea-8956-d21be89ace96.png)


### 입출력 예 설명
[1, 5, 2, 6, 3, 7, 4]를 2번째부터 5번째까지 자른 후 정렬합니다. [2, 3, 5, 6]의 세 번째 숫자는 5입니다.

[1, 5, 2, 6, 3, 7, 4]를 4번째부터 4번째까지 자른 후 정렬합니다. [6]의 첫 번째 숫자는 6입니다.

[1, 5, 2, 6, 3, 7, 4]를 1번째부터 7번째까지 자릅니다. [1, 2, 3, 4, 5, 6, 7]의 세 번째 숫자는 3입니다.


```java
import java.util.Arrays;

public class FindNumberOrderBy {


    public int[] solution(int[] array, int[][] commands) {

        int[] result = new int[commands.length];

        for (int i = 0; i < commands.length; i++) {
            // to 부터 from 전까지 문자열을 발췌
            int[] temp = Arrays.copyOfRange(array, commands[i][0]-1, commands[i][1]);
            Arrays.sort(temp);
            result[i] = temp[commands[i][2]-1];
        }
        return result;
    }
    
    public static void main(String[] args) {

        int[] array = {1, 5, 2, 6, 3, 7, 4};
        int[][] commands = {{2, 5, 3}, {4, 4, 1}, {1, 7, 3}};
        
        FindNumberOrderBy findNumberOrderBy = new FindNumberOrderBy();

        int[] result = findNumberOrderBy.solution(array, commands);

        for (int value : result)
            System.out.print(value + " ");
        
    }
}
```

단순하게 이 문제는 `Arrays.copyOfRange()`를 알고 있다면 훨씬 간결하게 풀수 있는 문제입니다. 처음에는 array를 문자열 타입의 배열로 solution() 메소드의 아규먼트로 넘기려다가 정수형 타입의 배열에서 특정 길이만큼 자를 수 있는 util 클래스를 찾다가 이런 유용한 메소드가 있다는 것을 알게 되었습니다.
