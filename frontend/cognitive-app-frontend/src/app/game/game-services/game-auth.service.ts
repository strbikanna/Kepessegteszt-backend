import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {GameplayModel} from "../../model/gameplay.model";
import {LoginResponse, OidcSecurityService} from "angular-auth-oidc-client";
import {GameInfo} from "../../auth/gameInfo";
import {UserInfo} from "../../auth/userInfo";

/**
 * Service that retrieves game access token for the chosen game
 * And helps with data transfer between sibling components: game-page and playground
 */
@Injectable({
    providedIn: 'root'
})
export class GameAuthService {
    public chosenGame = new BehaviorSubject<GameplayModel | undefined>(undefined);

    private GAME_CONFIG_ID = 'gameTokenConfig'

    constructor(private oidcSecurityService: OidcSecurityService) {
    }

    initAuthentication() {
        this.oidcSecurityService
            .checkAuth(undefined, this.GAME_CONFIG_ID)
            .subscribe((loginResponse: LoginResponse) => {
                console.log('Game authentication happened: ' + loginResponse.isAuthenticated)
                GameInfo.accessToken = loginResponse.accessToken
                GameInfo.authStatus.next(loginResponse.isAuthenticated)
            }
        );
    }

    getGameToken(id: number) {
        console.log('Trying to authorize with popup')
        GameInfo.currentGameId = id
        const popupOptions = { width: 500, height: 500, left: 50, top: 50 };
        this.oidcSecurityService.authorizeWithPopUp( {customParams: {'game_id': id}}, popupOptions, this.GAME_CONFIG_ID)
    }

    publishChosenGame(game: GameplayModel) {
        game.config.username = UserInfo.currentUser?.username ?? 'unknown'
        console.log('User for game: ' + game.config.username)
        game.config.access_token = GameInfo.accessToken
        this.chosenGame.next(game)
    }

}
