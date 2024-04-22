package tech.felipe.gadelha.twitter.api.handler

import jakarta.persistence.EntityNotFoundException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.lang.Nullable
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.BindingResult
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import java.time.Instant

@ControllerAdvice
class ApiExceptionHandler(
    @Autowired private val messageSource: MessageSource
): ResponseEntityExceptionHandler() {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)
        const val DOCUMENTATION = "Check the Documentation"
    }

    @ExceptionHandler(Exception::class)
    fun handleAll(ex: Exception, request: WebRequest): ResponseEntity<Any> {
        logger.error("handleAll", ex)
        val status = HttpStatus.INTERNAL_SERVER_ERROR
        val exceptionDetail = ExceptionDetail.builder()
            .title(status.reasonPhrase)
            .status(status.value())
            .detail(ex.message)
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .build()
        return ResponseEntity(exceptionDetail, HttpHeaders(), status)
    }

    override fun handleExceptionInternal(
        ex: java.lang.Exception,
        @Nullable body: Any?,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("handleExceptionInternal", ex)
        val exceptionDetail = when (body) {
            is ExceptionStatus -> ExceptionDetail.builder()
                .status(status.value())
                .title(body.title)
                .type(body.uri)
                .detail(ex.message ?: "Erro Padrão")
                .developerMessage(ex::class.java.name)
                .timestamp(Instant.now())
                .build()
            else -> ExceptionDetail.builder()
                .title("$status, $DOCUMENTATION")
                .status(status.value())
                .detail(ex.message)
                .developerMessage(ex::class.java.name)
                .timestamp(Instant.now())
                .build()
        }
        return super.handleExceptionInternal(ex, exceptionDetail, headers, status, request)
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.error("handleMethodArgumentNotValid", ex)
        return argumentNotValidException(ex, headers, status, request, ex.bindingResult)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: EntityNotFoundException, request: WebRequest): ResponseEntity<Any> {
        logger.error("handleEntityNotFoundException", ex)
        val status = HttpStatus.NOT_FOUND
        val exceptionDetail = ExceptionDetail.builder()
            .title(ExceptionStatus.RESOURCE_NOT_FOUND.title)
            .type(ExceptionStatus.RESOURCE_NOT_FOUND.uri)
            .status(status.value())
            .detail(ex.message ?: "Erro Padrão")
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .build()
        return ResponseEntity(exceptionDetail, HttpHeaders(), status)
    }

    private fun argumentNotValidException(
        ex: Exception,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
        bindingResult: BindingResult
    ): ResponseEntity<Any> {
        logger.error("argumentNotValidException", ex)
        val fieldErrors = bindingResult.fieldErrors
        val globalErrors = bindingResult.globalErrors

        var map: Map<String?, Set<String?>> = fieldErrors.groupBy{ field -> field.field }
            .mapValues{ it.value.map { field ->
                messageSource.getMessage(field, LocaleContextHolder.getLocale())
            }.toSet() }

        if (map.isEmpty()) {
            map = globalErrors.groupBy {global -> global.code }
                .mapValues{it.value.map { global -> global.defaultMessage }.toSet()}
        }
        val detail = ValidationExceptionDetail.builder()
            .title(ExceptionStatus.ARGUMENT_NOT_VALID.title + DOCUMENTATION)
            .status(status.value())
            .type(ExceptionStatus.ARGUMENT_NOT_VALID.uri)
            .detail("Check the error field(s)")
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .errors(map)
            .build()
        return ResponseEntity(detail, headers,HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException, request: WebRequest): ResponseEntity<Any> {
        logger.error("handleBadCredentialsException", ex)
        val status = HttpStatus.BAD_REQUEST
        val exceptionDetail = ExceptionDetail.builder()
            .title(status.reasonPhrase)
            .status(status.value())
            .detail(ex.message)
            .developerMessage(ex::class.java.name)
            .timestamp(Instant.now())
            .build()
        return ResponseEntity(exceptionDetail, HttpHeaders(), status)
    }
}