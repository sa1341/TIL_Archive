# react Input 상태 관리

리액트에서 form 태그 안에 input 태그들을 넣은 후 , 값을 입력하는데 이러한 값들의 상태를 관리하는 방법에 대해서 살펴보겠습니다.

아래 이름과 전화번호를 input 태그 안에 입력 후 컴포넌트의 상태 값에 동기화 해주는 에제 코드를 작성하였습니다.

```javascript
import React, { Component } from 'react';

class PhoneForm extends Component {

    state = {
        name: '',
        phone: '',
    }

    handleChange = (e) => {
        console.log(typeof e.target.name);
        this.setState({
            [e.target.name] : e.target.value 
        });
    }

    render() {
        return (
            <form>
                <input 
                    name="name"
                    placeholder="이름" 
                    onChange={this.handleChange} 
                    value={this.state.name} 
                />
                <input 
                    name="phone"
                    placeholder="전화번호" 
                    onChange={this.handleChange}
                    value={this.state.phone} 
                />
                <div>
                   {this.state.name} {this.state.phone}
                </div>
                
            </form>
        );
    }
}

export default PhoneForm;
```

>> 꿀팁 정보: Reactjs code snippets 플러그인을 설치하면 Componet 생성할 때 `rcc` 단축키만으로 Component를 간편하게 생성 할 수 있습니다.
`rsc` 단축키를 입력하면 함수형 컴포넌트를 생성 할 수 있습니다.

PhoneForm 컴포넌트를 생성한 후에 render() 함수에서 <form>과 하위의 <input>을 JSX로 리턴하고 있습니다. 이 때 onChange 함수는 이벤트로 내용을 감지하여 변화가 일어났는지 탐지해줍니다. 

만약 input 태그의 value 값에 값을 입력하면 PhoneComponent가 가지고 있는 클래스 필드인 state 객체의 name, phone 프로퍼티 값을 동기화를 어떻게 해줘야 할까요? 

onChange 이벤트 함수를 이용해서 값이 변경될 때마다 this.handleChange 메서드를 호출하여 setState() 함수로 state 값을 변경해주게 됩니다. 

결과는 아래 이미지 화면과 같습니다.

![image](https://user-images.githubusercontent.com/22395934/104024012-d3672b00-5205-11eb-90ab-4273f47d7d3f.png)


```javascript
handleChange = (e) => {
    this.setState({
        [e.target.name] : e.target.value 
    });
}
```

위 코드는 객체 안에서 key를 []로 감싸면 그 안에 넣은 그 안에 넣은 레퍼런스가 가리키는 실제 값이 key 값으로 사용됩니다. 예를 들어 다음과 같은 객체를 만들면 아래와 같은 결과를 얻을 수 있습니다.

```javascript
const name = 'junyoung';
const object = {
    [name]: 'value'
};

// 결과 값
{
    'variantKey': 'value
}
```

#### 참조: https://smujihoon.tistory.com/205
