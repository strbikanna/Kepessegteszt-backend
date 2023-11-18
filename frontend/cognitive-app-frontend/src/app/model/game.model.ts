import {Ability} from "./ability.model";

export interface Game {
    id: number;
    name: string;
    description: string;
    thumbnail: string;
    url: string | undefined;
    active: boolean;
    version: number;
    configDescription: any;
    affectedAbilities: Ability[];
}