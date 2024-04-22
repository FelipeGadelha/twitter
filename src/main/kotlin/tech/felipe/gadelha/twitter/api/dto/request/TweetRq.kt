package tech.felipe.gadelha.twitter.api.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import tech.felipe.gadelha.twitter.domain.entity.Tweet
import tech.felipe.gadelha.twitter.domain.entity.User

data class TweetRq(
    @JsonProperty("content") val content: String
) {
    fun toModel(user: User): Tweet = Tweet(null, user, content)
}