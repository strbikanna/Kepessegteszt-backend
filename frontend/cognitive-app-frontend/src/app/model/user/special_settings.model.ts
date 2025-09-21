export default interface SpecialSettings {
    distractionType: DistractionType;
}

export enum DistractionType{
    NONE = "NONE",
    SOUND = "SOUND",
    VISUAL = "VISUAL",
    COMBINED = "COMBINED",
    PAVLOV = "PAVLOV"
}