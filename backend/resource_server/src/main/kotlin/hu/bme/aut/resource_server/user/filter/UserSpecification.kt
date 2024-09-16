package hu.bme.aut.resource_server.user.filter

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.organization.Address
import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

class UserSpecification(
    private val filter: UserFilterDto
) : Specification<UserEntity> {

    private val epsilon: Double = 0.0000001

    override fun toPredicate(
        root: Root<UserEntity>,
        query: CriteriaQuery<*>,
        criteriaBuilder: CriteriaBuilder
    ): Predicate? {
        val predicateList = mutableListOf<Predicate>()
        val dateNow = LocalDate.now()
        filter.ageMin?.let {
            predicateList.add(criteriaBuilder.lessThanOrEqualTo(root.get("birthDate"), dateNow.minusYears(it.toLong())))
        }
        filter.ageMax?.let {
            predicateList.add(criteriaBuilder.greaterThanOrEqualTo(root.get("birthDate"), dateNow.minusYears(it.toLong())))
        }
        filter.addressCity?.let {
            predicateList.add(criteriaBuilder.equal(root.get<Address>("address").get<String>("city"), it))
        }
        filter.addressZip?.let {
            predicateList.add(criteriaBuilder.equal(root.get<Address>("address").get<String>("zip"), it))
        }
        filter.abilityCode?.let {
            val profileItem: Join<UserEntity, FloatProfileItem> = root.join("profileFloat");
            val ability : Join<FloatProfileItem, AbilityEntity> = profileItem.join("ability");

            val abilityPredicate = criteriaBuilder.equal(ability.get<String>("code"), it)
            predicateList.add(abilityPredicate)
            filter.abilityValueMin?.let {
                val abilityValueMin = filter.abilityValueMin - epsilon
                val valuePredicateMin = criteriaBuilder.greaterThanOrEqualTo(profileItem.get<Double>("abilityValue"), abilityValueMin)
                predicateList.add(valuePredicateMin)
            }
            filter.abilityValueMax?.let {
                val abilityValueMax = filter.abilityValueMax + epsilon
                val valuePredicateMax = criteriaBuilder.lessThanOrEqualTo(profileItem.get<Double>("abilityValue"), abilityValueMax)
                predicateList.add(valuePredicateMax)
            }
        }
        return criteriaBuilder.and(*predicateList.toTypedArray())
    }
}