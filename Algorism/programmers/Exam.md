```java
package jungja_study;

import java.util.Scanner;

public class Solution {

    private static final Scanner sc = new Scanner(System.in);

    public static int[] solution(int[] answers) {

        int[] a = {1,2,3,4,5};
        int[] b = {2,1,2,3,2,4,2,5};
        int[] c = {3,3,1,1,2,2,4,4,5,5};


        int[] answer = getRank(a,b,c,answers);
        return answer;
    }

    public static int[] getRank(int[] a, int[] b, int[] c, int[] answer){

        int[] rank = new int[3];

        rank[0] = getAnswerCount(a, answer);
        rank[1] = getAnswerCount(b, answer);
        rank[2] = getAnswerCount(c, answer);

        for (int i = 0; i < rank.length; i++) {



            answer[i] = String.valueOf(rank[i]).indexOf(rank[i]);


        }







        return null;
    }


    public static int getAnswerCount(int[] value, int[] answer){

        int length = value.length;
        int count = 0;

        for (int i = 0; i < answer.length; i++) {
            if(answer[i] == value[i % value.length]) {
                count++;
            }
        }

        System.out.println(count);

        return count;
    }



    public static void main(String[] args) {

        int num = sc.nextInt();

        int[] answer = new int[num];

        for (int i = 0; i < answer.length; i++) {

            System.out.print("answer[" + i + "] :");
            answer[i] = sc.nextInt();
        }


        int[] result = solution(answer);

    }


}


```
