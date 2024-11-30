package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResultService(
    @Autowired private var resultRepository: ResultRepository,
    @Autowired private var recommendedGameRepository: RecommendedGameRepository,
    @Autowired private var userRepository: UserRepository

) {
    @Transactional
    fun save(data: ResultDto): ResultEntity {
        val recommendedGame = recommendedGameRepository.findById(data.gameplayId).orElseThrow()
        recommendedGame.completed = true
        recommendedGameRepository.save(recommendedGame)
        val user = recommendedGame.recommendedTo
        val gameplay = ResultEntity(
            result = data.result,
            passed = isResultPassed(data.result),
            config = data.newConfig?.toMutableMap() ?: recommendedGame.config.toMutableMap(),
            user = user,
            recommendedGame = recommendedGame
        )
        return resultRepository.save(gameplay)
    }

    @Transactional
    fun getGameOfResult(resultId: Long): GameEntity {
        val result = resultRepository.findById(resultId).orElseThrow()
        return result.recommendedGame.game
    }

    @Transactional
    fun getAllByUser(username: String, page: Pageable): List<ResultDetailsDto> {
        val user = userRepository.findByUsername(username).orElseThrow()
        return resultRepository.findAllByUser(user, page).content.map { convertToDto(it) }
    }

    @Transactional
    fun getAllFiltered(usernames: List<String>,  gameIds: List<Int>?, resultPassed: Boolean?, page: Pageable,): List<ResultDetailsDto> {
        val users = userRepository.findAllByUsernameIn(usernames)
        val existsGameFilter = !gameIds.isNullOrEmpty()
        val existsResultFilter = resultPassed != null
        if(existsGameFilter && existsResultFilter){
            return resultRepository.findAllByUserInAndPassedAndRecommendedGameGameIdIn(users, resultPassed!!, gameIds!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        if(existsGameFilter){
            return resultRepository.findAllByUserInAndRecommendedGameGameIdIn(users, gameIds!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        if(existsResultFilter){
            return resultRepository.findAllByUserInAndPassed(users, resultPassed!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        return resultRepository.findAllByUserIn(users, page).toList().map { convertToDto(it) }
    }
    @Transactional
    fun getAllFiltered(gameIds: List<Int>?, resultPassed: Boolean?, page: Pageable,): List<ResultDetailsDto> {
        val existsGameFilter = !gameIds.isNullOrEmpty()
        val existsResultFilter = resultPassed != null
        if(existsGameFilter && existsResultFilter){
            return resultRepository.findAllByPassedAndRecommendedGameGameIdIn(resultPassed!!, gameIds!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        if(existsGameFilter){
            return resultRepository.findAllByRecommendedGameGameIdIn(gameIds!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        if(existsResultFilter){
            return resultRepository.findAllByPassed(resultPassed!!, page)
                .toList()
                .map { convertToDto(it) }
        }
        return resultRepository.findAll(page).toList().map { convertToDto(it) }
    }

    fun getCountByFilters(usernames: List<String>,  gameIds: List<Int>?, resultPassed: Boolean?): Long{
        val users = userRepository.findAllByUsernameIn(usernames)
        val existsGameFilter = !gameIds.isNullOrEmpty()
        val existsResultFilter = resultPassed != null
        if(existsGameFilter && existsResultFilter){
            return resultRepository.countByUserInAndRecommendedGameGameIdInAndPassed(users,  gameIds!!, resultPassed!!)
        }
        if(existsGameFilter){
            return resultRepository.countByUserInAndRecommendedGameGameIdIn(users, gameIds!!)
        }
        if(existsResultFilter){
            return resultRepository.countByUserInAndPassed(users, resultPassed!!)
        }
        return resultRepository.countByUserIn(users)
    }

    fun getCountByFilters(gameIds: List<Int>?, resultPassed: Boolean?): Long{
        val existsGameFilter = !gameIds.isNullOrEmpty()
        val existsResultFilter = resultPassed != null
        if(existsGameFilter && existsResultFilter){
            return resultRepository.countByRecommendedGameGameIdInAndPassed(gameIds!!, resultPassed!!)
        }
        if(existsGameFilter){
            return resultRepository.countByRecommendedGameGameIdIn(gameIds!!)
        }
        if(existsResultFilter){
            return resultRepository.countByPassed(resultPassed!!)
        }
        return resultRepository.count()
    }

    @Transactional
    fun getAll(page: Pageable): List<ResultDetailsDto>{
        return resultRepository.findAll(page).content.map { convertToDto(it) }
    }

    fun getAll(): List<ResultEntity> {
        return resultRepository.findAll().toList()
    }

    @Transactional
    fun getNextRecommendationForGameIfExists(recommendationId: Long, username: String): RecommendedGameEntity? {
        val user = userRepository.findByUsername(username).orElseThrow()
        val game = recommendedGameRepository.findById(recommendationId).orElseThrow().game
        return recommendedGameRepository.findByRecommendedToAndGameAndCompletedAndRecommender(user, game, false, null).firstOrNull()
    }

    fun convertSortBy(sortBy: String, sortOrder: String): Sort {
        val sort = when (sortBy) {
            "timestamp" -> Sort.by("timestamp")
            "user" -> Sort.by("user.firstName").and(Sort.by("user.lastName"))
            "username" -> Sort.by("user.username")
            "game" -> Sort.by("recommendedGame.game.name")
            else -> throw IllegalArgumentException("Invalid sort parameter")
        }
        return if (sortOrder.uppercase() == "ASC") sort.ascending() else sort.descending()
    }
    fun getCountOfResultsByUser(username: String): Long {
        val user = userRepository.findByUsername(username).orElseThrow()
        return resultRepository.countByUser(user)
    }

    fun getCountOfResults(): Long {
        return resultRepository.count()
    }

    private fun isResultPassed(result: Map<String, Any>): Boolean? {
        return result["passed"] as Boolean?
    }

    private fun convertToDto(result: ResultEntity): ResultDetailsDto {
        return ResultDetailsDto(
            id = result.id!!,
            result = result.result,
            timestamp = result.timestamp!!,
            config = result.config,
            gameId = result.recommendedGame.game.id!!,
            gameName = result.recommendedGame.game.name,
            username = result.user.username
        )
    }

}