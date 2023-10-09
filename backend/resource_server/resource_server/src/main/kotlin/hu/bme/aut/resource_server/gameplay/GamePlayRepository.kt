package hu.bme.aut.resource_server.gameplay

import org.springframework.data.repository.CrudRepository

interface GamePlayRepository: CrudRepository<GamePlay, Long> {
}