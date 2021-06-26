# GC(Garbage Collection)에 대해서 알아봅시다.

GC란 간략히 정의 하자면 Heap 메모리를 재활용 하기 위해 Root Set에서 참조되지 않는 Object(Unreachable Object)들을 해제해 가용한 공간을 만드는 작업을 의미합니다.

`Java8`은 아래와 같이 메모리를 관리합니다. 

![GC](https://user-images.githubusercontent.com/22395934/123261215-6d5f2d80-d531-11eb-9e35-facd66800049.png)

Java에서 객체가 생성되는 영역은 Heap입니다. 그 Heap은 Young Generation과 Old Generation으로 나뉩니다. 여기서 Young Generation은 다시 Suvival 0, Survival 1로 세분화 됩니다.

보통 새롭게 생성된 Object의 대부분은 Young Generation의 Eden 영역에 존재합니다. Eden 영역이 가득차면 MinorGC가 발생합니다. Unreachable Object들은 Eden 영역이 클리어 될때 메모리에서 해제 되며, Reachable Object들은 S0으로 이동됩니다. 기존에 S0에 있던 Reachable Object들은 S1으로 이동됩니다. S1이 가득차게 되면 S1의 Reachable Object들은 Old Generation으로 복사됩니다.

> 대부분의 Old Generation들은 Young Generation보다 크게 할당되며 크기가 큰 만큼 Young Generation보다 GC가 적게 발생합니다. 이 영역에서 Object들이 사라질때를 MajorGC가 발생한다고 합니다.

즉, Young Generation은 주기적으로 청소하고, 상대적으로 오랜 시간 사용되는 Object를 Old Generation에서 관리하는 방식합니다.

## 성능상의 이점

성능상의 이점으로는 Young Generation이 Old Generation 보다 사이즈가 작고, GC가 전체 영역을 처리하는 것보다 시간이 덜 걸립니다.

즉, 어플리케이션이 중지되는 시간이 짧아집니다.

## GC의 종류

- MinorGC: Young Generation에서 발생하는 GC
- MajorGC: Old Generation에서 발생하는 GC
- FullGC: Heap 전체를 clear하는 작업 (Young/Old 공간 모두 포함합니다.)


## Root Set

Java GC는 객체가 가비지인지 판별하기 위해서 reachbility라는 개념을 사용합니다.

어떤 객체에 유효한 참조가 있으면 `reachable`로 구별하고, unreachable 객체를 가비지로 간주해 GC를 수행합니다.

한 객체는 여러 다른 객체를 참조하고, 참조된 다른 객체들도 마찬가지로 또 다른 객체들을 참조할 수 있는 방식으로 참조 사슬을 이루고 있습니다.

이런 상황에서 유효한 참조 여부를 파악하려면 항상 유효한 최초의 참조가 있어야 되는데 이를 객체의 `root set`이라고 합니다.

객체의 참조는 보통 아래 4가지 종류 중 하나입니다.

- 힙 내의 다른 객체에 의한 참조
- Java 스택, 즉 Java 메서드 실행 시에 사용하는 지역 변수와 파라미터들에 의한 참조
- 네이티브 스택, 즉 JNI에 의해 생성된 객체에 대한 참조
- 메서드 영역의 정적 변수에 의한 참조

> 여기서 힙 내의 다른 객체에 의한 참조를 제외한 나머지 3개가 root set입니다. 

# Java 1.8 이후의 GC 변경사항

힙 영역이 기존에는 New / Survive / Old / Perm / Native에서 New / Survive / Old / Metaspace로 변경 되었습니다.

Java7까지의 Permanent

1. Class의 Meta 정보
2. Method의 Meta 정보
3. Static Object
4. 상수화된 String Object
5. Class와 관련된 배열 객체 Meta 정보
6. JVM 내부적인 객체들과 최적화 컴파일러(JIT)의 최적화 정보

Java8에서의 Metaspace과 Heap 분리

1. Class의 Meta 정보 -> Metaspace 영역으로 이동
2. Method의 Meta 정보 -> Metaspace 영역으로 이동
3. Static Object -> Heap 영역으로 이동
4. 상수화된 String Object -> Heap 영역으로 이동
5. Class와 관련된 배열 객체 Meta 정보 -> Metaspace 영역으로 이동
6. JVM 내부적인 객체들과 최적화 컴파일러(JIT)의 최적화 정보 ->  Metaspace 영역으로 이동


PermGen 영역에 저장되어 문제를 유발하던 Static Object는 Heap 영역으로 옮겨서 최대한 GC 대상이 되도록 변경 되었습니다.

> OutOfMemoryError: PerGen Space error이 발생하는 것을 본적이 있다면 이는 Permanent Generation 영역이 꽉 찼을때 발생하고 Memory leak가 발생했을 때 생기게 됩니다. Memory leak의 가장 흔한 이유중에 하나로 메모리에 로딩된 클래스와 클래스 로더가 종료될 때 이것들이 가비지 컬레션이 되지 않을 때 발생합니다.


수정될 필요가 없는 정보만 Metaspace에 저장하고 Metaspace에는 JVM이 필요에 따라서 리사이징 할 수 있는 구조로 개선되었습니다.

PermGen은 Java8부터 Metaspace로 완벽하게 대체 되었고, Metaspace는 클래스 메타 데이터를 native 메모리에 저장하고 메모리가 부족할 경우 이를 자동으로 늘려줍니다. Java8의 장점중에 하나로 OutOfMemoryError: PermGen Space error는 더 이상 볼 수 없고 JVM 옵션으로 사용했던 PermSize와 MaxPermSize는 더 이상 사용할 필요가 없어졌습니다. 대신에 MetaspaceSize 및 MaxMetaspaceSize가 새롭게 사용되게 되었습니다.



