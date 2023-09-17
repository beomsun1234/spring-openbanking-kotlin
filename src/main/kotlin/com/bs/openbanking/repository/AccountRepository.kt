package com.bs.openbanking.repository

import com.bs.openbanking.domain.Account
import com.bs.openbanking.domain.AccountType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface AccountRepository : JpaRepository<Account,Long>{
    fun findAccountsByMemberId(memberId:Long): List<Account>?

    @Query("select a from Account as a where a.memberId= :memberId and a.accountType = :accountType")
    fun findMainAccountByMemberId(@Param("memberId") memberId: Long, @Param("accountType") accountType: AccountType = AccountType.MAIN):Optional<Account>
}