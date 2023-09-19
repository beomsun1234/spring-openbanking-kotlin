package com.bs.openbanking.controller

import com.bs.openbanking.dto.AccountDto
import com.bs.openbanking.service.AccountService
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin
@RequestMapping("api")
class AccountController(
    val accountService: AccountService,
) {

    @PostMapping("members/{id}/accounts")
    suspend fun saveAccounts(@PathVariable id:Long):ResponseEntity<Empty>{
        accountService.saveAccounts(id)
        return ResponseEntity.status(200).build()
    }

    @GetMapping("members/{id}/accounts")
    suspend fun getAccountsByMemberId(@PathVariable id:Long):List<AccountDto>{
        return accountService.findAccountsByMemberId(id)
    }

    @PutMapping("members/{id}/accounts/{accountId}")
    suspend fun updateAccountType(@PathVariable id:Long, @PathVariable("accountId") accountId:Long):ResponseEntity<Empty>{
        accountService.updateAccountType(memberId = id, accountId = accountId)
        return ResponseEntity.status(200).build()
    }

}