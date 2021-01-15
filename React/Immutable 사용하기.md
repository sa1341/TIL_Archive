# React Immutable 사용하기

리엑트에서는 컴포넌트들은 상태 값을 가질 수 있고, 해당 상태 값이 변하면 리렌더링 하도록 설계가 되어있습니다. 

setState({...}) 함수로 상태 값을 변경할 때 주의할 점은 상태 객체는 immutable, 즉 불변의 값을 가져야 합니다. 따라서 상태 값을  변경할 때마다 이전 state 객체의 값을 변경하지 않고 새 객체를 생성하여 참조해야 합니다. 

만약 불변함을 유지하지 않으면 왜 컴포넌트가 최적화가 안되는지 아래 코드들을 먼저 살펴보겠습니다.

### User.js

```javascript
import React, { Component } from 'react';

class User extends Component {
  shouldComponentUpdate(prevProps, prevState) {
    return this.props.user !== prevProps.user;
  }
  render() {
    const { user } = this.props;
    console.log('%s가 렌더링 되고있어요!!!', user.username);

    return (
      <div>
        <div>이름: {user.username}</div>
        <div>이메일: {user.email}</div>
      </div>
    );
  }
}

export default User;
```

### UserList.js

```javascript
import React, { Component } from 'react';
import User from './User';

class UserList extends Component {
  
  shouldComponentUpdate(prevProps, prevState) {
    return prevProps.users !== this.props.users;
  }
  
  renderUsers = () => {
    const { users } = this.props;
    return users.map((user) => (
      <User key={user.id} user={user} />
    ))
  }

  render() {
    console.log('UserList 가 렌더링되고 있어요!')
    const { renderUsers } = this;
    return (
      <div>
        {renderUsers()}
      </div>
    );
  }
}

export default UserList;
```

### App.js

```javascript
import React, { Component } from 'react';
import UserList from './UserList';

class App extends Component {
  id = 3;

  state = {
    input: '',
    users: [
      {
        id: 1,
        username: 'velopert',
        email: 'syn7714@naver.com',
      },
      {
        id: 2,
        username: 'mjkim',
        email:'syn1341@nate.com',
      }
    ]
  }

  onChange = (e) => {
    const { value } = e.target;
    this.setState({
      input: value
    });
  }

  onButtonClick = (e) => {
    this.setState(({ users, input }) => ({
      input: '',
      users: users.concat({
        id: this.id++,
        username: input
      })
    }))
  }

  render() {
    const { onChange, onButtonClick } = this;
    const { input, users } = this.state;

    return (
      <div>
        <div>
          <input onChange={onChange} value={input} />
          <button onClick={onButtonClick}>추가</button>
        </div>
        <h1>사용자 목록</h1>
        <div>
          <UserList users={users} />
        </div>
      </div>
    );
  }
}

export default App;
```

화면에 보여지는 인풋에 입력을 하면 아래와 같이 사용자 목록이 렌더링 됩니다.

