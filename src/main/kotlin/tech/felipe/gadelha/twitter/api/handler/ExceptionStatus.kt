package tech.felipe.gadelha.twitter.api.handler

import org.springframework.http.HttpStatus

enum class ExceptionStatus(uri: String, val title: String) {
    MESSAGE_NOT_READABLE("/message-not-readable", "Message not readable"),
    RESOURCE_NOT_FOUND("/resource-not-found", "Resource not found"),
    ENTITY_IN_USE("/entity-in-use", "Entity in use"),
    INVALID_FORMAT("/invalid-format", "Invalid format"),
    INVALID_PARAMETER("/invalid-parameter", "Invalid parameter"),
    ILLEGAL_STATE("/illegal-state", "Illegal State"),
    ILLEGAL_ARGUMENT("/illegal-argument", "Illegal argument"),
    ARGUMENT_NOT_VALID("/argument-not-valid", "Argument not valid"),
    BUSINESS_ERROR("/business-error", "Business rule violation"),
    UNAUTHORIZED("/unauthorized", HttpStatus.UNAUTHORIZED.reasonPhrase);

    val uri: String = "https://localhost:8080/api/twitter$uri"
}