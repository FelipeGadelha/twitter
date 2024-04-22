package tech.felipe.gadelha.twitter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@SpringBootApplication
class TwitterApplication

fun main(args: Array<String>) {
	runApplication<TwitterApplication>(*args)
//	https://youtu.be/nDst-CRKt_k
//	https://dev.to/pryhmez/implementing-spring-security-6-with-spring-boot-3-a-guide-to-oauth-and-jwt-with-nimbus-for-authentication-2lhf
}
