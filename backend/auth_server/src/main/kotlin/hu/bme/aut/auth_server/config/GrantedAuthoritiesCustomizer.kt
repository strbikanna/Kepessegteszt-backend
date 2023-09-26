package hu.bme.aut.auth_server.config

import hu.bme.aut.auth_server.user.UserInfoService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service

@Service
class GrantedAuthoritiesCustomizer(
    @Autowired private var userInfoService: UserInfoService
) : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(source: Jwt): Collection<GrantedAuthority>? {
        val userEntity = userInfoService.loadUserByUsername(source.subject)

        return userEntity.get().roleEntities
            .map { role -> SimpleGrantedAuthority("ROLE_${role.roleName.name}") }
    }
}