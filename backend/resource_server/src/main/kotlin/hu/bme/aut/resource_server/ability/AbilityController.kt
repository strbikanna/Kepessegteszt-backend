package hu.bme.aut.resource_server.ability

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ability")
@PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST')")
class AbilityController(
        @Autowired private var abilityRepository: AbilityRepository
) {
    @GetMapping("/all")
    fun getAllAbilities(): ResponseEntity<List<Ability>> {
        val abilities = abilityRepository.findAll().toList()
        return ResponseEntity(abilities, HttpStatus.OK)
    }

    @PutMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    fun updateAbility(@RequestBody ability: Ability, @PathVariable code: String): Ability {
        if(code == ability.code) {
            return abilityRepository.save(ability)
        }else{
            throw IllegalArgumentException("Ability with code does not exist.")
        }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createAbility(@RequestBody ability: Ability): Ability {
        return abilityRepository.save(ability)
    }
}