package com.bs.openbanking.dto

import com.bs.openbanking.domain.AccountType

data class AccountDto(
    val id:Long,
    val memberId:Long,
    val bankName:String,
    val bankCode:String,
    val accountNumber:String,
    val fintechUseNum:String,
    val balance:String,
    val accountType: AccountType?=AccountType.SUB,
    val holderName:String,
)
