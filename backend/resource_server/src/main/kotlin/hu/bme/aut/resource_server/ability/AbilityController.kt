package hu.bme.aut.resource_server.ability

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/ability")
class AbilityController(
        @Autowired private var abilityRepository: AbilityRepository
) {
    @GetMapping("/all")
    fun getAllAbilities(): ResponseEntity<List<AbilityEntity>> {
        val abilities = abilityRepository.findAll().toList()
        return ResponseEntity(abilities, HttpStatus.OK)
    }

    @GetMapping("/{code}")
    @ResponseStatus(HttpStatus.OK)
    fun getAbilityByCode(@PathVariable code: String): AbilityEntity {
        return abilityRepository.findById(code).orElseThrow()

    }

    @PutMapping("/{code}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST')")
    @ResponseStatus(HttpStatus.OK)
    fun updateAbility(@RequestBody abilityEntity: AbilityEntity, @PathVariable code: String): AbilityEntity {
        if(code == abilityEntity.code) {
            return abilityRepository.save(abilityEntity)
        }else{
            throw IllegalArgumentException("Ability with code does not exist.")
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SCIENTIST')")
    @ResponseStatus(HttpStatus.CREATED)
    fun createAbility(@RequestBody abilityEntity: AbilityEntity): AbilityEntity {
        return abilityRepository.save(abilityEntity)
    }
}