package tech.felipe.gadelha.twitter.api.handler

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.Instant
import java.time.OffsetDateTime

// soluction = https://github.com/rtenhove/eg-builder-inheritance

@JsonInclude(JsonInclude.Include.NON_NULL)
open class ExceptionDetail internal constructor (builder: Builder<*>) {

    val title: String?
    val status: Int?
    val type: String?
    val detail: String?
    val developerMessage: String?
    val timestamp: Instant

    init {
        title = builder.title
        status = builder.status
        type = builder.type
        detail = builder.detail
        developerMessage = builder.developerMessage
        timestamp = builder.timestamp
    }
    private class ExceptionDetailBuilder : Builder<ExceptionDetailBuilder>() {
        override fun self(): ExceptionDetailBuilder { return this }
    }
    companion object { fun builder(): Builder<*> { return ExceptionDetailBuilder() } }

    abstract class Builder<B : Builder<B>> protected constructor() {
        var title: String? = null
        var status: Int? = null
        var type: String? = null
        var detail: String? = null
        var developerMessage: String? = null
        var timestamp: Instant = Instant.now()

        //getThis() trick](http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ206),
        // much beloved by Java generic-type gurus.
        protected abstract fun self(): B

        fun title(title: String): B { this.title = title; return self() }
        fun status(status: Int): B {this.status = status; return self() }
        fun type(type: String): B {this.type = type; return self() }
        fun detail(detail: String?): B {this.detail = detail; return self() }
        fun developerMessage(developerMessage: String): B {this.developerMessage = developerMessage; return self() }
        fun timestamp(timestamp: Instant): B {this.timestamp = timestamp; return self() }

        open fun build(): ExceptionDetail { return ExceptionDetail(this) }
    }
}