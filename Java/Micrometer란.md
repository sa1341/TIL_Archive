# Micrometer란?

이번에 회사에서 prometheus를 적용하는걸 공부해보면서 `Micrometer`라는 용어가 생소해서 구글링을 통해서 정리해보고자 합니다.

[Micrometer 공홈](https://micrometer.io/)에 접속하면 정의가 나와있습니다.

```
Micrometer provides a simple facade over the instrumentation clients for the most popular observability systems, allowing you to instrument your JVM-based application code without vendor lock-in. Think SLF4J, but for observability.
```

instrumentation이 정확히 무엇을 의미하는지 몰라서 다른 블로그에서 찾아보니 대략적으로 Micrometer은 JVM 기반의 애플리케이션에서 다양한 모니터링 도구가 제공하는 클라이언트 라이브러리에 대한 `facade`를 제공한다고 합니다.

로깅 관련 시스템에서 대표적으로 `SLF4J`가 있다면 모니터링 시스템에서는 `Micrometer`가 있는 것입니다.

모니터링 시스템을 만드는 vendor들은 `Micrometer 인터페이스`를 따르기 때문에 Micrometer를 사용하면 애플리케이션 내의 코드 상에서는 모니터링 시스템 클라이언트로 어떤 것을 사용할지는 Runtime Dependency에서 참조하고 있는 Micrometer 구현체 라이브러리를 사용하여 애플리케이션 `metric`을 수집하면 됩니다.


### Prometheus 라이러리 구성도

![Micrometer](https://user-images.githubusercontent.com/22395934/219048372-677646eb-df05-497e-9740-c0d77f697c76.png)


대표적으로 구현체 라이브러리로 많이 쓰이는 것으로 위에서 언급한 `prometheus`가 많이 쓰이고 있습니다.

Micrometer에 의해서 기록된 애플리케이션의 metric 정보는 시스템의 이상 유무를 판단하기 위한 모니터링 용도로 사용됩니다.

일부 모니터링 시스템은 metric 정보를 어플리케이션으로부터 polling 방식으로 사용합니다. 

## 모니터링 시스템의 주요 특징

- Dimensionality
    - metric 이름에 tag(key-value)를 붙일 수 있도록 지원함.

> 일부 모니터링 시스템의 경우에는 tag 형태가 아닌 flat한 metric name만 사용이 가능하다고 합니다. 전문용어로 hierarchial system이라고 함.

- Rate aggregation
    - 일정 시간 간격 동안의 특정 지표에 대한 평균 값을 필요로 하는 경우 해당 평균 값을 보내는 주체가 client, server side로 구분될 수 있습니다.

`prometheus`는 server-side에 속하기 때문에 평균 값을 직접 구하고 있습니다.

- Publishing
    - 일부 모니터링 시스템은 metric 정보를 애플리케이션으로부터 polling 하거나 push를 합니다.


참고로 `prometheus` polling 방식입니다. 아무래도 서버 client가 모니터링 시스템에 push를 하게 된다면 서버에 부하를 주기때문에 polling 방식을 선호하는게 아닐까 싶습니다. 

## Registry

`Meter`는 애플리케이션의 metric을 수집하기 위한 인터페이스 입니다. Meter는 MeterRegistry에 의해 생성되어 등록됩니다.

지원되는 각 모니터링 시스템은 `MeterRegistry 구현체`를 갖고 있습니다.

`SimpleMeterRegistry`는 각 meter의 최신 값을 메모리에 저장합니다. 그리고 metric 정보를 다른 시스템으로 내보내지 않습니다. 만약 어떤 모니터링 시스템을 사용할지 결정하지 못했다면 `SimpleMeterRegistry`를 사용하면 됩니다.

## Meters

Micrometer는 Meter의 구현체로 다음의 것들을 지원합니다. Meter의 type 별로 수집되는 metric 수가 다릅니다.


- Timerm Counter, Gauge, DistributionSummary, LongTaskTime, FunctionCounter, FunctionTimer, TimeGauge

Meter는 이름(name)과 태그(tag)로 고유하게 식별됩니다.

## Naming

Micrometer는 소문자 단어를 `.`으로 구분하는 naming 규칙을 사용합니다. 각각의 모니터링 시스템은 naming 규칙과 관련해 권장 사항을 갖고 있으며 일부 모니터링 시스템은 naming 규칙이 달라 호환되지 않을 수 있습니다.

Prometheus에서 사용하는 `timer meter`는 아래와 같은 규칙을 사용합니다.


```
# application.yml

http_server_request_duration_seconds
```

## Binders

Micrometer에는 JVM, 캐시, ExecutorService 및 로깅 서비스를 모니터링 하기 위한 여러 내장 Binder가 있습니다.

- JVM 및 시스템 모니터링: ClassLoaderMetrics

- GC metrics: JvmGcMetrics

- Thread 및 CPU 사용률: JvmThreadMetrics, ProcessorMetrics

> 참조 블로그: https://jongmin92.github.io/2019/12/03/Spring/micrometer/
