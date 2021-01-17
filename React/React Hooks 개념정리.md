# React Hooks

Hooks는 React v16.8에 새로 도입된 기능으로서, 함수형 컴포넌트에서도 상태 관리를 할 수 있는 useState, 그리고 렌더링 직후 작업을 설정하는 useEffect 등의 기능등을 제공하여 기존의 함수형 컴포넌트에서 할 수 없었던 다양한 작업을 할 수 있게 해줍니다.

## useState

useState는 가장 기본적인 Hook으로서, 함수형 컴포넌트에서도 가변적인 상태를 지니고 있을 수 있게 해줍니다. 만약에 함수형 컴포넌트에서 상태를 관리해야 되는 일이 발생하면 이 Hook을 사용하시면 됩니다.

한번 이 기능을 사용해서 숫자 카운터를 구현해보았습니다.

### Counter.js

```javascript
import React, { useState } from 'react';

const Counter = () => {
    const [ value, setValue ] = useState(0);
    return (
        <div>
          <p>
            현재 카운터 값은 <b>{value}</b> 입니다.  
          </p>
          <button onClick={() => setValue(value + 1)}>+1</button>
          <button onClick={() => setValue(value - 1)}>-1</button>
        </div>
    );
};

export default Counter;
```

useState를 사용할 땐 코드의 상단에 import 구문을 통하여 불러오고, 비구조화 할당 문법으로 사용합니다.

```javascript
const [ value, setValue ] = useState(0);
```

이 함수의 파라미터에는 상태의 기본값을 넣어줍니다. 여기서는 현재 0을 넣어줬는데, 결국 카운터의 기본 값을 0으로 설정하겠다는 의미입니다. 이 함수가 호출되고 나면 배열을 반환하는데요, 그 배열의 첫번째 원소는 상태 값이고, 두번째 원소는 상태를 설정하는 함수 입니다. 이 함수에 파라미터를 넣어서 호출하게 되면 전달받은 파라미터로 값이 바뀌게 되고 컴포넌트는 정상적으로 리렌더링 됩니다.

App 컴포넌트를 열어서 기존에 보여주고 있던 내용을 다 지우고 Counter 컴포넌트를 렌더링하였습니다.

### App.js

```javascript
import React, { useState } from 'react';

render() {
    return (
        <div>
          <Counter/>
        </div>
    );
};
```

### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/104813857-274dc180-584f-11eb-953f-c03d8346f26e.png)


이렇게 useState를 사용하면 함수형 컴포넌트에서 상태 관리를 위하여 굳이 클래스 형태로 변환 할 필요가 없어서 매우 편리합니다.

### useState를 여러번 사용하기

하나의 useState 함수는 하나의 상태 값만 관리할 수 있기 때문에 만약 컴포넌트에서 관리해야 할 상태가 여러 개라면 useState를 여러번 사용하면 됩니다.

```javascript
import React, { useState } from 'react';

const Info = () => {

    const [ name, setName ] = useState('');
    const [ nickname, setNickname ] = useState('');

    const onChangeName = (e) => {
        const { value } = e.target;
        setName(value);
    }

    const onChangeNickname = (e) => {
        const { value } = e.target;
        setNickname(value);
    }


    return (
        <div>
            <div>
                <input value={name} onChange={onChangeName} />
                <input value={nickname} onChange={onChangeNickname} />
            </div>
            <div>
                <div>
                    <b>이름:</b>{name}
                </div>
                <div>
                    <b>닉네임:</b>{nickname}
                </div>
            </div>
        </div>
    );
};

export default Info;
```

그 다음에 App 컴포넌트에서 이 컴포넌트를 렌더링 하면 됩니다.

```javascript
import React from 'react';
import Info from './Info';

const App = () => {
  return <Info />;
};

export default App;
```

### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/104814252-3afa2780-5851-11eb-9d61-8ae78ed8f8fe.png)

관리할 상태가 여러개인 경우에도, useState로 편리하게 관리할 수 있습니다.

### useEffect

useEffect는 리액트 컴포넌트가 렌더링 될 때마다 특정 작업을 수행하도록 설정 할 수 있는 Hook 입니다.

클래스형 컴포넌트의 ComponentDidMount와 componentDidUpdate를 합친 형태로 보아도 무방합니다.

위에서 작성한 기존에 만들었던 Info 컴포넌트에 useEffect를 적용해보겠습니다.


```javascript
import React, { useState, useEffect } from 'react';

const Info = () => {

    const [ name, setName ] = useState('');
    const [ nickname, setNickname ] = useState('');
    useEffect(() => {
        console.log('렌더링이 완료 돠었습니다.');
        console.log({
            name,
            nickname
        });
    });

    const onChangeName = (e) => {
        const { value } = e.target;
        setName(value);
    }

    const onChangeNickname = (e) => {
        const { value } = e.target;
        setNickname(value);
    }


    return (
        <div>
            <div>
                <input value={name} onChange={onChangeName} />
                <input value={nickname} onChange={onChangeNickname} />
            </div>
            <div>
                <div>
                    <b>이름:</b>{name}
                </div>
                <div>
                    <b>닉네임:</b>{nickname}
                </div>
            </div>
        </div>
    );
};

export default Info;
```

### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/104814705-ca083f00-5853-11eb-8fe6-8fd321ff67c8.png)


## 마운트 될 때만 실행하고 싶을 때

