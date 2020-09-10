## 정규표현식

정규표현식(Regular Expression)이란 컴퓨터 과학의 정규언어로부터 유래한 것으로 특정한 규칙을 가진 문자열의 집합을 표현하기 위해 쓰이는 형식 언어입니다. 개발을 하다보면 전화번호, 주민등록번호, 이메일 등과 같이 정해져있는 형식이 있고 사용자가 그 형식대로 제대로 입력했는지 검증해야 하는 경우가 종종 있습니다. 이런 입력값을 정해진 형식에 맞는지 검증해야 할 때에는 정규표현식을 쉽게 구현할 수 있습니다.

|  <center>정규 표현식</center> | <center>설명</center> 
|:--------|:--------:|
|  ^[0-9]*$ | <center>숫자</center> |  
| ^[a-zA-Z]*$ | <center>영문자</center> | 
| ^[가-힣]*$ | <center>한글</center> | 
| \\\w+@\\\w+\\\.\\\w+{\\\\.\\\w+}? | <center>Email</center> | 
| ^\\d{2,3}-\\d{3,4}-\\d{4}$ | <center>전화번호</center> | 
| ^01(?:0\|1\|[6-9])-(?:\\d{3}\|\\d{4})-\\d{4}$	 | <center>휴대전화번호</center> | 
| \\d{6} \- [1-4]\\d{6}| <center>주민등록번호</center> | 
| ^\\d{3}-\\d{2}$ | <center>우편번호</center> | 

## Pattern 클래스

정규 표현식에 대상 문자열을 검증하는 기능은 java.util.regex.Pattern 클래스의 mathches() 메소드를 활용하여 검증할 수 있습니다. matches() 메서드의 첫번째 매개값은 정규표현식이고 두번째 매개값은 검증 대상 문자열입니다. 검증 후 대상 문자열이 정규표현식과 일치하면 true, 그렇지 않다면 false를 리턴합니다.

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternExample {

    public static void main(String[] args) {

        // 숫자만
        String pattern = "^[0-9]*$";
        // 대상 문자열
        String val = "123456789";

        // 대상 문자열과 패턴이 일치할 경우 true 반환
        boolean regix = Pattern.matches(pattern, val);
        System.out.println("regix: " + regix);
    }
}
```

### 실행 결과: true

위 예제는 Pattern 클래스의 matches() 메서드를 활용하여 대상문자열이 숫자인지 아닌지 검증하는 예제입니다. 대상문자열이 숫자가 맞다면 true, 그렇지 않다면 false가 출력됩니다.


Pattern 클래스 주요 메서드

- compile(String regex) : 주어진 정규표현식으로부터 패턴을 만듭니다.
- matcher(CharSequence input) : 대상 문자열이 패턴과 일치할 경우 true를 반환합니다.
- asPredicate() : 문자열을 일치시키는 데 사용할 수있는 술어를 작성합니다.
- pattern() : 컴파일된 정규표현식을 String 형태로 반환합니다.
- split(CharSequence input) : 문자열을 주어진 인자값 CharSequence 패턴에 따라 분리합니다.

Parttern 플래그 값 사용(상수)
- Pattern.CANON_EQ : None표준화된 매칭 모드를 활성화합니다.
- Pattern.CASE_INSENSITIVE : 대소문자를 구분하지 않습니다. 
- Pattern.COMMENTS : 공백과 #으로 시작하는 주석이 무시됩니다. (라인의 끝까지).
- Pattern.MULTILINE : 수식 ‘^’ 는 라인의 시작과, ‘$’ 는 라인의 끝과 match 됩니다.
- Pattern.DOTALL : 수식 ‘.’과 모든 문자와 match 되고 ‘\n’ 도 match 에 포함됩니다.
- Pattern.UNICODE_CASE : 유니코드를 기준으로 대소문자 구분 없이 match 시킵니다.
- Pattert.UNIX_LINES : 수식 ‘.’ 과 ‘^’ 및 ‘$’의 match시에 한 라인의 끝을 의미하는 ‘\n’만 인식됩니다.

## Matcher 클래스

Matcher 클래스는 대상 문자열의 패턴을 해석하고 주어진 패턴과 일치하는지 판별할 때 주로 사용됩니다. Matcher 클래스의 입력값으로 CharSequence라는 새로운 인터페이스가 사용되는데 이를 통해 다양한 형태의 입력 데이터로부터 문자 단위의 매칭 기능을 지원 받을 수 있습니다. Matcher 객체는 Pattern 객체의 matcher() 메소드를 호출하여 받아올 수 있습니다.

```java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternExample {

    public static void main(String[] args) {

        // 주어진 정규표현식으로부터 패턴을 만듭니다.
        Pattern pattern = Pattern.compile("^[a-zA-Z]*$");
        // 컴파일된 정규표현식을 String 형태로 리턴
        String result = pattern.pattern();
        System.out.println(result);

        String input = "abcdef";
        Matcher matcher = pattern.matcher(input);
        // 대상 문자열과 패턴이 일치할 경우, true 리턴 후 그 위치로 이동합니다.
        System.out.println(matcher.find());
    }
}
```

### 실행결과: true

## 유효성 검사

정규 표현식은 유효성 검사 코드 작성 시 가장 효율적인 방법입니다.

```java
import java.util.regex.Pattern;

public class RegixExample {

    public static void main(String[] args) {
        String name = "임준영";
        String tel = "010-7900-1341";
        String email = "test@naver.com";

        // 유효성 검사
        boolean name_check = Pattern.matches("^[가-힣]*$", name);
        boolean tel_check = Pattern.matches("^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", tel);
        boolean email_check = Pattern.matches("\\w+@\\w.\\w+(\\.\\w+)?", email);

        // 출력
        System.out.println(name_check);
        System.out.println(tel_check);
        System.out.println(email_check);
    }
}
```

### 실행결과

![image](https://user-images.githubusercontent.com/22395934/92730515-d9134800-f3ae-11ea-87dd-e68f46c4ce38.png)

## 정규표현식 문법

- ^: 문자열 시작
- $: 문자열 종료
- *: 앞 문자가 없을 수도 무한정 많을 수도 있음
- {}: 횟수 또는 범위를 나타냅니다.
- []: 문자의 집합이나 범위를 나타내며 두 문자 사이는 - 기호로 범위를 나타냅니다. [] 내에서 ^가 선행하면 존재하면 not을 나타냅니다.
- (): 소괄호 안의 문자를 하나의 문자로 인식
- \\: 정규 표현식 역슬래시(\\)는 확장문자
- |: 패턴 안에서 or 연산을 수행할 때 사용
- \\d: 숫자 [0-9]와 동일함
- \\D: 숫자를 제외한 모든 문자
- ?: 앞 문자가 없거나 하나 있음
- +: 앞 문자가 하나 이상
- .: 임의의 한 문자(단, \\은 넣을 수 없음)


#### 참조: https://coding-factory.tistory.com/529?category=758267
