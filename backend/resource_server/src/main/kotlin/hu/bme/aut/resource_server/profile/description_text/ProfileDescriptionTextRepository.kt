package hu.bme.aut.resource_server.profile.description_text

import org.springframework.data.repository.CrudRepository

interface ProfileDescriptionTextRepository: CrudRepository<ProfileDescriptionTextEntity, Int> {
    fun findByUserUsername(username: String): ProfileDescriptionTextEntity?
    fun deleteByUserUsername(username: String)
}