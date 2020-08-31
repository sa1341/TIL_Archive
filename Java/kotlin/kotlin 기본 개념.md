## 코틀린을 공부하자

Intellij에서 코틀린 프로젝트를 생성하고, 마치 대학생때 가장 먼저 접하는 프로그래밍 대표 예제 `Hello Kotlin!`을 콘솔창에 띄우는 예제를 작성하였습니다.

```kotlin
fun main() {
    println("Hello Kotlin!")
}
```
위의 kotlin 파일에서 main() 함수만 정의하고 실행시키면 콘솔에 정상적으로 `Hello Kotlin!`이 출력됩니다.  

위의 main 함수는 JVM에서 실행되면 main 함수가 있는 파일 이름을 기준으로 자바 클래스가 자동생성 됩니다.

순서는 코틀린 -> 바이트 코드 순서로 JVM에서 실행됩니다.

위의 메서드를 decompile을 하면 아래와 같은 코드로 해석이됩니다.

```kotlin
public final class HelloKotlinKt {
    public static final void main() {
        String var0 = "Hello Kotlin!";
        boolean var1 = false;
        System.out.println(var0);
    }
}
```

위의 소스를 보면 main() 메서드는 HelloKotlinKt 클래스 안에 속한 멤버 메서드로 선언된 것을 알 수 있습니다. 이것은 자바 가상머신인 JVM에 실행되기 위해, 문자열은 String var1으로 선언되어 System.out.println()에 의해 콘솔 장치에 출력되는 것입니다.

## package

코틀린 프로젝트는 여러 개의 모듈로 구성됩니다. 보통 각각의 기능을 모듈 단위로 분리해서 관리하고, 그 안에 package를 둬서 클래스/파일들을 관리합니다.

## 변수 만들기

변수 선언 키워드는 2가지가 있습니다. 

- val: immutable 불변, 자바스크립트의 const, 자바의 String 클래스 같은 것입니다. 자바의 final 키워드랑은 살짝 다릅니다.

- var: mutable 가변, 일반적인 변수로 재할당이 가능합니다. 자바스크립트의 let과 같은 것입니다. 코틀린에서 변수를 선언하려면 반드시 저 둘중에 하나의 키워드를 사용해야 합니다.

>> 왠만하면 val 위주로 적용해서 안전성을 높이는 코딩을 지향해야하고, 꼭 가변적으로 할당되어야만 하는 변수에는 var를 사용하도록 합시다.


```kotlin
val username: String = "junyoung"
```

`변수선언키워드 변수명:타입=값` 이렇게 쓰면 됩니다. 코틀린 컴파일러가 타입을 추론할 수 있는 경우에는 타입을 생략해도 됩니다.

```kotlin
val username = "junyoung"
```

위와 같이 하면 값이 문자열인 것을 보고 코틀린 컴파일러가 username을 String 타입으로 추론합니다. 

주의할 점은 변수 선언과 할당을 동시에 하지 않을 경우에는 타입을 생략할 수 없습니다.

```kotlin
val name // 불가능
val name:String // 가능
name = "hello"
```
위와 같이 선언만 먼저 하는 경우에는 반드시 타입을 적어줘야 합니다.

ex) 변수 선언 예제

```kotlin
// val var 차이
val name:String = "immutable"
var age:Int = 20
name = "junyoung"  // val로 설정했으니까 컴파일 시점에 에러가 납니다.
age = 30
println("age: $age")  // $(달러표시)로 변수를 출력할 수 있습니다. 

// 선언과 할당
var number = 10
number = 20
var number2:Int // Int라고 타입을 줬으니까 선언만 해도 됩니다.
number2 = 30
val number4 // 이 변수는 타입을 가져야 하거나 초기화가 되어야된다는 에러가 납니다.
```
 
변수 타입 기본형(Primitive type)과 참조형(Reference type)

코틀린에서는 참조형을 사용해서 코딩해야 합니다.
참조형만 사용해도 괜찮은 이유는 컴파일러가 내부적으로 기본형으로 바꿔서 최적화된 바이트 코드로 만들어주기 때문이빈다.
(int, long, double,.. 이런 기본형 타입 대신 Long, Double,.. 같은 참조형을 써야하고 이렇게 해도 내부적으로 잘 바꿔서 씁니다.)

