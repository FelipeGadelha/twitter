package tech.felipe.gadelha.twitter.api.handler

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.OutputStream
import java.time.Instant


@Component
class RestAccessDeniedHandler(
    private val mapper: ObjectMapper
): AccessDeniedHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RestAccessDeniedHandler::class.java)
    }

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        ex: org.springframework.security.access.AccessDeniedException
    ) {
        logger.error("RestAccessDeniedHandler.handle", ex)
        val status = HttpStatus.FORBIDDEN
        val exceptionHandler = ExceptionDetail.builder()
            .status(status.value())
            .title(ExceptionStatus.ARGUMENT_NOT_VALID.title + ApiExceptionHandler.DOCUMENTATION)
            .type(ExceptionStatus.ARGUMENT_NOT_VALID.uri)
            .detail(ex.message ?: "Erro Padrão")
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .build()
        response.status = status.value()
        val out: OutputStream = response.outputStream
        mapper.writeValue(out, exceptionHandler)
        out.flush()
    }
}