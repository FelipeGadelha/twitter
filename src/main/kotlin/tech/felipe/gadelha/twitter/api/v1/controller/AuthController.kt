package tech.felipe.gadelha.twitter.api.v1.controller

import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import tech.felipe.gadelha.twitter.api.dto.request.TokenRq
import tech.felipe.gadelha.twitter.api.dto.response.TokenRs
import tech.felipe.gadelha.twitter.domain.entity.User
import tech.felipe.gadelha.twitter.domain.repository.UserRepository
import tech.felipe.gadelha.twitter.domain.service.TokenService


@RestController
@RequestMapping("/v1/auth")
class AuthController(
    private val passwordEncoder: PasswordEncoder,
    private val tokenService: TokenService,
    private val authManager: AuthenticationManager
) {
    private val logger: Logger = LoggerFactory.getLogger(AuthController::class.java)

    @PostMapping
    @Transactional
    fun authenticate(@RequestBody @Valid tokenRq: TokenRq): ResponseEntity<Any> {
        logger.info("authenticate = {}", tokenRq.username)
        val authentication: Authentication = authManager.authenticate(tokenRq.toAuth())
        SecurityContextHolder.getContext().authentication = authentication

        val userDetails: User = authentication.principal as User
        logger.info("userDetails = {}", userDetails.roles)
        val token: String = tokenService.generateToken(userDetails)
        logger.info("token = {}", token)
        return ResponseEntity.ok(TokenRs(token, "Bearer"))
    }

    @GetMapping
    fun hello(): String {
        return "Hello Felipe"
    }
}