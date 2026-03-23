package hu.bme.aut.resource_server.recommendation.visitor

interface GameConfigVisitor {
    fun visitConfig(config: Map<String, Any>): Map<String, Any>
}