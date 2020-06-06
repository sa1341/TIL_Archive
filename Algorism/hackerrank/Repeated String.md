## Repeated String 문제

Lilah has a string, of lowercase English letters that she repeated infinitely many times.
Given an integer, , find and print the number of letter a's in the first  letters of Lilah's infinite string.
For example, if the string  and , the substring we consider is , the first  characters of her infinite string. There are  occurrences of a in the substring.
Function Description
Complete the repeatedString function in the editor below. It should return an integer representing the number of occurrences of a in the prefix of length  in the infinitely repeating string.

### repeatedString has the following parameter(s):
* s: a string to repeat
* n: the number of characters to consider

### Input Format
- The first line contains a single string s
- The second line contains an integer n

### Output Format

Print a single integer denoting the number of letter a's in the first  letters of the infinite string created by repeating  infinitely many times.

![image](https://user-images.githubusercontent.com/22395934/83942786-b57db600-a831-11ea-8575-eea2346ee8a1.png)

```java
import java.io.IOException;
import java.util.Scanner;

public class RepeatedStringEx {

    private static final Scanner sc = new Scanner(System.in);

    private static long repeatedString(String s, long n) {

        long length = s.length();
        long mok = n / length;
        long rem = n % length;
        long count = 0;

        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == 'a') {
                count++;
            }
        }

        int remCount = 0;
        for (int i = 0; i < rem; i++) {
            if (s.charAt(i) == 'a') {
                remCount++;
            }
        }

        long result = (count * mok) + remCount;
        return result;
    }

    public static void main(String[] args) throws IOException {

        String s = sc.nextLine();
        long n = sc.nextLong();
        sc.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        long result = repeatedString(s, n);
        System.out.println(result);
        sc.close();
    }
}
```



문제의 핵심은 두 번째 파라미터로 주어진 정수 타입의 수만큼 문자를 반복하는데 이 때 `a`라는 문자가 들어가는 횟수를 구하는게 핵심입니다.
사실 처음에 어렵게 생각하지 않는 문제였는데 두 번째 파라미터로 받는 반복 횟수가 long 타입이기 때문에 Integer 타입으로 표현할 수 없는 큰 수가 입력되는 경우 단순히
for문을 이용해서 풀게 되면 타임아웃이 발생하는 문제가 생긴다는 것을 알게 되었고, 이러한 문제를 해결하기 위해서는 첫 번째 파라미터로 받는 반복되는 문자열의 개수와 반복되는 총 횟수 n을 나누어서 
몫과 나머지를 이용해서 문제를 해결하는 방법을 생각하였습니다.

먼저, 주어진 반복 문자열의 길이에서 `a` 문자가 몇번 들어가는지 체크를 한 후에 n과 나눈 몫이랑 곱(*)을 하면 반복 횟수 * a 문자의 개수를 구할 수 가 있었습니다.
그리고 나머지(rem)만큼 for문을 돌면서 `a` 문자의 수를 체크해서 결과 값을 도출하였습니다. 