## 자료형 

- 정수 자료형
Long, Int, Short, Byte가 있습니다.
변수를 선언할 때 타입을 별도로 적지 않으면 코틀린이 타입을 추론해서 지정해줍니다. 만약 100이라는 값을 주고, 타입을 생략하면 Int로 추론하고 99933322233344455는 Long으로 추론합니다.
val ex1 = 100L 이렇게 접미사 L을 붙이면 Long 타입으로 추론합니다. 정수타입에서는 Int가 기본입니다. Short 범위 내 정수라고 해도 Short로 추론하지 않고 Int로 추론합니다.

```kotlin
val myLong = 99_933_322_233_344_455 // Long으로 추론
val ex1 = 0x0F // 16진수 표기 15
val ex2 = 0b0001111 // 2진수 표기 15
```

- 실수 자료형
기본적으로 double로 추론, 값에 F를 붙이면 Float 타입으로 추론합니다.

- 논리 자료형
val isOpen = true 이렇게 하면 자동으로 Boolean으로 추론합니다.

- 문자 자료형
"(single quote)으로 한 문자를 지정하면 Char로 추론합니다.

- 문자열 자료형
문자열 타입은 다른 것들과 다르게 기본형이 아니라 참조형 타입입니다. 값에 문자를 배열하면 String 타입으로 추론합니다. 특징적인 것은 자바와 마찬가지로 힙에 StringPool을 공유한다는 것입니다.

```kotlin
var string1 = "hello"
var string2 = "hello"
println(${string1 === string2}) // true
// string1과 string2가 참조하는 주소값이 같습니다.
```

문자열 내에 "(쌍따옴표)나 $(달러표시)를 쓰고 싶으면 백슬래시(\\)를 앞에 붙여서 사용하면 됩니다.

```kotlin
val expression = "\"hello\" this is \$10"
```

백슬래시 대신 ${""}이렇게 써도 쌍따옴표를 적용할 수 있습니다.

