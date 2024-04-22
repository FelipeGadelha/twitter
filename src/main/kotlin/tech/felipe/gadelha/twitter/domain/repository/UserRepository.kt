package tech.felipe.gadelha.twitter.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.felipe.gadelha.twitter.domain.entity.User
import java.util.UUID

@Repository
interface UserRepository: JpaRepository<User, UUID> {
    fun findByUsername(username: String): User?
}