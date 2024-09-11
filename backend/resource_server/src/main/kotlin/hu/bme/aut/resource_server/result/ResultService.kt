package hu.bme.aut.resource_server.result

import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameRepository
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
            config = recommendedGame.config.toMutableMap(),
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
    fun getAllFiltered(usernames: List<String>, page: Pageable, gameIds: List<Int>?, resultPassed: Boolean?): List<ResultDetailsDto> {
        val resultCount = page.pageSize
        val users = userRepository.findAllByUsernameIn(usernames)
        val results = resultRepository.findAllByUserIn(users, page).content
            .filter { (gameIds.isNullOrEmpty() || gameIds.contains(it.recommendedGame.game.id)) && (resultPassed == null || it.result["passed"] as Boolean? == resultPassed) }
            .map { convertToDto(it) }.toMutableList()
        var currPage = page
        while(results.size < resultCount){
            currPage = currPage.next()
            val nextPage = resultRepository.findAllByUserIn(users, currPage).content
                .filter { (gameIds.isNullOrEmpty() || gameIds.contains(it.recommendedGame.game.id)) && (resultPassed == null || it.result["passed"] as Boolean? == resultPassed) }
                .map { convertToDto(it) }
            if(nextPage.isEmpty()){
                break
            }
            results.addAll(nextPage)
        }
        return results.take(resultCount)
    }

    @Transactional
    fun getAll(page: Pageable): List<ResultDetailsDto>{
        return resultRepository.findAll(page).content.map { convertToDto(it) }
    }

    @Transactional
    fun getAllFiltered(page: Pageable, gameIds: List<Int>?, resultPassed: Boolean?): List<ResultDetailsDto> {
        val resultCount = page.pageSize
        val results0 = resultRepository.findAll(page).content.toMutableList()
        val results = results0.filter { (gameIds.isNullOrEmpty() || gameIds.contains(it.recommendedGame.game.id)) && (resultPassed == null || it.result["passed"] as Boolean? == resultPassed) }
            .map { convertToDto(it) }.toMutableList()
        var currPage = page
        while(results.size < resultCount){
            currPage = currPage.next()
            val nextPage = resultRepository.findAll(currPage).content
                .filter { (gameIds.isNullOrEmpty() || gameIds.contains(it.recommendedGame.game.id)) && (resultPassed == null || it.result["passed"] as Boolean? == resultPassed) }
                .map { convertToDto(it) }
            if(nextPage.isEmpty()){
                break
            }
            results.addAll(nextPage)
        }
        return results.take(resultCount)
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