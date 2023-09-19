package com.bs.openbanking.controller

import com.bs.openbanking.dto.MemberDto
import com.bs.openbanking.dto.SignUpDto
import com.bs.openbanking.dto.loginDto
import com.bs.openbanking.service.MemberService
import com.fasterxml.jackson.databind.introspect.TypeResolutionContext.Empty
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@CrossOrigin
@RequestMapping("api")
class MemberController(
    private val memberService: MemberService,
) {

    @GetMapping("members/{id}")
    suspend fun findMemberById(@PathVariable id:Long):ResponseEntity<MemberDto>{
        return ResponseEntity.status(200).body(memberService.findMemberById(id))
    }

    @PostMapping("members/login")
    suspend fun login(@RequestBody loginDto: loginDto):ResponseEntity<MemberDto>{
        return ResponseEntity.status(200).body(memberService.login(loginDto))
    }

    @PostMapping("members/signup")
    suspend fun signUp(@RequestBody signUpDto: SignUpDto):ResponseEntity<Empty>{
        memberService.signUp(signUpDto)
        return ResponseEntity.status(200).build()
    }
    @PutMapping("members/{id}/open-bank/ci")
    suspend fun updateOpenBankCi(@PathVariable id:Long):ResponseEntity<Empty> {
        memberService.updateOpenBankCi(id)
        return ResponseEntity.status(200).build()
    }

}