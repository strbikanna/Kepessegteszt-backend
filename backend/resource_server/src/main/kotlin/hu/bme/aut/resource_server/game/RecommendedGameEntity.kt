package hu.bme.aut.resource_server.game

import jakarta.persistence.*
import java.sql.Date

@Entity
@Table(name = "RECOMMENDED_GAMES")
data class RecommendedGameEntity(
    @Id
    @GeneratedValue
    val id: Int,

    val time_stamp: Date,

    val recommender_id: Int,

    val recommendee_id: Int,

    val game_id: Int,
)