Lisa just got a new math workbook. A workbook contains exercise problems, grouped into chapters. Lisa believes a problem to be special if its index (within a chapter) is the same as the page number where it's located. The format of Lisa's book is as follows:

직역:리사는 방금 새로운 수학 문제집을 얻었습니다. 문제집은 챕터 단위로 구성된 연습 문제들을 포함합니다. 리사는 만약 챕터 안의 문제 번호가 그것이 위치한 페이지 번호랑 동일하면 문제가 특별하다고 믿고 있습니다. 그 리사의 문제집 양식은 다음과 같습니다. 


- There are  chapters in Lisa's workbook, numbered from  to .(리사의 문제집 챕터들은 1부터 n번까지 있습니다.)
- The  chapter has  problems, numbered from 1 to n. (챕터들은 1부터 n번까지 문제들을 가지고 있습니다.)
- Each page can hold up to  problems. Only a chapter's last page of exercises may contain fewer than  problems. (각각의 페이지는 문제들을 저장할 수 있습니다. 오직 한 챕터의 마지막 페이지의 연습문제는 페이지가 가질 수 있는 문제들의 수보다 적을 수 있습니다.)
- Each new chapter starts on a new page, so a page will never contain problems from more than one chapter. (각각의 새로운 챕터는 새 페이지로 시작합니다. 그래서 페이지는 결고 한 챕터보다 더 많은 문제들을 포함하지 않을 것입니다.)
- The page number indexing starts at .

Given the details for Lisa's workbook, can you count its number of special problems?

For example, Lisa's workbook contains arr[1] = 4 problems for chapter 1, and arr[2] = 2 problems for chapter 2. Each page can hold  k = 3 problems. The first page will hold 3 problems for chapter 1. Problem 1 is on page 1, so it is special. Page 2 contains only Chapter 1, Problem 4, so no special problem is on page 2. Chapter 2 problems start on page 3 and there are 2 problems. Since there is no problem 3 on page 3, there is no special problem on that page either. There is 1 special problem in her workbook.

이 문제를 이해하는데 너무 오래걸렸습니다. 오랜만에 알고리즘을 풀어본 것도 있지만... 한글로 직역해도 이해하는데 한참 걸릴 것 같습니다. 난이도는 easy인데... 이런 난이도도 이해 못하는 제가 원망스러웠습니다. 

설명을 하자면 각 페이지마다 최대로 가질 수 있는 페이지 수(k = 3)가 있습니다. 
여기서는 페이지당 3개의 문제만 저장할 수 있습니다. 예를 들어, 챕터1는 4개 연습문제로 구성되어 있고, 챕터2는 2개의 문제로 구성되어 있습니다. 챕터 1의 첫 번째 페이지에는 연습문제 1번이 있습니다. 그래서 그것은 특별한 문제입니다. 챕터 1의 두번째 페이지는 오직 4번 문제만 포함합니다. 2 페이지에는 특별한 문제가 없습니다. 챕터 2 문제들은 3페이지 부터 시작합니다. 그리고 연습문제 1, 2가 있습니다. 연습문제 3번이 없기 때문에 특별한 연습문제는 없습니다. 따라서 리사의 문제지에서 특별한 연습문제는 1개만 있습니다.


#### Sample Input

```java
5 3  
4 2 6 1 10
```

#### Sample Output

```java
4
```

Sample Input에서 첫 번째 줄 파라미터들은 챕터 수, 페이지가 최대한 가지는  


![image](https://user-images.githubusercontent.com/22395934/101984254-77bb8780-3cc3-11eb-9c73-264c099e1f43.png)


### 작성한 코드

```java
import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

public class Solution {

    // Complete the workbook function below.
    static int workbook(int n, int k, int[] arr) {
        
        // 특별한 문제의 총 개수
        int specialProblemCount = 0;
        // 페이지 번호는 1번부터 시작
        int pageNo = 1;

        for (int i = 0; i < n; i++>) {
            // 챕터가 가지고 있는 연습문제 수
            int problemCount = arr[i];
            for (int j = 1; j <= problemCount; j++) {
                 // 페이지 번호랑 연습문제 번호랑 같으면 특별한 문제 개수 증가
                if (pageNo == j)
                    specialProblemCount++;
                // 페이지가 가질 수 있는 최대 연습문제 개수와 챕터가 가지고 있는 연습문제 개수를 나눈 몫이 0이거나, 챕터의 마지막 연습문제까지 오면 페이지 번호를 증가시킵니다.
                if (j % k == 0 || j == problemCount)            
                    pageNo++;
            }
        }
        return specialProblemCount;
    }

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        String[] nk = scanner.nextLine().split(" ");
        // 챕터 수
        int n = Integer.parseInt(nk[0]);
        // 페이지마다 가질 수 있는 최대 연습문제 개수
        int k = Integer.parseInt(nk[1]);

        // 챕터들이 가지고 있는 연습문제 개수를 저장하기 위한 정수형 배열
        int[] arr = new int[n];
        
        // Scanner 객체에서 제공하는 nextLine() 메서드 버그로 인해서 사용
        // 위에 정수형 타입의 값을 입력받을 때 개행문자가 포함되어서 다음 입력값을 받을 경우에 건너뛰는 문제 발견
        scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");
        // 챕터들이 가지고 있는 연습문제 개수를 입력받습니다.
        String[] arrItems = scanner.nextLine().split(" ");
       

        for (int i = 0; i < n; i++) {
            int arrItem = Integer.parseInt(arrItems[i]);
            arr[i] = arrItem;
        }

        int result = workbook(n, k, arr);

        bufferedWriter.write(String.valueOf(result));
        bufferedWriter.newLine();
        bufferedWriter.close();

        scanner.close();
    }
}
```

위의 문제는 사실 페이지 번호를 증가시키는 조건식에 대해서 충분히 생각을 하면 풀 수도 있었던 문제일거 같은데.. 너무 어렵게 생각을 했다. 사실 easy부터 이렇게 막히면서 어릴 때 수학을 포기했던게 너무나도 후회됩니다. 이러한 유형의 문제에 대해서 잘 대처할 수 있도록 더 노력해야 될 것 같습니다.

#### 참조: https://www.hackerrank.com/challenges/lisa-workbook/problem
