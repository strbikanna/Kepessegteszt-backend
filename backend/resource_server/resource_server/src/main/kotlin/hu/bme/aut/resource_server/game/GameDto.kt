package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.databind.BeanDescription

open class GameDto(
        var name: String,

        var description: String,

        var icon: String,

        var active: Boolean,

        var url: String,

        var jsonDescriptor: String,

) {
    constructor(game: GameEntity) : this(
            game.name,
            game.description,
            game.icon,
            game.active,
            game.url,
            game.jsonDescriptor
    )

}