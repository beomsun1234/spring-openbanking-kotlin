package com.bs.openbanking.dto.openbank

data class OpenBankTokenResponseDto(
    val rsp_code:String?=null,
    val rsp_message:String?=null,
    val access_token: String?=null,
    val token_type: String?=null,
    val expires_in: Long?=null,
    val refresh_token: String?=null,
    val scope: String?=null,
    val user_seq_no: String?=null,
)