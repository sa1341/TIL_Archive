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
이와 같이 여러 코드에서 한 객체에 대한 null 검사를 하게 되면 null 검사 코드를 누락하기 쉬우며, 이는 프로그램 실행 도중에 NullPointException을 발생시킬 가능성을 높여줍니다.

널(Null) 객체 패턴은 null 검사 코드 누락에 따른 문제를 없애 줍니다. 널 객체 패턴은 null을 리턴하지 않고 null을 대신할 객체를 리턴함으로써 null 검사 코드를 업앨 수 있도록 합니다. 널 객체 패턴은 다음과 같이 구현합니다.

- null 대신 사용될 클래스를 구현합니다. 이 클래스는 상위 타입을 상속 받으며, 아무 기능도 수행 하지 않습니다.

- null을 리턴하는 대신, null을 대체할 클래스의 객체를 리턴합니다.

앞서 특별 할인 내역을 명세서에 등록하는 예에서 특별 할인 내역을 처리하기 위해 SpecialDiscount 클래스를 사용했는데, SpecialDiscount 객체가 null일 때 대신 사용할 클래스를 아래 코드처럼 구현합니다.

```java
public class NullSpecialDiscount extends SpecialDiscount {

    @Override
    public void addDetailTo(Bill bill) {
        // 아무것도 하지 않습니다.
    }
}
```

위 코드에서 NullSpecialDiscount 클래스는 SpecialDiscount 클래스를 상속받고 있는데, addDetailTo() 메서드가 아무것도 수행하지 않도록 재정의하고 있습니다.

SpecialDiscount 객체를 생성하는 코드는 이제 null을 리턴하는 대신 NullSpecialDiscount  객체를 리턴하도록 수정합니다.

```java
public class SpecialDiscountFactory {

    public SpecialDiscount create(Customer customer) {
        if (checkNewCustomer(customer)) {
            return new NewCustomerSpecialDiscount();
            ..// 다른 코드 실행

            // 특별 할인 혜택이 없을 때, null 대신 NullSpecialDiscount 객체 리턴
            return new NullSpecialDiscount();        
    }
}
```

이제 SpecialDiscountFactory.create() 메서드를 이용해서 특별 할인 내역을 처리하는 코드는 아래 코드처럼 더 이상 null 검사를 할 필요가 없어집니다.

```java
public Bill createBill(Customer customer) {

    Bill bill = new Bill();
    // .. 특별 할인 내역 추가
    // 특별 할인 대상이 아닐 경우 NullSpecialDiscount 객체 리턴
    SpecialDiscount specialDiscount = specialDiscountFactory.create(customer);
    specialDiscount.addDetailTo(bill); // null 검사 불필요
    ...
    return bill;
}
```

specialDiscountFactory.create() 코드는 해당 고객이 특별 할인 대상이 아닐 경우 NullSpecialDiscount 객체를 리턴합니다. 따라서 위 코드에서 specialDiscount는 null이 되지 않으므로 null 검사를 할 필요가 없어집니다. 또한 specialDiscount가 NullSpecialDiscount 객체인 경우 specialDiscount.addDetailTo() 코드는 아무 동작도 하지 않으므로 어떤 문제도 발생하지 않습니다.

널 객체 패턴을 사용할 때의 장점은 null 검사 코드를 사용할 필요가 없기 때문에 코드가 간결해진다는 점입니다. 코드가 간결해진다는 것은 그 만큼 코드의 가독성을 높여주므로, 향후에 코드 수정을 보다 쉽게 만들어 줍니다.



