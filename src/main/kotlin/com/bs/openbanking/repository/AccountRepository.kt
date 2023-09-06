package com.bs.openbanking.repository

import com.bs.openbanking.domain.Account
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : JpaRepository<Account,Long>{
    fun findAccountsByMemberId(memberId:Long): List<Account>?
}