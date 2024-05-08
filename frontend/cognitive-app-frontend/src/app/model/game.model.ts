import {Ability} from "./ability.model";
import {ConfigItem} from "./config_item.model";

export interface Game {
    id: number | undefined;
    name: string;
    description: string;
    thumbnail: string;
    active: boolean;
    version: number;
    configDescription: any;
    affectedAbilities: Ability[];
    configItems: ConfigItem[];
}