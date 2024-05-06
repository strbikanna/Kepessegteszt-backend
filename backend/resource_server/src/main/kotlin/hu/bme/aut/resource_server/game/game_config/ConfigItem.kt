package hu.bme.aut.resource_server.game.game_config

import jakarta.persistence.*

@Entity(name = "config_description_item")
data class ConfigItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name="param_name")
    val paramName: String,

    @Column(name="param_order")
    val paramOrder: Int,

    @Column(name="easiest_value")
    val easiestValue: Int,

    @Column(name="hardest_value")
    val hardestValue: Int,

    @Column(name="initial_value")
    val initialValue: Int,

    val increment: Int,

    val description: String
)
fun ConfigItem.isSame(other: ConfigItem): Boolean {
    return paramName == other.paramName &&
            paramOrder == other.paramOrder &&
            easiestValue == other.easiestValue &&
            hardestValue == other.hardestValue &&
            initialValue == other.initialValue &&
            increment == other.increment &&
            description == other.description
}
