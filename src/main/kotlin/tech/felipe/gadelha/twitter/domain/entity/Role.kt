package tech.felipe.gadelha.twitter.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.security.core.GrantedAuthority

@Entity
@Table(name = "roles")
data class Role(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    val name: String = ""
): GrantedAuthority {
    enum class Values(var roleId: Long) {
        ADMIN(1L),
        BASIC(2L)
    }

    override fun getAuthority(): String {
        return name
    }

}
