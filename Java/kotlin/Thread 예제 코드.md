# Kotlin Thread 예제코드


```kotlin
class SimpleThread: Thread() {
    override fun run() {
        println("Current Threads: ${Thread.currentThread()}")
    }
}

class SimpleRunnable: Runnable {

    override fun run() {
        println("Current Threads: ${Thread.currentThread()}")
    }
}

fun main(args: Array<String>) {

    val thread = SimpleThread()
    thread.start()

    val runnable = SimpleRunnable()
    val thread1 = Thread(runnable)
    thread1.start()

    object: Thread() {
        override fun run() {
            println("Current Threads: ${Thread.currentThread()}")
        }
    }.start()
}
```

이렇게 보면 Java에서 쓰레드를 생성하는 방식과는 크게 다르다고 생각하지 않았습니다.
그나마 생소해보이는건 `object: Trhead()`구문에서 쓰레드를 익명함수 방식으로 구현하는것입니다.

## Thread 옵션을 설정하여 실행하기

```kotlin
fun main(args: Array<String>) {

    // 스레드의 옵션 변수를 손쉽게 설정할 수 있습니다.
    thread(start = true) {
        println("Current Threads(Custom function): ${Thread.currentThread()}")
        println("Priority: ${Thread.currentThread().priority}")
        println("Name: ${Thread.currentThread().name}")
        println("Name: ${Thread.currentThread().isDaemon}")
    }
}

public fun thread(start: Boolean = true, isDaemon: Boolean = false,
                  contextClassLoader: ClassLoader? = null, name: String? = null,
                  priority: Int = -1, block: () -> Unit): Thread {


    val thread = object: Thread() {
        override fun run() {
            block()
        }
    }

    if (isDaemon) {
        thread.isDaemon = true
    }

    if (priority > 0) {
        thread.priority = priority
    }

    if (name != null) {
        thread.name = name
    }

    if (contextClassLoader != null) {
        thread.contextClassLoader = contextClassLoader
    }

    if (start)
        thread.start()

    return thread
}
```

Kotlin에서 메서드를 정의하고 실제 호출할때 나머지 argument 값들이 이미 메서드 내에서 정의되어 있다면 `start = true`로 원하는 argument만 넘겨서 호출할 수 있는 장점이 있습니다.

## async

async 메서드도 마찬가지로 코루틴 블록을 생성하는 빌더입니다. 

```kotlin
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

private fun worksInParallel() {

    // Deferred<T>를 통해 결괏값을 반환함.
    val one = GlobalScope.async {
        doWork1()
    }

    val two = GlobalScope.async {
        doWork2()
    }

    GlobalScope.launch {
        val combined = one.await() + "_" + two.await()
        println("Kotlin Combined: $combined")
    }
}

fun main(args: Array<String>) {

    worksInParallel()
    readLine()
}
```

launch 메서드와의 차이점은 Deferred<T>를 통해 결괏값을 반환한다는 것입니다. 이 때 지연된 결괏값을 받기 위해 await()를 사용할 수 있습니다.
위 예제에서는 doWork1()과doWork2()는 async에 의해 감싸져 있으므로 완전히 `병행` 수행할 수 있습니다.

상대적으로 doWork1()의 지연시간이 짧아서 먼저 종료될거라고 예측이 가능하지만 복잡한 실무에서는 많은 태스크들과 같이 병행 수행되므로 어떤 루틴이 먼저 종료될지 알기 어렵습니다. 
따라서 태스크가 종료되는 시점을 기다렸다가 결과를 받을 수 있도록 await()를 사용해 현재 스레드의 블로킹 없이 먼저 종료되면 결과를 가져올 수 있습니다.
    
    
## 코루틴 시작시점 늦추기    
   
async에서 기본 인수는 문맥을 지정할 수 있고, 문맥 이외에도 몇 가지 매개변수를 더 지정할 수 있습니다.
    
```kotlin
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

suspend fun doWork11(): String {
    delay(1000)
    return "Work1"
}

suspend fun doWork22(): String {
    delay(3000)
    return "Work3"
}



fun main(args: Array<String>) = runBlocking {
    val time = measureTimeMillis {
        val one = async(start = CoroutineStart.LAZY) { doWork11() }
        val two = async(start = CoroutineStart.LAZY) { doWork22() }
        println("AWAIT: ${one.await() + "_" + two.await()}")
    }
    println("Completed in $time ms")
}    
```   

위 예제코드를 보면 start 매개변수를 사용하면 async() 함수의 시작 시점을 조절할 수 있습니다. `CoroutineStart.LAZY`를 사용하면 코루틴의 메서드를 호출하거나 await() 메서드를 호출하는
시점에서 async() 메서드가 실행되도록 코드를 작성할 수 있습니다.    
