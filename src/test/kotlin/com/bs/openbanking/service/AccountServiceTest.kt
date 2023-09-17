package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.domain.Account
import com.bs.openbanking.domain.AccountType
import com.bs.openbanking.domain.Member
import com.bs.openbanking.dto.OpenBankTokenDto
import com.bs.openbanking.dto.openbank.OpenBankAccountDto
import com.bs.openbanking.dto.openbank.OpenBankAccountRequestDto
import com.bs.openbanking.dto.openbank.OpenBankBalanceRequestDto
import com.bs.openbanking.dto.openbank.OpenBankBalanceResponseDto
import com.bs.openbanking.repository.AccountRepository
import com.bs.openbanking.repository.MemberRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
internal class AccountServiceTest {

    @Mock private lateinit var memberRepository: MemberRepository
    @Mock private lateinit var openBankApiClient: OpenBankApiClient
    @Mock private lateinit var tokenService: TokenService
    @Mock private lateinit var accountRepository: AccountRepository
    @InjectMocks lateinit var accountService: AccountService


    @Test
    @DisplayName("저장된 계좌가 없을경우 모두 저장")
    fun saveAccounts() = runTest {
        val memberId=1L
        val account1=Account(
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = memberId,
                    email = "test",
                    password = "test",
                    openBankCi = "test",
                    openBankId = "1234")))
        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(
                arrayListOf()
            )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= memberId,
                    accessToken = "test",
                    refreshToken = "test",))
        Mockito.`when`(openBankApiClient.requestAccount(OpenBankAccountRequestDto(
            accessToken = "test",
            openBankId = "1234",
        ))).thenReturn(
            arrayListOf(
                OpenBankAccountDto(
                    account_holder_name = "test",
                    fintech_use_num = "1111",
                    bank_code_std = "11",
                    bank_name = "test",
                    account_num = "test",
                    account_num_masked = "test",
                ),
                OpenBankAccountDto(
                    account_holder_name = "test",
                    fintech_use_num = "2222",
                    bank_code_std = "22",
                    bank_name = "test2",
                    account_num = "test2",
                    account_num_masked = "test2",)))
        Mockito.`when`(accountRepository.saveAll(arrayListOf(
            account1,
            account2,
        ))).thenReturn(arrayListOf(
            account1,
            account2,
        ))
        //when, then
        assertEquals(2,accountService.saveAccounts(memberId))
    }
    @Test
    @DisplayName("2개의 계좌중 1개는 이미 저장되어있을경우")
    fun saveAccounts_1() = runTest {
        val memberId=1L
        val account1=Account(
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = memberId,
                    email = "test",
                    password = "test",
                    openBankCi = "test",
                    openBankId = "1234")))
        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(
                arrayListOf(
                    account1
                )
            )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= memberId,
                    accessToken = "test",
                    refreshToken = "test",))
        Mockito.`when`(openBankApiClient.requestAccount(OpenBankAccountRequestDto(
            accessToken = "test",
            openBankId = "1234",
        ))).thenReturn(
            arrayListOf(
                OpenBankAccountDto(
                    account_holder_name = "test",
                    fintech_use_num = "1111",
                    bank_code_std = "11",
                    bank_name = "test",
                    account_num = "test",
                    account_num_masked = "test",
                ),
                OpenBankAccountDto(
                    account_holder_name = "test",
                    fintech_use_num = "2222",
                    bank_code_std = "22",
                    bank_name = "test2",
                    account_num = "test2",
                    account_num_masked = "test2",)))
        Mockito.`when`(accountRepository.saveAll(arrayListOf(
            account2,
        ))).thenReturn(arrayListOf(
            account2
        ))
        //when, then
        assertEquals(1,accountService.saveAccounts(memberId))
    }
    @Test
    @DisplayName("2개의 계좌 모두 이미 저장되어 있다면 IllegalArgumentException 에러")
    fun saveAccounts_IllegalArgumentException() = runTest {
        val memberId=1L
        val account1=Account(
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = memberId,
                    email = "test",
                    password = "test",
                    openBankCi = "test",
                    openBankId = "1234")))
        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(
                arrayListOf(
                    account1,
                    account2
                )
            )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= memberId,
                    accessToken = "test",
                    refreshToken = "test",))
        Mockito.`when`(openBankApiClient.requestAccount(OpenBankAccountRequestDto(
            accessToken = "test",
            openBankId = "1234",
        ))).thenReturn(
            arrayListOf(
                OpenBankAccountDto(
                    fintech_use_num = "1111",
                    bank_code_std = "11",
                    bank_name = "test",
                    account_num = "test",
                    account_num_masked = "test",
                ),
                OpenBankAccountDto(
                    fintech_use_num = "2222",
                    bank_code_std = "22",
                    bank_name = "test2",
                    account_num = "test2",
                    account_num_masked = "test2",)))
        //when, then
        assertThrows<IllegalArgumentException>{
            accountService.saveAccounts(memberId)
        }
    }
    @Test
    @DisplayName("유저가 존재하지 않는다.")
    fun saveAccounts_NoSuchElementException1() = runTest {
        //given
        val memberId=1L
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                null))
        //when, then
        assertThrows<NoSuchElementException>{
            accountService.saveAccounts(memberId)
        }
    }
    @Test
    @DisplayName("유저가 openBankId를 가지고있지 않다.")
    fun saveAccounts_NoSuchElementException2() = runTest {
        //given
        val memberId=1L
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = memberId,
                    email = "test",
                    password = "test",
                    openBankCi = "test",)))
        //when, then
        assertThrows<NoSuchElementException>{
            accountService.saveAccounts(memberId)
        }
    }
    @Test
    @DisplayName("토큰이 없다.")
    fun saveAccounts_NoSuchElementException3() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = 1L,
                    email = "test",
                    password = "test",
                    openBankCi = "test",
                    openBankId = "1234")))
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenThrow(NoSuchElementException::class.java)
        //when, then
        assertThrows<NoSuchElementException>{
            accountService.saveAccounts(1L)
        }
    }
    @Test
    @DisplayName("오픈뱅킹 요청에러")
    fun saveAccounts_NoSuchElementException4() = runTest {
        //given
        Mockito.`when`(memberRepository.findById(Mockito.anyLong()))
            .thenReturn(Optional.ofNullable(
                Member(
                    id = 1L,
                    email = "test",
                    password = "test",
                    openBankCi = "test",
                    openBankId = "1234")))
        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(
                arrayListOf()
            )
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= 1L,
                    accessToken = "test",
                    refreshToken = "test",))
        Mockito.`when`(openBankApiClient.requestAccount(OpenBankAccountRequestDto(
            accessToken = "test",
            openBankId = "1234",
        ))).thenThrow(NoSuchElementException::class.java)
        //when, then
        assertThrows<NoSuchElementException>{
            accountService.saveAccounts(1L)
        }
    }

    @Test
    fun findAccountsByMemberId()= runTest{
        //given
        val memberId = 1L
        val account1=Account(
            id = 1L,
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            id = 2L,
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        val accounts = arrayListOf(
            account1,
            account2)

        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(accounts)
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= 1L,
                    accessToken = "test",
                    refreshToken = "test",))

        accounts.map {
                Mockito.`when`(openBankApiClient.requestBalance(OpenBankBalanceRequestDto(
                    accessToken = "test",
                        fintechUseNum = it.fintechUseNum
                    ))).thenReturn(OpenBankBalanceResponseDto(
                        available_amt = "10000",
                        balance_amt = "20000",
                    rsp_code = "A0000",

                    ))
                }

        //when
        val result = accountService.findAccountsByMemberId(memberId)
        //then
        assertEquals("20000", result[0].balance)
    }

    @Test
    @DisplayName("오픈뱅킹 금액조회 실패시 0을 대입한다.")
    fun findAccountsByMemberId_2()= runTest{
        //given
        val memberId = 1L
        val account1=Account(
            id = 1L,
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            id = 2L,
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        val accounts = arrayListOf(
            account1,
            account2)

        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(accounts)
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenReturn(
                OpenBankTokenDto(
                    id = 1L,
                    memberId= 1L,
                    accessToken = "test",
                    refreshToken = "test",))

        accounts.map {
            Mockito.`when`(openBankApiClient.requestBalance(OpenBankBalanceRequestDto(
                accessToken = "test",
                fintechUseNum = it.fintechUseNum
            ))).thenThrow(NoSuchElementException::class.java)
        }
        //when
        val result = accountService.findAccountsByMemberId(memberId)
        //then
        assertEquals("0", result[0].balance)
    }

    @Test
    @DisplayName("계좌가 없다.")
    fun findAccountsByMemberId_NoSuchElementException()= runTest{
        //given
        val memberId = 1L

        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(
                arrayListOf()
            )
        //when, then
        assertThrows<NoSuchElementException> {
            accountService.findAccountsByMemberId(memberId)
        }
    }

    @Test
    @DisplayName("토큰이 존재하지 않는다.")
    fun findAccountsByMemberId_NoSuchElementException2()= runTest{
        //given
        val memberId = 1L
        val account1=Account(
            id = 1L,
            memberId=memberId,
            fintechUseNum = "1111",
            accountNum = "test",
            bankCode = "11",
            bankName = "test",
            holderName = "test"
        )
        val account2 = Account(
            id = 2L,
            memberId=memberId,
            fintechUseNum = "2222",
            accountNum = "test2",
            bankCode = "22",
            bankName = "test2",
            holderName = "test"
        )
        val accounts = arrayListOf(
            account1,
            account2)

        Mockito.`when`(accountRepository.findAccountsByMemberId(Mockito.anyLong()))
            .thenReturn(accounts)
        Mockito.`when`(tokenService.findOpenBankUserTokenByIdMemberId(Mockito.anyLong()))
            .thenThrow(NoSuchElementException::class.java)
        //when, then
        assertThrows<NoSuchElementException> {
            accountService.findAccountsByMemberId(memberId)
        }
    }

    @Test
    @DisplayName("주계좌가 설정되어있지 않을때 업데이트 요청")
    fun updateAccountType()= runTest {
        //given
        val account = Account(
            id = 1L,
            memberId = 1L,
            fintechUseNum = "1111",
            bankName = "test",
            bankCode = "11",
            accountNum = "11112312312",
            holderName = "test"
        )

        Mockito.`when`(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(account))
        Mockito.`when`(accountRepository.findMainAccountByMemberId(1L)).thenReturn(Optional.ofNullable(null))
        //when
        accountService.updateAccountType(1L,1L)
        //then
        assertEquals(AccountType.MAIN, account.accountType)
    }

    @Test
    @DisplayName("주계좌가 설정되어있을 경우 요청 계좌를 주계좌로 기존 주계좌를 보조 계좌로 업데이트")
    fun updateAccountType_1()= runTest {
        //given
        val preMainAccount = Account(
            id = 1L,
            memberId = 1L,
            fintechUseNum = "1111",
            bankName = "test",
            bankCode = "11",
            accountNum = "11112312312",
            accountType = AccountType.MAIN,
            holderName = "test"
        )
        val account = Account(
            id = 2L,
            memberId = 1L,
            fintechUseNum = "222222",
            bankName = "test2",
            bankCode = "11",
            accountNum = "22222222222",
            holderName = "test"
        )
        Mockito.`when`(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(account))
        Mockito.`when`(accountRepository.findMainAccountByMemberId(1L)).thenReturn(Optional.ofNullable(preMainAccount))
        //when
        accountService.updateAccountType(1L,1L)
        //then
        assertEquals(AccountType.SUB, preMainAccount.accountType)
        assertEquals(AccountType.MAIN, account.accountType)
    }

    @Test
    @DisplayName("계좌가 없다.")
    fun updateAccountType_NoSuchElementException()= runTest {
        val account = Account(
            id = 2L,
            memberId = 1L,
            fintechUseNum = "222222",
            bankName = "test2",
            bankCode = "11",
            accountNum = "22222222222",
            holderName = "test"
        )
        Mockito.`when`(accountRepository.findById(Mockito.anyLong())).thenReturn(Optional.ofNullable(null))
        assertThrows<NoSuchElementException> {
            accountService.updateAccountType(1L, 1L)
        }

    }
}