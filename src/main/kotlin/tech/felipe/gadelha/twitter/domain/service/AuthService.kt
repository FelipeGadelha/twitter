package tech.felipe.gadelha.twitter.domain.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import tech.felipe.gadelha.twitter.domain.repository.UserRepository


@Service
class AuthService(
    private val userRepository: UserRepository
): UserDetailsService  {
    private val logger: Logger = LoggerFactory.getLogger(AuthService::class.java)

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        logger.info("loadUserByUsername::USERNAME = {}", username)
        return userRepository.findByUsername(username)?.also {
            logger.info("loadUserByUsername::USERNAME = {}", it)
        } ?: throw BadCredentialsException("Dados inválidos!")
    }

}