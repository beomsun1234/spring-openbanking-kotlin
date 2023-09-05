package com.bs.openbanking.domain

import org.jetbrains.annotations.NotNull
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Index
import javax.persistence.Table

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
    var accountSeq:String?=null
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
}