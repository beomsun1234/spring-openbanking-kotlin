package com.bs.openbanking.config

import io.netty.handler.ssl.SslContextBuilder
import io.netty.handler.ssl.util.InsecureTrustManagerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import java.time.Duration

@Configuration
class WebClientConfig {
    @Bean
    fun webClient():WebClient{
        val httpClient = HttpClient.create()
            .responseTimeout(Duration.ofSeconds(2))
            .secure { it.sslContext(
                SslContextBuilder.forClient()
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    .build())
            }
        return WebClient.builder()
            .baseUrl("https://testapi.openbanking.or.kr")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .build()
    }
}