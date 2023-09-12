package com.bs.openbanking.dto.openbank

data class OpenBankAccountDto(
    val fintech_use_num:String?=null,
    val bank_code_std:String?=null,
    val bank_code_sub:String?=null,
    val bank_name:String?=null,
    val savings_bank_name:String?=null,
    val account_num:String?=null,
    val account_num_masked:String?=null,
    val account_seq:String?=null,
    val account_holder_name:String?=null,
    val account_holder_type:String?=null,
    val account_type:String?=null,
    val inquiry_agree_yn:String?=null,
    val inquiry_agree_dtime:String?=null,
    val transfer_agree_yn:String?=null,
    val transfer_agree_dtime:String?=null,
    val account_state:String?=null,
)
