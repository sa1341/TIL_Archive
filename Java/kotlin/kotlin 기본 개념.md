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
위와 같이 선언만 먼저 하는 ㄱ ㅕㅇ우에는 반드시 타입을 적어줘야 합니다.
