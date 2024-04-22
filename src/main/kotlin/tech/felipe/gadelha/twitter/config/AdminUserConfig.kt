package tech.felipe.gadelha.twitter.config

import jakarta.persistence.EntityManager
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.transaction.annotation.Transactional
import tech.felipe.gadelha.twitter.domain.entity.Role
import tech.felipe.gadelha.twitter.domain.entity.User
import tech.felipe.gadelha.twitter.domain.repository.RoleRepository
import tech.felipe.gadelha.twitter.domain.repository.UserRepository


@Configuration
class AdminUserConfig(
    private val manager: EntityManager,
    private val passwordEncoder: PasswordEncoder
): CommandLineRunner {

    @Transactional
    override fun run(vararg args: String?) {
        val roleAdmin = manager.find(Role::class.java, 1L)
        val user: User = User(
            username = "felipe",
            password = passwordEncoder.encode("123")
        )
        user.addRole(roleAdmin)
        manager.merge(user)
    }
}