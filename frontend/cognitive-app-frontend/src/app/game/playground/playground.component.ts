import {Component, OnInit} from '@angular/core';
import {initializeGame} from './main.js'
import {GameAuthService} from "../game-services/game-auth.service";
import {TEXTS} from "../../utils/app.text_messages";

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
                this.gameChosen = true
                initializeGame(game.config)
            }else{
                this.gameChosen = false
                this.dataService.tryLoadChosenGame()
            }
        });
    }

}
