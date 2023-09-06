package com.bs.openbanking.repository

import com.bs.openbanking.domain.Account
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.ActiveProfiles



@DataJpaTest
@ActiveProfiles("test")
internal class AccountRepositoryTest(
    @Autowired private val accountRepository: AccountRepository
){
    @Test
    @Rollback
    fun 회원ID를사용해서_account찾기(){
        //given
        var memberId = 1L
        val account = accountRepository.save(
            Account(
                memberId = memberId,
                bankCode = "test",
                fintechUseNum = "1111",
                bankName = "test",
                accountNum = "test",
                accountSeq = "test"
            )
        )
        //when
        val result = accountRepository.findAccountsByMemberId(memberId).orEmpty()
        //then
        Assertions.assertEquals(1,result.size)
    }
    @Test
    @Rollback
    fun 회원ID를사용해서_account찾기_계좌가없을경우(){
        //given
        var memberId = 1L
        //when
        //then
        Assertions.assertEquals(true,accountRepository.findAccountsByMemberId(memberId).isNullOrEmpty())
    }
    @Test
    @Rollback
    fun 계좌저장시_핀테크사용번호가_이미존재할경우_에러발생한다(){
        //given
        var memberId = 1L
        val alreadySaved = accountRepository.save(
            Account(
                memberId = memberId,
                bankCode = "test",
                fintechUseNum = "2222",
                bankName = "test",
                accountNum = "test",
                accountSeq = "test"
            )
        )
        accountRepository.save(alreadySaved)
        //when then
        Assertions.assertThrows(DataIntegrityViolationException::class.java){
                accountRepository.save(Account(
                    memberId = memberId,
                    bankCode = "test",
                    fintechUseNum = "2222",
                    bankName = "test",
                    accountNum = "test",
                    accountSeq = "test"
                ))
        }
    }
}