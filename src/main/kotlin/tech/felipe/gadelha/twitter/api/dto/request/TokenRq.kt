package tech.felipe.gadelha.twitter.api.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken


data class TokenRq(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
) {
    fun toAuth(): UsernamePasswordAuthenticationToken {
        return UsernamePasswordAuthenticationToken(username, password)
    }
}