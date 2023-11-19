import {Component, OnInit} from '@angular/core';
import {initializeGame} from './main.js'
import {GameAuthService} from "../game-services/game-auth.service";
import {TEXTS} from "../../utils/app.text_messages";
import {RecommendedGame} from "../../model/recommended_game.model";
import {UserInfo} from "../../auth/userInfo";

@Component({
    selector: 'app-playground',
    templateUrl: './playground.component.html',
    styleUrls: ['./playground.component.scss']
})
export class PlaygroundComponent implements OnInit {
    constructor(private dataService: GameAuthService) {}

    gameChosen: boolean = true
    text = TEXTS.playground

    ngOnInit(): void {
        this.dataService.chosenGame.subscribe(game => {
            if(game !== undefined){
                console.log(game)
                this.gameChosen = true
                this.setupGameConfig(game)
                initializeGame(game.config)
            }else{
                this.gameChosen = false
                this.dataService.tryLoadChosenGame()
            }
        });
    }
    private setupGameConfig(game: RecommendedGame){
        if(!game.config.game_id) game.config.game_id = game.id
        if(!game.config.username) game.config.username = UserInfo.currentUser?.username
        if(!game.config.gameName) game.config.gameName = game.game.configDescription.gameName ?? game.game.name
        if(!game.config.gameTitle) game.config.gameTitle = game.game.configDescription.gameTitle ?? game.game.name
    }

}
