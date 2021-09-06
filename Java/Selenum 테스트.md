# Selenum 테스트

웹 브라우저 자동화 프로그래밍을 구현하기 위해 여러 라이브러리를 찾아보다가 Selenum으로 자동화를 많이 구현하는데 쓴다고 하여 자바로 간단하게 웹브라우저 열기 및 확장프로그램 제어까지 구현해봤습니다.


```java
@ExtendWith(SpringExtension::class)
@SpringBootTest
class SelenumTests {

    private lateinit var driver: WebDriver

    @BeforeEach
    fun setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/driver/chromedriver")
        
        var cOptions = ChromeOptions()
        val extensionPath = "src/test/resources/laookkfknpbbblfpciffpaejjkokdgca.crx"
        cOptions.addExtensions(File(extensionPath))

        var capabilities = DesiredCapabilities()
        capabilities.setCapability(ChromeOptions.CAPABILITY, cOptions)

        driver = ChromeDriver(capabilities)
    }

    @AfterEach
    fun tearDown() {
        //driver.quit()
        println("종료")
    }

    @Test
    fun test() {
        driver.get("chrome-extension://laookkfknpbbblfpciffpaejjkokdgca/loading.html")
    }
}
```
