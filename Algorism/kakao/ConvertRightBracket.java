package kakao;

public class ConvertRightBracket {


    // 올바른 괄호인지 판단하는 메소드
    public boolean isRightBracket(String p) {

        int temp = 0;

        for (int i = 0; i < p.length(); i++) {

            if (p.charAt(i) == '(') temp++;
            if (p.charAt(i) == ')') temp--;
            if (temp < 0) return false;

            //String a = "(()())()";
        }
        return true;
    }

     // 올바른 괄호 문자열이 아니면 올바른 괄호 문자열로 변경하는 메소드
    public String convertBracket(String p) {

        //문자열 길이가 0 이거나 ""(빈문자열)이면 "" 리턴
        if (p.equals("") || p.length() == 0)
            return "";

        String u = balancedBracket(p);
        String v = p.substring(u.length());


        StringBuilder sb = new StringBuilder();

        if (isRightBracket(u)) {
            sb.append(u);
            sb.append(convertBracket(v));
            return sb.toString();
        } else {
            sb.append("(");
            sb.append(convertBracket(v));
            sb.append(")");
            sb.append(removeAndReverse(u));
            return sb.toString();
        }
    }

    // 괄호 문자 w를 u, v로 분기하는 메소드
    public String balancedBracket(String w) {

        int temp = 0;

        for (int i = 0; i < w.length(); i++) {

            if (w.charAt(i) == '(') temp++;
            if (w.charAt(i) == ')') temp--;

            if (i >= 1 && temp == 0) return w.substring(0, i + 1);
        }

        return "";
    }

    // 괄호 문자열 맨 앞 뒤 제거 후 남은 문자열 역순으로 나열하기
    public String removeAndReverse(String u) {
        return reverse(u.substring(1, u.length() - 1));
    }

    // 문자열 역순화 하는 
    public String reverse(String u){

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < u.length(); i++) {

            if(u.charAt(i) == '(')
                sb.append(")");

            if(u.charAt(i) == ')')
                sb.append("(");

        }

        return sb.toString();
    }



    public String soluton(String p) {

        if (isRightBracket(p)) return p;
        else return convertBracket(p);

    }


    public static void main(String[] args) {

        ConvertRightBracket c = new ConvertRightBracket();
        String p = "()))((()";
        System.out.println(c.soluton("()))((()"));
        System.out.println(c.soluton(")("));
        System.out.println(c.soluton("()))((()"));


    }


}

/*
1. 입력이 빈 문자열인 경우, 빈 문자열을 반환합니다.
2. 문자열 w를 두 "균형잡힌 괄호 문자열" u, v로 분리합니다. 단, u는 "균형잡힌 괄호 문자열"로 더 이상 분리할 수 없어야 하며, v는 빈 문자열이 될 수 있습니다.
3. 문자열 u가 "올바른 괄호 문자열" 이라면 문자열 v에 대해 1단계부터 다시 수행합니다.
  3-1. 수행한 결과 문자열을 u에 이어 붙인 후 반환합니다.
4. 문자열 u가 "올바른 괄호 문자열"이 아니라면 아래 과정을 수행합니다.
  4-1. 빈 문자열에 첫 번째 문자로 '('를 붙입니다.
  4-2. 문자열 v에 대해 1단계부터 재귀적으로 수행한 결과 문자열을 이어 붙입니다.
  4-3. ')'를 다시 붙입니다.
  4-4. u의 첫 번째와 마지막 문자를 제거하고, 나머지 문자열의 괄호 방향을 뒤집어서 뒤에 붙입니다.
  4-5. 생성된 문자열을 반환합니다.



 */
