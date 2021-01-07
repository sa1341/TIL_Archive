# LifeCycle API

LifeCycle API는 컴포넌트가 브라우저에 나타날 때, 사라질때, 그리고 업데이트 될 때, 호출되는 API 입니다.

컴포넌트가 브라우저에 나타나기 전, 후에 호출되는 API들이 있습니다.


### constructor

```javascript
constructor(props) {
    super(props);
}
```

위 코드는 컴포넌트 생성자 함수입니다. 컴포넌트가 새로 만들어질 때마다 이 함수가 호출됩니다.


### componentWillMount

```javascript
componentWillMount() {


}
```

위 API는 컴포넌트가 화면에 나가기 직전에 호출되는 API 입니다. 원래는 주로 브라우저가 아닌 환경에서 (서버사이드)도 호출하는 용도로 사용했는데, 지금은 더 이상 필요하지 않게되어 리액트 v16.3에서는 해당 API가 deprecated 되었습니다.


### componentDidMount

```javascript
componentDidMount() {
    // 외부 라이브러리 연동: D3, masonry, etc'
    // 컴포넌트에서 필요한 데이터 요청: Ajax, GraphQL, etc
    // DOM에 관련된 작업: 스크롤 설정, 크기 읽어오기 등
}
```
이 API는 컴포넌트가 화면에 나타나게 됐을 때 호출됩니다. 여기선 주로 D3, masonry 처럼 DOM을 사용하는 외부 라이브러리 연동을 하거나, 해당 컴포넌트에서 필요로하는 데이터를 요청하기 위해 axios, fetch 등을 통하여 ajax 요청을 하거나, DOM의 속성을 읽거나 직접 변경하는 작업을 진행합니다.


## 컴포넌트 업데이트

컴포넌트 업데이트는 props의 변화, 그리고 state의 변화에 따라 결정됩니다. 업데이트가 되기 전과 그리고 된 후에 어떠한 API가 호출되는지 살펴보겠습니다.


### componentWillReciveProps

```javascript
componentWillReceiveProps(nextProps) {
    // this.props는 아직 바뀌지 않은 상태
}
```

이 API는 컴포넌트가 새로운 props를 받게 됐을 때 호출됩니다. 이 안에서는 주로, state가 props에 따라 변해ㅐ야하는 로직을 작성합니다. 새로 받게될 nextProps로 조회할 수 있으며, 이 때 this.props를 조회하면 업데이트 되기 전의 API이니 참고만 하면 됩니다. 이 API 또한 v16.3부터 deprecate 됩니다.

## static getDerivedStateFromProps)()

이 함수는, v16.3 이후에 만들어진 라이프사이클 API 입니다. 이 API는 props로 받아온 값을 state로 동기화 하는 작업을 해줘야 하는 경우에 사용됩니다.


```javascript
// 여기서는 setState를 하는 것이 아니라 특정 props가 바뀔 때 설정하고 싶은 state 값을 리턴하는 형태로 사용됩니다.
static getDerivedStateFromProps(nextProps, prevState) {
    if (prevState.value !== nextProps.value) {
     return {
     value: nextProps.value
    };
 }
 return null; // null을 리턴하면 따로 업데이트 할 것은 없다는 의미입니다.
}
```


### shouldComponentUpdate

```javascript
shouldComponentUpdate(nextProps, nextState) {
// return false 하면 업데이트를 안함
// return this.props.checked !== nextProps.checked    
if (nextProps.value === 10) return false;
    return true;
}
```

이 API는 컴포넌트를 최적화하는 작업에서 매우 유용하게 사용됩니다. 리액트에서는 변화가 발생하는 부분만 업데이트를 해줘서 성능이 꽤 잘나옵니다. 하지만, 변화가 발생한 부분만 감지해내기 위해서는 Virtual DOM에 한번 그려줘야 합니다.

즉, 현재 컴포넌트의 상태가 업데이트 되지 않아도, 부모 컴포넌트가 리렌더링되면, 자식 컴포넌트들도 렌더링 됩니다. 여기서 `렌더링` 된다는 건, render() 함수가 호출된다는 의미입니다.

변화가 없으면 물론 DOM 조작은 하지 않게 됩니다. 그저 Virtual DOM에만 렌더링 할 뿐입니다. 이 작업은 그렇게 부하가 많은 작업은 아니지만, 컴포넌트가 무수히 많아 렌더링된다면 애기가 달라집니다. CPU 자원을 어느정도 사용하고 있는것은 사실이니까요

쓸대없이 낭비되고 있는 이 CPU 처리량을 줄여주기 위해서 우리는 Virtual DOM에 리렌더링 하는것도, 불필요한 경우엔 방지하기 위해서 shouldComponentUpdate를 작성합니다.

