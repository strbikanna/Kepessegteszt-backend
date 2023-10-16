import {BehaviorSubject} from "rxjs";

export class GameInfo{
    public static readonly authStatus: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false)
    public static accessToken: string
}