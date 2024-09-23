export interface UserFilter {
    ageMin: number | undefined;
    ageMax: number | undefined;
    addressCity: string | undefined;
    addressZip: string | undefined;
    userGroupId: number | undefined;
    abilityFilter: AbilityFilter[];
}
export interface AbilityFilter{
    code: string | undefined;
    valueMin: number | undefined;
    valueMax: number | undefined;
}