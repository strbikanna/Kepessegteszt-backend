import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {GameplayModel} from "../model/gameplay.model";

@Injectable({
    providedIn: 'root'
})
export class GameDataService {
    public chosenGame = new BehaviorSubject<GameplayModel | undefined>(undefined);

    publishChosenGame(game: GameplayModel) {
        this.chosenGame.next(game)
    }

}
