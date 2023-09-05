package com.bs.openbanking.domain

import javax.persistence.*


@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    var email: String,
    var password: String,
    var openBankCi: String?=null,
    @Column(unique = true, nullable = true)
    var openBankId: String?=null,
):BaseTime() {
    fun updateOpenBankCi(openBankCi: String){
        this.openBankCi = openBankCi
    }
    fun updateOpenBankId(openBankId: String){
        this.openBankId = openBankId
    }
    fun isVailedPassword(password: String):Boolean{
        return this.password == password
    }
    fun hasOpenBankCi():Boolean{
        return !(this.openBankCi == null || this.openBankCi!!.isBlank())
    }
}