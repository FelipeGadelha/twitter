package tech.felipe.gadelha.twitter.config

import com.nimbusds.jose.jwk.JWK
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.web.SecurityFilterChain
import tech.felipe.gadelha.twitter.api.handler.RestAccessDeniedHandler
import tech.felipe.gadelha.twitter.api.handler.RestAuthenticationEntryPoint
import tech.felipe.gadelha.twitter.domain.service.AuthService
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig(
    private val authService: AuthService,
    private val jacksonDataType: JacksonDataType
) {

    @Value("\${rsa.public-key}")
    private val publicKey: RSAPublicKey? = null

    @Value("\${rsa.private-key}")
    private val privateKey: RSAPrivateKey? = null

    @Bean
//    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { auth -> auth
                .requestMatchers(HttpMethod.POST, "/*/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/*/users/**").permitAll()
//                .requestMatchers(HttpMethod.GET, "/*/users/**").hasAnyRole("ADMIN")
//                .requestMatchers("/error/**").permitAll()
                .anyRequest().authenticated()
            }
            .csrf{ csrf -> csrf.disable() }
            .cors{ cors -> cors.disable() }
//            .oauth2ResourceServer{ oauth2 -> oauth2.jwt(Customizer.withDefaults())}
            .oauth2ResourceServer{ oauth2 -> oauth2.jwt{jwt -> jwt.decoder(jwtDecoder())}}
            .userDetailsService(authService)
            .exceptionHandling{exceptionHandling -> exceptionHandling
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint())
            }
            .sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()
    }
    @Bean
    fun authenticationManager(): AuthenticationManager {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(authService)
        authProvider.setPasswordEncoder(passwordEncoder())
        return ProviderManager(authProvider)
    }

    @Bean
    fun jwtDecoder(): JwtDecoder {
        return NimbusJwtDecoder.withPublicKey(publicKey).build()
    }

//    @Bean
////    @Throws(Exception::class)
//    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
//        return config.authenticationManager
//    }

    @Bean
    fun jwtEncoder(): JwtEncoder {
        val jwk: JWK = RSAKey.Builder(publicKey).privateKey(privateKey).build()
        val jwks: JWKSource<SecurityContext> = ImmutableJWKSet(JWKSet(jwk))
        return NimbusJwtEncoder(jwks)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun accessDeniedHandler(): RestAccessDeniedHandler
        = RestAccessDeniedHandler(jacksonDataType.objectMapper())

    @Bean
    fun authenticationEntryPoint(): RestAuthenticationEntryPoint
        = RestAuthenticationEntryPoint(jacksonDataType.objectMapper())

}