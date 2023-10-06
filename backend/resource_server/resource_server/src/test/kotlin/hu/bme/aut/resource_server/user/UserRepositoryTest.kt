package hu.bme.aut.resource_server.user

import hu.bme.aut.resource_server.ability.Ability
import hu.bme.aut.resource_server.profile.ProfileItem
import hu.bme.aut.resource_server.user.role.Role
import hu.bme.aut.resource_server.user.role.RoleName
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@SpringBootTest
class UserRepositoryTest(
    @Autowired private var userRepository: UserRepository
) {
    @Test
    fun shouldSaveUser(){
        val profile = mutableSetOf(
            ProfileItem(
                ability = Ability("Gv", "Visual processing", "?"),
                abilityValue = 10
                ),
            ProfileItem(
                ability = Ability("Gs", "Processing speed", "?"),
                abilityValue = 4
                ),
        )
        val user = UserEntity(
            firstName = "Test", lastName = "User", username = "test_user",
            profile = profile, roles = mutableSetOf(Role(roleName= RoleName.PARENT))
        )
        userRepository.save(user)
        assertNotNull(user.id)
        val savedUser = userRepository.findById(user.id!!).get()
        assertNotNull(savedUser.id)
        assertNotNull(savedUser.profile.first().id)
        assertEquals(2, savedUser.profile.size)
    }
}