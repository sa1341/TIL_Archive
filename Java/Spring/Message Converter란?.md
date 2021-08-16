# Message Converter란?

Spring에서 주로 `서버 to 서버` 통신을 하기 위해서 RestTemplate을 빈으로 등록하여 사용합니다. 

이 RestTemaplate을 `RestTemplateBuilder` 객체를 생성하여 설정을 하게 됩니다. 이때 MessageConverter를 추가하는 부분이 있었는데 이 부분이 궁금해서 찾아봤습니다.

메시지 컨버터는 서버로 들어오는 데이터를 오브젝트로 가공하거나 서비스의 비즈니스단에서 처리 된 오브젝트를 HTTP 메시지로 변환하는 역할을 담당하고 있습니다.

![Untitled Diagram](https://user-images.githubusercontent.com/22395934/129485201-fb0532a9-d09b-4946-b74c-ed3d6277b2b1.png)


## @EnableWebMvc 어노테이션

스프링에서는 @EnableWebMvc 어노테이션을 사용하여 4가지 기본 메시지 컨버터들을 등록할 수 있게 되어 있습니다.

- ByteArrayHttpMessageConverter: byte[]를 읽고 쓰는 메시지 컨버터입니다.
- StringHttpMessageConverter: String을 읽고 쓰는 메시지 컨버터입니다.
- ResourceRegionHttpMessageConverter: interface ResourceRegion을 읽고 쓰는 메시지 컨버터입니다.
- ResourceHttpMessageConverter: interface Resource을 읽고 쓰는 메시지 컨버터입니다.


위의 규격을 제외하고 `Gson, Atom, Jackson, XML`과 같은 규격을 의존성에 등록할 경우 스프링에서는 이와 같은 변화를 감지하여 각 메시지 컨버터를 등록하도록 하고 있습니다.
