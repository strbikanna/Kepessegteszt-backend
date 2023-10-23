import {Component, OnInit} from '@angular/core';
import {initializeGame} from './main.js'
import {GameDataService} from "../game-services/game-data.service";
import {GameplayModel} from "../model/gameplay.model";
import {TEXTS} from "../utils/app.text_messages";
import {MatDialog} from "@angular/material/dialog";

@Component({
    selector: 'app-playground',
    templateUrl: './playground.component.html',
    styleUrls: ['./playground.component.scss']
})
export class PlaygroundComponent implements OnInit {
    constructor(private dataService: GameDataService, private dialog: MatDialog) {}

    gameChosen: boolean = true
    text = TEXTS.playground
    ngOnInit(): void {
        console.log('Init playground')
        this.dataService.chosenGame.subscribe(game => {
            if(game !== undefined){
                this.gameChosen = true
                console.log(game)
                initializeGame(game.config)
            }else{
                this.gameChosen = false
            }
        })

    }

}
