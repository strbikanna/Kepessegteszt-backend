package hu.bme.aut.resource_server.llm.mistral_kmp.api

import hu.bme.aut.resource_server.llm.mistral_kmp.domain.*

internal class ResponseMapper {

    fun mapModels(remoteData: List<ModelDataResponse>): List<Model> {
        return remoteData.map {
            Model(
                id = it.id,
                name = it.obj,
                createdAt = it.createdAt,
                ownedBy = it.ownBy,
            )
        }
    }

    fun mapChoices(remoteChoices: List<CompletionsChoiceResponse>): List<Message> {
        return remoteChoices.map {
            Message(
                role = mapRole(it.message?.role),
                content = it.message?.content.orEmpty(),
            )
        }
    }

    fun mapStreamChoices(remoteChoices: List<CompletionsChoiceResponse>): List<Message> {
        return remoteChoices.map {
            Message(
                role = mapRole(it.message?.role),
                content = it.delta?.content.orEmpty(),
            )
        }
    }

    fun mapEmbeddings(remoteEmbeddings: EmbeddingsResponse): Embeddings {
        return Embeddings(
            id = remoteEmbeddings.id,
            obj = remoteEmbeddings.obj,
            model = remoteEmbeddings.model,
            embeddings = remoteEmbeddings.data.map {
                Embedding(
                    obj = it.obj,
                    embeddings = it.embedding,
                    index = it.index,
                )
            },
            usage = Usage(
                promptTokens = remoteEmbeddings.usage.promptTokens,
                totalTokens = remoteEmbeddings.usage.totalTokens,
            )
        )
    }

    private fun mapRole(role: String?): Role {
        return when (role) {
            "user" -> Role.USER
            "system" -> Role.SYSTEM
            "assistant" -> Role.ASSISTANT
            "tool" -> Role.TOOL
            else -> Role.UNKNOWN
        }
    }
}

internal class RequestMapper {

    fun mapCompletionRequest(
        model: String,
        messages: List<Message>,
        stream: Boolean = false,
        params: ModelParams? = null
    ): CompletionRequest {
        return CompletionRequest(
            model = model,
            messages = messages.map {
                MessageRequest(
                    role = it.role.role,
                    content = it.content,
                )
            },
            stream = stream,
            temperature = params?.temperature,
            topP = params?.topP,
            maxTokens = params?.maxTokens,
            randomSeed = params?.randomSeed,
            safePrompt = params?.safePrompt ?: true
        )
    }

    fun mapCodeCompletionRequest(
        model: String,
        prompt: String,
        stream: Boolean = false,
        params: ModelParams? = null
    ): CodeCompletionRequest {
        return CodeCompletionRequest(
            model = model,
            prompt = prompt,
            suffix = params?.suffix,
            stream = stream,
            temperature = params?.temperature,
            topP = params?.topP,
            maxTokens = params?.maxTokens,
            minTokens = params?.minTokens,
            randomSeed = params?.randomSeed,
            stop = params?.stop.orEmpty(),
        )
    }
}
