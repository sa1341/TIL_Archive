# BigDecimal이란?

최근에 금융쪽 도메인에서 개발 업무를 하는 친구한테 돈을 계산할때 자료형으로 double, float보다 BigDecimal이라는 자료형을 사용한다고 들었습니다. 그 이유를 물어보니 위 자료형(double, float)보다 BigDecimal이 미세한 숫자의 변동도 허용하지 않기 때문이라는 사실을 알게 되었습니다.

먼저, float과 double 타입의 표현 범위를 알아보았습니다.

| Type | 범위 |
|:-----|:-----|
| float| 1.4E-45 ~ 3.4028235E38 |
| double | 4.9E~324 ~ 1.7976931348623157E308 | 

소수점을 저장할 수 있는 타입인 `float과 double`은 소수점의 정밀도가 완벽하지 않아 값의 오차가 생길 수 있습니다. 소수점 이하의 수를 다룰 때 double과 float은 사칙연산 시 정확한 값을 출력하지 않을 수 있습니다. 그 이유는 내부적으로 수를 저장할 때 이진수의 근사치를 저장하기 때문입니다. BigDecimal은 이러한 문제점을 해결해주는 자료형입니다. 하지만 속도가 위 자료형들보다는 느리다는 단점이 있지만, 정확한 소수점을 다룰려면 사용해야 됩니다.

## 1. BigDecimal 용어 정리

- precision: 숫자를 구성하는 전체 자리수라고 생각하면 편하나, 정확하게 풀이하면 왼쪽이 0이 아닌 수가 시작하는 위치부터 오른쪽이 0이 아닌 수로 끝나는 위치까지의 총 자리수 입니다. `unscale`과 동의어 입니다. 

ex) 012345.67890의 precision은 11이 아닌 9입니다.

- scale: 전체 소수점 자리수라고 생각하면 편하나, 정확하게 풀이하면 소수점 첫째 자리부터 오른쪽부터 0이 아닌 수로 끝나는 위치까지의 총 소수점 자리수입니다. `fraction`과 동의어입니다.

ex) 012345.67890의 scale은 4입니다. 하지만 0.00, 0.0의 scale은 모두 1입니다. `BigDecimal`은 32bit의 소수점 크기를 가집니다.

- DECIMAL128: IEEE 754-2008에 의해 표준화된, 부호와 소수점을 수용하며, `최대 34자리까지 표현 가능한 10진수를 저장할 수 있는 형식입니다`

극단적으로 미국 정부의 총 부채액이 15조 7천 500억 달러로 총 14자리임을 감안하면, 금융권에서 처리되는 대부분의 금액을 수용할 수 있는 크기입니다. Java에서는 BigDecimal 타입을 공식적으로 지원합니다.


## 2. BigDecimal 선언

```java
public class BigDecimalExam {
   
    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("10000.12345");
        System.out.println(bigDecimal);
    }
}
```

BigDecimal은 java.math 패키지 안에 존재하며 위와 같이 선언합니다. double 타입으로 초기화를 해줘도 되지만 기대값과 다르기 때문에 일반적으로 문자열로 인자 값을 넘겨줘야 의도한 값이 출력됩니다. 

> BigDecimal의 생성자를 호출 할때 BigDecimal.valueOf()를 사용해서 객체를 생성할 수 있는데, 내부적으로 0부터 10까지 캐싱을 하고 있기 때문에 new BigDecimal("123.9")보다 성능이 더 좋습니다. 또한 double 실수 값을 생성자로 생성해도 연산결과가 정확하지 않을 수 있으니, valueOf()를 사용하여 생성하는 것이 좋다고 합니다.

## 3. BigInteger 계산

BigDecimal은 문자열 타입이기 때문에 Integer 타입처럼 사칙연산이 불가능합니다. 그래서 내부적으로 정의되어 있는 메서드를 통해서 사칙연산을 수행합니다.

```java
public class BigDecimalExam {

    public static void main(String[] args) {

        BigDecimal number1 = new BigDecimal("10000.12345");
        BigDecimal number2 = new BigDecimal("10000");

        System.out.println(number1.add(number2));
    }
}
```

#### 실행 결과

```java
20000.12345
0.12345
100001234.50000
1.000012345
0.12345
```

## 4. BigDecimal 형변환

```java
public class BigDecimalExam {

    public static void main(String[] args) {

        BigDecimal bigDecimal = new BigDecimal("10000.12345");

        int intNum = bigDecimal.intValue();
        long longNum = bigDecimal.longValue();
        float floatNum = bigDecimal.floatValue();
        double doubleNum = bigDecimal.doubleValue();
        String strNum = bigDecimal.toString();
    }
}
```

#### 실행 결과

```java
10000
10000
10000.123
10000.12345
10000.12345
```

## 5. BigDecimal 두 수 비교

```java
public class BigDecimalExam {

    public static void main(String[] args) {

        BigDecimal bigDecimal1 = new BigDecimal("10000.82345");
        BigDecimal bigDecimal2 = new BigDecimal("10000.6789");

        // 두 수 비교 compareTo 맞으면 0, 틀리면 -1
        int compare = bigDecimal1.compareTo(bigDecimal2);
        System.out.println(compare);
    }
}
```
bigDecimal1이 크면 1을, 작으면 -1, 같으면 0을 리턴합니다.

#### 실행 결과
```java
1
```

## 6. BigDecimal 기본 상수

- float, double 타입과 다르게 BigDecimal 타입은 초기화가 장황한 편입니다. 자주 쓰는 0, 1, 100은 쓰기 편리하게 미리 상수로 정의되어 있습니다.

```java
public class BigDecimalExam {

    public static void main(String[] args) {
        System.out.println(BigDecimal.ZERO);
        System.out.println(BigDecimal.ONE);
        System.out.println(BigDecimal.TEN);
    }
}
````

#### 실행 결과

```java
0
1
10
```

> 참조 사이트: https://coding-factory.tistory.com/605, https://jsonobject.tistory.com/466, https://velog.io/@new_wisdom/Java-BigDecimal%EA%B3%BC-%ED%95%A8%EA%BB%98%ED%95%98%EB%8A%94-%EC%95%84%EB%A7%88%EC%B0%8C%EC%9D%98-%EB%84%88%EB%93%9C%EC%A7%93
