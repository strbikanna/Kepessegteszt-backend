import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Observable} from "rxjs";
import {Game} from "../../model/game.model";
import {GameService} from "../game-services/game.service";
import {GameAuthService} from "../game-services/game-auth.service";
import {GameInfo} from "../../auth/gameInfo";

@Component({
    selector: 'app-games',
    templateUrl: './games.component.html',
    styleUrls: ['./games.component.scss']
})
export class GamesComponent implements OnInit {

    text = TEXTS.games;
    gamesForYou: Observable<Game[]> = new Observable<Game[]>()
    teacherRecommendedGames: Observable<Game[]> = new Observable<Game[]>()
    scientistRecommendedGames: Observable<Game[]> = new Observable<Game[]>()
    allGames: Observable<Game[]> = new Observable<Game[]>()

    loading = false;

    private storageKey = 'games_chosenGame'

    private chosenGame: Game | undefined = undefined

    constructor(private gameService: GameService, private authService: GameAuthService, ) {
    }

    ngOnInit(): void {
        this.gamesForYou = this.gameService.getGamesForCurrentUser()
        this.teacherRecommendedGames = this.gameService.getTeacherRecommendedGames()
        this.scientistRecommendedGames = this.gameService.getScientistRecommendedGames()
        this.allGames = this.gameService.getAllGames()
        this.getChosenGame()
        this.initGameAuthentication()
    }

    private initGameAuthentication() {
        this.getChosenGame()
        this.authService.initAuthentication()
        if(this.validChosenGame(this.chosenGame)){
            this.loading = true;
        }
        GameInfo.authStatus.subscribe(isAuthenticated => {
            if (isAuthenticated && this.validChosenGame(this.chosenGame)) {
                this.loadPlayground(this.chosenGame!);
            } else {
                this.loading = false;
                console.log('Not authenticated')
                //TODO handle failure
            }
        })

    }

    private loadPlayground(chosenGame: Game) {
        this.resetStateToIdle();
        GameInfo.currentGameId = chosenGame.config.game_id
        this.authService.publishChosenGame(chosenGame)
    }

    private resetStateToIdle() {
        this.loading = false;
        sessionStorage.removeItem(this.storageKey)
        this.chosenGame = undefined
    }


    onGameChosen(game: Game) {
        if (this.validChosenGame(game)) {
            this.loading = true;
            sessionStorage.setItem(this.storageKey, JSON.stringify(game))
            this.authService.getGameToken(game.config.game_id)
        }
    }

    private validChosenGame(chosenGame: Game | undefined): boolean {
        return chosenGame !== undefined && chosenGame.config.game_id !== undefined && chosenGame.config.game_id !== null
    }

    private getChosenGame() {
        const chosenGameString = sessionStorage.getItem(this.storageKey)
        if (!chosenGameString) {
            return
        }
        this.chosenGame = JSON.parse(chosenGameString)
    }
}
