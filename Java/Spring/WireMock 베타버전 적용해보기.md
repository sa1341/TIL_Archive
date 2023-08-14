# WireMock Beta 적용해보기
이번에 신규로 연금채널서비스를 구축하면서 외부 API 테스트 코드를 작성하기 위해 `wiremock:3.0.0-beta-10` 버전을 사용해봤습니다.
WireMock 개요에 대해서는 공홈이나 Baeldung에서 아래와 같이 소개하고 있습니다.
```
WireMock is a library for stubbing and mocking web services. It constructs an HTTP server that we can connect to as we would to an actual web service.
When a WireMock server is in action, we can set up expectations, call the service and then verify its behaviors.
```
웹서비스를 mocking 및 stubbing 하기 위한 라이브러리라고 소개하고 있습니다.
mocking과 stub의 차이를 아시는분들도 계시겠지만 저는 잘 몰랐었던 내용이라서 둘의 차이점을 간략하게 정리해봤습니다.
일단 둘의 차이점을 알기전에 `Test Double(테스트 대역)`의 개념에 대해서 알아두면 좋지만
이번 주제는 WireMock 편리하게 사용하는 방법을 다루고 있다보니 구체적인 내용은 여기 [블로그](https://azderica.github.io/00-test-mock-and-stub/)로 대체하였습니다.
## Mock과 Stub의 차이점
- Stub: 테스트 중에 만들어진 호출에 미리 준비된 답변을 제공하며 일반적으로 테스트를 위해 프로그래밍된 것 외에는 전혀 응답하지 않습니다.
- Mock: 예상되는 기대값으로 미리 프로그래밍 객체입니다.
## WireMock을 Spring Boot에 적용하기
1. gradle에 3.0.0-beta-10 버전 적용
```kotlin
testImplementation(“com.github.tomakehurst:wiremock:3.0.0-beta-10”)
```
2. WireMock Server 셋팅
WireMock은 위에서 설명했듯이 웹서비스 mocking, stubbing을 위한 라이브러리기 때문에 실제 내부적으로는 HTTP 서버가 내장되어 있습니다.
따라서 실제 호스트와 포트를 설정을 할 수 있습니다. 호스트는 기본적으로 별도로 명시하지 않으면 localhost이고, port 같은 경우에는 8080 입니다.
먼저 테스트 코드를 실행하기 전에 아래와 같이 helper 클래스를 하나 만들어서 WireMock 서버를 셋팅하였습니다.
host는 따로 작성하지 않아서 localhost이고, port 같은 경우에는 dynamicPort()를 사용하여 random하게 사용하지 않는 포트를 할당하도록 설정하였습니다.
```kotlin
@WireMockTest
abstract class WireMockHelper {
    companion object {
        @JvmStatic
        @RegisterExtension
        val wm = WireMockExtension.newInstance()
            .options(WireMockConfiguration().dynamicPort())
            .build()
    }
}
```
이제 WireMock을 사용하여 아래와 같이 단순한 연금계좌 조회 API를 stubbing 하였습니다.
아래는 실제 샌박환경에서 연금계좌 조회 응답 명세이고, 이 객체를 하기와 같이 stubbing 하였습니다.
```kotlin
class TFPU7231 {
    companion object {
        val pfpq7231Response = “”"
            {
                “common_header”: {
                    “user_grp_auth_tcd”: “”,
                    “th_cont_tr_tcd”: “0”,
                    “cont_trkey”: [],
                    “th_page”: 0,
                    “call_s”: 0,
                    “th_tr_tcd”: “R”,
                    “th_if_tcd”: “0",
                    “th_scr_no”: “”,
                    “th_tr_resp_tcd”: “0",
                    “th_qry_c”: “10",
                    “th_dum_tr_tcd”: “”,
                    “th_tr_ccd”: “Q”,
                    “mda_tcd”: “P20",
                    “th_tr_dt”: “20230711",
                    “th_tr_dl_tmd”: “145138553557",
                    “th_biz_dt”: “20230711",
                    “th_sys_tcd”: “D”,
                    “wrk_tmnl_id”: “PAY000000",
                    “th_conn_loc_tcd”: “O”,
                    “th_mac_adr”: “”,
                    “th_ip_tcd”: “0",
                    “th_conn_pbip_adr”: “”,
                    “th_conn_prip_adr”: “10.170.211.141",
                    “onln_user_id”: “PAY”,
                    “blng_dept_cod”: “PAY”,
                    “fds_mobl_dvc_idfy”: “”,
                    “th_pwd_er_nc_inc_yn”: “”,
                    “th_btn_auth_tcd”: “”,
                    “guid”: “PAY20210923194108900aa7280016001",
                    “th_src_if_tcd”: “”,
                    “th_tr_id”: “TACQ7231"
                },
                “message”: {
                    “msg_cod”: “22899”,
                    “msg_cn”: “정상처리 되었습니다.“,
                    “th_msg_oput_tcd”: “0"
                },
                “data”: {
                    “list”: [
                        {
                            “acno”: “02000154506",
                            “ac_nm”: “TEST01",
                            “ac_stat_tcd”: “10",
                            “ac_opn_dt”: “20210528",
                            “ac_abnd_dt”: “”,
                            “pd_id”: “PS2102",
                            “pd_nm”: “(연금)이전전용“,
                            “sub_ac_nm”: “윤태웅“,
                            “ac_ad_stat_tcd”: “01",
                            “pd_abr_nm”: “연금입금“,
                            “ac_sbsc_no”: “6cdbf2231173b2f2cd25a4ac52a23209391819de193175f454f154c5f3f28ca7"
                        },
                        {
                            “acno”: “02000154507",
                            “ac_nm”: “TEST01",
                            “ac_stat_tcd”: “10",
                            “ac_opn_dt”: “20210528",
                            “ac_abnd_dt”: “”,
                            “pd_id”: “PS2101",
                            “pd_nm”: “연금저축계좌“,
                            “sub_ac_nm”: “윤탱탱의 연금저축계좌“,
                            “ac_ad_stat_tcd”: “01",
                            “pd_abr_nm”: “연금저축“,
                            “ac_sbsc_no”: “f7a44f164f76fa6a903ab49a43457848e790ee4ae4f000dfbd108d762033b088"
                        },
                        {
                            “acno”: “02000162768",
                            “ac_nm”: “TEST01",
                            “ac_stat_tcd”: “10",
                            “ac_opn_dt”: “20220320",
                            “ac_abnd_dt”: “”,
                            “pd_id”: “PS2101",
                            “pd_nm”: “연금저축계좌“,
                            “sub_ac_nm”: “TEST01의 연금저축10",
                            “ac_ad_stat_tcd”: “02",
                            “pd_abr_nm”: “연금저축“,
                            “ac_sbsc_no”: “0815b51e7f9e6e675312a242bce496129461f6d7578524fbe9fe73c6e2b9a4d9"
                        }
                    ]
                }
            }
        “”".trimIndent()
    }
}
```
이제 테스트 코드를 작성합니다. 여기서는 webclient를 같이 사용해서 작성해봤습니다.
```kotlin
class SecuritiesAccountServiceTest : WireMockHelper() {
    private val webClient: WebClient = WebClientHelper.createWebClient()
    private val host = “http://localhost”
    private val path = “/api/v1/account-number”
    private val expectedAccount = “02000154507”
    private val expectedPdId = “PS2101"
    @Test
    fun `연금 계좌목록을 조회한다`() {
        val runtimeInfo = wm.runtimeInfo
        val wireMock = runtimeInfo.wireMock
        wireMock.register(post(path).willReturn(jsonResponse(TFPU7231.pfpq7231Response, 200)))
        val result = webClient.post()
            .uri(URI.create(“$host:${wm.port}$path”))
            .retrieve()
            .bodyToFlux(typeReference<SecuritiesClientResult<AccountsResult>>())
            .blockFirst()
            ?.data!!
        result.list.size shouldBe 8
        val filteredAccount = result.list.find { it.pdId == expectedPdId }
        filteredAccount?.acno shouldBe expectedAccount
        filteredAccount?.pdId shouldBe expectedPdId
    }
}
```
위에 코드를 보면 `wireMock.register(post(path).willReturn(jsonResponse(TFPU7231.pfpq7231Response, 200)))`만 선언한 부분이 있는데 여기서는 호출하고자 하는 path와 위에 정의한 stub 객체, 응답코드 200을 리턴하도록 stub를 설정하였습니다.
## 사용 후기
WireMock 베타 이전버전을 사용했을 때에는 dynamicPort 설정 및 서버 셋팅자체가 조금 복잡했었는데, 베타 버전을 사용하니까 어노테이션 기반으로 손쉽고 빠르게 더 직관적으로 테스트 코드 작성을 도와줄 수 있어서 나름 유용했다고 생각합니다.
 > 참조: [junit5에서 wiremock 연동 샘플코드](https://wiremock.org/docs/junit-jupiter/)
