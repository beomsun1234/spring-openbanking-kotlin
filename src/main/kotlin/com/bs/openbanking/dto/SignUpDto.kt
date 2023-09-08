package com.bs.openbanking.dto

import com.bs.openbanking.domain.Member

data class SignUpDto(
    val email:String,
    val password:String
    ) {
    fun toEntity(): Member {
        return Member(email = this.email, password = this.password!!)
    }
}
