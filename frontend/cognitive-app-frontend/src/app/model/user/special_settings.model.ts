export default interface SpecialSettings {
    distractionTypes: DistractionType[];
    minInterval?: number;
    maxInterval?: number;
    validMinutes: number;
}

export enum DistractionType{
    SOUND = "SOUND",
    VISUAL = "VISUAL",
    PAVLOVIAN = "PAVLOVIAN",
    BLACKSCREEN = "BLACKSCREEN",
    NOTIFICATION = "NOTIFICATION",
    VIBRATION = "VIBRATION"
}