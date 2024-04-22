package tech.felipe.gadelha.twitter.domain.repository

import org.springframework.data.jpa.repository.JpaRepository
import tech.felipe.gadelha.twitter.domain.entity.Tweet
import java.util.UUID

interface TweetRepository: JpaRepository<Tweet, UUID> {

}