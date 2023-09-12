package com.bs.openbanking.repository

import com.bs.openbanking.domain.OpenBankToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles
import java.util.*

@DataJpaTest
@ActiveProfiles("test")
internal class OpenBankTokenRepositoryTest(
    @Autowired private val openBankTokenRepository:OpenBankTokenRepository
    ){

    @Test
    @Rollback
    fun 회원ID로_오픈뱅킹사용자토큰_조회(){
        //given
        var memberId = 1L
        val savedToken = openBankTokenRepository.save(
            OpenBankToken(
                memberId = memberId,
                accessToken = "test",
                refreshToken = "test",
                expiresIn = 1L
            )
        )
        //when
        val result = openBankTokenRepository.findByMemberId(memberId).orElseThrow()
        //then
        Assertions.assertEquals(savedToken.accessToken, result.accessToken)
    }
    @Test
    @Rollback
    fun 회원ID로_오픈뱅킹사용자토큰_조회_null(){
        //given
        var memberId = 1L
        //when, then
        Assertions.assertThrows(NoSuchElementException::class.java) {
            openBankTokenRepository.findByMemberId(memberId).orElseThrow()
        }
    }
    @Test
    @Rollback
    fun 동일한_MemberId를_가지고있으면_에러가발생한다(){
        //given
        var memberId = 1L
        openBankTokenRepository.save(
            OpenBankToken(
                memberId = memberId,
                accessToken = "test",
                refreshToken = "test",
                expiresIn = 1L
            )
        )
        //when, then
        Assertions.assertThrows(DataIntegrityViolationException::class.java) {
            openBankTokenRepository.save(
                OpenBankToken(
                    memberId = memberId,
                    accessToken = "test2",
                    refreshToken = "test2",
                    expiresIn = 1L
                )
            )
        }
    }

    @Test
    @Rollback
    @DisplayName("토큰이 존재한다.")
    fun existsByMemberId(){
        //given
        openBankTokenRepository.save(OpenBankToken(
            memberId = 1L,
            accessToken = "test",
            refreshToken = "test",
            expiresIn = 1L
        ))
        //when, then
        Assertions.assertEquals(true,openBankTokenRepository.existsByMemberId(1L))
    }
    @Test
    @Rollback
    @DisplayName("토큰이 존재하지않는다.")
    fun existsByMemberId_없음(){
        //given
        val memberId = 1L
        //when, then
        Assertions.assertEquals(false,openBankTokenRepository.existsByMemberId(memberId))
    }

}