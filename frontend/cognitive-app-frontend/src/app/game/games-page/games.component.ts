import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Observable} from "rxjs";
import {GameplayModel} from "../../model/gameplay.model";
import {GameService} from "../game-services/game.service";
import {GameAuthService} from "../game-services/game-auth.service";
import {GameInfo} from "../../auth/gameInfo";
import {Router} from "@angular/router";

@Component({
    selector: 'app-games',
    templateUrl: './games.component.html',
    styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {

    text = TEXTS.games;
    gamesForYou : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    teacherRecommendedGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    scientistRecommendedGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()
    allGames : Observable<GameplayModel[]> = new Observable<GameplayModel[]>()

    loading = false;

    private storageKey = 'chosenGame'

    constructor(private gameService: GameService, private authService: GameAuthService, private router: Router) {}

    ngOnInit(): void {
        this.gamesForYou = this.gameService.getGamesForCurrentUser()
        this.teacherRecommendedGames = this.gameService.getTeacherRecommendedGames()
        this.scientistRecommendedGames = this.gameService.getScientistRecommendedGames()
        this.allGames = this.gameService.getAllGames()
        this.initGameAuthentication()
    }
    initGameAuthentication(){
        const chosenGameString = sessionStorage.getItem(this.storageKey)
        if (!chosenGameString) {
            return
        }
        const chosenGame: GameplayModel = JSON.parse(chosenGameString)
        if (this.validChosenGame(chosenGame)) {
            this.loading = true;
            GameInfo.authStatus.subscribe(isAuthenticated => {
                if (isAuthenticated) {
                    this.loadPlayground(chosenGame);
                } else {
                    this.loading = false;
                    console.log('Not authenticated')
                    //TODO handle failure
                }
            })
            this.authService.initAuthentication()
        } else {
            alert('No game id')
            //TODO handle failure
        }
    }

    private loadPlayground(chosenGame: GameplayModel) {
        GameInfo.currentGameId = chosenGame.config.game_id
        this.authService.publishChosenGame(chosenGame)
        this.loading = false;
        sessionStorage.removeItem(this.storageKey)
        this.router.navigate(['/playground'],)
    }

    onGameChosen(game: GameplayModel){
        if(this.validChosenGame(game)){
            this.loading = true;
            sessionStorage.setItem(this.storageKey, JSON.stringify(game))
            this.authService.getGameToken(game.config.game_id)
            this.initGameAuthentication()
        }
    }
    private validChosenGame(chosenGame: GameplayModel): boolean{
        return chosenGame.config.game_id !== undefined && chosenGame.config.game_id !== null
    }

}
