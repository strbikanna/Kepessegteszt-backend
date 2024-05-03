export interface ConfigItem {
    id: number | undefined;
    paramName: string;
    initialValue: number;
    hardestValue: number;
    easiestValue: number;
    increment: number;
    paramOrder: number;
    description: string;
}