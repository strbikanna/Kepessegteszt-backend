package hu.bme.aut.resource_server.ability

import org.springframework.data.repository.CrudRepository
import java.util.*

interface AbilityRepository : CrudRepository<Ability, String> {
    fun findAllByName(name: String): List<Ability>
    fun findByCode(code: String): Optional<Ability>
}