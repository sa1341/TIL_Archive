# Java Stack 

A string containing only parentheses is balanced if the following is true: 1. if it is an empty string 2. if A and B are correct, AB is correct, 3. if A is correct, (A) and {A} and [A] are also correct.

Examples of some correctly balanced strings are: `"{}()", "[{()}]", "({()})"` 

Examples of some unbalanced strings are: `"{}(", "({)}", "[[", "}{"` etc.

Given a string, determine if it is balanced or not.


## Input Format

There will be multiple lines in the input file, each having a single non-empty string. You should read input till end-of-file.
The part of the code that handles input operation is already provided in the editor.

## Output Format
For each case, print 'true' if the string is balanced, 'false' otherwise.

## Sample Input

![스크린샷 2019-11-21 오전 2 15 40](https://user-images.githubusercontent.com/22395934/69261430-e0d9e400-0c04-11ea-8386-20fc43762e9c.png)


```java
import java.util.Scanner;
import java.util.Stack;

public class HelloWorld {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        while (sc.hasNext()) {

            String input = sc.next();
            Stack<Character> str = new Stack<Character>();
            
            System.out.println(isBalanced(input, str));
        }
    }


    public static boolean isBalanced(String input, Stack<Character> str) {

        boolean result = true;

        for (char c : input.toCharArray()) {
            if (c == '{' || c == '(' || c == '[') {
                str.push(c);
            }else{

                if (str.size() < 1) {
                    result = false;
                    break;
                }

                if(c == '}' && str.pop() != '{'){
                    result = false;
                    break;
                }

                if(c == ')' && str.pop() != '('){
                    result = false;
                    break;
                }

                if(c == ']' && str.pop() != '['){
                    result = false;
                    break;
                }
            }
        }
        if(str.size() != 0)
            result = false;

            return result;
    }
}
```

위의 문제는 스택 자료구조에 대해 알아야 싑게 풀수 있는 문제였습니다.
스택은 LIFO(Last In First Out) 후입선출 구조로 되어있습니다. 
한마디로 가장 늦게 들어온 값이 가장 먼저 나가는 자료구조입니다.
예를들어서....매일 아침마다 지하철을 가장 먼저 타지만 역을 거치면서 많은 사람들이 타게되고 결국 가장 먼저 온 제가 가장 늦게..지하철 문에 내리는 
저의 상황과 아이러니하게 맞아 떨어지는건 기분탓이 아니겠죠 ㅎㅎ?

문제는 열린 괄호와 닫힌 괄호의 수를 체크해야 하지만 괄호의 종류에 따라서 순서도 체크해야 하기 때문에 이 부분에서 고민을 하였습니다. 
예를들어서 `[{()}]` 이 경우에는 정상적인 균형잡힌 괄호들이지만 `([)]` 이 경우에는 균형잡힌 괄호라고 볼 수 없습니다. 따라서 아래코드에서 첫 부분에 닫힌 괄호가 들어오면 false를 리턴하고 반복문을 종료하도록 구현하였습니다.

```java
if (c == '{' || c == '(' || c == '[') {
    str.push(c);
}else{
    // 스택의 크기가 0이면서 닫힌 괄호일 경우에는 바로 false로 리턴합니다.
    if (str.size() < 1) {
        result = false;
        break;
    }
```

input 값에 문자들을 순회하면서 열린 괄호를 만날 경우에는 스택에 넣어주고 그 다음에 닫힌 괄호가 오면 스택에 맨 위에 있는 값을 꺼내어 그 괄호가 닫힌 괄호와 같은 종류인 열린 괄호라면 false 값을 넣지 않고 그 다음 값을 순회하거나 그 다음 문자가 없다면 처음에 true 값이 유지되기 때문에 결국 true를 리턴하도록 구현하였습니다.





