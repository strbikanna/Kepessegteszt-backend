package hu.bme.aut.resource_server.result


import hu.bme.aut.resource_server.game.GameEntity
import hu.bme.aut.resource_server.recommended_game.RecommendedGameEntity
import hu.bme.aut.resource_server.user.UserEntity
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.StringWriter
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ExportCsvServiceTest {
    @Mock
    lateinit var resultEntity : ResultEntity

    @Mock
    lateinit var userEntity : UserEntity

    @Mock
    lateinit var recommendedGameEntity : RecommendedGameEntity

    @Mock
    lateinit var gameEntity : GameEntity

    @Test
    fun `should write result entity to csv in correct format`(){
        `when`(resultEntity.id).thenReturn(1)
        `when`(resultEntity.timestamp).thenReturn(LocalDateTime.now())
        `when`(resultEntity.user).thenReturn(userEntity)
        `when`(resultEntity.recommendedGame).thenReturn(recommendedGameEntity)
        `when`(recommendedGameEntity.game).thenReturn(gameEntity)
        `when`(userEntity.id).thenReturn(1)
        `when`(gameEntity.name).thenReturn("Game1")
        `when`(resultEntity.config).thenReturn(mutableMapOf("level" to 1, "difficulty" to "easy"))
        `when`(resultEntity.result).thenReturn(mutableMapOf("passed" to true, "score" to 100))
        val writer = StringWriter()
        ExportCsvService.exportCsv(listOf(resultEntity), writer)
        val csv = writer.toString()
        assertTrue(csv.contains("ID,Timestamp,User ID,Game name,Config,Result,Result passed"))
        assertTrue(csv.contains("\"{level:1,difficulty:easy}\""))
    }
}