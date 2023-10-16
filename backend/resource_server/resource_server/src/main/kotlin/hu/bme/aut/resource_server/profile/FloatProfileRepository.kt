package hu.bme.aut.resource_server.profile

import org.springframework.data.repository.CrudRepository

interface FloatProfileRepository : CrudRepository<FloatProfileItem, Long> {
}