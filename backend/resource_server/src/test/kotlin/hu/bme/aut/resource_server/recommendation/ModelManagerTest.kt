package hu.bme.aut.resource_server.recommendation

import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ModelManagerTest(
    @Autowired private var modelManager: ModelManager
) {
    @Test
    fun shouldCreateModelWithoutError() {
        val modelInput = createModelInputData()
        assertDoesNotThrow {
            runBlocking {
                val job = modelManager.createNewModel(1, modelInput.first, modelInput.second)
                job.join()
                assertTrue(modelManager.existsModel(1))
            }
        }
    }

    @Test
    fun shouldReturnEstimation(){
        val modelInput = createModelInputData()
        runBlocking {
            val job = modelManager.createNewModel(1, modelInput.first, modelInput.second)
            job.join()
            assertTrue(modelManager.existsModel(1))
            val estimation = modelManager.getEstimationForResult(1, modelInput.first[0])
            //assertTrue(estimation in 40.0..60.0)
        }
    }

    @Test
    fun shouldCalculateAbilityRates(){
        val modelInput = createModelInputData()
        runBlocking {
            val job = modelManager.createNewModel(1, modelInput.first, modelInput.second)
            job.join()
            assertTrue(modelManager.existsModel(1))
            val rates = modelManager.calculateRates(modelInput.first, modelInput.second)
            assertTrue(rates.size == 3)
        }
    }

    private fun createModelInputData(): Pair<List<List<Double>>, List<Double>> {
        val mock2DArray = listOf(
            listOf(1.0, 1.2, 0.7),
            listOf(1.1, 1.1, 0.9),
            listOf(0.7, 1.0, 0.75),
            listOf(0.95, 1.02, 0.78),
            listOf(1.0, 1.3, 1.1),
        )
        val mock1DArray = listOf(51.0, 53.0, 47.0, 50.0, 54.0)
        return Pair(mock2DArray, mock1DArray)
    }
}