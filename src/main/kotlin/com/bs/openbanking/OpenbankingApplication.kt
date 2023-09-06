package com.bs.openbanking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@SpringBootApplication
@EnableJpaAuditing
class OpenbankingApplication

fun main(args: Array<String>) {
    runApplication<OpenbankingApplication>(*args)
}
