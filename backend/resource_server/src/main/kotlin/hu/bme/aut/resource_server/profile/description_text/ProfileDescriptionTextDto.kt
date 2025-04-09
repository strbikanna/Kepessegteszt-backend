package hu.bme.aut.resource_server.profile.description_text

data class ProfileDescriptionTextDto(
    val id: Int? = null,
    val generatedText: String,
    val timestamp: String? = null,
){
    constructor(entity: ProfileDescriptionTextEntity) : this(
        id = entity.id,
        generatedText = entity.generatedText,
        timestamp = entity.timestamp?.toString(),
    )
}
