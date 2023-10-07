import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../utils/app.text_messages";
import {Observable, of, Subject} from "rxjs";
import {GameplayModel} from "../model/gameplay.model";
import {GameDataService} from "../game-services/game-data.service";
import {GameService} from "../game-services/game.service";

@Component({
    selector: 'app-games',
    templateUrl: './games.component.html',
    styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {

    public text = TEXTS.games;
    public gamesForYou : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    public teacherRecommendedGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    public scientistRecommendedGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    public allGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()

    constructor(private dataService: GameDataService, private gameService: GameService) {}

    ngOnInit(): void {
        this.gamesForYou = this.gameService.getGamesForCurrentUser()
        this.teacherRecommendedGames = this.gameService.getTeacherRecommendedGames()
        this.scientistRecommendedGames = this.gameService.getScientistRecommendedGames()
        this.allGames = this.gameService.getAllGames()
    }

    onGameChosen(game: GameplayModel){
        this.dataService.publishChosenGame(game)
    }

}
