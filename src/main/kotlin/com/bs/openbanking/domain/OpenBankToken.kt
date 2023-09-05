package com.bs.openbanking.domain
import javax.persistence.*

@Entity
class OpenBankToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id:Long?=null,
    @Column(unique = true, nullable = false)
    val memberId: Long,
    @Column(length = 1000, nullable = false)
    var accessToken: String,
    @Column(length = 1000, nullable = false)
    var refreshToken: String,
    var expiresIn: Long
) : BaseTime() {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OpenBankToken

        if (id != other.id) return false
        if (memberId != other.memberId) return false
        if (accessToken != other.accessToken) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + memberId.hashCode()
        result = 31 * result + accessToken.hashCode()
        return result
    }
}