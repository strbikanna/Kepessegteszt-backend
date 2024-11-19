export interface Ability {
    code: string,
    name: string,
    description: string,
    type: AbilityType,
}
export enum AbilityType{
    FLOAT = "FLOATING",
    ENUM = "ENUMERATED",
}