![image](https://user-images.githubusercontent.com/22395934/104738337-be9c1180-5788-11eb-9b15-9ccf26e2a0db.png)

input에 값을 입력할 때마다 렌더 함수가 실행됩니다.

이것은 리엑트의 기본적인 속성입니다. 부모 컴포넌트가 렌더링 되면, 자식 컴포넌트들 또한 리렌더링이 됩니다. 이 과정은, 가상 DOM에만 이뤄지는 렌더링이며, 렌더링을 마치고, 리액트의 diffing 알고리즘을 통하여 변화가 일어나는 부분만 실제로 업데이트 해줍니다.

지금은 인풋 내용이 수정될 때마다 UserList도 새로 렌더링이 되고 있습니다. 아무리 실제 DOM에는 반영되지는 않겠지만, 그래도 CPU 쪽에 미세한 낭비가 발생합니다.

지금의 규모의 프로젝트에서는 이런건 문제가 되지 않지만 규모가 큰 프로젝트를 작업하게 되면 저런게 쌓이고 쌓여서 서비스의 성능에 큰 지장을 줄 수 있습니다. 

이러한 문제점을 해결하기 위해 아주 쉽게 최적화를 할 수 있는 방법이 있는데 shouldComponentUpdate를 구현해주면 됩니다.

### UserList.js

```javascript
shouldComponentUpdate(nextProps, nextState) {
    return nextProps.users !== this.props.users;
}
```

### User.js

```javascript
shouldComponentUpdate(nextProps, nextState) {
    return this.props.user !== nextProps.user;
}
```

이러한 이유 때문에 우리는 state를 업데이트 할 때는 불변함을 유지하면서 업데이트를 해주어야 합니다.

## 불변함을 유지하다 보면 코드가 좀 복잡해집니다.

추후 진행할 최적화를 위하여 불변함을 유지하며 코드를 작성하다보면 가끔식 복잡해질 때가 있습니다. 예를 들어 다음과 같은 상태가 있다고 가정해보겠습니다.

```javascript
state = {
  users: [
    { 
      id: 1, 
      username: 'velopert', 
      email: 'public.velopert@gmail.com' 
    },
    { 
      id: 2, 
      username: 'lopert', 
      email: 'lopert@gmail.com' 
    }
  ]
}
```

이렇게 두 가지의 객체가 배열안에 있을 때, 원하는 계정의 이메일을 변경하고 싶다면 이렇게 해야 합니다.

```javascript
const { users } = this.state;

const index = users.findIndex(user => user.id === id);

const selected = users[index];

const nextUsers = [...users];
nextUsers[index] = {
    ...selected,
    email: 'new@naver.com'
};

// 기존의 users는 건들이지 않고
// 새로운 배열/객체를 만들어 setState
this.setState({
    users: nextUsers
});
```

혹은, 수정하고 싶은 state가 어쩌다 보니 아주 깊은 구조로 되어있다면 어떻게 할까요?

```javascript
state = {
  where: {
    are: {
      you: {
        now: 'faded',
        away: true // 요놈을 바꾸고 싶다!
      },
      so: 'lost'
    },
    under: {
      the: true,
      sea: false
    }
  }
}
```

위 state를 분변함을 유지하면서 업데이트 하려면 완전 귀찮습니다.

```javascript
const { where } = this.state;
this.setState({
  where: {
    ...where,
    are: {
      ...where.are,
      you: {
        ...where.are.you,
        away: false
      }
    }
  }
});
```

이렇게 해야 비로소 기존의 객체를 건들이지 않고 새 객체를 생서하여 불변함을 유지하며 값을 업데이트 할 수 있습니다. 애초에 state의 구조를 저렇게 복잡하게 하면 안되긴 하지만, 위와 같은 작업을 매번 하기는 엄청나게 번거롭습니다. 실수도 유발할 수 있구요. 

이러한 작업을 쉽게 해줄 수 있는 것이 바로 Immutable.js 입니다.

## Immutable 시작하기

프로젝트에서 immutable을 사용할 땐, 아래와 같은 패키지를 설치해서 사용합니다.

```javascript
yarn add immutable
```

Immutable을 사용할 때는 아래와 같은 규칙들을 꼭 기억해야 합니다.

- 객체는 Map
- 배열은 List
- 설정할땐 set
- 읽을땐 get
- 읽은 다음에 설정 할 땐 update
- 내부에 있는걸 ~ 할땐 뒤에 In 을 붙인다: 
- setIn, getIn, updateIn
- 일반 자바스크립트 객체로 변환 할 땐 toJS
- List 엔 배열 내장함수와 비슷한 함수들이 존재함.
- push, slice, filter, sort, concat… 전부 불변함을 유지함
- 특정 key 를 지울때 (혹은 List 에서 원소를 지울 때) delete 사용


### Immutable 예제 코드

```javascript
import React from 'react';
import { render } from 'react-dom';
import App from './App';
import { Map, List} from 'immutable';

// 1. 객체는 Map
const obj = Map({
  foo: 1,
  inner: Map({
    bar:10,
    name:'jun',
  })
});

console.log(obj.toJS());

// 2. 배열은 List
const arr = List([
  Map({ foo:1 }),
  Map({ bar:2 }),
]);

console.log(arr.toJS());

// 3. 설정할땐 set
let nextObj = obj.set('foo', 5);
console.log(nextObj.toJS());
console.log(nextObj !== obj);

// 4. 값을 읽을 땐 get
console.log(obj.get('foo'));
console.log(arr.get(0));

// 5. 읽은 다음에 설정 할 때는 update
// 두번째 파라미터로는 updater 함수가 들어갑니다.
nextObj = nextObj.update('foo', value => value + 1);
console.log(nextObj.toJS());

// 6. 내부에 있는걸 ~ 할 땐 In을 붙입니다.
nextObj = obj.setIn(['inner', 'name'], 'junyoung');
console.log(nextObj.toJS());

let nextArr = arr.setIn([0, 'foo'], 10);
console.log(nextArr.getIn([0, 'foo']));

// 8. List 내장함수는 배열이랑 비슷합니다.
nextArr = arr.push(Map({ qaz: 3 }));
console.log(nextArr.toJS());

nextArr = arr.filter(item => item.get('foo') === 1);
console.log(nextArr.getIn([0, 'foo']));

// 9. delete로 key를 지울 수 있습니다.
nextObj = nextObj.delete('foo');
console.log(nextObj.toJS());

nextArr = nextArr.delete(0);
console.log(nextArr.toJS());

render(<App />, document.getElementById('root'));
```


#### 참조: https://velopert.com/3486
