package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.dto.MemberDto
import com.bs.openbanking.dto.loginDto
import com.bs.openbanking.dto.SignUpDto
import com.bs.openbanking.dto.openbank.OpenBankUserInfoRequestDto
import com.bs.openbanking.repository.MemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException


@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberRepository: MemberRepository,
    private val tokenService: TokenService,
    private val openBankApiClient: OpenBankApiClient,
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
    suspend fun updateOpenBankCi(memberId: Long){
        val member = memberRepository.findById(memberId).orElseThrow()

        if (!member.hasOpenBankId()) throw IllegalArgumentException("오픈뱅킹 id가 없음")

        if (member.hasOpenBankCi()) throw IllegalArgumentException("ci정보가 이미 있습니다.")

        val openBankToken = tokenService.findOpenBankUserTokenByIdMemberId(memberId)

        val openBankUserInfo = openBankApiClient.requestOpenBankUserInfo(
            OpenBankUserInfoRequestDto(
                openBankId = member.openBankId!!,
                accessToken = openBankToken.accessToken!!
            )
        )

        member.updateOpenBankCi(openBankUserInfo.user_ci!!)
    }



}