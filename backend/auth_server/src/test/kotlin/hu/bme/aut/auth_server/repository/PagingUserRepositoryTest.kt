package hu.bme.aut.auth_server.repository

import hu.bme.aut.auth_server.user.UserRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class PagingUserRepositoryTest(@Autowired private var pagingUserRepository: UserRepository) {
    @Test
    fun shouldFindAll() {
        val users = pagingUserRepository.findAll(PageRequest.of(0, 3))
        assertEquals(3, users.content.size)
    }
}