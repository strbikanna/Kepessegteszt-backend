import {Ability} from "./ability.model";
import {ConfigItem} from "./config_item.model";

export interface Game {
    id: number | undefined;
    modelId: string | undefined;
    name: string;
    description: string;
    thumbnail: string;
    active: boolean;
    version: number;
    affectedAbilities: Ability[];
    configItems: ConfigItem[];
    storedConfig: boolean;
}