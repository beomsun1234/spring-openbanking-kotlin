package com.bs.openbanking.domain

import javax.persistence.*

@Entity
@Table(
    indexes = [Index(name = "idx_member_id", columnList = "member_id", unique = false)]
)
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long?=null,
    @Column(name = "member_id", nullable = false)
    val memberId:Long,
    @Column(unique = true, nullable = false)
    val fintechUseNum:String,
    val bankName:String,
    val accountNum:String,
    @Column(nullable = false)
    val bankCode:String,
    var accountSeq:String?=null,
    @Enumerated(EnumType.STRING)
    var accountType: AccountType?=AccountType.SUB,
    var holderName:String,
) : BaseTime(){
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Account
        if (fintechUseNum != other.fintechUseNum) return false
        return true
    }
    override fun hashCode(): Int {
        return fintechUseNum.hashCode()
    }

    fun updateAccountState(accountType: AccountType){
        this.accountType = accountType
    }

    fun isMainAccount():Boolean{
        if (this.accountType==AccountType.MAIN) return true
        return false
    }
}