만약 useEffect에서 설정한 함수가 컴포넌트가 화면에 가장 처음 렌더링 될 때만 실행되고 업데이트 할 경우에는 실행 할 필요가 없는 경우엔 함수의 두번째 파라미터로 비어있는 배열을 넣어주면 됩니다.

```javascript
useEffect(() => {
    console.log('렌더링이 완료 돠었습니다.');
    console.log({
        name,
        nickname
    });
}, []);
```

코드를 수정하고 다시 브라우저를 열어보면 컴포넌트가 처음 나타날 때만 콘솔에 문구가 나타나고 그 이후에는 나타나지 않을 것입니다.

## 특정 값이 업데이트 될 때만 실행하고 싶을 때

useEffect를 사용할 때 특정 값이 변경이 될 때만 호출하게 하고 싶을 경우도 있을 것입니다. 만약 클래스형 컴포넌트라면 다음과 같이 작성합니다.

```javascript
componentDidUpdate(prevProps, prevState) {
    if (prevProps.value !== this.props.value) {
        doSomething();
    }
}
```

위 코드에서 props 안에 들어있는 value 값이 바뀔 때에만 특정 작업을 수행하도록 하였습니다. 만약 이러한 작업을 useEffect에서 해야한다면 어떻게 해야 할까요?

바로, useEffect의 두번쨰 파라미터로 전달되는 배열 안에 검사하고 싶은 값을 넣어주면 됩니다.

한번 Info 컴포넌트의 useEffect 부분을 수정해보겠습니다.

```javascript
 useEffect(() => {
        console.log('렌더링이 완료 돠었습니다.');
        console.log(name);
}, [name]);
```

배열 안에는 useState를 통해 관리하고 있는 상태를 넣어줘도 되고, props로 전달받은 값을 넣어주어도 됩니다.

### 실행 결과

![image](https://user-images.githubusercontent.com/22395934/104814965-0d16e200-5855-11eb-9a4f-325cb3acf9bd.png)


## 뒷정리 하기

useEffect는 기본적으로 렌더링 되고난 직후마다 실행되며, 두번째 파라미터 배열에 무엇을 넣느냐에 따라 실행되는 조건이 달라집니다.

만약 컴포넌트가 언마운트 되기 전이나, 업데이트 되기 직전에 어떠한 작업을 수행하고 싶다면 useEffect에서 뒷 정리(cleanup) 함수를 반환해주어야 합니다.

info 컴포넌트의 useEffect 부분을 다음과 같이 수정해보세요.

### Info.js

```javascript
import React, { useState, useEffect } from "react";

const Info = () => {
  const [name, setName] = useState("");
  const [nickname, setNickname] = useState("");
  useEffect(() => {
    console.log("effect");
    console.log(name);
    return () => {
      console.log("cleanup");
      console.log(name);
    };
  });

  const onChangeName = (e) => {
    const { value } = e.target;
    setName(value);
  };

  const onChangeNickname = (e) => {
    const { value } = e.target;
    setNickname(value);
  };

  return (
    <div>
      <div>
        <input value={name} onChange={onChangeName} />
        <input value={nickname} onChange={onChangeNickname} />
      </div>
      <div>
        <div>
          <b>이름:</b>
          {name}
        </div>
        <div>
          <b>닉네임:</b>
          {nickname}
        </div>
      </div>
    </div>
  );
};

export default Info;
```

### App.js

```javascript
import React, { useState } from 'react';
import Info from './Info';

const App = () => {
  const [visible, setVisible] = useState(false);
  return (
    <div>
      <button
        onClick={() => {
          setVisible(!visible);
        }}
      >
        {visible ? '숨기기' : '보이기'}
      </button>
      <hr />
      {visible && <Info />}
    </div>
  );
};

export default App;
```

컴포넌트가 나타날 때 콘솔에 effect가 보이고, 사라질 때 cleanup이 보여지게 됩니다.
그 다음엔, 한번 인풋에 이름을 적어보고 콘솔을 보면 렌더링이 될 때마다 뒷정리 함수가 계속 보여지고 있는 것을 확인할 수 있습니다. 그리고, 뒷정리 함수가 호출될 때에는 업데이트 되기 직전의 값을 보여주고 있습니다. 만약에 , 오직 언마운트 될 때만 뒷정리 함수를 호출하고 싶다면 useEffect 함수의 두번째 파라미터에 비어있는 배열을 넣으면 됩니다.

```javascript
useEffect(() => {
    console.log("effect");
    console.log(name);
    return () => {
      console.log("cleanup");
      console.log(name);
    };
},[]);
```


>> 함수형 컴포넌트와 클래스 형 컴포넌트의 차이점이 무엇일까? 클래스 형 컴포넌트의 경우 state 기능 및 라이프사이클 기능을 사용할 수 있으며 임의 메서드를 정의할 수 있다는 점입니다. 반면 함수형 컴포넌트는 클래스형 컴포넌트보다 선언하기가 좀 더 편하고, 메모리 자원을 덜 사용한다는 장점입니다. 단순하게 데이터를 받아서 UI에 뿌려줄 경우 많이 사용합니다. 과거에는 클래스형에 비해서 state와 라이프사이클 API를 사용할 수 없다는 단점이 있었는데 v16.8 이후로 훅이 도입되면서 해결되었습니다. 지금 공식 문서에서도 함수형 컴포넌트 + 훅을 사용하는 것을 권고하고 있습니다.
