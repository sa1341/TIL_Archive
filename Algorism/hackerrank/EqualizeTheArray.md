![스크린샷 2019-10-27 오후 10 01 21](https://user-images.githubusercontent.com/22395934/67634997-67045280-f905-11e9-8a8a-20d8ee09915c.png)




```java
package hackerrank;

import java.util.*;

public class EqualizeTheAraay {

    private static final Scanner sc = new Scanner(System.in);

    public static int equalizeArray(int[] arr) {

        int duplicate = findDuplicate(arr);
        int deleteCount = 0;

        // 중복 개수가 가장 많은 값과 비교하여 일치하지 값을 삭제합니다.
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != duplicate)
                deleteCount++;
        }
        return deleteCount;
    }

    // 중복 개수가 가장 많은 값을 찾기
    static int findDuplicate(int[] arr) {

        int duplicateValue = 0;
        List<Integer> list = new ArrayList<>();
        
        //스트림 객체의 필터 메소드를 이용하여 중복 없는 원소들을 가진 정수형 타입의 배열을 리턴합니다.
        int[] noRedundant = Arrays.stream(arr).distinct().toArray();
       
        int[] count = new int[noRedundant.length];

        for (int i = 0; i < noRedundant.length; i++){
            int redundant = 0;
            for (int j = 0; j < arr.length; j++) {
                if(noRedundant[i] == arr[j]){
                    redundant++;
                }
            }
            count[i] = redundant;
        }

        duplicateValue = nooRedundant[getMax(count)];

        return duplicateValue;
    }


    // 중복 개수가 들어있는 배열에서 중복개수가 가장 많은 원소의 인덱스를 리턴.
    static int getMax(int[] count){

        int max = count[0];
        int index = 0;

        for (int i = 0; i < count.length; i++) {
            if(max <= count[i]){
                max = count[i];
                index = i;
            }
        }
        return index;
    }


    public static void main(String[] args) {

        int n = sc.nextInt();
        sc.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

        int[] arr = new int[n];

        String[] arrItems = sc.nextLine().split(" ");

        for (int i = 0; i < n; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        int result = equalizeArray(arr);
        System.out.println(result);

        sc.close();
    }
}
```
# 같은 원소만 가진 배열

위의 문제를 해석해보면 정수타입의 배열이 주어질때 칼이라는 아이는 이 배열안에 있는 모든 원소들이 같아질때까지 배열의 크기를 줄기를 원한다는 게 이 문제의 핵심입니다.

그렇기 위해서는 배열안의 엘리먼트 중에서 중복 개수가 가장많은 값을 제외하고 나머지 원소들의 삭제 횟수를 구하는게 핵심입니다.

한마디로 중복 개수가 제외한 값을 제외하고 나머지 원소들의 삭제를 몇건이나 했는지 출력하라는 문제입니다. 


이 문제를 금방 풀줄 알았지만... 저는 2시간정도 걸렸습니다. 
사실 난이도가 easy임에도 불구하고... 많은 생각을 많이했습니다. 이걸 풀면서 저의 문제해결 능력이 너무 나쁘다는걸 깨달았습니다. 저의 프로그래머로써의 자질도 의심하게 되더군요....

사실 이 문제를 해결하기 위해서 어떤 자료구조를 써야할지.. 많은 고민을 했습니다.
제가 생각한 전체적인 흐름은 컬렉션 타입인 List 객체를 사용하여 중복없이 원소 값들을 저장하고 그 저장된 원소값들과 기존의 정수형 타입의 배열과 값을 비교하여 각각의 중복 값의 개수를 저장하는 배열을 리턴하고, 그 리턴한 배열 속에서 중복 개수가 가장 많은 배열의 index를 이용하여 삭제 횟수를 구하도록 비즈니스 로직을 구현하였습니다.

이걸 푼 다음에 해커랭크에서 다른 분들의 풀이방법을 보고나서... 정말 쉽게 풀수있는 방법이 많았습니다. 
