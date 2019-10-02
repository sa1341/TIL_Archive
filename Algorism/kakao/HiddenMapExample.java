package kakao;

import java.util.Scanner;

public class HiddenMapExample {

    static final Scanner sc = new Scanner(System.in);

    public static String[] solution(int n, int[] arr1, int[] arr2) {

        String[] map = new String[n];

        for (int i = 0; i < n; i++) {

            String binary = Integer.toBinaryString(arr1[i]|arr2[i]); // or 연산으로 이진수 연산하는 메소드
            String format = String.format("%0"+n+"d", Integer.parseInt(binary)); // 배열의 크기가 n인 자릿수만큼 포맷지정
            System.out.println(format);

            map[i] = format.replace("1", "#").replace("0"," "); //1일때 "#", 0일때 " "으로 문자변경 메소드
            //System.out.println(map[i]);
        }


        return map;
    }


    public static void main(String[] args) {

        int n1 = 5;

        int[] testcase1_1	= {9, 20, 28, 18, 11};
        int[] testcase1_2 = {30, 1, 21, 17, 28};

        printResult(solution(n1, testcase1_1, testcase1_2));




        int n2 = 6;

        int[] arr1 = {46, 33, 33, 22, 31, 50};
        int[] arr2 = {27, 56, 19, 14, 14, 10};

        printResult(solution(n2, arr1, arr2));



    }


    public static void printResult(String[] result){

        for(String str : result){
            System.out.println(str);
        }

    }

}
