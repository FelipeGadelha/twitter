package tech.felipe.gadelha.twitter.api.v1.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import tech.felipe.gadelha.twitter.api.dto.request.UserRq
import tech.felipe.gadelha.twitter.domain.entity.Role
import tech.felipe.gadelha.twitter.domain.entity.User
import tech.felipe.gadelha.twitter.domain.repository.RoleRepository
import tech.felipe.gadelha.twitter.domain.repository.UserRepository

@RestController
@RequestMapping("/v1/users")
class UserController(
    private val userRepository: UserRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    private val logger: Logger = LoggerFactory.getLogger(UserController::class.java)

    @Transactional
    @PostMapping
    fun newUser(@RequestBody userRq: UserRq): ResponseEntity<Void> {
        val basicRole = roleRepository.findByName(Role.Values.BASIC.name)

        return userRepository.findByUsername(userRq.username)
            ?.let{ throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY) }
            ?: run {
                val user = userRq.toModel(basicRole, passwordEncoder)
                userRepository.save(user)
                ResponseEntity.ok().build()
            }
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
    fun listUsers(): ResponseEntity<List<User>> {
        logger.info("listUsers")
        val users = userRepository.findAll()
        return ResponseEntity.ok(users)
    }
}