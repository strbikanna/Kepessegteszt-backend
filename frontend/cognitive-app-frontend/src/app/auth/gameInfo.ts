import {BehaviorSubject} from "rxjs";

/**
 * Global class to store chosen and authenticated game information
 */
export class GameInfo{
    public static readonly authStatus: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
    public static accessToken: string | undefined
    public static currentGameId: number | undefined
}