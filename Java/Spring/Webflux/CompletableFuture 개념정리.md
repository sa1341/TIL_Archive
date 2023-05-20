# CompletableFuture 정리

Java8 이후에 도입된 Future 인터페이스의 구현체인 CompletableFuture 클래스에 대해서 간략하게 스펙을 살펴봤습니다.

```kotlin
public class CompletableFuture<T> implements Future<T>, CompletionStage<T>
```

## 1. Future 클래스

- 비동기적인 작업을 수행
- 해당작업이 완료되면 결과를 반환하는 인터페이스

먼저, Future 클래스 스펙을 살펴보겠습니다.

- isDone: task가 완료되었다면, 원인과 상관없이 true를 반환 

> isDone은 Running 상태가 아니라면 예외가 발생하든 작업이 취소되든 무조건 true를 반환한다고 보면 됩니다.

- isCancelled: task가 명시적으로 취소된 경우, true를 반환합니다.

- get: 결과를 구할 때까지 thread가 계속 block
또한, future에서 무한 루프나 오랜 시간이 걸린다면 스레드가 blocking 상태를 유지합니다.

- get(long timeout, TimeUnit unit): 결과를 구할 때까지 timeout동안 thread가 block, timeout이 넘어가도 응답이 반환되지 않으면 TimeoutException이 발생합니다.

- cancel: future의 작업 실행을 취소합니다, 취소할 수 없는 상황이라면 false를 반환합니다.
인자 값으로 boolean 타입의 `mayInterruptIfRunning`를 받는데, 해당 인자 값이 false라면 시작하지 않은 작업에 대해서만 취소가 가능합니다.

### 1.1 Future 인터페이스의 한계

- cancel을 제외하고 외부에서 future를 컨트롤 할 수 없습니다.

- 반환된 결과를 get()해서 접근하기 때문에 비동기 처리가 어렵습니다.


## 2. CompletionStage

- 비동기적인 작업을 수행
- 해당작업이 완료되면 결과를 처리하거나 다른 CompletionStage를 연결하는 인터페이스

### 2.1 CompletionStage 연산자 조합

CompletionStage는 50개에 가까운 연산자들을 활용하여 비동기 task들을 실행하고 값을 변형하는 등 chaining을 이용한 조합이 가능합니다.
또한 에러를 처리하기 위한 콜백도 제공합니다.

CompletionStage 연산자가 어떻게 연결되는지도 정리해봤습니다.

- thenAccept(Consumer action)
- thenApply(Function fn)
- thenCompose(Function fn)
- thenRun(Runnable action)
- exceptionally

thenAccept[Async]의 실행 스레드는 done 상태에 따라서 스레드가 달리집니다. 예를 들어서 done 상태에서 thenAccept는 caller(main)의 스레드에서 실행되고, done 상태의 completionStage에 thenAccept를 사용하는 경우, caller 스레드를 block 할 수 있습니다.

exceptionally라는 메서드는 CompletionStage 파이프라인에서 예외가 발생하면 예외처리를 위해 제공되는 메서드입니다.

아래와 같은 예제코드로 `exceptionally()` 메서드 예외처리가 가능합니다.

```java
Helper.completionStage()
    .thenApplyAsync(i -> {
        log.info("in thenApplyAsync");
        return i / 0;
    }).exceptionally(e -> {
        log.info("{} in exceptionally", e.getMessage());
        return 0;
    }).thenApplyAsync(value -> {
        log.info("{} in thenAcceptAsync", value);
    });

Thread.sleep(1000);
```


### 2.2 CompletionStage의 동작 방식

CompletionStage는 내부적으로 비동기 함수들을 실행하기 위해 `ForkJoinPool`이라는 스레드 풀을 사용합니다. 

ForkJoinPool의 기본 size는 할당된 CPU 코어 - 1만큼 제공됩니다. 즉, 옥타코어(코어 6개)인 경우에는 5개의 스레드를 가지고 있는 ForkJoinPool이 생성됩니다.

또한, ForkJoinPool은 `steal work` 알고리즘을 이용해서 task들을 subtask로 나누고 각 스레드 풀의 스레드들에게 균등하게 작업을 분할해서 처리하는 방식으로 동작합니다.


> 참고로 ForkJoinPool은 데몬 스레드이기 때문에, main 스레드가 종료되면 실행여부와 상관없이 즉각적으로 종료가 됩니다. 

## 3. CompletableFuture 스펙

대표적으로 supplyAsync 메서드가 있는데 살펴보면 다음과 같습니다.

- Supplier를 제공하여 CompletabeFuture를 생성 가능합니다.

- Supplier의 반환값이 CompletableFuture의 결과로 


### 3.1 CompletableFuture.complete 살펴보기

두 번째로 complete를 살펴봤습니다.

- CompletableFuture가 완료되지 않았다면 주어진 값으로 채웁니다.

- complete에 의해서 상태가 바뀌었다면 true, 아니라면 false를 반환합니다.

마찬가지로.. 코드로 이해해보는게 좋아서 예시를 적어놨습니다.

```java
CompletableFuture<Integer> future = new CompletableFuture<>(); // 껍데기 CompletableFuture 인스턴스 생성

var triggered = futre.complete(1);
assert future.isDone(); // true
assert triggered; // true
assert future.get() == 1; // true

triggered = future.complete(2); 
assert future.isDone(); // 이미 위에서 완료를 했기 때문에 true 반환 합니다.
assert !triggered; // false
assert future.get() == 1; // true
```

## 4. Future와 CompletableFuture의 차이

Future는 상태 값이 `completed, canceled` 두 가지 밖에 존재하지 않아 예외상황을 알기가 어려웠는데, CompletableFuture는 `isCompletedExceptionally`와 같은 메서드를 제공하여 Exception에 의해서 completed 되었는지 확인 할 수 있습니다.

## 5. CompletableFuture의 한계

- 지연 로딩 기능을 제공하지 않습니다.
  - CompletableFuture를 반환하는 메서드를 호출 시 즉시 작업이 실행됩니다.
- 지속적으로 생성되는 데이터를 처리하기 어렵습니다.
  - CompletableFuture에서 데이터를 반환하고 나면 다시 다른 값을 전달하기 어렵습니다.
