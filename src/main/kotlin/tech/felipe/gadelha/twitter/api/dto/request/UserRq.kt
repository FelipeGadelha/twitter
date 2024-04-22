package tech.felipe.gadelha.twitter.api.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import tech.felipe.gadelha.twitter.domain.entity.Role
import tech.felipe.gadelha.twitter.domain.entity.User

data class UserRq(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
) {
    fun toModel(basicRole: Role, passwordEncoder: BCryptPasswordEncoder): User = User(
        id = null,
        username = username,
        password = passwordEncoder.encode(password),
        roles = mutableSetOf(basicRole)
    )
}