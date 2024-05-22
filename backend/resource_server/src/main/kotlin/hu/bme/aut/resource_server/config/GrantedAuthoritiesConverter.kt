package hu.bme.aut.resource_server.config

import hu.bme.aut.resource_server.role.Role
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user.UserRepository
import hu.bme.aut.resource_server.utils.RoleName
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

/**
 * Used to convert the JWT token's claims to a list of granted authorities.
 * Saves new users in database.
 */
@Service
class GrantedAuthoritiesConverter(
        @Autowired private var userRepository: UserRepository,
) : Converter<Jwt, Collection<GrantedAuthority>> {

    override fun convert(source: Jwt): Collection<GrantedAuthority> {
        val roles = getRolesFromJwt(source)
        //TODO load subscription and add subscription role
        saveUser(source, roles)

        val grantedAuthorities = mutableListOf<GrantedAuthority>()
        roles.forEach { role ->
            grantedAuthorities.add(SimpleGrantedAuthority("ROLE_${role.roleName}"))
        }
        return grantedAuthorities
    }

    private fun saveUser(source: Jwt, roles: MutableList<Role>) {
        val username = source.subject
        val userDb = userRepository.findByUsername(username).orElse(null)
        val firstNameClaim = source.getClaimAsString("family_name")
        val lastNameClaim = source.getClaimAsString("given_name")
        if(userDb == null || userDb.roles.isEmpty() || userDb.firstName != firstNameClaim || userDb.lastName != lastNameClaim){
            userRepository.save(
                UserEntity(
                    id = userDb?.id,
                    username = username,
                    firstName = source.getClaimAsString("family_name"),
                    lastName = source.getClaimAsString("given_name"),
                    roles = roles.toMutableSet()
                )
            )
        }
    }

    private fun getRolesFromJwt(source: Jwt): MutableList<Role> {
        val tokenRoles = source.claims["roles"] as Collection<String>
        val roles = mutableListOf<Role>()
        tokenRoles.forEach {
            if (!it.contains("REQUEST") && !it.contains("request")) {
                roles.add(Role(RoleName.valueOf(it)))
            }
        }
        return roles
    }


}