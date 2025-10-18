import {Ability} from "../ability.model";

export interface GenericProfileData {
    ability: Ability;
    value: number | string;
    accuracy: number;
}

export interface ProfileData{
    ability: Ability;
    value: number;
    accuracy: number;
}