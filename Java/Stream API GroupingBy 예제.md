오늘 업무시간에 쿼리에서 조회한 결과가 여러개 나올 수 밖에 없는 상황에서 key 값을 기준으로 value를 List<CouponDto>로 묶는 작업이 필요했는데 
아래와 같은 groupingBy() 함수를 사용하여 해결했습니다.
  
```java
Map<Long, List<CouponDto>> result = dtos.stream()
        .map(cc -> cc.toDto())
        .collect(Collectors.groupingBy(cc -> cc.getId()));
```

Collectors.java 파일을 까보면 아래와 같이 groupingBy() 함수가 정의되어 있습니다.

```java
public static <T, K> Collector<T, ?, Map<K, List<T>>> groupingBy(Function<? super T, ? extends K> classifier) {
    return groupingBy(classifier, toList());
}
```
위 메서드는 T를 K로 맵핑하고, K에 저장된 List에 T를 저장한 Map을 생성합니다.

아래 대표적인 예제 코드입니다.

```java
List<Integer> numbers = Arrays.asList(1, 2, 2, 2, 3, 3, 4, 5, 6, 6);
Map<Integer, List<Integer>> map = numbers.stream()
                .collect(Collectors.groupingBy(n -> n));

System.out.println(map);
```

위 예제는 리스트의 각 숫자들을(T)을 숫자 그 자체(K)로 그룹핑한 다음, 같은 그룹(K)에 속한 리스트(T)를 생성합니다. 숫자를 Key로, 리스트를 Value로 갖는 Map을 생성합니다.

두번째 예제도 괜찮게 많이 사용할거 같아서 실습을 해봤습니다.

```java
static <T,K,D,A,M extends Map<K,D>> Collector<T,?,M> groupingBy(Function<? super T,? extends K> classifier, Supplier<M> mapFactory,
    Collector<? super T,A,D> downstream)
```

- mapFactory: 함수의 수행 결과로서 새롭게 만들어지는 Map을 생성하는 Function 입니다.
- downstream: groupingBy의 결과로서 얻어지는 결과 Collector입니다.

```java
String[] persons = {"a", "b", "a", "b", "b", "b"};
Map<String, Long> countsByPerson = Arrays.stream(persons)
                .collect(Collectors.groupingBy(person -> person, HashMap::new, Collectors.counting()));

System.out.println(countsByPerson);
```
위 예제는 배열의 각 문자열(T)를 문자열 그 자체(K)로 맵핑하고, HashMap의 문자열 Key(K)에 저장된 Long 객체(D)에 counting(), (T, reduce 메서드)을 누적합니다.

> 참조 블로그: https://xlffm3.github.io/java/map_using_collectors/



