package hu.bme.aut.resource_server.user.filter

import hu.bme.aut.resource_server.ability.AbilityEntity
import hu.bme.aut.resource_server.profile.FloatProfileItem
import hu.bme.aut.resource_server.user.UserEntity
import hu.bme.aut.resource_server.user_group.organization.Address
import hu.bme.aut.resource_server.utils.EnumAbilityValue
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
        filter.abilityFilter.forEach { abilityE ->
        abilityE.code?.let {
            val profileItem: Join<UserEntity, FloatProfileItem> = root.join("profileEnum");
            val ability: Join<FloatProfileItem, AbilityEntity> = profileItem.join("ability");

            val abilityPredicate = criteriaBuilder.equal(ability.get<String>("code"), it)
            predicateList.add(abilityPredicate)
            val valuePredicate = criteriaBuilder.equal(profileItem.get<EnumAbilityValue>("abilityValue"), EnumAbilityValue.YES)
            predicateList.add(valuePredicate)
//            abilityE.valueMin?.let { minVal ->
//                val abilityValueMin = minVal - epsilon
//                val valuePredicateMin =
//                    criteriaBuilder.greaterThanOrEqualTo(profileItem.get<Double>("abilityValue"), abilityValueMin)
//                predicateList.add(valuePredicateMin)
//            }
//            abilityE.valueMax?.let { maxVal ->
//                val abilityValueMax = maxVal + epsilon
//                val valuePredicateMax =
//                    criteriaBuilder.lessThanOrEqualTo(profileItem.get<Double>("abilityValue"), abilityValueMax)
//                predicateList.add(valuePredicateMax)
//            }
        }
        }
        return criteriaBuilder.and(*predicateList.toTypedArray())
    }
}