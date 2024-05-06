export interface Result{
    id: number,
    gameId: number,
    timestamp: Date,
    config: any,
    result: any,
    passed: boolean | undefined,
    username: string | undefined
}