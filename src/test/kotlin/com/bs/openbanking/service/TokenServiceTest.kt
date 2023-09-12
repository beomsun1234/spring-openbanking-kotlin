package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.domain.Member
import com.bs.openbanking.domain.OpenBankToken
import com.bs.openbanking.dto.OpenBankTokenSaveDto
import com.bs.openbanking.dto.openbank.OpenBankTokenResponseDto
import com.bs.openbanking.repository.MemberRepository
import com.bs.openbanking.repository.OpenBankTokenRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.NoSuchElementException

@ExtendWith(MockitoExtension::class)
internal class TokenServiceTest {

    @Mock
    private lateinit var memberRepository: MemberRepository
    @Mock
    private lateinit var openBankApiClient: OpenBankApiClient
    @Mock
    private lateinit var openBankTokenRepository: OpenBankTokenRepository
    @InjectMocks
    private lateinit var tokenService: TokenService

    @Test
    fun saveOpenBankTokenWithOpenBankId() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    Member(
                        id = 1L,
                        email = "test",
                        password = "test",
                    )
                ))
        Mockito.`when`(openBankTokenRepository.existsByMemberId(Mockito.anyLong()))
            .thenReturn(false)
        Mockito.`when`(openBankApiClient.requestToken(Mockito.anyString()))
            .thenReturn(
                OpenBankTokenResponseDto(
                    rsp_code = "A0001",
                    rsp_message = "성공",
                    access_token = "test",
                    refresh_token = "test",
                    user_seq_no = "1234",
                    expires_in = 100L
                )
            )
        Mockito.`when`(openBankTokenRepository.save(
            Mockito.any()
        )).thenReturn(
            OpenBankToken(
                id = 1L,
                accessToken = "test",
                refreshToken = "test",
                expiresIn = 100L,
                memberId = 1L
            )
        )
        //when, then
        tokenService.saveOpenBankTokenAndOpenBankId(
            OpenBankTokenSaveDto(
                memberId = 1L,
                code="test"
            )
        )
    }
    @Test
    @DisplayName("오픈뱅킹 아이디를 가지고있어 업데이트를 하지 않는다.")
    fun saveOpenBankTokenWithOpenBankId_성공2() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    Member(
                        id = 1L,
                        email = "test",
                        password = "test",
                        openBankId = "1234",
                    )))
        Mockito.`when`(openBankTokenRepository.existsByMemberId(Mockito.anyLong()))
            .thenReturn(false)
        Mockito.`when`(openBankApiClient.requestToken(Mockito.anyString()))
            .thenReturn(
                OpenBankTokenResponseDto(
                    rsp_code = "A0001",
                    rsp_message = "성공",
                    access_token = "test",
                    refresh_token = "test",
                    user_seq_no = "1234",
                    expires_in = 100L
                )
            )
        Mockito.`when`(openBankTokenRepository.save(
            Mockito.any()
        )).thenReturn(
            OpenBankToken(
                id = 1L,
                accessToken = "test",
                refreshToken = "test",
                expiresIn = 100L,
                memberId = 1L
            )
        )
        //when, then
        tokenService.saveOpenBankTokenAndOpenBankId(
            OpenBankTokenSaveDto(
                memberId = 1L,
                code="test"
            )
        )
    }
    @Test
    @DisplayName("유저가 존재하지 않는다. NoSuchElementException")
    fun saveOpenBankTokenWithOpenBankId_실패() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    null
                ))
        //when, then
        assertThrows<NoSuchElementException>{
            tokenService.saveOpenBankTokenAndOpenBankId(
                OpenBankTokenSaveDto(
                    memberId = 1L,
                    code="test"
                )
            )
        }
    }
    @Test
    @DisplayName("오픈뱅킹 요청 실패 NoSuchElementException")
    fun saveOpenBankTokenWithOpenBankId_실패2() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    Member(
                        id = 1L,
                        email = "test",
                        password = "test",
                    )))
        Mockito.`when`(openBankTokenRepository.existsByMemberId(Mockito.anyLong()))
            .thenReturn(false)
        Mockito.`when`(openBankApiClient.requestToken(Mockito.anyString()))
            .thenThrow(
                NoSuchElementException::class.java
            )
        //when, then
        assertThrows<NoSuchElementException> {
            tokenService.saveOpenBankTokenAndOpenBankId(
                OpenBankTokenSaveDto(
                    memberId = 1L,
                    code = "test"
                )
            )
        }
    }
    @Test
    @DisplayName("token이 이미 존재할 경우 IllegalArgumentException")
    fun saveOpenBankTokenWithOpenBankId_실패3() = runTest{
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    Member(
                        id = 1L,
                        email = "test",
                        password = "test",
                    )))
        Mockito.`when`(openBankTokenRepository.existsByMemberId(Mockito.anyLong()))
            .thenReturn(true)
        //when, then
        assertThrows<IllegalArgumentException> {
            tokenService.saveOpenBankTokenAndOpenBankId(
                OpenBankTokenSaveDto(
                    memberId = 1L,
                    code = "test"
                )
            )
        }
    }
    @Test
    fun findOpenBankTokenByMemberId()= runTest{
        //given
        Mockito.`when`(openBankTokenRepository.findByMemberId(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    OpenBankToken(
                        id = 1L,
                        memberId = 1L,
                        accessToken = "test",
                        refreshToken = "test",
                        expiresIn = 100L,
                    )
                )
            )
        //when
        val result = tokenService.findOpenBankUserTokenByIdMemberId(1L)
        //then
        assertEquals(1L, result.memberId)
        assertEquals("test", result.accessToken)
        assertEquals("test", result.refreshToken)
    }
    @Test
    @DisplayName("토큰이 존재하지 않는다.  NoSuchElementException")
    fun findOpenBankTokenByMemberId_실패()= runTest{
        //given
        Mockito.`when`(openBankTokenRepository.findByMemberId(Mockito.anyLong()))
            .thenReturn(
                Optional.ofNullable(
                    null
                )
            )
        //when, then
        assertThrows<NoSuchElementException>{
            tokenService.findOpenBankUserTokenByIdMemberId(1L)
        }
    }
}