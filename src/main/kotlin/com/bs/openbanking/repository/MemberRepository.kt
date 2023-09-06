package com.bs.openbanking.repository

import com.bs.openbanking.domain.Member
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MemberRepository:JpaRepository<Member, Long> {
    fun existsMemberById(id:Long):Boolean
    fun findMemberByEmail(email:String):Optional<Member>
}