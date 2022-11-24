# PoolingHttpClientConnectionManager란?

서비스를 개발하는 도중에 외부 서비스와 통신을 하는 경우 대부분 스프링에서 제공해주는 `RestTemplate` 클래스를 사용하여 원하는 데이터를 주고받았지만, 서비스가 점점 커지면서 요청 수가 늘어남에 따라서 가끔씩 `SocketTimeoutException`이나 `ReadTimeoutException` 등의 예외들이 발생하기 시작했습니다.  

이러한 이슈들을 해결하기 위해 RestTemplate 내부적으로 타 서비스에 요청 시 Connection을 어떻게 설정하는지 좀 더 알 고 싶었습니다.

RestTemplate은 Thread Safe하게 설계되어 있습니다.

이제 RestTemplate으로 서비스에 요청을 하는 경우에는 
PoolingHttpClientConnectionManager를 사용하여 Connection 수를 지정할 수 있습니다. 

> PoolingHttpClientConnectionManager는 HttpClientConnectionManager 인터페이스를 구현하고 있습니다.


아래 예제코드는 `Kotlin`으로 RestTemplate 객체를 생성 시에 Connection Pool을 설정하는 예제코드입니다.

```kt
package com.yolo.jean.config

import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

const val MAX_TOTAL_CONNECTION = 200
const val MAX_PER_ROUTE = 20

@Configuration
class RestConfig {

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate(getHttpRequestFactory())
    }

    private fun getHttpRequestFactory(): HttpComponentsClientHttpRequestFactory {

        val factory = HttpComponentsClientHttpRequestFactory()

        val cm = PoolingHttpClientConnectionManager()
        cm.maxTotal = MAX_TOTAL_CONNECTION
        cm.defaultMaxPerRoute = MAX_PER_ROUTE

        val config = RequestConfig.custom()
            .setConnectTimeout(5000)
            .setConnectionRequestTimeout(5000)
            .setSocketTimeout(5000).build()

        val client = HttpClients.custom()
            .setConnectionManager(cm)
            .setDefaultRequestConfig(config)
            .build()

        factory.httpClient = client

        return factory
    }
}
```

MAX_TOTAL_CONNECTION은 Connection Pool의 수용 가능한 최대 사이즈입니다.

MAX_PER_ROUTE는 호스트(IP + PORT)당 Connection Pool에 생성할 수 있는 Connection 수를  의미합니다. 

예를 들어서 http://localhost:8080/testA, http://localhost:8080/testB 두개의 경로에 대해서 최대 20개의 연결이 생성된다는 뜻입니다.

> 참조: https://jodu.tistory.com/46
