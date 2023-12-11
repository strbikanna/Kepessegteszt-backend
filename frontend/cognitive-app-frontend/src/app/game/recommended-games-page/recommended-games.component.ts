import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Observable} from "rxjs";
import {RecommendedGame} from "../../model/recommended_game.model";
import {RecommendedGameService} from "../game-services/recommended-game.service";
import {GameAuthService} from "../game-services/game-auth.service";
import {GameInfo} from "../../auth/gameInfo";

@Component({
    selector: 'app-games',
    templateUrl: './recommended-games.component.html',
    styleUrls: ['./recommended-games.component.scss']
})
export class RecommendedGamesComponent implements OnInit {

    text = TEXTS.games;
    gamesForYou: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>()
    teacherRecommendedGames: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>()
    scientistRecommendedGames: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>()
    allGames: Observable<RecommendedGame[]> = new Observable<RecommendedGame[]>()

    loading = true;

    private storageKey = 'games_chosenGame'

    private chosenGame: RecommendedGame | undefined = undefined

    constructor(private gameService: RecommendedGameService, private authService: GameAuthService, ) {
    }

    ngOnInit(): void {
        console.log(this.loading)
        this.gamesForYou = this.gameService.getGamesForCurrentUser()
        this.gamesForYou.subscribe(games => {
            console.log('Not loading')
          this.loading = false;
        })
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
                console.log('Not authenticated')
                //TODO handle failure
            }
        })

    }

    private loadPlayground(chosenGame: RecommendedGame) {
        this.resetStateToIdle();
        GameInfo.currentGameId = chosenGame.config.game_id
        this.authService.publishChosenGame(chosenGame)
    }

    private resetStateToIdle() {
        this.loading = false;
        sessionStorage.removeItem(this.storageKey)
        this.chosenGame = undefined
    }


    onGameChosen(rGame: RecommendedGame) {
        console.log('Game chosen: ', rGame)
        if (this.validChosenGame(rGame)) {
            this.loading = true;
            sessionStorage.setItem(this.storageKey, JSON.stringify(rGame))
            this.authService.getGameToken(rGame.game.id)
        }
    }

    private validChosenGame(chosenGame: RecommendedGame | undefined): boolean {
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
