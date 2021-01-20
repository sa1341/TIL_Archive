# Redux를 왜 사용할까?

리엑트에서 애플리케이션을 만들 때, 기본적으로 보통 하나의 루트 컴포넌트 (App.js)에서 상태를 관리합니다. 예를 들어서, TodoList 프로젝트에서는 다음과 같은 구조로 상태가 관리되고 있습니다.

![image](https://user-images.githubusercontent.com/22395934/105184481-ba9a3600-5b72-11eb-96e8-fb158b4faac5.png)

![image](https://user-images.githubusercontent.com/22395934/105184582-d8679b00-5b72-11eb-8d4a-134468fde298.png)

리엑트 프로젝트에서는 대부분의 작업을 할 때 부모 컴포넌트가 중간자 역할을 합니다.
컴포넌트끼리 직접 소통하는 방법은 있긴 하지만, 그렇게 하면 코드가 굉장히 많이 꼬여버리기 때문에 절대 권장되지 않는 방식입니다. `(ref를 사용하면 이러한 작업을 할 수 있긴 합니다.)`

App 에서는 인풋의 값인 input 값과, 이를 변경하는 onChange 함수와, 새 아이템을 생성하는 onCreate 함수를 props로 Form에게 전달해줍니다. Form은 해당 함수와 값을 받아서 화면에 보여주고, 변경 이벤트가 일어나면 부모에게 받은 onChange를 호출하여 App이 지닌 input 값을 업데이트 합니다.

그렇게 인풋 값을 수정하여 추가 버튼을 클릭하면, onCreate를 호출하여 todos 배열을 업데이트 합니다.

이런식으로 App 컴포넌트를 거쳐서 건너건너 필요한 값을 업데이트하고, 리렌더링하는 방식으로 프로젝트가 개발됩니다.

이러한 구조는, 부모 컴포넌트에서 모든걸 관리하고 아래로 내려주는 것이기 때문에, 매우 직관적이기도 하고, 관리하는 것도 꽤 편합니다. 그런데 문제는 앱의 규모가 커졌을 때 입니다.

보여지는 컴포넌트의 개수가 늘어나고, 다루는 데이터도 늘어나고, 그 데이터를 업데이트하는 함수들도 늘어납니다. 그렇게 진행하다보면 App의 코드가 엄청나게 길어지고 이에 따라 유지보수 하는 것도 힘들 것입니다.

만약에 루트 컴포넌트에서 A -> E -> G 컴포넌트에게 어떠한 값을 전달해줘야 하는 상황이면 코드는 아래처럼 작성됩니다.

```javascript
// App.js에서 A 렌더링
<A value={5} />

// A.js에서 E 렌더링
<E value={this.props.value} />

// E.js에서 G 렌더링
<G value={this.props.value} />
```

그러다가 value라는 이름을 anotherValue라는 이름으로 바꾸는 일이 발생한다면 위의 파일 3개를 열어서 다 수정해줘야 합니다.

## 리덕스를 쓰면, 상태 관리를 컴포넌트 바깥에서 합니다.

리덕스는 상태 값을 컴포넌트에 종속시키지 않고, 상태 관리를 컴포넌트 바깥에서 관리할 수 있게 됩니다.

먼저, 리덕스 스토어라는 용어가 있습니다. 스토어는 프로젝트의 상태에 관한 데이터들이 담겨져 있습니다. 

컴포넌트는 스토어에 구독을 할 수 있습니다. 구독을 하는 과정에서, 특정 함수가 스토어에 전달이 됩니다. 그리고 나중에 스토어의 상태 값에 변동이 생기면 전달받았던 함수를 호출해줍니다.

예를 들어서, B 컴포넌트에서 어떤 이벤트가 생겨서, 상태를 변화 할 일이 생겼습니다. 이 때 dispatch라는 함수를 통하여 액션을 스토어에게 던져줍니다. 액션은 상태에 변화를 일으킬 때 참조할 수 있는 객체입니다. 액션 객체는 필수적으로 type이라는 값을 가지고 있어야 합니다.

```javascript
{ type: 'INCREMENT'}
```

위의 객체를 전달 받게 된다면, 리덕스 스토어는 아 ~ 상태에 값을 더해야 하는구나 하고 액션을 참조하게 됩니다.

추가적으로, 상태 값에 2를 더해야 한다면, 이러한 액션 객체를 만들게 됩니다.

```javascript
{ type: 'INCREMENT', diff: 2 }
```

그러면, 나중에 이 diff 값을 참고해서 기존 값에 2를 더하게 됩니다. type을 제외한 값은 선택적(optional)인 값입니다.

## 리듀서를 통하여 상태를 변화시키기

![image](https://user-images.githubusercontent.com/22395934/105186540-2c737f00-5b75-11eb-888a-7b056271d97c.png)

액션 객체를 받으면 전달받은 액션의 타입에 따라 어떻게 상태를 업데이트 해야 할지 정의를 해줘야 합니다. 이러한 업데이트 로직을 정의하는 함수를 리듀서라고 부릅니다. 이 함수는 나중에 예제코드를 통해서 구현해보겠습니다. 예를 들어, type이 INCREMENT라는 액션이 들어오면 숫자를 더해주고, DECREMENT 라는 액션이 들어오면 숫자를 감소시키는 그런 작업을 여기서 합니다.

리듀서 함수는 두 가지의 파라미터를 받습니다.

1. state: 현재 상태
2. action: 액션 객체

그리고, 이 두가지 파라미터를 참조하여, 새로운 상태 객체를 만들어서 이를 반환합니다.

## 상태가 변화가 생기면, 구독하고 있던 컴포넌트에게 알림

상태에 변화가 생기면, 이전에 컴포넌트가 스토어한테 구독 할 때 전달해줬었던 함수 listener가 호출됩니다. 이를 통하여 컴포넌트는 새로운 상태를 받게되고, 이에 따라 컴포넌트는 리렌더링 하게 됩니다.

## 정리

핀트는 기존에는 부모에서 자식의 자식의 자식까지 상태가 흘렀었는데, 리덕스를 사용하면 스토어를 사용하여 상태를 컴포넌트 구조의 바깥에 두고, 스토어를 중간자로 두고 상태를 업데이트 하거나, 새로운 상태를 전달받습니다. 따라서, 여러 컴포넌트를 거쳐 받아올 필요 없이 아무리 깊숙한 컴포넌트에 있다 하더라도 직속 부모에게 받아오는 것처럼 원하는 상태 값을 골라서 props를 편리하게 받아올 수 있습니다.


## 리엑트 없이 쓰는 리덕스

리덕스는 리액트에 종속되는 그런 라이브러리가 아닙니다. 물론 리액트에서 쓰기 위해 만든거니 궁합은 매우 잘맞습니다. 한번 평범한 HTML과 Javascript 환경에서 리덕스를 사용해가면서, 리덕스의 기본 개념을 배워보겠습니다.

우리는 간단한 카운터를 구현해보겠습니다. HTML 섹션엔 다음과 같이 입력해보겠습니다.

```html
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width">
  <title>jus 평범한 리덕스</title>
</head>
<body>
  <h1>평범한 리덕스</h1>
  <h1 id="number">0</h1>
  <button id="increment">+</buttom>
  <button id="decrement">-</buttom>
  <script src="https://cdnjs.cloudflare.com/ajax/libs/redux/3.6.0/redux.js"></script>
</body>
</html>
```

그럼 이러한 결과가 만들어집니다.

![image](https://user-images.githubusercontent.com/22395934/105188840-a7d63000-5b77-11eb-8087-65206defaae3.png)

이제 자바스크립트를 작성하였습니다.

```javascript
// DOM 엘리먼트에 대한 레퍼런스를 만들어줍니다.
const elNumber = document.getElementById("number");
const btnIncrement = document.getElementById("increment");
const btnDecrement = document.getElementById("decrement");

// action type을 정의합니다.
const INCREMENT = 'INCREMENT';
const DECREMENT = 'DECREMENT';

// 액션 객체를 만들어주는 액션 생성 함수를 정의합니다.
const increment = (diff) => ({type: INCREMENT, diff: diff});
const decrement = () => ({type: DECREMENT});

// 초기 값을 설정합니다. 상태의 형태는 개발자 마음입니다.
const initialState = {
  number: 0
};

/*
    아래 counter는 리듀서 함수입니다.
    state와 action을 파라미터로 전달받습니다.
    그리고 그에 따라 다음 상태를 정의 한 다음에 반환해줍니다.
*/
const counter = (state = initialState, action) => {
  console.log(action);
  switch(action.type) {
    case INCREMENT:
      return {
        number: state.number + action.diff
      };
    case DECREMENT:
      return {
        number: state.number - 1
      };
    default:
      return state;
  } 
};

// 스토어를 만들 땐 createStore에 리듀서 함수를 넣어서 호출합니다.
const { createStore } = Redux;
const store = createStore(counter);

// 상태가 변경 될 때마다 호출시킬 listener 함수입니다.
const render = () => {
  elNumber.innerText = store.getState().number;
  console.log('내가 실행됨');
}

// 상태가 변경 될 때마다 호출시킬 listener 함수입니다.
store.subscribe(render);
// 초기 렌더링을 위하여 직접 실행시켜줍니다.
render();

// 버튼에 이벤트를 바인딩합니다.
// 스토어에 변화를 일으키라고 할 때에는 dispatch 함수에 액션 객체를 넣어서 호출합니다.
btnIncrement.addEventListener('click', () => {
   store.dispatch(increment(25));
})

btnDecrement.addEventListener('click', () => {
  store.dispatch(decrement());
})
```

이제 버튼을 눌러보면 숫자가 변경 될 것입니다. 전체적으로 리덕스가 어떻게 동작하는지 정리해봤습니다.

1. 액션 타입을 만들었습니다.
2. 각 액션타입들을 위한 액션 생성함수를 만들었습니다. 액션 함수를 만드는 이유는 그때 그때 액션을 만들 때마다 직접 `{ 이러한 객체 }` 형식으로 객체를 일일히 생성하는 것이 번거롭기 때문에 이를 함수화 한 것입니다. 나중에는 특히, 액션에 다양한 파라미터가 필요해 질 때 유용합니다.

3. 변화를 일으켜주는 함수, 리듀서를 정의해주었습니다. 이 함수에서는 각 액션타입마다, 액션이 들어오면 어떠한 변화를 일으킬지 정의합니다. 지금의 경우에는 상태 객체에 number이라는 값이 들어져 있습니다. 변화를 일으킬 때에는 불변성을 유지시켜주어야 합니다.

4. 스토어를 만들었습니다. 스토어를 만들 땐 createStore를 사용하여 만듭니다. createStore에는 리듀서가 들어갑니다. (스토어의 초기 상태, 그리고 미들웨어도 넣을 수 있습니다.)

5. 스토어에 변화가 생길 때마다 실행시킬 리스너 함수 render를 만들어주고, store.subscribe를 통하여 등록해주었습니다. 

6.각 버튼의 클릭 이벤트에, store.dispatch를 사용하여 액션을 넣어주었습니다.
