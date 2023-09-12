package com.bs.openbanking.dto.openbank


data class OpenBankAccountResponseDto(
    val rsp_code:String?=null,
    val rsp_message:String?=null,
    val api_tran_id:String?=null,
    val api_tran_dtm:String?=null,
    val user_name:String?=null,
    val res_cnt:String?=null,
    val res_list:List<OpenBankAccountDto>?= arrayListOf()
)
