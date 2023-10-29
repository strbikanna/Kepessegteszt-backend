package hu.bme.aut.resource_server.gameplay

data class GameplayDto(

    var id: Int? = null,

    val name: String,

    val description: String,

    val thumbnail: String,

    val url: String?,

    val config: Map<String, Any>
) {
}