이 함수는 기본적으로 true를 반환합니다. 따로 작성을 해주어서 조건에 따라 false를 반환하면 해당 조건에는 render 함수를 호출하지 않습니다.

### componentWillUnmount

컴포넌트가 더 이상 필요하지 않게 되면 단 하나의 API가 호출됩니다.

```javascript
 render() {
   return (
     <div>
       {this.state.counter < 10 && <MyComponent value={this.state.counter} />}
       <button onClick={this.handleClick}>Click Me</button>
     </div>
  );
}

componentWillUnmount() {
  // 이벤트, setTimeout, 외부 라이브러리 인스턴스 제거
  console.log('Good Bye');
}
```

여기서는 주로 등록했었던 이벤트를 제거하고, 만약에 setTimeout을 걸은것이 있다면 clearTimeout을 통하여 제거를 합니다. 추가적으로, 외부 라이브러리를 사용한게 있고 해당 라이브러리에 dispose 기능이 있다면 여기서 호출해주시면 됩니다.

### componentDidCatch

render 함수에서 에러가 발생하면, 리액트 앱이 크래쉬 되어버립니다. 그러한 상황에 사용 할 수 있는 API가 componentDidCatch 함수입니다.

```javascript
 
componentDidCatch(error, info) { 
 this.setState({
    error: true
 });
}
```

에러가 발생하면 이런식으로 componentDidCatch가 실행되게 하고, state.error를 true로 설정하게 하고, render 함수쪽에서 이에 따라 에러를 띄어주며 됩니다.

이 API를 사용하게 될 때 주의할 점은 컴포넌트 자신의 render 함수에서 에러가 발생해버리는 것은 잡아낼 수는 없지만, 그 대신에 컴포넌트의 자식 컴포넌트 내부에서 발생하는 에러들을 잡아 낼 수 있습니다.

아래 예제 코드를 살펴보겠습니다.

```javascript
class MyComponent extends Component {
  
  state = {
    value: 0
  };

  render() {
    return (
      <div>
        {this.props.missing.something}
        <p>props: {this.props.value}</p>
        <p>state: {this.state.value}</p>
      </div>
    );
  }
}

export default MyComponent;
```

render() 함수에서 `{this.props.missing.something}` 값을 브라우저에 랜더링 할 경우 missing이 객체인데 undefined 일 경우 something 값에 접근 할 경우 아래와 같이 에러 문구가 노출이 됩니다.

![image](https://user-images.githubusercontent.com/22395934/103901901-1d380e80-513d-11eb-8628-12239301bfd7.png)


이건 사용자들이 봤을 때 좋지 않은 화면이기 때문에 개발자들에게 에러정보를 전달해 주고 개발자는 사용자가 이해하기 쉬운 에러 문구를 보여줘야 할 경우가 있습니다.

이 경우에는 부모 컴포넌트에서 자식 컴포넌트가 랜더링 할 때 에러가 발생하면 해당 에러를 캐치하여 에러 문구를 보여줄 수 있도록 해줄 수 있습니다.

```javascript
import React, { Component } from 'react';
import MyComponent from './MyComponent';

class App extends Component {
  
  state = {
    counter: 1,
    error: false
  }

  constructor(props) {
    super(props);
  }

  componentDidCatch(error, info) {
    console.log(this.state.error);
    this.setState({
      error: true
    });
    // API를 통해서 서버로 오류 내용 날리기
  }

  componentDidMount() {
    console.log('componentDidMount');
    console.log(`${this.state.counter}`);
  }

  handleClick = () => {
    const { counter } = this.state;
    this.setState({
      counter: counter + 1
    });
  };

  render() {
    if (this.state.error) {
      return <div>에러가 났어요!</div>;
    }

    return (
      <div>
        {this.state.counter < 10 && <MyComponent value={this.state.counter} />}
        <button onClick={this.handleClick}>Click Me</button>
      </div>
    );
  }
}

export default App;
```

위 App은 부모 컴포넌트이고 state 객체에 error라는 프로퍼티를 가지고 있습니다. 디폴트로 false로 설정해주고, MyComponent인 자식 컴포넌트에서 에러가 발생하면 위에서 설명했듯이 `componentDidCatch(error, info)`로 에러를 확인 하고 처리 할 수 있습니다. error와 info를 console에 찍으면 아래와 같이 나옵니다.

![image](https://user-images.githubusercontent.com/22395934/103902427-db5b9800-513d-11eb-8288-c314e2ebfa26.png)


error는 어떤 자식 컴포넌트에서 랜더링 에러가 발생했는지 보여줍니다. 그리고 info는 stackTrace 정보를 보여주고 있습니다.

여기서 에러 문구를 보여주기 위해서 componentDidCatch 호출 시 state.error를 true로 변경하면 render() 함수 호출 시 에러 문구만 보여주도록 랜더링 할 수 있습니다. 

