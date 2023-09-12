package com.bs.openbanking.dto

import com.bs.openbanking.domain.OpenBankToken

data class OpenBankTokenDto (
    val id:Long?=null,
    val memberId:Long?=null,
    val accessToken:String?=null,
    val refreshToken:String?=null,
    val expiresIn:Long?=null,
){
    companion object{
        fun from(openBankToken: OpenBankToken):OpenBankTokenDto{
            return OpenBankTokenDto(
                id= openBankToken.id,
                memberId = openBankToken.memberId,
                accessToken = openBankToken.accessToken,
                refreshToken = openBankToken.refreshToken,
                expiresIn = openBankToken.expiresIn,
            )
        }
    }
}