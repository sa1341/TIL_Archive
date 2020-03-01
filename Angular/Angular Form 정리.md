## Reactive Forms Module

Reactive Forms Module을 사용하면 form을 객체화 시킬 수 있습니다.
html 의 form 태그는 FormGroup이라는 타입이에 연결되고, 각각의 form field 들은 FormControl 이라는 타입이 되어 FormGroup 오브젝트안에 포함됩니다. 이렇게 form 태그와 form field 들이 객체화가 되면, FormGroup, FormControl 타입이 제공하는 함수를 통해서 form의 정보를 읽어오거나 조작할 수 있게 됩니다.

## FormControl과 FormGroup

### FormControl
FormControl은 입력 필드 하나를 나타냅니다.
앵귤러 폼에서 가장 작은 단위인 셈입니다.
FormControl은 필드의 값을 캡슐화하고, 이 값이 유효한지 혹은 변경되었는지 오류가 있는지 적시합니다.

이를 테면 아래 처럼 타입스크립트에서 FormControl을 사용합니다.
```java
// Nate라는 값으로 새 FormControl 객체를 생성
let nameControl = new FormControl("nate");

let name = nameControl.value; // -> Nate

// 이제 이 컨트롤에 값을 조회할 수 있습니다.
nameControl.error // -> StringMap<String, any> 등의 오류
nameControl.dirty // -> false
nameControl.valid // -> true
```

폼을 만들기 위해 FormControl(그리고 FormGroup의 그룹)을 만들었고, 메타데이터 로직을 추가했습니다.
앵귤러의 다른 구조들처럼 여기서도 클래스가 있고, DOM에 속성(formControl)을 추가했습니다.

```javascript
<input type="text" [formControl]="name"/>
```

## FormGroup

폼에는 대부분 필드가 여럿입니다. 따라서 여러 FormControl을 다룰 방법이 있어야 합니다. 폼의 유효성을 검증할 때 FormControl 배열을 일일이 반복 처리하는 것은 비효율입니다. FormGroup은 여러 FormControl에 래퍼 인터페이스(wrapper interface)를 두어 이런 단점을 극복했습니다.

```javascript
consumptionForm = new FormGroup({
    amount: new FormControl(''),
    desc: new FormControl('')
});
```

FormGroup과 FormControl 객체의 조상은 같습니다. (AbstractControl)
따라서 FormControl이 하나뿐 일 때 처럼 consumptionForm의 status나 value를 확인 할 수 있습니다.

FormGroup의 value를 가져오기 위해 키-값 쌍으로 구성된 객체를 받습니다.
이렇게 하면 FormControl 하나하나 따로 반복 처리하지 않고도 폼의 값 전체를 온전히 가져올 수 있어 매우 편리합니다.
