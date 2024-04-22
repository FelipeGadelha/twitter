package tech.felipe.gadelha.twitter.domain.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import tech.felipe.gadelha.twitter.domain.entity.User
import java.time.Instant
import java.util.stream.Collectors


@Service
class TokenService(
    private val jwtEncoder: JwtEncoder,
    private val jwtDecoder: JwtDecoder
) {
    @Value("\${twitter.jwt.expiration:864000}") //one day
    private val expiration: Long? = null

    private val logger: Logger = LoggerFactory.getLogger(TokenService::class.java)

    fun generateToken(userDetails: UserDetails): String {
        val scopes: String = userDetails.authorities
            .stream()
            .map { obj: GrantedAuthority -> obj.authority }
            .collect(Collectors.joining(" "))
        val map = mutableMapOf<String, Any>()
        map["scope"] = scopes
        return generateToken(userDetails, map)
    }

//    fun <T> extractClaim(token: String, claimsResolver: Function<Map<String, Any>?, T>): T {
//        val claims: Map<String, Any> = extractAllClaims(token)
//        return claimsResolver.apply(claims)
//    }

    fun generateToken(
        userDetails: UserDetails,
        extraClaims: Map<String, Any>
    ): String {

        val user = userDetails as User
        var now = Instant.now();
        val claims = JwtClaimsSet
            .builder()
            .issuer("twitter")
            .claims{ claims -> claims.putAll(extraClaims) }
            .subject(user.id!!.toString()) //                .setIssuedAt(new Date(System.currentTimeMillis()))
            .issuedAt(now)
            .expiresAt(this.expiration?.let { now.plusSeconds(it) })
            .build()
        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).tokenValue;
        return jwtValue
    }

    private fun extractAllClaims(token: String): Map<String, Any> {
        val jwt = jwtDecoder.decode(token)
        return jwt.claims
    }
}