package hu.bme.aut.resource_server.game

import hu.bme.aut.resource_server.ability.AbilityEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.Optional

@Service
class GameService (
    @Autowired private var gameRepository: GameRepository) {

    @Value("\${app.thumbnail-location}")
    private lateinit var thumbnailLocation: String

    @Value("\${app.image-path}")
    private lateinit var gameImageLocation: String

    fun getAllGames(pageIndex: Int, pageSize: Int): List<GameEntity> {
        return gameRepository.findAll(PageRequest.of(pageIndex, pageSize)).toList()
    }

    fun getGameById(id: Int): Optional<GameEntity> {
        return gameRepository.findById(id)
    }

    fun updateGame(updatedGame: GameEntity): GameEntity {
        val oldGame = gameRepository.findById(updatedGame.id!!).orElseThrow()
        if(oldGame.url != updatedGame.url || !sameConfigDescription(oldGame, updatedGame)) {
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
        val updatedGame = copyGame(game).copy(
            thumbnailPath = "$gameImageLocation/${fileName}.png",
            version = game.version + 1
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
            url = game.url,
            active = game.active,
            configDescription = game.configDescription,
            thumbnailPath = game.thumbnailPath,
            version = game.version
        )
    }
    private fun sameConfigDescription(game1: GameEntity, game2: GameEntity): Boolean{
        game1.configDescription.entries.forEach { (key, value) ->
            if(game2.configDescription[key] != value){
                return false
            }
        }
        return true
    }

}