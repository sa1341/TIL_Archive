# @JobScope와 @StepScope

`@JobScope`와 `@StepScope`는 스프링의 기본 Scope인 `싱글톤` 방식과는 대치되는 역할입니다.

Bean의 생성 시점이 스프링 애플리케이션이 실행되는 시점이 아닌 @JobScope, @StepScope가 명시된 메서드가 실행될 때까지 지연시키는 것을 의미합니다. 이러한 행위를 `Late Binding`이라고도 합니다.

#### Spring Batch에서 이렇게 Late Binding을 하면서 얻는 이점들은 아래와 같습니다.

1. JobParameter를 특정 메서드가 실행하는 시점까지 지연시켜 할당시킬 수 있습니다.
즉, 애플리케이션이 구동되는 시점이 아니라 비즈니스 로직이 구현되는 어디든 JobParameter를 할당함으로 유연한 설계를 가능하게 합니다.
2. 병렬처리에 안전합니다.
Step의 구성요소인 ItemReader, ItemProcessor, ItemWriter이 있고, ItemReader에서 데이터를 읽어 오는 메서드를 서로 다른 Step으로 부터 동시에 병렬 실행이 된다면 서로 상태를 간섭받게 될 수 있습니다. 하지만 @StepScope를 적용하면 각각의 Step에서 실행될 때 서로의 상태를 침범하지 않고 처리를 완료할 수 있습니다.

> @JobScope는 Step 선언문에서만 사용이 가능하고, @StepScope는 Step을 구성하는 ItemReader, ItemProcessor, ItemWriter에서 사용 가능합니다.


## 1. JobParameters 사용 시 주의사항

JobParameters는 아래 예제코드처럼 @Value를 통해서 가능합니다. JobPameters는 Step이나 Tasklet, Reader 등 배치 컴포넌트 Bean의 생성 시점에 호출할 수 있습니다. 정확하게 말해서 Scope Bean을 생성할때만 가능합니다.

즉, `@StepScope`, `@JobScope` Bean을 생성할 때만 JobParameters가 생성되기 때문에 사용할 수 있습니다.

```java
 @Bean
 @JobScope
 public Step inactiveJobStep(@Value("#{jobParameters[requestDate]}") final String requestDate) {
       log.info("requestDate: {}", requestDate);
       return stepBuilderFactory.get("inactiveUserStep")
               .<User, User>chunk(10)
               .reader(inactiveUserReader())
               .processor(inactiveUserProcessor())
               .writer(inactiveUserWriter())
               .build();
 }
```
