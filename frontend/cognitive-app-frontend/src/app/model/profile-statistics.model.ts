import {Ability} from "./ability.model";

export interface ProfileStatistics{
    ability: Ability;
    mean: number;
    deviation: number;
    individualValue: number;
}