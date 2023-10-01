package hu.bme.aut.resource_server.game

import jakarta.persistence.*
import java.sql.Date

@Entity
@Table(name = "GAMEPLAY")
data class RecommendedGameEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val time_stamp: Date,

    val score: String,

    val user_id: Int,

    val game_id: Int,
)