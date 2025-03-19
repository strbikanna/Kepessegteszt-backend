export interface ConfigItem {
    id: number | undefined;
    paramName: string;
    initialValue: number;
    hardestValue: number;
    easiestValue: number;
    increment: number;
    maxAbilityEffect: number;
    description: string;
}

export function isSameConfigItem(a: ConfigItem, b: ConfigItem): boolean {
    return a.id === b.id &&
        a.paramName === b.paramName &&
        a.initialValue === b.initialValue &&
        a.hardestValue === b.hardestValue &&
        a.easiestValue === b.easiestValue &&
        a.increment === b.increment &&
        a.maxAbilityEffect === b.maxAbilityEffect &&
        a.description === b.description
}