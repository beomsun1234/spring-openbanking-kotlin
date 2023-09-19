package com.bs.openbanking.controller

import com.bs.openbanking.dto.OpenBankTokenSaveDto
import com.bs.openbanking.service.TokenService
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@CrossOrigin
@RequestMapping("api")
class TokenController(
    private val tokenService: TokenService,
) {

    @PostMapping("/members/open-bank/token")
    suspend fun saveOpenBankToken(@RequestBody openBankTokenSaveDto: OpenBankTokenSaveDto):ResponseEntity<Empty>{
        tokenService.saveOpenBankTokenAndOpenBankId(openBankTokenSaveDto)
        return ResponseEntity.status(200).build()
    }

}