package hu.bme.aut.resource_server.gameplay

import hu.bme.aut.resource_server.TestUtilsService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class GameplayRepositoryTest(
    @Autowired private var gameplayRepository: GameplayRepository,
    @Autowired private var testService: TestUtilsService

) {
    @BeforeEach
    fun init(){
        testService.fillAbilityRepository()
    }
    @Test
    fun shouldSaveGameplay(){
        val gameplay = testService.createGamePlay()
        gameplayRepository.save(gameplay)

    }
}