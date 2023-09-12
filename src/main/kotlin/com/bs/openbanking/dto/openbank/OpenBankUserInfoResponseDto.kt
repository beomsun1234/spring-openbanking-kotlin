package com.bs.openbanking.dto.openbank

data class OpenBankUserInfoResponseDto(
    val api_tran_id:String?=null,
    val api_tran_dtm:String?=null,
    val rsp_code:String?=null,
    val rsp_message:String?=null,
    val user_seq_no:String?=null,
    val user_ci:String?=null,
)
