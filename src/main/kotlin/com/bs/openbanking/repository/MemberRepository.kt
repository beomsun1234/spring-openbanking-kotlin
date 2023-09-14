package com.bs.openbanking.repository

import com.bs.openbanking.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

@Repository
interface MemberRepository:JpaRepository<Member, Long> {
    fun existsMemberById(id:Long):Boolean
    fun findMemberByEmail(email:String):Optional<Member>

    @Modifying
    @Transactional
    @Query("UPDATE Member set openBankId = :openBankId where id = :id")
    fun updateOpenBankId(id:Long,openBankId:String)

    @Modifying
    @Transactional
    @Query("UPDATE Member set openBankCi = :openBankCi where id = :id")
    fun updateOpenBankCi(id:Long,openBankCi:String)
}