-""" 문자열 사용 법(문자열 그 자체 그대로 표현하는 방법)

```kotlin
val num = 10
val formattedString = """
    var a = 11
    var b = "hello kotlin"
    """
println(formattedString)
```

결과:

var a = 11

var b = "hello kotlin"


- 자료형에 별명 붙이기

```kotlin
typealias Username = String
val user:Username = "junyoung"
```
typealias 키워드를 이용해서 String 타입에 Username이라는 별명을 붙여서 사용했습니다. 

## 자료형 검사

- 코틀린 null

코틀린은 변수를 사용할 때 반드시 값이 할당되어 있어야 한다! 라는 원칙이 있습니다. (null을 싫어함) 지금까지 위에서 작성한 코드는 전부 다 변수에 null을 허용하지 않는 방식이 있습니다. 만약 null을 허용하고 싶으면 물음표(?) 기호를 타입 뒤에 붙여서 변수를 선언하면 됩니다.

```kotlin
// null 문제
val str:String = "hello_world"
str = null // 컴파일러에서 이미 에러 발생함
// 타입 뒤에 ? 붙이면 null이 할당될 수 있는 변수라는 의미입니다.
var str2:String? = "hello_world"
str2 = null
println(str2) // null 출력
```

```kotlin
// non-null 단정 기호와 세이프 콜
var nullableString:String? = "hello_kotlin"
nullableString = null
// println("nullableString: $nullableString.length: ${nullableString.length}") // .에서 컴파일 에러 발생

//String? 타입에서는 ?.(세이프콜) 또는 !!.(non-null 단정기호)만 쓸 수 있다고 나온다. 
//- safe call이란 말 그대로 안전하게 호출하도록 도와주는 기법이다. println("nullableString : $nullableString , length : ${nullableString?.length}") //출력 값 : nullableString : null , length : null
```

- 엘비스 연산자(?:)

위에서 배운 세이프 콜과 엘비스 연산자를 같이 사용하면 아주 아름다운 null 처리가 가능합니다.

```kotlin
var testString:String? = "hello_kotlin"
testString = null
println("testString length: ${testString?.length ?: -1}")
```

testString이 세이프 콜로 length에 접근하지 않고 null을 리턴한다고 하였으니 엘비스 연산자의 앞의 값은 null 입니다.
엘비스 연산자는 값이 null이면, 뒤에 미리 지정해 놓은 값을 리턴하는 연산자입니다. 따라서 출력은 -1로 지정해놓았으니까 -1이 나올 것입니다.

if 문으로 null 검사를 일일이 할 필요가 없어지고 처리까지 한 줄에 되므로 가독성도 좋아집니다.

## 자료형 변환

자바에서는 int 값을 double 변수에 할당하면 자동으로 타입이 변환되었습니다.

```java
int a = 3;
double d = a; // 3.0으로 자동 변환
```

코틀린은 자동으로 형 변환을 시켜주지 않습니다. 오히려 타입 미스매치 에러를 내면서 실수를 철저하게 방지합니다. 그래서 코틀린은 형변환 메서드를 제공합니다.

```kotlin
val intValue:Int = 3
val doubleValue:Double = intValue.toDouble()
val result = 1L + 3 // Long으로 형변환 -> 표현범위가 더 큰쪽으로 변환됩니다.
```

> 기본형과 참조형 타입 비교 원리

값 비교: ==, 값이 같으면 true 다르면 false

참조 주소 비교: ===, 값과 상관없이 참조 자체가 동일하면 true 다르면 false(값과 상관없지만 참조 자체가 동일하면 값도 같습니다...)

```kotlin
val one:Int = 128
val two:Int = 128

println(one == two) // true
println(one === two) // true

val three:Int - 128
val four:Int? = 128

println(three == four) // true
println(three === four) // false
// Int 형으로 선언된 three는 128 값 자체가 스택에 저장, Int? 형으로 서언된 four는 128이 힙 영역에 저장되어 있고, 그것의 주소로 저장합니다.

val data1:Int = 128 
val data2 = data1; 
println(data1 == data2)//기본형으로 int로 변환되어 값이 동일하므로 true val data3:Int? = data1 //128이라는 값을 힙에 할당하고 그것을 가리킨다. val data4:Int? = data1 //128이라는 값을 힙에 할당하고 그것을 가리킨다. val data5:Int? = data3 
println(data3 == data4)//true 값만 비교했는데 두 개의 값은 128로 같습니다.

println(data3 === data4) //false 
println(data3 === data5) // true

각각 값은 128로 동일하지만 힙에 생긴 128은 각각생기기 때문에 다른 주소를 가리키므로 주소비교는 false println(data3 === data5)//true data3는 128을 힙에 할당하고 그것을 가리키고 있다. data5는 data3를 가리키는데 data3가 힙의 128의 주소를 가리키고 있으므로 
//복사되었을 때 같은 힙에 있는 128을 가리킨다.
```

메모리에 힙에 생성되느냐 스택에 생성되느냐를 일단 알아야 하는데 간략하게 설명하면 null을 허용하지 않는 변수는 참조형을 사용하더라도 내부적으로 기본형으로 값을 갖게 되는데 그것이 스택에 직접 할당됩니다. 그러나 null을 허용하는 변수는 기본형으로 변환되는 참조형일지라도 자기만의 값을 힙에 하당하고 그것을 가리키는 주소를 스택공간에 할당합니다.

- 기본적으로 null을 허용한다는 것 그 자체로 그 변수를 객체로 본다는 뜻입니다. 기본형에는 null이란 것을 할당할 수 조차 없습니다.
- -128 ~ 127의 정수 값은 캐시에 저장되어 참조됩니다.

따라서 위의 예제에서 값을 만약 10으로 했다면 10이라는 값 자체가 캐시에 저장되고 모든 변수들은 그 캐시의 주소값을 저장하고 있을 것입니다.

## 스마트 캐스트
어떤 값이 정수일 때도 있고 실수일 때도 있으면 어떻게 해야할까요?
Number 타입이라는 것도 있습니다. (특수한 객체)
Number 타입으로 정의된 변수에는 정수, 실수 둘다 들어갈 수 있습니다.

```kotlin
var sc:Number = 3.14
println(sc) // 3.14 Double
sc = 3
println(sc) // 3 Int
sc = 30L
println(sc) // 30 Long
sc = 10.1F
println(sc) // 10.1 Float
```

#### 참조: https://jeong-pro.tistory.com/194 [기본기를 쌓는 정아마추어 코딩블로그]
