# Kotlin 정보은닉 캡슐화

각 클래스나 메서드, 프로퍼티의 접근 범위를 가시성(Visibility)이라고 합니다. 클래스에서 민감하거나 불필요한 부분은 숨기고 사용하기 위해 필요한 부분만 공개하듯이 각 클래스나 메서드, 프로퍼티에 가시성 지시자에 의해 공개할 부분과 숨길 부분을 정할 수 있습니다.

#### 가시성 지시자

>
- private: 이 요소는 외부에서 접근할 수 없습니다.
- public: 이요소는 어디서든 접근이 가능합니다.(기본값)
- protected: 외부에서 접근할 수 없으나 하위 상속 요소에서는 가능합니다.
- internal: 같은 정의의 모듈 내부에서는 접근이 가능합니다.


## 1. private 지시자

private은 접근 범위가 선언된 요소에 한정하는 가시성 지시자입니다. 만약 클래스를 private와 함께 선언하면 그 클래스 안의 맴버만 접근할 수 있습니다.

```kotlin
private class PrivateClass {
    private var i = 1
    private fun privateFunc() {
        i += 1
    }

    fun access() {
        privateFunc()
    }
}

class OtherClass {
    private val opc = PrivateClass() // private를 생략하면 접근 불가합니다. 
    fun test() {
       val pc = PrivateClass()
    }
}

fun main() {
    val pc = PrivateClass() // 생성 가능
    pc.i // 접근 불가
    pc.privateFunc() // 접근 불가
}

fun TopFunction() {
    val tpc = PrivateClass() // 객체 생성 가능
}
```

위 예제에서는 PrivateClass 클래스는 private으로 선언되어 있으므로 다른 파일에서 접근할 수 없습니다. 만일 다른 클래스에서 프로퍼티로서 PrivateClass에 접근하려하면 똑같이 private으로 선언해야 합니다.


## 2. protected 지시자

protected 지시자는 최상위에 선언된 요소에는 지정할 수 없고 클래스나 인터페이스와 같은 요소의 멤버에만 지정할 수 있습니다. 맴버가 클래스인 경우에는 protected로 선언할 수 있습니다.


```kotlin
open class Base {
    protected var i = 1
    protected fun protectedFunc() {
        i += 1
    }

    fun access() {
        protectedFunc()
    }
    protected class Nested // 내부 클래스에는 지시자 허용
}

class Derived: Base() {
    fun test(base: Base): Int {
        protectedFunc()
        return i
    }
}

fun main() {
    val base = Base()
    // base.i // 접근 불가
    // base.protectedFunc() // 접근 불가
    base.access()
}
```

## 3. internal

코틀린의 internal은 자바와 다르게 새롭게 정의된 이름입니다. internal 키워드를 사용합니다. 이 지시자는 모듈 내부에서만 접근이 가능하고, 모듈이 달라지면 접근이 불가능합니다.  


> Java의 package로 지정된 경우 접근 요소가 패키지 내부에 있다면 접근할 수 있습니다. 하지만 프로젝트 단위 묶음의 .jar 파일이 달라져도 패키지 이름이 동일하면 다른 .jar에서도 접근할 수 있었기 때문에 보안 문제가 발생 할 수 있습니다. 코틀린에서는 이것을 막고자 package를 버리고 internal로 프로젝트의 같은 모듈이 아니면 외부에서 접근할 수 없게 했습니다.

```kotlin
internal class InternalClass {
    internal var i = 1

    internal fun icFunc() {
        i += 1
    }

    fun access() {
        icFunc()
    }
}

class Other {
    internal val ic = InternalClass()
    fun test() {
        ic.i // 접근허용
        ic.icFunc() // 접근허용
    }
}

fun main() {
    val mic = InternalClass() // 생성 가능
    mic.i // 접근 허용
    mic.icFunc() // 접근 허용
}
```

이제 같은 프로젝트 모듈에만 있으면 어디서든 접근이 가능합니다. 


최종적으로 자동차와 도둑의 예제를 통해서 가시성에 대해서 더 확실하게 이해할 수 있었습니다.

## 4. 예제 코드

```kotlin
open class Car protected constructor(_year: Int, model: String, _power: String, _wheel: String) {
    private var year: Int = _year
    public var model: String = model
    protected open var power: String = _power
    internal var wheel: String = _wheel

    protected fun start(key: Boolean) {
        if (key) println("Start the Engine!")
    }

    class Driver(_name: String, _license: String) {
        private var name: String = _name
        var license: String = _license
        internal fun driving() = println("[Driver] Driving() - $name")
    }
}

class Tico(_year: Int, model: String, _power: String, _wheel: String, var name: String, private var key: Boolean): Car(_year, model, _power, _wheel) {
    override var power: String = "50hp"
    val driver = Driver(name, "first Class")

    constructor(_name: String, _key: Boolean): this(2014, "basic", "100hp", "normal", _name, _key) {
        name = _name
        key = _key
    }

    fun access(password: String) {
        if (password == "gotico") {
            println("------[Tico] access()--------")
            println("super.model = ${super.model}")
            println("super.power = ${super.power}")
            println("super.wheel = ${super.wheel}")
            super.start(key)

            println("Driver().license = ${driver.license}")
            driver.driving()
        } else{
            println("You're a burglar")
        }
    }
}


class Burglar() {

    fun steal(anycar: Any) {
        if (anycar is Tico) {
            println("--------[Burglar] steal()----------")
            println("anycar.name = ${anycar.name}")
            println("anycar.wheel = ${anycar.wheel}")
            println("anycar.model = ${anycar.model}")

            println(anycar.driver.license)
            anycar.driver.driving()
            anycar.access("dontknow")
        } else {
            println("Nothing to steal")
        }
    }
}

fun main() {
    // val car = Car()
    val tico = Tico("kildong", true)
    tico.access("gotico")

    val burglar = Burglar()
    burglar.steal(tico)
}
```

Car 클래스는 상속이 가능하고, 이 클래스를 상속하는 Tico 클래스를 정의하였습니다. 그리고 도둑을 의미하는 Burglar로 정의하였습니다.

여기서 주의깊게 볼점은 아래와 같습니다.

1. Car 클래스의 주 생성자는 protected 지시자가 있기 때문에 constructor 키워드를 생략할 수 없으며 Car 클래스를 상속한 클래스만이 Car 클래스의 객체를 생성할 수 있습니다. 

2. Driver 클래스는 Car 클래스 안에 있고, Car 클래스를 상속받는 Tico 클래스에서는 access() 메서드에서 super 키워드를 사용해 상위 클래스에 접근을 시도합니다. 이때 private 지시자가 적용된 year에는 접근이 불가합니다. 


3. Burglar 클래스를 살펴보면 steal() 메서드 하나만 정의하고 있습니다. Any 자료형의 매개변수인 anycar를 받아서 검사하고 있습니다. 이때 자료형 검사 키워드인 is를 사용해 Tico 의 객체인 경우 이 Tico 객체인 anycar를 통해 접근을 시도합니다. 특히 internal의 경우 파일이 달라져도 같은 모듈에 있으면 접근이 가능합니다.

> 참조 문헌: Do it 코틀린 프로그래밍

