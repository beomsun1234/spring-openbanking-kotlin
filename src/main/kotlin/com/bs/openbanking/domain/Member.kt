package com.bs.openbanking.domain

import org.springframework.data.domain.Persistable
import javax.persistence.*


@Entity
class Member(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long?=null,
    var email: String,
    var password: String,
    var openBankCi: String?=null,
    @Column(unique = true, nullable = true)
    var openBankId: String?=null,
): BaseTime() {
    fun isValidPassword(password: String):Boolean{
        return this.password == password
    }
    fun hasOpenBankCi():Boolean{
        return !(this.openBankCi == null || this.openBankCi!!.isBlank())
    }
    fun hasOpenBankId():Boolean{
        return !(this.openBankId == null || this.openBankId!!.isBlank())
    }
}