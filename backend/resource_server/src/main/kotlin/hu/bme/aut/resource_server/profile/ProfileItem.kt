package hu.bme.aut.resource_server.profile

import hu.bme.aut.resource_server.ability.AbilityEntity

data class ProfileItem (

     val ability: AbilityEntity,

     val value: Any
){
}