package hu.bme.aut.resource_server.profile.description_text

import org.springframework.data.repository.CrudRepository

interface ProfileDescriptionTextRepository: CrudRepository<ProfileDescriptionTextEntity, Int> {
    fun findAllByUserUsername(username: String): List<ProfileDescriptionTextEntity>
    fun deleteByUserUsername(username: String)
}