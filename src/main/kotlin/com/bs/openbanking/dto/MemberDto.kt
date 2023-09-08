package com.bs.openbanking.dto

import com.bs.openbanking.domain.Member

data class MemberDto(
    val memberId:Long?=null,
    val email:String?=null,
){
    companion object{
        fun from(member:Member):MemberDto{
            return MemberDto(member.id, member.email)
        }
    }
}

