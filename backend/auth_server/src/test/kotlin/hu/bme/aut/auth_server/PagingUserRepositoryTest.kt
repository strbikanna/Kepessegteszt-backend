package hu.bme.aut.auth_server

import hu.bme.aut.auth_server.user.PagingUserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest

@SpringBootTest
class PagingUserRepositoryTest(@Autowired private var pagingUserRepository: PagingUserRepository) {
    @Test
    fun shouldFindAll() {
        val users = pagingUserRepository.findAll(PageRequest.of(0, 3))
        assertEquals(3, users.content.size)
    }
}