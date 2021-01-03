# Props란?

props란 부모 컴포넌트에서 자식 컴포넌트로 값을 전달해주는 것을 의미합니다.

![image](https://user-images.githubusercontent.com/22395934/103473446-111a1d00-4ddc-11eb-914c-783e09a20321.png)

예를 들어서, MyName이라는 컴퍼넌트를 만들고, 부모 컴퍼넌트인 App에서 props의 값을 html로 보여준다고 하면 코드는 아래와 같습니다.

```javascript
import React, { Component } from 'react';

class MyName extends Component {

  static defaultProps = {
    name: '기본이름'
  }

  render() {
    return (
      <div>
        안녕하세요! 제 이름은 <b>{this.props.name}</b> 입니다.
      </div>
    )
  }

}

export default MyName;
```

아래 App 컴포넌트에서 \<MyName name ='임준영'> 으로 선언 및 값을 입력하면 MyName 컴포넌트에서 this.props.name 값에서 `임준영`이라는 값을 받아서 처리할 수 가 있습니다. 만약 name이라는 프로퍼티가 존재하지 않을 시에 defaultProps라는 객체를 정의 후 `기본이름`으로 초기화 하는 방법도 있습니다. 

```javascript
import React, { Component } from 'react';
import './App.css';
import MyName from './MyName';

class App extends Component {
  render() {
    return <MyName name ="임준영" />;
  }
}

export default App;
```


위 코드들은 단순히 컴포넌트를 만들고, 부모 컴포넌트에서 props를 받아서 화면에 출력하는 부분입니다. 하지만 단순하게 props만 출력하고 싶다면 함수형 컴포넌트만 만들어서 구현할 수 있습니다. 

## 함수형 컴포넌트

함수형 컴포넌트를 사용하면 컴포넌트를 불러올 필요가 없습니다. 

```javascript
import React from 'react';

const MyName = ({name}) => {
  return (
    <div>
      안녕하세요! 제이름은 {name} 입니다.
    </div> 
  )
};

MyName.defaultProps = {
  name: '임준영'
}

export default MyName;
```

>> 비구조화 할당 문법은 간단하게 객체의 프로퍼티 값을 다른 객체나 원시 타입에 할당할 경우 간편하게 할당하기 위한 방법으로 아래와 같이 사용할 수 있습니다.

```javascript
// obj 객체 리터럴 방식으로 생성
const obj = { a:1, b:2 };
// 값을 할당하고 싶을 경우
// 아래와 같은 방식으로 값을 할당해야 함. 
const a = obj.a; 
const b = obj.b;

// 비구조화 할당 문법을 사용할 경우 아래처럼 할당이 가능합니다.
const {a,b} = obj;
console.log(a) // 1
console.log(b) // 2

// 함수에서도 비구조화 할당 문법 사용은 가능합니다.
function printInfo({name, age}) {
    console.log(name + '의 나이는 ' + age + '입니다');
}

printInfo({name:'junyoung', age:30}); // junyoung의 나이는 30입니다.
```

만약 위의 코드처럼 단순히 컴포넌트의 용도를 값만 받아와서 랜더링을 할 경우 컴포넌트 파일이 많이 생성할 경우에는 함수형 컴포넌트 생성 방식으로 구현한다면 메모리를 효율적으로 사용할 수 있습니다. 또한 성능적인 측면에서도 최적화가 되는 장점이 있습니다.

하지만 컴포넌트 파일이 그렇게 많지 않다면 사실 상 함수형이랑 기본 컴포넌트 생성 방식이랑 차이는 크지 않습니다.
