package com.bs.openbanking.service

import com.bs.openbanking.domain.Member
import com.bs.openbanking.dto.loginDto
import com.bs.openbanking.dto.SignUpDto
import com.bs.openbanking.repository.MemberRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.lang.IllegalArgumentException
import java.util.*

@ExtendWith(MockitoExtension::class)
internal class MemberServiceTest {

    @Mock
    private lateinit var memberRepository:MemberRepository

    @InjectMocks
    private lateinit var memberService: MemberService

    @Test
    fun signUp() = runBlocking {
        //given
        Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.save(Mockito.any(Member::class.java))).thenReturn(Member(id = 1L, email = "test", password = "test"))
        //when
        val signUpMember = memberService.signUp(SignUpDto("test", "test"))
        //then
        assertEquals(1L, signUpMember)
    }

    @Test
    fun signIn() = runBlocking{
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
        assertThrows(NoSuchElementException::class.java){
            runBlocking {
                memberService.login(loginDto("test", "test"))
            }
        }
    }
    @Test
    fun 로그인실패_비밀번호다름()= runBlocking{
        //given
        val member = Member(id = 1L, email = "test", password = "test")
        Mockito.`when`(memberRepository.findMemberByEmail(Mockito.anyString()))
            .thenReturn(Optional.ofNullable(member))
        //when, then
        assertThrows(IllegalArgumentException::class.java){
            runBlocking {
                memberService.login(loginDto("test", "test1"))
            }
        }
    }

    @Test
    fun findMemberById() = runBlocking{
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
    fun findMemberById_없는ID(){
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        //when, then

        assertThrows(NoSuchElementException::class.java){
            runBlocking{
                memberService.findMemberById(1L)
            }
        }
    }

    @Test
    fun updateOpenBankId() = runBlocking{
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(Member(id=1L, email = "test", password = "test", openBankId = "1234")))
        //when, then
        memberService.updateOpenBankId(1L,"1234")
    }
    @Test
    fun updateOpenBankId_실패_존재하지않는유저(){
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        //when, then
        assertThrows(NoSuchElementException::class.java){
            runBlocking{
                memberService.updateOpenBankId(1L,"1234")
            }
        }
    }
    @Test
    fun updateOpenBankCi() = runBlocking{
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(Member(id=1L, email = "test", password = "test", openBankCi = "ci")))
        //when, then
        memberService.updateOpenBankCi(1L,"ci")
    }

    @Test
    fun updateOpenBankCi_실패_존재하지않는유저() {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        //when, then
        assertThrows(NoSuchElementException::class.java){
            runBlocking{
                memberService.updateOpenBankCi(1L,"ci")
            }
        }
    }

}