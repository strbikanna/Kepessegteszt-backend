package hu.bme.aut.resource_server.ability

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AbilityRepository : CrudRepository<AbilityEntity, String> {
    fun findAllByName(name: String): List<AbilityEntity>
    fun findByCode(code: String): Optional<AbilityEntity>
}