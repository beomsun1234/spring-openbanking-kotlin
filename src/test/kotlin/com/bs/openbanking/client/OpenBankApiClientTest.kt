package com.bs.openbanking.client

import com.bs.openbanking.dto.openbank.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import org.springframework.web.reactive.function.client.WebClient
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.lang.String
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.assertThrows

@ExtendWith(MockitoExtension::class)
internal class OpenBankApiClientTest() {


    private lateinit var openBankApiClient: OpenBankApiClient

    private lateinit var mockWebServer: MockWebServer
    private lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun setUp(){
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val mock = String.format("http://localhost:%s", mockWebServer.port)
        val webClient= WebClient.builder()
            .baseUrl(mock)
            .build()

        objectMapper=ObjectMapper()

        openBankApiClient = OpenBankApiClient(webClient)

        ReflectionTestUtils.setField(openBankApiClient, "useCode", "test")
        ReflectionTestUtils.setField(openBankApiClient, "clientId", "test")
        ReflectionTestUtils.setField(openBankApiClient, "clientSecret", "test")
        ReflectionTestUtils.setField(openBankApiClient, "redirectUrl", "test")
    }

    @AfterEach
    fun testDown(){
        mockWebServer.shutdown()
    }
    @Test
    @DisplayName("오픈뱅킹토큰의 경우 성공시 rsp_code, rsp_message에 값이 들어가지 않는다.")
    fun requestToken() = runTest{
        //given
        val bankTokenResponseDto = OpenBankTokenResponseDto(
            access_token = "test_token",
            expires_in = 100L,
            refresh_token = "test_token",
            user_seq_no = "1234"
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .setBody(objectMapper.writeValueAsString(bankTokenResponseDto))
        )
        //when
        val token = openBankApiClient.requestToken("test")
        //then
        assertEquals("test_token",token.access_token)
    }
    @Test
    @DisplayName("응답코드 알파벳 O로 시작하면 잘못된 요청이다.")
    fun requestToken_응답코드가O로시작한다() = runTest{
        //given
        val bankTokenResponseDto = OpenBankTokenResponseDto(
            "O00002",
            "실패",
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON.toString())
                .setBody(objectMapper.writeValueAsString(bankTokenResponseDto))
        )
        //when, then
        assertThrows<NoSuchElementException>{
            openBankApiClient.requestToken("test")
        }
    }

    @Test
    fun requestOpenBankUserInfo() = runTest {
        //given
        val openBankUserInfoResponse = OpenBankUserInfoResponseDto(
            rsp_code = "A00001",
            rsp_message = "성공",
            user_seq_no = "1234",
            user_ci = "ci"
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankUserInfoResponse))
                .addHeader("Content-Type", "application/json")
        )
        //when
        val openBankUserInfo = openBankApiClient.requestOpenBankUserInfo(
            OpenBankUserInfoRequestDto(
                openBankId = "1234",
                accessToken = "test"
            )
        )
        //then
        assertEquals("1234",openBankUserInfo.user_seq_no)
        assertEquals("ci",openBankUserInfo.user_ci)
        StepVerifier.create(openBankUserInfo.toMono())
            .assertNext { assertEquals("1234", it.user_seq_no) }
            .verifyComplete()
    }
    @Test
    @DisplayName("응답코드 알파벳 O로 시작하면 잘못된 요청이다.")
    fun requestOpenBankUserInfo_실패code() = runTest {
        //given
        val openBankUserInfoResponse = OpenBankUserInfoResponseDto(
            rsp_code = "O00001",
            rsp_message = "실패",
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankUserInfoResponse))
                .addHeader("Content-Type", "application/json")
        )
        //when, then
        assertThrows<NoSuchElementException> {
            openBankApiClient.requestOpenBankUserInfo(
                OpenBankUserInfoRequestDto(
                    openBankId = "1234",
                    accessToken = "test"
                )
            )
        }

    }

    @Test
    fun requestAccount()= runTest{
        //given
        val openBankRes = OpenBankAccountResponseDto(
            rsp_code = "A00001",
            rsp_message = "성공",
            res_list = arrayListOf(
                OpenBankAccountDto(
                    fintech_use_num = "11111",
                ),
                OpenBankAccountDto(
                    fintech_use_num = "22222",
                ),
                OpenBankAccountDto(
                    fintech_use_num = "33333",
                )
            )
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankRes))
                .addHeader("Content-Type", "application/json")
        )
        //when
        val accounts = openBankApiClient.requestAccount(
            OpenBankAccountRequestDto(
                accessToken = "test",
                openBankId = "1234"
            )
        )
        //then
        assertEquals(3,accounts.size)
        assertEquals("11111",accounts[0].fintech_use_num)
    }

    @Test
    @DisplayName("응답코드 알파벳 O로 시작하면 잘못된 요청이다.")
    fun requestAccount_실패코드()= runTest{
        //given
        val openBankRes = OpenBankAccountResponseDto(
            rsp_code = "O00001",
            rsp_message = "실패",
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankRes))
                .addHeader("Content-Type", "application/json")
        )
        //when, then
        assertThrows<NoSuchElementException> {
            openBankApiClient.requestAccount(
                OpenBankAccountRequestDto(
                    accessToken = "test",
                    openBankId = "1234"
                )
            )
        }
    }

    @Test
    fun requestBalance()= runTest{
        //given
        val openBankRes = OpenBankBalanceResponseDto(
            rsp_code = "A00001",
            rsp_message = "성공",
            balance_amt = "100000",
            available_amt = "100000",
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankRes))
                .addHeader("Content-Type", "application/json")
        )
        //when
        val result = openBankApiClient.requestBalance(
            OpenBankBalanceRequestDto(
                accessToken = "test",
                fintechUseNum = "11111",
            )
        )
        //then
        assertEquals("100000", result.balance_amt)
        assertEquals("100000", result.available_amt)
    }

    @Test
    @DisplayName("응답코드 알파벳 O로 시작하면 잘못된 요청이다.")
    fun requestBalance_실패()= runTest{
        //given
        val openBankRes = OpenBankBalanceResponseDto(
            rsp_code = "O0001",
            rsp_message = "실패",
        )
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(objectMapper.writeValueAsString(openBankRes))
                .addHeader("Content-Type", "application/json")
        )
        //when,then
        assertThrows<NoSuchElementException> {
            openBankApiClient.requestBalance(
                OpenBankBalanceRequestDto(
                    accessToken = "test",
                    fintechUseNum = "11111",
                )
            )
        }
    }
}