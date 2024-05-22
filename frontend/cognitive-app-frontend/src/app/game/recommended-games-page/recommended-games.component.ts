import {Component, OnInit} from '@angular/core';
import {TEXTS} from "../../utils/app.text_messages";
import {Observable} from "rxjs";
import {RecommendedGame} from "../../model/recommended_game.model";
import {RecommendedGameService} from "../game-services/recommended-game.service";


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

    constructor(private gameService: RecommendedGameService) {}

    ngOnInit(): void {
        this.gamesForYou = this.gameService.getGamesForCurrentUser()
        this.gamesForYou.subscribe(games => {
          this.loading = false;
        })
        this.teacherRecommendedGames = this.gameService.getTeacherRecommendedGames()
        this.scientistRecommendedGames = this.gameService.getScientistRecommendedGames()
        this.allGames = this.gameService.getAllGames()
    }

}
