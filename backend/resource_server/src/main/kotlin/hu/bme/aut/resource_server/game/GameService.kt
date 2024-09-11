package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.game.game_config.isSame
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

@Service
class GameService (
    @Autowired private var gameRepository: GameRepository) {

    @Value("\${app.thumbnail-location}")
    private lateinit var thumbnailLocation: String

    @Value("\${app.image-path}")
    private lateinit var gameImageLocation: String

    fun getAllGamesPaged(pageIndex: Int, pageSize: Int): List<GameEntity> {
        return gameRepository.findAll(PageRequest.of(pageIndex, pageSize)).toList()
    }
    fun getAllGames(): List<GameEntity> {
        return gameRepository.findAll()
    }

    fun getGameById(id: Int): Optional<GameEntity> {
        return gameRepository.findById(id)
    }

    /**
     * Updates game to have the same fields as param game.
     * If the url or the configDescription is different, then the old game is set to inactive and a new game is created
     * with higher version number.
     */
    fun updateGame(updatedGame: GameEntity): GameEntity {
        val oldGame = gameRepository.findById(updatedGame.id!!).orElseThrow()
        if(!sameConfigDescription(oldGame, updatedGame)) {
            oldGame.active = false
            gameRepository.save(oldGame)

            val newVersionedGame = copyGame(updatedGame).copy(
                id = null,
                version = oldGame.version + 1
            )
            return gameRepository.save(newVersionedGame)
        } else {
            updatedGame.version = oldGame.version + 1
            return gameRepository.save(updatedGame)
        }
    }

    /**
     * Saves the thumbnail for the game with the given id.
     * File location is: thumbnailLocation/gameId.png
     */
    fun saveThumbnailForGame(id: Int, thumbnail: MultipartFile): GameEntity{
        val game = gameRepository.findById(id).orElseThrow()
        var fileName = "${game.id}"
        var file = File("$thumbnailLocation/${fileName}.png")
        while(file.exists()){
            fileName = "${game.id}_${System.currentTimeMillis()}"
            file = File("$thumbnailLocation/${fileName}.png")
        }
        file.outputStream().use {
            it.write(thumbnail.bytes)
        }
        val updatedGame = game.copy(
            thumbnailPath = "$gameImageLocation/${fileName}.png",
        )
        return gameRepository.save(updatedGame)
    }

    private fun copyGame(game: GameEntity): GameEntity{
        val affectedAbilities = mutableSetOf<AbilityEntity>()
        affectedAbilities.addAll(game.affectedAbilities)
        return GameEntity(
            id = game.id,
            name = game.name,
            description = game.description,
            affectedAbilities = affectedAbilities,
            active = game.active,
            configDescription = game.configDescription,
            thumbnailPath = game.thumbnailPath,
            version = game.version,
            configItems = game.configItems.map { it.copy(id=null) }.toMutableSet()
        )
    }
    private fun sameConfigDescription(game1: GameEntity, game2: GameEntity): Boolean{
        if(game1.configItems.size != game2.configItems.size){
            return false
        }
        game1.configItems.forEach { item ->
            val itemToCompare = game2.configItems.find{ it.paramName == item.paramName } ?: return false
            if(!item.isSame(itemToCompare)){
                return false
            }
        }
        return true
    }

}