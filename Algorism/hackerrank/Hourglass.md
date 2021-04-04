## Hourglass 

# 문제

You are given a 6 * 6 2D array. An hourglass in an array is a portion shaped like this:

```java
a b c
  d
e f g
```
For example, if we create an hourglass using the number 1 within an array full of zeros, it may look like this:

```java
1 1 1 0 0 0
0 1 0 0 0 0
1 1 1 0 0 0
0 0 0 0 0 0
0 0 0 0 0 0
0 0 0 0 0 0
```
Actually, there are many hourglasses in the array above. The three leftmost hourglasses are the following:

```java
1 1 1     1 1 0     1 0 0
  1         0         0
1 1 1     1 1 0     1 0 0
```

The sum of an hourglass is the sum of all the numbers within it. The sum for the hourglasses above are 7, 4, and 2, respectively.
In this problem you have to print the largest sum among all the hourglasses in the array.


```java
import java.util.Scanner;

public class Hourglass {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int[][] arr = new int[6][6];

        for (int i = 0; i < 6; i++) {
            String[] arrRowItems = scanner.nextLine().split(" ");
            scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

            for (int j = 0; j < 6; j++) {
                int arrItem = Integer.parseInt(arrRowItems[j]);
                arr[i][j] = arrItem;
            }
        }
        int result = -1000;
        for (int i = 0; i < arr.length - 2; i++) {
            for (int j = 0; j < arr[i].length - 2; j++) {
                int sum = arr[i][j] + arr[i][j + 1] + arr[i][j + 2] + arr[i + 1][j + 1] + arr[i + 2][j] + arr[i + 2][j + 1] + arr[i + 2][j + 2];
                if (sum > result) result = sum;
            }
        }
        
        System.out.println(result);
        scanner.close();
    }
}
```
