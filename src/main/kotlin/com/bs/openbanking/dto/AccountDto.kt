package com.bs.openbanking.dto

data class AccountDto(
    val id:Long,
    val memberId:Long,
    val bankName:String,
    val bankCode:String,
    val accountNumber:String,
    val fintechUseNum:String,
    val balance:String,
)
