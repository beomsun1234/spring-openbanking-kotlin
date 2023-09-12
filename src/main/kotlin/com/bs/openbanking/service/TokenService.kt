package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.domain.OpenBankToken
import com.bs.openbanking.dto.OpenBankTokenDto
import com.bs.openbanking.dto.OpenBankTokenSaveDto
import com.bs.openbanking.repository.MemberRepository
import com.bs.openbanking.repository.OpenBankTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class TokenService(
    private val memberRepository: MemberRepository,
    private val openBankApiClient: OpenBankApiClient,
    private val tokenRepository: OpenBankTokenRepository
){

    @Transactional
    suspend fun saveOpenBankTokenAndOpenBankId(openBankTokenSaveDto: OpenBankTokenSaveDto){
        val member = memberRepository.findById(openBankTokenSaveDto.memberId).orElseThrow()

        if(tokenRepository.existsByMemberId(openBankTokenSaveDto.memberId)){
            throw IllegalArgumentException("이미 토큰을 가지고있습니다.")
        }

        val responseOpenBankToken = openBankApiClient.requestToken(openBankTokenSaveDto.code)

        tokenRepository.save(
            OpenBankToken(
                memberId = openBankTokenSaveDto.memberId,
                accessToken = responseOpenBankToken.access_token!!,
                refreshToken = responseOpenBankToken.refresh_token!!,
                expiresIn = responseOpenBankToken.expires_in!!
            )
        )

        if (!member.hasOpenBankId()){
            member.updateOpenBankId(responseOpenBankToken.user_seq_no!!)
        }
    }

    suspend fun findOpenBankUserTokenByIdMemberId(memberId: Long): OpenBankTokenDto {
        return OpenBankTokenDto.from(tokenRepository.findByMemberId(memberId).orElseThrow())
    }

}