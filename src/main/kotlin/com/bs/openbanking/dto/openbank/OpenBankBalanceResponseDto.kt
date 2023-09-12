package com.bs.openbanking.dto.openbank

data class OpenBankBalanceResponseDto(
    val api_tran_id:String?=null,
    val api_tran_dtm:String?=null,
    val rsp_code:String?=null,
    val rsp_message:String?=null,
    val balance_amt: String? = null,
    val available_amt: String? = null,
)
