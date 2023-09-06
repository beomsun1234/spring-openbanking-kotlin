package com.bs.openbanking.repository

import com.bs.openbanking.domain.OpenBankToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface OpenBankTokenRepository:JpaRepository<OpenBankToken, Long> {
    fun findByMemberId(memberId:Long):Optional<OpenBankToken>
}