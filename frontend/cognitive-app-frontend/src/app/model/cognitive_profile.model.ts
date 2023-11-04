import {Ability} from "./ability.model";

export interface CognitiveProfile{
    timestamp: Date,
    profileItems: Map<Ability, any>,
}