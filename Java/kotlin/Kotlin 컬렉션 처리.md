# Kotlin 컬렉션 처리 

```kotlin
val list2 = listOf<Int>(1, 3, 5, 7, 9, 10)

println(list2.reduce { total, next -> total + next})
println(list2.all { it < 10 })

println(list2.filterNot { it % 2 == 0 })

val grpMap = list2.groupBy { if (it % 2 == 0) "even" else "odd" }
println(grpMap)


val list3 = listOf(1, 2, 3, 4, 5, 6)
val list4 = listOf(2, 2, 3, 4, 5, 5, 6, 7)

println(list3.union(list4))

val part = list3.partition { it % 2 == 0 }
println(part)

val zip = list3.zip(listOf(7, 8, 4))
println(zip)
```

## reduce 메서드

reduce 메서드는 컬렉션인 List 객체의 각 요소들을 초기값 없이 처리방식을 람다식으로 정의하여 처리하는 유용한 메서드 입니다.

## all or any 메서드

all 메서드는 List 객체의 요소가 해당 조건식을 모두 만족하면 true를 리턴합니다. 만약 하나라도 만족하는 요소라도 있는지 판단하기 위해서는 any 메서드를 사용합니다.

## groupBy 메서드

groupBy 메서드는 List 객체에서 각 요소들이 조건식에 따라서 해당 key 값들을 가진 Map 객체를 리턴합니다. 이것도 실무에서는 많이 사용할거 같아서 정리해봤습니다.

## union 메서드

union은 각각의 List 객체 2개가 서로 중복없이 통합하는 메서드입니다. 중복이 없는 Set 객체를 리턴하기 때문에 어떻게 사용하느냐에 따라서 유용할거 같습니다.

## partition 메서드

partition 메서드는 주어진 식에 따라 2개의 컬렉션으로 분리해 Pair를 반환합니다.
주어진 식은 2로 나눈 나머지가 true에 해당하는 값은 첫 번째 위치에 반환하고, false에 해당하는 값은 두 번째 위치에 반환합니다. 
즉, 분리된 2개의 List 컬렉션은 Pair로 반환됩니다.

## zip 메서드

zip()은 2개의 컬렉션에서 동일한 인덱스끼리 Pair를 만들어 반환합니다. 이 때 요소의 개수가 가장 적은 컬렉션에 맞춰 Pair가 구성됩니다.









