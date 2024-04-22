package tech.felipe.gadelha.twitter.api.dto.response

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import tech.felipe.gadelha.twitter.domain.entity.Tweet
import java.util.UUID

class FeedRs private constructor(
    val page: Int,
    val pageSize: Int,
    val totalPages: Int,
    val totalElements: Long,
    val feedItens: List<FeedItemRs>,
) {
    constructor(tweets: Page<Tweet>, pageRequest: PageRequest) : this(
        page = pageRequest.pageNumber,
        pageSize = pageRequest.pageSize,
        totalPages = tweets.totalPages,
        totalElements = tweets.totalElements,
        feedItens = tweets.map { tweet: Tweet -> FeedItemRs(tweet) }.content
    )
}

data class FeedItemRs private constructor(
    val tweetId: UUID,
    val content: String,
    val username: String
) {
    constructor(tweet: Tweet): this (
        tweetId = tweet.id!!,
        content = tweet.content,
        username = tweet.user!!.username
    )
}

