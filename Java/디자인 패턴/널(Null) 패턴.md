## 널(Null) 객체 패턴
장기 고객 할인이라든가 신규 고객 할인과 같이 고객의 상태에 따라 특별 할인을 해준다고 가정해봅시다. 사용 요금 명세서를 생성하는 기능은 아래 코드와 같이 명세서 상세 내역에 특별 할인 기능을 추가할 수 있을 것입니다.

```java
public Bill createBill(Customer customer) {
    Bill bill = new Bill();
    // ... 사용 내역 추가
    bill.addItem(new Item("기본사용요금", price));
    bill.addItem(new Item("할부금", somePrice));

    // 특별 할인 내역 추가
    SpecialDiscount specialDiscount = specialDiscountFactory.create(customer);
    if (specialDiscout != null) { // 특별 할인 대상인 경우만 처리
        specialDiscount.addDetailTo(bill);
    }
}
```

고객에 따라 특별 할인이 없는 경우도 있기 때문에 위 코드에서는 specialDiscount가 null이 아닌 경우에만 특별 할인 내역을 추가하도록 했습니다. null 검사 코드를 사용할 때의 단점은 개발자가 null 검사 코드를 빼 먹기 쉽다는 점입니다. 예를 들어, 아래 코드처럼 한 객체에 대한 null 검사를 여러 코드에서 사용한다고 가정해보겠습니다.


```java
public void someMethod(MyObject obj) {
    if (obj != null) {
        obj.someOperation();
    }
    ...
    callAnyMethod(obj, other);
}

private void callAnyMethod(MyObject obj, Other other) {
    if (other.someOp()) {
        if (obj != null) {
            obj.process();
        }
    } else {
        if( obj != null) {
            obj.processOther();
        }
    }
}
```
