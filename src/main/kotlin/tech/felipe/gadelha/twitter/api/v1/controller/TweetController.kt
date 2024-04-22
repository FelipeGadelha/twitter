package tech.felipe.gadelha.twitter.api.v1.controller

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import tech.felipe.gadelha.twitter.api.dto.request.TweetRq
import tech.felipe.gadelha.twitter.api.dto.response.FeedRs
import tech.felipe.gadelha.twitter.domain.entity.Role
import tech.felipe.gadelha.twitter.domain.repository.TweetRepository
import tech.felipe.gadelha.twitter.domain.repository.UserRepository
import java.util.UUID


@RestController
@RequestMapping("/v1/tweets")
class TweetController(
    private val tweetRepository: TweetRepository,
    private val userRepository: UserRepository
) {
    private val logger: Logger = LoggerFactory.getLogger(TweetController::class.java)

    @GetMapping("/feed")
    fun feed(
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "pageSize", defaultValue = "10") pageSize: Int
    ): ResponseEntity<FeedRs> {
        val pageRequest = PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationDate")
        val tweets = tweetRepository.findAll(pageRequest)

        return ResponseEntity.ok(FeedRs(tweets, pageRequest))
    }

    @PostMapping
    fun createTweet(@RequestBody tweetRq: TweetRq, token: JwtAuthenticationToken): ResponseEntity<Void> {
        logger.info("createTweet::token.name {}", token.name)
        logger.info("createTweet::token.token {}", token.token)
        val user = userRepository.findById(UUID.fromString(token.name))
            .orElseThrow { throw RuntimeException("Erro ao buscar user") }
        val tweet = tweetRq.toModel(user)
        tweetRepository.save(tweet)

        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun deleteTweet(
        @PathVariable("id") tweetId: UUID,
        token: JwtAuthenticationToken
    ): ResponseEntity<Void> {
        val user = userRepository.findById(UUID.fromString(token.name))
        val tweet = tweetRepository.findById(tweetId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }

        val isAdmin = user.get().roles
            .stream()
            .anyMatch { role: Role -> role.name.equals(Role.Values.ADMIN.name, ignoreCase = true) }

        if (isAdmin || tweet.user!!.id!! == UUID.fromString(token.name)) {
            tweetRepository.deleteById(tweetId)
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        return ResponseEntity.ok().build()
    }
}