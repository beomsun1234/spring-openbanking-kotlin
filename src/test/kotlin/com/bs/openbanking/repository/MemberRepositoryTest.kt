package com.bs.openbanking.repository

import com.bs.openbanking.domain.Member
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import java.util.NoSuchElementException


@DataJpaTest
@ActiveProfiles("test")
internal class MemberRepositoryTest(
    @Autowired private val memberRepository: MemberRepository
) {

    @Test
    @Rollback
    fun 회원이존재한다(){
        //given
        val savedMember = memberRepository.save(Member(email = "test", password = "test"))
        //when
        val result= savedMember!!.id?.let { memberRepository.existsMemberById(it) }
        //then
        Assertions.assertEquals(true, result)
    }

    @Test
    @Rollback
    fun 회원이존재하지않는다(){
        val memberId = 1L
        //given//when
        val reslut = memberRepository.existsMemberById(memberId)
        //then
        Assertions.assertEquals(false, reslut)
    }

    @Test
    @Rollback
    fun email로조회(){
        //given
        val email= "test"
        memberRepository.save(Member(email = email, password = "test"))
        //when
        val result = memberRepository.findMemberByEmail("test").orElseThrow()
        //then
        Assertions.assertEquals("test", result.email)
    }

    @Test
    @Rollback
    fun email로조회_없는email(){
        //given
        val email = "test"
        //when then
        Assertions.assertThrows(NoSuchElementException::class.java){
            memberRepository.findMemberByEmail(email).orElseThrow()
        }
    }

}