We use the integers a, b, and n to create the following series:

You are given  queries in the form of a, b, and n. For each query, print the series corresponding to the given a, b, and n values as a single line of  space-separated integers.

Input Format

The first line contains an integer, q, denoting the number of queries.
Each line  of the subsequent lines contains three space-separated integers describing the respective a, b, and n values for that query.

Constraints

Output Format

For each query, print the corresponding series on a new line. Each series must be printed in order as a single line of  space-separated integers.


```java
import java.util.Scanner;

public class IfElseExample {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int t = in.nextInt();

        for (int i = 0; i < t; i++) {
            int a = in.nextInt();
            int b = in.nextInt();
            int n = in.nextInt();

            for (int j = 0; j < n; j++) {
                System.out.print(getValue(a, b, j) + " ");
            }
            System.out.println();
        }


        in.close();
    }

    public static int getValue(int a, int b, int n) {
        int temp = a;
        for (int i = 0; i <= n; i++) {
            temp += ((int) (Math.pow(2, i)) * b);
        }
        return temp;
    }
}
```
