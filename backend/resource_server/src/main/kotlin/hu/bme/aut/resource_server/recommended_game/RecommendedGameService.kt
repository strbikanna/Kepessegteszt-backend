package hu.bme.aut.resource_server.recommended_game

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.game.GameRepository
import hu.bme.aut.resource_server.recommendation.RecommenderService
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import jakarta.transaction.Transactional
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class RecommendedGameService(
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository,
    @Autowired private var gameRepository: GameRepository,
    private val recommenderService: RecommenderService,
) {
    var log: Logger = LoggerFactory.getLogger(RecommendedGameService::class.java)

    /**
     * Get all recommendations to user which are not yet completed.
     */
    @Transactional
    fun getAllRecommendedToUser(
        username: String,
        acceptedGameIds: List<Int>?,
        pageIndex: Int = 0,
        pageSize: Int = 100
    ): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val sort = Sort.by(Sort.Order.desc("timestamp"))
        val recommendedGames =
            if (acceptedGameIds != null) {
                recommendedGameRepository.findAllPagedByRecommendedToAndCompletedAndGameIn(
                    user, false, gameRepository.findAllById(acceptedGameIds), PageRequest.of(pageIndex, pageSize, sort)
                )
            } else {
                recommendedGameRepository.findAllPagedByRecommendedToAndCompleted(
                    user,
                    false,
                    PageRequest.of(pageIndex, pageSize, sort)
                )
            }
        return recommendedGames
            .filter { it.game.active }
            .map { it.apply { config = recommenderService.applySpecialSettings(config, username) }.toDto() }
    }

    @Transactional
    fun getNextChoiceForUser(username: String, acceptedGameIds: List<Int>?): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val possibleGames: MutableSet<GameEntity> = mutableSetOf()
        val top2Recommendation: MutableList<RecommendedGameEntity> = mutableListOf()
        val neverPlayedGames = getNeverPlayedGames(user)
        neverPlayedGames.forEach { game ->
            tryAddGameAndRecommendation(game, user, acceptedGameIds, possibleGames, top2Recommendation)
        }
        if (top2Recommendation.size < 2) {
            val latestCompleted = recommendedGameRepository.findLatestCompleted(user)
            latestCompleted.forEach { rg ->
                tryAddGameAndRecommendation(rg.game, user, acceptedGameIds, possibleGames, top2Recommendation)
            }
        }
        return top2Recommendation.map { it.apply { config = recommenderService.applySpecialSettings(config, user.username) }.toDto() }
    }

    /**
     * Retrieve the configuration of a recommended game. If the configuration is not yet available, it waits for it to be available.
     */
    suspend fun getRecommendedGameConfig(recommendedGameId: Long): Map<String, Any>? = withContext(Dispatchers.IO) {
        var rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        repeat(10) {
            if (rGame.config.isNotEmpty()) {
                val config = rGame.game.validateConfig(rGame.config)
                return@withContext recommenderService.applySpecialSettings(config, rGame.recommendedTo.username)
            }
            log.info("Config not found for recommendation with id: $recommendedGameId. Waiting...")
            delay(300)
            rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        }
        log.info("No result found for recommendation with id: $recommendedGameId.")
        return@withContext emptyMap()
    }

    fun addRecommendation(recommendation: RecommendationDto, recommenderUsername: String): RecommendedGameEntity {
        val recommender = userRepository.findByUsername(recommenderUsername).orElseThrow()
        val recommendedTo = userRepository.findByUsername(recommendation.recommendedTo)
            .orElseThrow { NoSuchElementException("User with username ${recommendation.recommendedTo} not found.") }
        val game = gameRepository.findById(recommendation.gameId)
            .orElseThrow { NoSuchElementException("Game with id ${recommendation.gameId} not found.") }
        return recommendedGameRepository.save(
            RecommendedGameEntity(
                game = game,
                recommendedTo = recommendedTo,
                recommender = recommender,
                config = recommenderService.applySpecialSettings(recommendation.config, recommendedTo.username)
            )
        )
    }

    @Transactional
    fun getRecommendationsToUserAndGame(username: String, gameId: Int?, completed: Boolean?): List<RecommendedGameDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        val page = PageRequest.of(0, 100, Sort.by("timestamp").descending())
        if (gameId == null) {
            return if (completed == null) {
                recommendedGameRepository.findAllPagedByRecommendedTo(user, page)
                    .map { it.apply { config = recommenderService.applySpecialSettings(config, user.username) }.toDto() }
            } else {
                recommendedGameRepository.findAllPagedByRecommendedToAndCompleted(user, completed, page)
                    .map { it.apply { config = recommenderService.applySpecialSettings(config, user.username) }.toDto() }
            }
        }
        val game = gameRepository.findById(gameId).orElseThrow()
        return if (completed == null) {
            recommendedGameRepository.findAllPagedByRecommendedToAndGame(user, game, page)
                .map { it.apply { config = recommenderService.applySpecialSettings(config, user.username) }.toDto() }
        } else {
            recommendedGameRepository.findAllPagedByRecommendedToAndCompletedAndGame(user, completed, game, page)
                .map { it.apply { config = recommenderService.applySpecialSettings(config, user.username) }.toDto() }
        }
    }

    fun deleteRecommendedGame(recommendedGameId: Long) {
        val rGame = recommendedGameRepository.findById(recommendedGameId).orElseThrow()
        if (rGame.completed) {
            throw IllegalArgumentException("Cannot delete completed recommendation, because it is completed already.")
        }
        recommendedGameRepository.deleteById(recommendedGameId)
    }

    fun deleteAllRecommendationsByUser(user: UserEntity) {
        val allRecommendedTo = recommendedGameRepository.findAllByRecommendedTo(user)
        val allRecommendedBy = recommendedGameRepository.findAllByRecommender(user)
        recommendedGameRepository.deleteAll(allRecommendedTo)
        recommendedGameRepository.deleteAll(allRecommendedBy)
    }

    private fun getNeverPlayedGames(user: UserEntity): List<GameEntity> {
        val neverCompletedRecommendations = recommendedGameRepository.findByRecommendedToAndNeverPlayed(user)
        return neverCompletedRecommendations.map { it.game }
    }

    private fun tryAddGameAndRecommendation(
        game: GameEntity,
        user: UserEntity,
        acceptedGameIds: List<Int>?,
        possibleGames: MutableSet<GameEntity>,
        top2Recommendation: MutableList<RecommendedGameEntity>
    ) {
        if (top2Recommendation.size >= 2) return
        if (!possibleGames.contains(game) && game.active && (acceptedGameIds == null || acceptedGameIds.contains(game.id))) {
            possibleGames.add(game)
            val recommendation = recommendedGameRepository.findAllByRecommendedToAndGameAndCompleted(
                user,
                game,
                false
            ).firstOrNull()
            if (recommendation != null) {
                top2Recommendation.add(recommendation)
            }
        }
    }
}