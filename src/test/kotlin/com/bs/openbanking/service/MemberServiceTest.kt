package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.domain.Member
import com.bs.openbanking.dto.OpenBankTokenDto
import com.bs.openbanking.dto.loginDto
import com.bs.openbanking.dto.SignUpDto
import com.bs.openbanking.dto.openbank.OpenBankUserInfoRequestDto
import com.bs.openbanking.dto.openbank.OpenBankUserInfoResponseDto
import com.bs.openbanking.repository.MemberRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.quality.Strictness
import java.lang.IllegalArgumentException
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class MemberServiceTest {

    @Mock
    private lateinit var memberRepository: MemberRepository

    @Mock
    private lateinit var openBankApiClient: OpenBankApiClient

    @Mock
    private lateinit var tokenService: TokenService

    @InjectMocks
    private lateinit var memberService: MemberService

    @Test
    fun signUp() = runTest {
        //given
        Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.save(Mockito.any(Member::class.java)))
            .thenReturn(Member(id = 1L, email = "test", password = "test"))
        //when
        val signUpMember = memberService.signUp(SignUpDto("test", "test"))
        //then
        assertEquals(1L, signUpMember)
    }

    @Test
    fun signIn() = runTest {
        //given
        Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.findMemberByEmail(Mockito.anyString()))
            .thenReturn(Optional.ofNullable(Member(id = 1L, email = "test", password = "test")))
        //when
        val loginDtoMember = memberService.login(loginDto("test", "test"))
        //then
        assertEquals("test", loginDtoMember.email)
    }

    @Test
    fun 로그인실패_없는email() {
        //given
        Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.findMemberByEmail(Mockito.anyString()))
            .thenReturn(Optional.ofNullable(null))
        //when, then
        assertThrows(NoSuchElementException::class.java) {
            runBlocking {
                memberService.login(loginDto("test", "test"))
            }
        }
    }

    @Test
    fun 로그인실패_비밀번호다름() = runTest {
        //given
        val member = Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.findMemberByEmail(Mockito.anyString()))
            .thenReturn(Optional.ofNullable(member))
        //when, then
        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                memberService.login(loginDto("test", "test1"))
            }
        }
    }

    @Test
    fun findMemberById() = runTest {
        //given
        val member = Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(member))
        //when
        val result = memberService.findMemberById(1L)
        //then
        assertEquals(member.id, result.memberId)
        assertEquals(member.email, result.email)
    }

    @Test
    fun findMemberById_없는ID() = runTest{
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        //when, then
        assertThrows<NoSuchElementException>{
            memberService.findMemberById(1L)
        }
    }


    @Test
    fun updateOpenBankCi() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(
            Optional.ofNullable(Member(id = 1L, email = "test", openBankId = "1234", password = "test"))
        )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong())).thenReturn(
            OpenBankTokenDto(1L, 1L, "test", "test", 1L)
        )
        Mockito.`when`(
            openBankApiClient.requestOpenBankUserInfo(
                OpenBankUserInfoRequestDto(
                    openBankId = "1234",
                    accessToken = "test"
                )
            )
        ).thenReturn(
            OpenBankUserInfoResponseDto(
                rsp_code = "A0001",
                user_ci = "1234",
                user_seq_no = "1234",
                rsp_message = "test"
            )
        )
        //when, then
        memberService.updateOpenBankCi(1L)
    }

    @Test
    fun updateOpenBankCi_실패_존재하지않는유저() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        //when, then
        assertThrows<NoSuchElementException>{
            memberService.updateOpenBankCi(1L,)
        }
    }

    @Test
    fun updateOpenBankCi_실패_이미_ci정보를가지고있다()= runTest{
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(
            Optional.ofNullable(
                Member(
                    id = 1L,
                    email = "test",
                    openBankId = "1234",
                    password = "test",
                    openBankCi = "test"
                )
            )
        )
        assertThrows<IllegalArgumentException>{
            memberService.updateOpenBankCi(1L)
        }
    }

    @Test
    fun updateOpenBankCi_실패_token이없다()= runTest{
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(
            Optional.ofNullable(
                Member(
                    id = 1L,
                    email = "test",
                    openBankId = "1234",
                    password = "test",
                )
            )
        )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong())).thenThrow(
            NoSuchElementException::class.java
        )
        assertThrows<NoSuchElementException>{
            memberService.updateOpenBankCi(1L)
        }
    }
    @Test
    fun updateOpenBankCi_오픈뱅킹요청실패() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(
            Member(id=1L, email = "test", password = "test", openBankId = "1234")
        ))
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong())).thenReturn(OpenBankTokenDto(
            1L, 1L, "test","test",100L
        ))
        Mockito.`when`(openBankApiClient.requestOpenBankUserInfo(OpenBankUserInfoRequestDto(
            openBankId = "1234",
            accessToken = "test",
        ))).thenThrow(
            NoSuchElementException::class.java
        )
        //when, then
        assertThrows<NoSuchElementException>{
            memberService.updateOpenBankCi(1L,)
        }
    }


}