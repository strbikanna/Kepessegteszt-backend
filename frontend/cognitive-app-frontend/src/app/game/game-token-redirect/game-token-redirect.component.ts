import {Component, OnDestroy, OnInit, } from '@angular/core';
import {GameplayModel} from "../../model/gameplay.model";
import {GameAuthService} from "../game-services/game-auth.service";
import {GameInfo} from "../../auth/gameInfo";
import {Router} from "@angular/router";
import {TEXTS} from "../../utils/app.text_messages";

@Component({
    selector: 'app-game-token-redirect',
    templateUrl: './game-token-redirect.component.html',
    styleUrls: ['./game-token-redirect.component.scss']
})
export class GameTokenRedirectComponent implements OnInit, OnDestroy {

    text = TEXTS.games.progress
    loading = true;
    private redirectUrl = '/playground'
    private failureRedirectUrl = '/playground'

    constructor(private authService: GameAuthService, private router: Router) {
    }

    ngOnInit(): void {
        // console.log('Redirect')
        // const chosenGameString = sessionStorage.getItem('chosenGame')
        // if (!chosenGameString) {
        //     alert('No game chosen')
        //     //TODO handle failure
        //     return
        // }
        // const chosenGame: GameplayModel = JSON.parse(chosenGameString)
        // const chosenGameId = chosenGame.config.game_id
        // if (chosenGameId) {
        //     //TODO potentially save user data
        //     this.authService.initAuthentication()
        //     GameInfo.authStatus.subscribe(isAuthenticated => {
        //         if (isAuthenticated) {
        //             this.authService.publishChosenGame(chosenGame)
        //             this.loading = false;
        //             this.router.navigate([this.redirectUrl, {game_id: chosenGameId}])
        //         } else {
        //             console.log('Not authenticated')
        //             //TODO handle failure
        //             this.router.navigate([this.failureRedirectUrl])
        //         }
        //     })
        //     this.authService.getGameToken(chosenGameId)
        // } else {
        //     alert('No game id')
        //     //TODO handle failure
        //     this.router.navigate([this.failureRedirectUrl])
        // }
    }

    ngOnDestroy(): void {
        //GameInfo.authStatus.unsubscribe()
    }

}
