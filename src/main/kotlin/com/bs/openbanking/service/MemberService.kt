package com.bs.openbanking.service

import com.bs.openbanking.dto.MemberDto
import com.bs.openbanking.dto.loginDto
import com.bs.openbanking.dto.SignUpDto
import com.bs.openbanking.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException


@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
) {

    @Transactional
    suspend fun signUp(signUpDto: SignUpDto): Long{
        return memberRepository.save(signUpDto.toEntity()).id!!
    }


    suspend fun login(loginDto: loginDto): MemberDto{
        val member = memberRepository.findMemberByEmail(loginDto.email).orElseThrow()

        if (!member.isVailedPassword(loginDto.password)) throw IllegalArgumentException("비밀번호 오류")

        return MemberDto.from(member)
    }

    suspend fun findMemberById(memberId:Long): MemberDto{
        val member = memberRepository.findById(memberId).orElseThrow()
        return MemberDto.from(member)
    }
    @Transactional
    suspend fun updateOpenBankCi(memberId:Long, openBankCi:String){
        val member = memberRepository.findById(memberId).orElseThrow()
        member.updateOpenBankCi(openBankCi)
    }
    @Transactional
    suspend fun updateOpenBankId(memberId:Long, openBankId:String){
        val member = memberRepository.findById(memberId).orElseThrow()
        member.updateOpenBankId(openBankId)
    }

}