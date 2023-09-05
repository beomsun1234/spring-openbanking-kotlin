package com.bs.openbanking.domain

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import javax.persistence.EntityListeners
import javax.persistence.MappedSuperclass


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseTime(
    @CreatedDate
    var createdAt:LocalDateTime?=null,
    @LastModifiedDate
    var modifiedAt:LocalDateTime?=null
)