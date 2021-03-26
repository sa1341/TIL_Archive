## MyRegex.java

정규식으로 IP의 유효범위를 체크하는 예제 코드 입니다.


```java
import java.util.Scanner;


class Solution {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        while (in.hasNext()) {
            String IP = in.next();
            System.out.println(IP.matches(new MyRegex().pattern));
        }

    }
}


//Write your code here
class MyRegex {
    String pattern = "^([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d?\\d|2[0-4]\\d|25[0-5])\\."
            + "([01]?\\d?\\d|2[0-4]\\d|25[0-5])$";
}
```

첫번째 자리인 ^[01] 0또는 1부터 값이 올 수 있고 ?가 뒤에 오는데 이 의미는 0 또는 1을 생략할 수 있다는 뜻입니다. 두 번째 자리 \\d는 [0-9]와 같고, 마찬가지로 생략이 가능합니다. 
세 번째 자리는 무조건 0 ~ 9 범위에서 값이 들어와야 됩니다. 

