# State를 사용하는 방법

이전에 배웠던 Props는 부모 컴포넌트에서 자식 컴포넌트로 특정 원하는 값을 내려주어 렌더링 하는 방식이라면 state는 말 그대로 컴포넌트 내부에서 가지고 있는 상태 값입니다. Props는 읽기 전용이지만, state는 컴포넌트에서 언제든지 변경이 가능합니다.

간단하게 state를 사용하는 예제 코드에 대해서 살펴보겠습니다.

```javascript
import React, { Component } from 'react';

class Counter extends Component {

  state = {
    number: 1
  }

  handleIncrease = () => {
    this.setState({
      number: this.state.number + 1
    });
  }

  handleDecrease = () => {
    this.setState({
      number: this.state.number - 1
    });
  }

  render() {
    return (
      <div>
        <h1>카운터</h1>
        <div>값: {this.state.number}</div>
        <button onClick={this.handleIncrease}>+</button>
        <button onClick={this.handleDecrease}>-</button>
      </div>
    );
  }
}

export default Counter; // 모듈을 사용하기 위해서 항상 export를 해줘야 합니다. default 키워드는 모듈안의 개체가 하나일 때 사용합니다.
```

counter.js 파일을 만들고 내부에 Counter 컴포넌트를 생성하고 JSX 문법으로 값을 증감시키는 버튼을 만들어서 버튼을 통해서 값을 변경하는 예제 코드입니다.

그리고 App 컴포넌트에서 state를 가지고 있는 Counter 컴포넌트를 import하여 실제 html 페이지에서 Counter 컴포넌트가 랜더링한 페이지를 보여주고 있습니다.

```javascript
import React, { Component } from 'react';
import './App.css';
import Counter from './Counter';

class App extends Component {
  render() {
    return <Counter/>;
  }
}

export default App;
```

Counter 컴포넌트에서 state를 변경할 때 반드시 setState()를 사용해서 바꿔야 합니다. 만약 아래처럼 handleIncrease, handleDecrease() 메서드로 바꾸게 된다면 컴포넌트에서 state 값이 update가 되었는지 알 수가 없기 때문에 이렇게 변경하는건 절대 안됩니다.

```javascript
handleIncrease = () => {
    this.state.number = this.state.number + 1;
}
handleDecrease = () => {
    this.state.number = this.state.number - 1;
}
```

올바른 예는 setState()를 사용하는 것입니다.

```javascript
 handleIncrease = () => {
    this.setState({
      number: this.state.number + 1
    });
}

handleDecrease = () => {
    this.setState({
      number: this.state.number - 1
    });
}
```

그리고 render() 메서드처럼 함수 선언식이 아니고 화살표 방식으로 함수를 생성한 이유는 선언식으로 생성할 경우에는 this.setState()에서 this가 Counter 컴포넌트에 바인딩이 되지 않아 정상적으로 setState()가 실행되지 않기 때문입니다. 
