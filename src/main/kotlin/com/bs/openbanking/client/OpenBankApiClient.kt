package com.bs.openbanking.client

import com.bs.openbanking.dto.openbank.*
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@Service
@Slf4j
class OpenBankApiClient(
    private val webClient: WebClient,
    ){
    @Value("\${openbank.useCode}")
    private lateinit var useCode: String
    @Value("\${openbank.client-id}")
    private lateinit var clientId:String
    @Value("\${openbank.client-secret}")
    private lateinit var clientSecret:String
    @Value("\${openbank.redirect-url}")
    private lateinit var redirectUrl:String


    /**
     * 오픈뱅킹 사용자 토큰 요청
     */
    suspend fun requestToken(code:String):OpenBankTokenResponseDto{
        val requestUrl = "/oauth/2.0/token"

        val requestBody = LinkedMultiValueMap<String, String>()
        requestBody.add("code",code)
        requestBody.add("client_id", clientId)
        requestBody.add("client_secret", clientSecret)
        requestBody.add("redirect_uri",redirectUrl)
        requestBody.add("grant_type", "authorization_code")

        val openBankResponseToken = webClient
            .post()
            .uri{
                it.path(requestUrl).build()
            }
            .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8")
            .bodyValue(requestBody)
            .retrieve()
            .awaitBody<OpenBankTokenResponseDto>()

        if (isOpenBankRequestError(rspCode = openBankResponseToken.rsp_code)){
            println(openBankResponseToken.rsp_message)
            throw NoSuchElementException(openBankResponseToken.rsp_message)
        }
        return openBankResponseToken
    }

    /**
     * user ci 정보 가져오기
     */
    suspend fun requestOpenBankUserInfo(openBankUserInfoRequestDto: OpenBankUserInfoRequestDto):OpenBankUserInfoResponseDto{
        val requestUrl  =  "/v2.0/user/me"

        val openBankUserInfo = webClient
            .get()
            .uri{it.path(requestUrl)
                .queryParam("user_seq_no", openBankUserInfoRequestDto.openBankId)
                .build()
            }
            .header("Authorization", "bearer ${openBankUserInfoRequestDto.accessToken}")
            .retrieve()
            .awaitBody<OpenBankUserInfoResponseDto>()

        if (isOpenBankRequestError(openBankUserInfo.rsp_code)){
            throw NoSuchElementException(openBankUserInfo.rsp_message)
        }
        return openBankUserInfo
    }

    /**
     * 계좌조회
     */
    suspend fun requestAccount(openBankAccountRequestDto: OpenBankAccountRequestDto):List<OpenBankAccountDto>{
        val requestUrl = "/v2.0/account/list"

        val accountResponseDto = webClient
            .get()
            .uri {
                it.path(requestUrl)
                    .queryParam("user_seq_no", openBankAccountRequestDto.openBankId)
                    .queryParam("include_cancel_yn", openBankAccountRequestDto.includeCancelYn)
                    .queryParam("sort_order", openBankAccountRequestDto.sortOrder).build()
            }
            .header("Authorization", "bearer ${openBankAccountRequestDto.accessToken}")
            .retrieve()
            .awaitBody<OpenBankAccountResponseDto>()

        if (isOpenBankRequestError(rspCode = accountResponseDto.rsp_code)){
            println(accountResponseDto.rsp_message)
            throw NoSuchElementException(accountResponseDto.rsp_message)
        }
        return accountResponseDto.res_list.orEmpty()
    }

    /**
     * 잔액조회
     */
    suspend fun requestBalance(openBankBalanceRequestDto: OpenBankBalanceRequestDto):OpenBankBalanceResponseDto{
        val requestUrl = "/v2.0/account/balance/fin_num"

        val body = webClient
            .get()
            .uri {
                it.path(requestUrl)
                    .queryParam("bank_tran_id", getTransId())
                    .queryParam("fintech_use_num", openBankBalanceRequestDto.fintechUseNum)
                    .queryParam("tran_dtime", getTransTime()).build()
            }
            .header("Authorization", "bearer ${openBankBalanceRequestDto.accessToken}")
            .retrieve()
            .awaitBody<OpenBankBalanceResponseDto>()

        if(isOpenBankRequestError(body.rsp_code)){
            throw NoSuchElementException("error")
        }
        return body
    }

    private fun getTransId():String{
        return useCode+"U"+generateRandomString()
    }

    private fun generateRandomString():String{
        val charPool = ('A'..'Z') + ('0'..'9')
        return List(8){charPool.random()}.joinToString { "" }
    }

    private fun getTransTime():String{
        return ZonedDateTime
            .of(LocalDateTime.now(), ZoneId.of("Asia/Seoul"))
            .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
    }


    //공통
    private fun isOpenBankRequestError(rspCode:String?):Boolean{
        if (rspCode.isNullOrEmpty()) return false
        if (rspCode.startsWith("O")) return true
        return false
    }

}