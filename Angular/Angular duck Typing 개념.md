## duck Typing이란?

Angular 프로젝트를 진행하면서 Duck Typing이라는 개념에 대해서 한번 찾아봤습니다. 자바와 다르게 인터페이스를 구현하거나 상위 클래스를 상속받지 않아도 비슷한 프로퍼티나 메서드가 있으면 타입을 체크하지 않는다는 것이 핵심입니다. 

duck이란 오리를 의미하는데 만약 어떤 새가 오리처럼 걷고, 헤엄치고, 꽥괙거리는 소리를 낸다면 나는 그 새를 오리라고 부를것이다라는 말에서 duck typing이라는 언어가 유래되었습니다.

한마디로 타입을 미리 정하는게 아니라 실행이 되었을 때 해당 Method들을 확인하여 타입을 정하는 것 입니다.


- 장점 
   - 타입에 대해 매우 자유롭습니다.
   - 런타임 데이터를 기반으로 한 기능과 자료형을 창줄합니다.

- 단점
    - 런타임 자료형 오류가 발생할 수 있습니다. 런타임에서 값은 예상치 못한 유형이 있을 수 있고, 그 자료형에 대한 무의미한 작업이 적용됩니다.
    - 이런 오류가 프로그래밍 실수 구문에서 오랜 시간후에 발생할 수 있습니다.
    - 데이터의 잘못된 자료형의 장소로 전달되는 구문은 작성하지 않아야 합니다. 이것은 버그를 찾기 어려울 수도 있습니다.

duck Typing에 대한 간단한 예제코드를 살펴보겠습니다.

```javascript
interface IItem 
{
    id: number,
    title: string
};

function print(item: IItem) 
{
    console.log(item.id + " > " + item.title);
}

var i : IItem = {
    id: 10,
    title : "ABC"
};

print(i);
// 묵시적으로 Item 인터페이스 타입을 구현하는 객체를 파라미터로 받습니다.
print({ id: 11, title: "XYZ" });
```

print 메서드는 Item 인터페이스 타입을 파라미터도 받습니다. i가 item 타입이기  때문에 예상한것 처럼 id와 title 프로퍼티를 출력할 것이라는 것을 예상할 수 있습니다. 

이제 본론으로 Duck Typing에 대해서 설명하겠습니다.

```javascript
interface IProduct {

    id: number,
    title: string,
    author: string
};

var book: IProduct = {
    id: 1,
    title: "C# in Depth",
    author: "Jon Skeet"
};

print(book);
```

book이라는 변수가 있고, Item 인터페이스 타입을 구현하지 않습니다. 그러나 실제로 print(book) 메서드를 호출하면 id, tilte 값이 호출되는 것을 알 수 있습니다.

요약하면, print 메서드는 실제로 파라미터가 `id` 와 `title`이라는 프로퍼티들을 가지고 있는지에만 초점을 맞추고 있을 뿐 그것의 타입에 대해서 별도로 체크를 하지 않습니다. 즉 Item 인터페이스 타입을 구현했는지는 중요하지가 않다는 것이 duck Typing의 핵심 개념입니다.

