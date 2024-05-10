export interface Result{
    id: number,
    gameId: number,
    gameName: string,
    timestamp: Date,
    config: any,
    result: any,
    passed: boolean | undefined,
    username: string | undefined
}