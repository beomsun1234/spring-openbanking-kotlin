package com.bs.openbanking.service

import com.bs.openbanking.client.OpenBankApiClient
import com.bs.openbanking.domain.Account
import com.bs.openbanking.dto.AccountDto
import com.bs.openbanking.dto.openbank.OpenBankAccountRequestDto
import com.bs.openbanking.dto.openbank.OpenBankBalanceRequestDto
import com.bs.openbanking.repository.AccountRepository
import com.bs.openbanking.repository.MemberRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.springframework.stereotype.Service
import kotlin.NoSuchElementException

@Service
class AccountService (
    private val openBankApiClient: OpenBankApiClient,
    private val memberRepository: MemberRepository,
    private val tokenService: TokenService,
    private val accountRepository: AccountRepository,
) {
    /**
     * 계좌저장
     */
    suspend fun saveAccounts(memberId: Long):Int {
        val member = memberRepository.findById(memberId).orElseThrow()

        if (!member.hasOpenBankId()) throw NoSuchElementException("오픈뱅킹 id가 없다.")

        val token = tokenService.findOpenBankUserTokenByIdMemberId(memberId)

        val duplicateAccountCheckMap =
            createDuplicateAccountCheckMap(accountRepository.findAccountsByMemberId(memberId)!!)

        val accounts = openBankApiClient.requestAccount(
            OpenBankAccountRequestDto(
                accessToken = token.accessToken!!,
                openBankId = member.openBankId!!)
        ).filter { !isDuplicatedAccount(duplicateAccountCheckMap, it.fintech_use_num!!) }
            .map { Account(
                    memberId = memberId,
                    accountNum = it.account_num_masked!!,
                    fintechUseNum = it.fintech_use_num!!,
                    bankCode = it.bank_code_std!!,
                    bankName = it.bank_name!!,
                ) }

        if (accounts.isEmpty()) throw IllegalArgumentException("모두 존재하는 계좌입니다.")

        return accountRepository.saveAll(accounts).size

    }

    private fun createDuplicateAccountCheckMap(accounts: List<Account>): HashMap<String, String> {
        if (accounts.isNullOrEmpty()) return hashMapOf()
        val checkDbMap = accounts.associate { it.fintechUseNum to it.fintechUseNum }
        return HashMap(checkDbMap)
    }

    private fun isDuplicatedAccount(db: HashMap<String, String>, fintechUseNum: String): Boolean {
        if (db.isEmpty()) return false
        if (db.contains(fintechUseNum)) return true
        return false
    }

    /**
     * 계좌조회
     */
    suspend fun findAccountsByMemberId(memberId: Long): List<AccountDto> {
        val accounts = accountRepository.findAccountsByMemberId(memberId)

        if (accounts.isNullOrEmpty()) throw NoSuchElementException("계좌 없음")

        val token = tokenService.findOpenBankUserTokenByIdMemberId(memberId)

        return addBalanceToAccounts(accounts = accounts, token.accessToken!!)
    }

    private suspend fun addBalanceToAccounts(accounts:List<Account>, accessToken: String):List<AccountDto>{
        return coroutineScope {
            accounts.map {
                async {
                    val balance = getBalance(accessToken = accessToken, fintechUseNum = it.fintechUseNum)
                    AccountDto(
                        id = it.id!!,
                        balance = balance,
                        bankName = it.bankName,
                        bankCode = it.bankCode,
                        memberId = it.memberId,
                        accountNumber = it.accountNum,
                        fintechUseNum = it.fintechUseNum,
                    )
                }
            }.awaitAll()
        }
    }

    private suspend fun getBalance(accessToken:String, fintechUseNum: String):String{
        return try {
            openBankApiClient.requestBalance(
                OpenBankBalanceRequestDto(
                    accessToken = accessToken,
                    fintechUseNum = fintechUseNum,
                )).balance_amt!!
        } catch (e: NoSuchElementException){
            println(e.message)
            "0"
        }

    }
}