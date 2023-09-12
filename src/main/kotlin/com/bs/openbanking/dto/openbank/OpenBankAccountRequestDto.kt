package com.bs.openbanking.dto.openbank

data class OpenBankAccountRequestDto(
    val accessToken:String,
    val openBankId:String,
    val includeCancelYn:String?="N",
    val sortOrder:String?="D",
)
