package hu.bme.aut.resource_server.game

import jakarta.persistence.*
import org.hibernate.annotations.Type
import io.hypersistence.utils.hibernate.type.json.JsonType
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path

@Entity
@Table(name = "GAME")
data class GameEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name ="_name")
    val name: String,

    @Column(name ="_description")
    val description: String,

    val icon: String,

    val thumbnailPath: String,

    @Column(name ="_active")
    val active: Boolean,

    val url: String,

    @Type(JsonType::class)
    val configDescription: Map<String, Any>
) {
    fun getImage(): String {
        val fileContent: ByteArray = Files.readAllBytes(Path(thumbnailPath))
        return Base64.getEncoder().encodeToString(fileContent)
    }
}