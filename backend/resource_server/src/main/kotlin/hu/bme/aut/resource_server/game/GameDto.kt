package hu.bme.aut.resource_server.game

open class GameDto(

    var name: String,

    var description: String,

    val icon: String,

    val url: String?,

    val configDescription: Map<String, Any>

) {
    constructor(game: GameEntity) : this(
        game.name,
        game.description,
        game.thumbnailPath,
        game.url,
        game.configDescription
    )
}
