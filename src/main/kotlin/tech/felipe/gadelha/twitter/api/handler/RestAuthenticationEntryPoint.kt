package tech.felipe.gadelha.twitter.api.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import java.io.OutputStream
import java.time.Instant


@Component
class RestAuthenticationEntryPoint(
    private val mapper: ObjectMapper
) : AuthenticationEntryPoint {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RestAccessDeniedHandler::class.java)
    }
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        httpServletRequest: HttpServletRequest?,
        httpServletResponse: HttpServletResponse,
        ex: AuthenticationException
    ) {
        logger.error("RestAuthenticationEntryPoint.commence", ex)
        val status = HttpStatus.UNAUTHORIZED
        val exceptionHandler = ExceptionDetail.builder()
            .status(status.value())
            .title("${ExceptionStatus.UNAUTHORIZED.title}, ${ApiExceptionHandler.DOCUMENTATION}")
            .type(ExceptionStatus.UNAUTHORIZED.uri)
            .detail(ex.message ?: "Erro Padrão")
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .build()

        httpServletResponse.status = status.value()
        val out: OutputStream = httpServletResponse.outputStream
        mapper.writeValue(out, exceptionHandler)
        out.flush()
    }
}