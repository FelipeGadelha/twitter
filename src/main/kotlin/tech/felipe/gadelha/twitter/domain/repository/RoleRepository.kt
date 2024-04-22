package tech.felipe.gadelha.twitter.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import tech.felipe.gadelha.twitter.domain.entity.Role


@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(name: String): Role
}