package hu.bme.aut.resource_server.game

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import org.hibernate.annotations.Type
import io.hypersistence.utils.hibernate.type.json.JsonType

@Entity
@Table(name = "GAME")
data class Game(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int,

    val name: String,

    val description: String,

    val icon: String,

    val thumbnailPath: String,

    val active: Boolean,

    val url: String,

    @Type(JsonType::class)
    val configDescription: JsonNode
)